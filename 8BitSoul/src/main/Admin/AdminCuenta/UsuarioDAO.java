package main.Admin.AdminCuenta;

// 1. IMPORTACIÓN CORREGIDA APUNTANDO A TU CLASE REAL
import main.Conexion.ConexionSQL; 
import java.sql.*;
import java.text.SimpleDateFormat;

public class UsuarioDAO {

    public PerfilCompleto buscarUsuarioReal(String criterio) {
        String sql = "SELECT u.id_usuario, u.username, u.correo, u.contrasena, u.fecha_registro, " +
                     "       r.nombre_rol, COALESCE(p.nivel_cuenta, 1) AS nivel_cuenta, " +
                     "       COALESCE(b.cantidad_monedas, 0) AS cantidad_monedas " +
                     "FROM USUARIO u " +
                     "JOIN ROL r ON u.id_rol = r.id_rol " +
                     "LEFT JOIN PERFIL p ON u.id_usuario = p.id_usuario " +
                     "LEFT JOIN BILLETERA b ON u.id_usuario = b.id_usuario " +
                     "WHERE u.username LIKE ? OR u.correo LIKE ? OR u.id_usuario = ?";

        SimpleDateFormat formateador = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        // 2. LLAMADA CORREGIDA USANDO ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String paramLike = "%" + criterio + "%";
            ps.setString(1, paramLike);
            ps.setString(2, paramLike);
            
            int idBuscado = -1;
            try {
                String soloNumeros = criterio.replaceAll("[^0-9]", "");
                if (!soloNumeros.isEmpty()) idBuscado = Integer.parseInt(soloNumeros);
            } catch (NumberFormatException e) {}
            ps.setInt(3, idBuscado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp ts = rs.getTimestamp("fecha_registro");
                    String fechaFormateada = (ts != null) ? formateador.format(ts) : "----/--/--";

                    return new PerfilCompleto(
                        rs.getInt("id_usuario"),
                        rs.getString("username"),
                        rs.getString("correo"),
                        rs.getString("contrasena"),
                        fechaFormateada,
                        rs.getString("nombre_rol"),
                        rs.getInt("nivel_cuenta"),
                        rs.getInt("cantidad_monedas")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("[ERROR CRUD] Fallo al localizar registro: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizarCredenciales(int idUsuario, String nuevoEmail, String nuevaContrasena) {
        String sql = "UPDATE USUARIO SET correo = ?, contrasena = ? WHERE id_usuario = ?";
        // 3. LLAMADA CORREGIDA USANDO ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nuevoEmail);
            ps.setString(2, nuevaContrasena);
            ps.setInt(3, idUsuario);
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR CRUD] Fallo al actualizar credenciales: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarUsuario(int idUsuario) {
        String sql = "DELETE FROM USUARIO WHERE id_usuario = ?";
        // 4. LLAMADA CORREGIDA USANDO ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[ERROR CRUD] Fallo al eliminar registro maestro: " + e.getMessage());
            return false;
        }
    }
}