package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RolDAO {

    /**
     * Consulta y extrae todos los roles configurados en el sistema de seguridad.
     */
    public static List<Object[]> obtenerRoles() {
        List<Object[]> lista = new ArrayList<>();
        // Ajusta los nombres de columnas según coincidan con tu tabla ROL
        String sql = "SELECT id_rol, nombre_rol, codigo_token, jerarquia FROM ROL ORDER BY jerarquia DESC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    "#ROL-" + String.format("%03d", rs.getInt("id_rol")),
                    rs.getString("nombre_rol"),
                    rs.getString("codigo_token"),
                    "Nivel " + rs.getInt("jerarquia")
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al auditar la tabla ROL: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inyecta una nueva política de privilegios en el sistema (INSERT).
     */
    public static boolean insertarRol(String nombre, String token, int jerarquia) {
        String sql = "INSERT INTO ROL (nombre_rol, codigo_token, jerarquia) VALUES (?, ?, ?)";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setString(1, nombre.toUpperCase().trim());
            ps.setString(2, token.toUpperCase().trim());
            ps.setInt(3, jerarquia);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al registrar política de rol: " + e.getMessage());
            return false;
        }
    }

    /**
     * Purga físicamente un rol mediante su identificador numérico (DELETE).
     */
    public static boolean eliminarRol(int idRol) {
        String sql = "DELETE FROM ROL WHERE id_rol = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, idRol);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] No se pudo purgar el rol debido a llaves foráneas activas: " + e.getMessage());
            return false;
        }
    }
}