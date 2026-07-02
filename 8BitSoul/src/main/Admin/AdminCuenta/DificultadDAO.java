package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DificultadDAO {

    /**
     * Obtiene todas las configuraciones de la matriz de escalado matemático.
     * Ideal para renderizar de forma directa en un componente JTable.
     */
    public static List<Object[]> obtenerDificultades() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_dificultad, mult_danio, bonus_xp, drop_rate FROM CONFIG_DIFICULTAD";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("id_dificultad"),
                    "x " + String.format("%.2f", rs.getDouble("mult_danio")),
                    "+ " + rs.getInt("bonus_xp") + "%",
                    String.format("%.1f", rs.getDouble("drop_rate") * 100) + "%"
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al leer matriz de dificultades: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Guarda o actualiza los coeficientes matemáticos de un modo de juego (Operación UPSERT).
     */
    public static boolean guardarOActualizarDificultad(String id, double mult, int xp, double drop, String entrada) {
        String sql = "INSERT INTO CONFIG_DIFICULTAD (id_dificultad, mult_danio, bonus_xp, drop_rate, modificador_entrada) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE mult_danio = ?, bonus_xp = ?, drop_rate = ?, modificador_entrada = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;

            String idLimpio = id.toUpperCase().trim();

            // Parámetros de Inserción (INSERT)
            ps.setString(1, idLimpio);
            ps.setDouble(2, mult);
            ps.setInt(3, xp);
            ps.setDouble(4, drop);
            ps.setString(5, entrada);

            // Parámetros de Actualización si la PK ya existe (UPDATE)
            ps.setDouble(6, mult);
            ps.setInt(7, xp);
            ps.setDouble(8, drop);
            ps.setString(9, entrada);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] No se pudo inyectar o actualizar dificultad: " + e.getMessage());
            return false;
        }
    }

    /**
     * Purga físicamente un algoritmo de dificultad de la BD mediante su ID.
     */
    public static boolean eliminarDificultad(String idDificultad) {
        String sql = "DELETE FROM CONFIG_DIFICULTAD WHERE id_dificultad = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            
            ps.setString(1, idDificultad.trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al ejecutar DELETE en dificultad: " + e.getMessage());
            return false;
        }
    }
}