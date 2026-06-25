package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) para la gestión y persistencia de las curvas de nivel,
 * experiencia requerida y recompensas globales del sistema BitSoul.
 */
public class NivelDAO {

    /**
     * Consulta y extrae el mapa completo de progresión y curvas de experiencia.
     * Retorna los tipos de datos nativos para facilitar operaciones lógicas y 
     * ordenamientos en la interfaz de usuario.
     * * @return Lista de arreglos de Objetos [int, int, int, String]
     */
    public static List<Object[]> obtenerNiveles() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_nivel, xp_requerida, recompensa_moneda, recompensa_cosmetico FROM CONFIG_NIVEL ORDER BY id_nivel ASC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_nivel"),               // Columna 0: (int) ID numérico puro
                    rs.getInt("xp_requerida"),           // Columna 1: (int) Experiencia pura
                    rs.getInt("recompensa_moneda"),      // Columna 2: (int) Monedas puras
                    rs.getString("recompensa_cosmetico") // Columna 3: (String) Identificador de cosmético
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al extraer mapa de niveles: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inyecta o actualiza una regla de nivel utilizando comportamiento UPSERT (ON DUPLICATE KEY UPDATE).
     * Si el 'idNivel' ya existe en el índice primario de CONFIG_NIVEL, actualiza dinámicamente 
     * sus umbrales y recompensas en vez de generar una colisión.
     * * @param idNivel   Identificador único del escalón de nivel.
     * @param xp        Cantidad de experiencia requerida para alcanzar el nivel.
     * @param moneda    Monto de Monedas Core otorgadas como recompensa.
     * @param cosmetico Identificador o nombre del cosmético desbloqueable (puede ser null).
     * @return true si la operación en la base de datos fue exitosa, false de lo contrario.
     */
    public static boolean guardarOActualizarNivel(int idNivel, int xp, int moneda, String cosmetico) {
        String sql = "INSERT INTO CONFIG_NIVEL (id_nivel, xp_requerida, recompensa_moneda, recompensa_cosmetico) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE xp_requerida = ?, recompensa_moneda = ?, recompensa_cosmetico = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            
            // Parámetros para la sección del INSERT primario
            ps.setInt(1, idNivel);
            ps.setInt(2, xp);
            ps.setInt(3, moneda);
            ps.setString(4, cosmetico);
            
            // Parámetros para la sección del UPDATE (Si ocurre colisión de Llave Primaria)
            ps.setInt(5, xp);
            ps.setInt(6, moneda);
            ps.setString(7, cosmetico);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Transacción fallida en escalón de nivel: " + e.getMessage());
            return false;
        }
    }

    /**
     * Remueve de manera permanente un escalón de nivel del motor de juego a través de su ID.
     * * @param idNivel Identificador del nivel a purgar de la base de datos.
     * @return true si se eliminó el registro exitosamente, false si no se encontró o hubo un fallo.
     */
    public static boolean eliminarNivel(int idNivel) {
        String sql = "DELETE FROM CONFIG_NIVEL WHERE id_nivel = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, idNivel);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al purgar escalón de nivel: " + e.getMessage());
            return false;
        }
    }
}