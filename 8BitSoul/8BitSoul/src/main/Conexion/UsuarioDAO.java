package main.Conexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import main.Util.PasswordUtil;

public class UsuarioDAO {

    /**
     * Busca el id_usuario basándose en el username.
     * @param username El nombre de usuario.
     * @return El ID numérico del usuario en la base de datos, o 0 si no se encuentra.
     */
    public int obtenerIdPorUsername(String username) {
        int idUsuario = 0;
        String sql = "SELECT id_usuario FROM USUARIO WHERE username = ? AND estado_cuenta = 'ACTIVO'";
        
        ConexionSQL conexion = new ConexionSQL();
        
        try (Connection con = conexion.obtenerConexion();
             PreparedStatement pstm = con.prepareStatement(sql)) {
            
            pstm.setString(1, username);
            
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                }
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ [Error UsuarioDAO]: Error al intentar recuperar el ID del usuario.");
            e.printStackTrace();
        }
        
        return idUsuario;
    }

    /**
     * Valida las credenciales unificando la tabla USUARIO con la tabla ROL.
     * @param username El nombre de usuario ingresado en el JTextField.
     * @param contrasena La contraseña ingresada en el JPasswordField.
     * @return El nombre real del rol (ej: 'ADMINISTRADOR', 'USUARIO_CORE') o null si no se encuentra.
     */
    public String autenticarUsuario(String username, String contrasena) {
        String rolDetectado = null;
        String sql = "SELECT u.contrasena, r.nombre_rol FROM USUARIO u " +
                     "INNER JOIN ROL r ON u.id_rol = r.id_rol " +
                     "WHERE u.username = ? AND u.estado_cuenta = 'ACTIVO'";
        
        ConexionSQL conexion = new ConexionSQL();
        
        try (Connection con = conexion.obtenerConexion();
             PreparedStatement pstm = con.prepareStatement(sql)) {
            
            pstm.setString(1, username);
            
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    String passwordHash = rs.getString("contrasena");
                    if (PasswordUtil.verifyPassword(contrasena, passwordHash)) {
                        rolDetectado = rs.getString("nombre_rol");
                    }
                }
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ [Error UsuarioDAO]: Error al mapear credenciales con las tablas.");
            e.printStackTrace();
        }
        
        return rolDetectado;
    }

    /**
     * Busca el username registrado por correo electrónico.
     * @param correoFinal El correo electrónico ingresado en el diálogo de recuperación.
     * @return El username registrado, o null si no existe.
     */
    public String obtenerUsernamePorCorreo(String correoFinal) {
        String username = null;
        String sql = "SELECT username FROM USUARIO WHERE correo = ? AND estado_cuenta = 'ACTIVO'";
        
        ConexionSQL conexion = new ConexionSQL();
        
        try (Connection con = conexion.obtenerConexion();
             PreparedStatement pstm = con.prepareStatement(sql)) {
            
            pstm.setString(1, correoFinal);
            
            try (ResultSet rs = pstm.executeQuery()) {
                if (rs.next()) {
                    username = rs.getString("username");
                }
            }
            
        } catch (Exception e) {
            System.err.println("⚠️ [Error UsuarioDAO]: Error en el protocolo de recuperación por correo.");
            e.printStackTrace();
        }
        
        return username;
    }

    public boolean actualizarCredenciales(int idUsuario, String nuevoEmail, String nuevaContrasena) {
        String sql = "UPDATE USUARIO SET correo = ?, contrasena = ? WHERE id_usuario = ?";
        ConexionSQL conexion = new ConexionSQL();

        try (Connection con = conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String hashedPassword = PasswordUtil.hashPassword(nuevaContrasena);
            ps.setString(1, nuevoEmail);
            ps.setString(2, hashedPassword);
            ps.setInt(3, idUsuario);
            
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("⚠️ [Error UsuarioDAO]: Fallo al actualizar credenciales.");
            e.printStackTrace();
            return false;
        }
    }
}