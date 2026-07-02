package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BilleteraDAO {

    /**
     * CORREGIDO: Ahora lee desde HISTORIAL_COMPRA para listar de forma cronológica 
     * cada ingreso o egreso exacto que se ha ordenado desde el panel.
     */
    public List<Object[]> obtenerLibroContable() {
        List<Object[]> lista = new ArrayList<>();
        
        // Consultamos el historial de transacciones ordenando por las más recientes primero
        String sql = "SELECT h.id_compra, u.username, h.monto_core, h.item_adquirido " +
                     "FROM HISTORIAL_COMPRA h " +
                     "INNER JOIN USUARIO u ON h.id_usuario = u.id_usuario " +
                     "ORDER BY h.id_compra DESC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                String tipoOperacion = rs.getString("item_adquirido");
                double monto = rs.getDouble("monto_core");
                
                // Si la operación fue un ajuste manual de crédito, le ponemos el signo +
                String prefijo = tipoOperacion.contains("INYECTAR") ? "+" : "";
                
                lista.add(new Object[]{
                    "WLT-" + String.format("%03d", rs.getInt("id_compra")),
                    rs.getString("username"),
                    prefijo + (int)monto + " B$", // Muestra limpiamente el monto que ingresó
                    "0 pts"
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al cargar Libro Contable: " + e.getMessage());
        }
        return lista;
    }

    /**
     * CORREGIDO: 
     * 1. Suma/Resta directamente sobre la fila ÚNICA del usuario en la tabla BILLETERA.
     * 2. Inserta una fila en HISTORIAL_COMPRA para dejar la auditoría visual de cuánto ingresó.
     */
    public boolean procesarAjusteFinanciero(String username, String tipoAjuste, int monto) {
        int idUsuario = -1;
        String sqlBuscar = "SELECT id_usuario FROM USUARIO WHERE username = ?";
        
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sqlBuscar) : null) {
            
            if (ps == null) return false;
            ps.setString(1, username);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    idUsuario = rs.getInt("id_usuario");
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al buscar usuario: " + e.getMessage());
            return false;
        }

        if (idUsuario == -1) {
            System.err.println("[DAO ERROR] El usuario '" + username + "' no existe.");
            return false;
        }

        // Asegurar que el usuario tenga un único registro inicial en BILLETERA (Garantía)
        String sqlGarantia = "INSERT IGNORE INTO BILLETERA (id_usuario, cantidad_monedas) VALUES (?, 0)";
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sqlGarantia) : null) {
            if (ps != null) {
                ps.setInt(1, idUsuario);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error en insercion de garantia: " + e.getMessage());
        }

        // Evaluar la operación del ComboBox
        boolean esInyeccion = tipoAjuste.toUpperCase().contains("INYECTAR") || tipoAjuste.contains("➕");
        
        // 1. Query para actualizar el saldo total acumulado del usuario
        String sqlUpdateMaster = esInyeccion 
            ? "UPDATE BILLETERA SET cantidad_monedas = cantidad_monedas + ? WHERE id_usuario = ?"
            : "UPDATE BILLETERA SET cantidad_monedas = GREATEST(0, cantidad_monedas - ?) WHERE id_usuario = ?";
            
        // 2. Query para guardar la auditoría del ingreso en el historial
        String sqlInsertHistorial = "INSERT INTO HISTORIAL_COMPRA (id_transaccion, id_usuario, item_adquirido, monto_core, estado_sql) VALUES (?, ?, ?, ?, 'SUCCESS')";

        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) return false;
            con.setAutoCommit(false); // Iniciamos transacción atómica SQL

            // Ejecución 1: Actualizar Billetera Maestra (Lo que verá el perfil del usuario)
            try (PreparedStatement psUpdate = con.prepareStatement(sqlUpdateMaster)) {
                psUpdate.setInt(1, monto);
                psUpdate.setInt(2, idUsuario);
                psUpdate.executeUpdate();
            }

            // Ejecución 2: Insertar fila de auditoría (Lo que verá la JTable de la derecha)
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsertHistorial)) {
                String txId = "#ADM-" + (System.currentTimeMillis() % 100000); // ID de transacción único dinámico
                String descripcionHistorial = esInyeccion ? "INYECTAR FONDOS (ADMIN)" : "DEBITAR AJUSTE (ADMIN)";
                
                psInsert.setString(1, txId);
                psInsert.setInt(2, idUsuario);
                psInsert.setString(3, descripcionHistorial);
                psInsert.setDouble(4, esInyeccion ? monto : -monto); // Guardamos con signo para claridad del ledger
                psInsert.executeUpdate();
            }

            con.commit(); // Confirmar operación completa en la BD
            return true;
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error crítico en la transacción financiera: " + e.getMessage());
            return false;
        }
    }
}