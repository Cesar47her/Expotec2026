package main.Conexion;

import main.Util.PasswordUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class PasswordRecoveryDAO {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Map<String, String> solicitarCodigoRecuperacion(String correo) {
        String consulta = "SELECT id_usuario, username FROM USUARIO WHERE correo = ? AND estado_cuenta = 'ACTIVO'";
        String actualizar = "UPDATE USUARIO SET codigo_recuperacion = ?, fecha_expiracion_recuperacion = ? WHERE id_usuario = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(consulta)) {

            ps.setString(1, correo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int idUsuario = rs.getInt("id_usuario");
                    String username = rs.getString("username");
                    String codigo = PasswordUtil.generateRecoveryCode();
                    String expiracion = LocalDateTime.now().plusMinutes(15).format(FORMATTER);

                    try (PreparedStatement psActualiza = con.prepareStatement(actualizar)) {
                        psActualiza.setString(1, codigo);
                        psActualiza.setString(2, expiracion);
                        psActualiza.setInt(3, idUsuario);
                        psActualiza.executeUpdate();
                    }

                    Map<String, String> resultado = new HashMap<>();
                    resultado.put("username", username);
                    resultado.put("codigo", codigo);
                    resultado.put("idUsuario", String.valueOf(idUsuario));
                    resultado.put("expiracion", expiracion);
                    return resultado;
                }
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Fallo de recuperación de código: " + e.getMessage());
        }
        return null;
    }

    public boolean verificarCodigo(String correo, String codigo) {
        String sql = "SELECT id_usuario FROM USUARIO WHERE correo = ? AND codigo_recuperacion = ? " +
                     "AND fecha_expiracion_recuperacion >= ? AND estado_cuenta = 'ACTIVO'";
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, correo);
            ps.setString(2, codigo);
            ps.setString(3, LocalDateTime.now().format(FORMATTER));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Fallo al validar código de recuperación: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarContrasena(int idUsuario, String nuevaContrasena) {
        String sql = "UPDATE USUARIO SET contrasena = ?, codigo_recuperacion = NULL, fecha_expiracion_recuperacion = NULL WHERE id_usuario = ?";
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            String hashed = PasswordUtil.hashPassword(nuevaContrasena);
            ps.setString(1, hashed);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Fallo al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }
}
