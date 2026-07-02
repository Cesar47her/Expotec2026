package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoItemDAO {

    /**
     * Consulta y extrae todas las categorías base de la tabla TIPO_ITEM.
     * Mapeado correctamente a la estructura física: id_tipo_item y nombre_tipo.
     */
    public static List<Object[]> obtenerTipos() {
        List<Object[]> lista = new ArrayList<>();
        // CORRECCIÓN: Nombres de columnas reales según tu script SQL
        String sql = "SELECT id_tipo_item, nombre_tipo FROM TIPO_ITEM ORDER BY id_tipo_item ASC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    // Formateador estético para la columna ID de tu JTable (#TYP-01, #TYP-02...)
                    "#TYP-" + String.format("%02d", rs.getInt("id_tipo_item")),
                    rs.getString("nombre_tipo"),
                    "CATEGORÍA ACTIVA" // Marcador de posición para rellenar la tercera columna de tu tabla visual si lo requieres
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al mapear la tabla TIPO_ITEM: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Inyecta una nueva categoría taxonómica en la tabla TIPO_ITEM (INSERT).
     * Mapeado correctamente a 'nombre_tipo'.
     */
    public static boolean insertarTipo(String nombreTipo) {
        // CORRECCIÓN: Ajustado a las columnas reales del script de inserción
        String sql = "INSERT INTO TIPO_ITEM (nombre_tipo) VALUES (?)";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setString(1, nombreTipo.toUpperCase().trim());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al registrar categoría estructural: " + e.getMessage());
            return false;
        }
    }

    /**
     * Da de baja físicamente un tipo de ítem mediante su PK (DELETE).
     * Mapeado correctamente a 'id_tipo_item'.
     */
    public static boolean eliminarTipo(int idTipoItem) {
        // CORRECCIÓN: Ajustado al identificador primario real 'id_tipo_item'
        String sql = "DELETE FROM TIPO_ITEM WHERE id_tipo_item = ?";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return false;
            ps.setInt(1, idTipoItem);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Violación de integridad referencial. El tipo posee ítems asociados en la tienda: " + e.getMessage());
            return false;
        }
    }
}