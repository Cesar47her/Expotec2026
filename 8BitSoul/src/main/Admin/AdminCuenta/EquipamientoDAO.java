package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EquipamientoDAO {

    /**
     * Recupera el listado unificado de los ítems en el inventario aplicando DISTINCT 
     * para mitigar duplicados estéticos por desajustes en las ranuras de equipamiento.
     */
    public static List<Object[]> obtenerHistorialEquipamiento() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT DISTINCT u.username, i.id_item, i.nombre_item, " +
                     "IF(ea.id_usuario IS NOT NULL AND (ea.id_item_personaje_equipado = i.id_item OR ea.id_item_arma_equipado = i.id_item), 'EQUIPADO', 'EN MOCHILA') AS estado " +
                     "FROM INVENTARIO inv " +
                     "INNER JOIN USUARIO u ON inv.id_usuario = u.id_usuario " +
                     "INNER JOIN ITEM_TIENDA i ON inv.id_item = i.id_item " +
                     "LEFT JOIN EQUIPAMIENTO_ACTUAL ea ON u.id_usuario = ea.id_usuario " +
                     "ORDER BY u.username ASC, i.id_item ASC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getString("username"),
                    "ITM-" + String.format("%03d", rs.getInt("id_item")),
                    rs.getString("nombre_item"),
                    rs.getString("estado")
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al cargar auditoría de equipamiento: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Vincula un ítem al inventario de un usuario y gestiona de forma segura el equipamiento
     * en ranuras limpiando registros previos para evitar duplicaciones infinitas de IDs.
     */
    public static boolean registrarAsignacion(String username, int idItem, String estadoInicial, String slot) {
        int idUsuario = -1;
        String sqlBuscar = "SELECT id_usuario FROM USUARIO WHERE username = ?";
        
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sqlBuscar)) {
            
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) idUsuario = rs.getInt("id_usuario");
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al rastrear usuario: " + e.getMessage());
            return false;
        }

        if (idUsuario == -1) return false;

        String sqlInventario = "INSERT INTO INVENTARIO (id_usuario, id_item) VALUES (?, ?)";
        
        // CORRECCIÓN DE LOGICA: Verificamos si ya existe el registro del usuario en la ranura.
        String sqlCheckEquipado = "SELECT id_usuario FROM EQUIPAMIENTO_ACTUAL WHERE id_usuario = ?";
        
        String sqlInsertEquipado = slot.contains("ARMA")
                ? "INSERT INTO EQUIPAMIENTO_ACTUAL (id_usuario, id_item_arma_equipado) VALUES (?, ?)"
                : "INSERT INTO EQUIPAMIENTO_ACTUAL (id_usuario, id_item_personaje_equipado) VALUES (?, ?)";
                
        String sqlUpdateEquipado = slot.contains("ARMA")
                ? "UPDATE EQUIPAMIENTO_ACTUAL SET id_item_arma_equipado = ? WHERE id_usuario = ?"
                : "UPDATE EQUIPAMIENTO_ACTUAL SET id_item_personaje_equipado = ? WHERE id_usuario = ?";

        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) return false;
            con.setAutoCommit(false); // Transacción atómica

            // 1. Agregar el objeto a la mochila global (Inventario)
            try (PreparedStatement psInv = con.prepareStatement(sqlInventario)) {
                psInv.setInt(1, idUsuario);
                psInv.setInt(2, idItem);
                psInv.executeUpdate();
            }

            // 2. Si el administrador ordenó que aparezca 'EQUIPADO' de forma inmediata
            if ("EQUIPADO (ACTIVO)".equals(estadoInicial) || "EQUIPADO".equals(estadoInicial)) {
                boolean existeRegistro;
                try (PreparedStatement psCheck = con.prepareStatement(sqlCheckEquipado)) {
                    psCheck.setInt(1, idUsuario);
                    try (ResultSet rsCheck = psCheck.executeQuery()) {
                        existeRegistro = rsCheck.next();
                    }
                }

                if (existeRegistro) {
                    // El usuario ya tiene una fila de equipamiento creada, actualizamos su ranura específica
                    try (PreparedStatement psUp = con.prepareStatement(sqlUpdateEquipado)) {
                        psUp.setInt(1, idItem);
                        psUp.setInt(2, idUsuario);
                        psUp.executeUpdate();
                    }
                } else {
                    // No tiene fila registrada en la tabla, hacemos una inserción nueva limpia
                    try (PreparedStatement psIns = con.prepareStatement(sqlInsertEquipado)) {
                        psIns.setInt(1, idUsuario);
                        psIns.setInt(2, idItem);
                        psIns.executeUpdate();
                    }
                }
            }
            
            con.commit(); // Éxito en las operaciones combinadas
            return true;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Transacción fallida en inyección de ítem: " + e.getMessage());
            return false;
        }
    }

    /**
     * Rompe la relación de pertenencia eliminando el ítem del inventario del usuario y
     * desequipándolo de forma automática de su ranura si lo cargaba puesto.
     */
    public static boolean revocarEquipamiento(String username, int idItem) {
        String sqlGetId = "SELECT id_usuario FROM USUARIO WHERE username = ?";
        String sqlDelInv = "DELETE FROM INVENTARIO WHERE id_usuario = ? AND id_item = ? LIMIT 1";
        
        // Si el ítem revocado estaba equipado en alguna ranura, lo removemos (ponemos NULL)
        String sqlClearSkin = "UPDATE EQUIPAMIENTO_ACTUAL SET id_item_personaje_equipado = NULL WHERE id_usuario = ? AND id_item_personaje_equipado = ?";
        String sqlClearArma = "UPDATE EQUIPAMIENTO_ACTUAL SET id_item_arma_equipado = NULL WHERE id_usuario = ? AND id_item_arma_equipado = ?";
        
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) return false;
            con.setAutoCommit(false);
            
            int idUsuario = -1;
            try (PreparedStatement ps = con.prepareStatement(sqlGetId)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) idUsuario = rs.getInt("id_usuario");
                }
            }
            
            if (idUsuario == -1) return false;

            // 1. Quitar del inventario general
            int filasBorradas;
            try (PreparedStatement psDel = con.prepareStatement(sqlDelInv)) {
                psDel.setInt(1, idUsuario);
                psDel.setInt(2, idItem);
                filasBorradas = psDel.executeUpdate();
            }

            // 2. Limpiar ranuras de equipamiento por si el ítem estaba puesto
            try (PreparedStatement psSkin = con.prepareStatement(sqlClearSkin);
                 PreparedStatement psArma = con.prepareStatement(sqlClearArma)) {
                
                psSkin.setInt(1, idUsuario);
                psSkin.setInt(2, idItem);
                psSkin.executeUpdate();
                
                psArma.setInt(1, idUsuario);
                psArma.setInt(2, idItem);
                psArma.executeUpdate();
            }

            con.commit();
            return filasBorradas > 0;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al revocar equipamiento: " + e.getMessage());
            return false;
        }
    }
}