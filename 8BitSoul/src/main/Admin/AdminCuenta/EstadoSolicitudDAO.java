package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL; // Importamos tu clase de conexión real
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EstadoSolicitudDAO {

    /**
     * SELECT GLOBAL: Obtiene los estados válidos reales de tu BD para rellenar el ComboBox.
     * Lee directamente la tabla ESTADO_SOLICITUD mapeando id y nombre.
     */
    public static ArrayList<Object[]> obtenerEstados() {
        ArrayList<Object[]> lista = new ArrayList<>();
        String sql = "SELECT id_estado_solicitud, nombre_estado FROM ESTADO_SOLICITUD";

        // Corregido: Ahora apunta a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                Object[] fila = new Object[2];
                fila[0] = rs.getInt("id_estado_solicitud");
                fila[1] = rs.getString("nombre_estado");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error en obtenerEstados (Catálogo): " + e.getMessage());
        }
        return lista;
    }

    /**
     * SELECT BANDEJA: Carga los datos básicos de los tickets dinámicamente para la JTable.
     * Hace un INNER JOIN para obtener el nombre real del estado en lugar de solo el número.
     */
    public static ArrayList<Object[]> obtenerTicketsBandeja() {
        ArrayList<Object[]> lista = new ArrayList<>();
        String sql = "SELECT s.id_solicitud, s.id_usuario, s.titulo_consulta, e.nombre_estado " +
                     "FROM SOLICITUD_AYUDA s " +
                     "INNER JOIN ESTADO_SOLICITUD e ON s.id_estado_solicitud = e.id_estado_solicitud " +
                     "ORDER BY s.id_solicitud DESC";

        // Corregido: Ahora apunta a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                Object[] fila = new Object[4];
                fila[0] = "#TK-" + rs.getInt("id_solicitud");
                
                // Conversión explícita para el String.format seguro que implementamos en el paso anterior
                int idUsuarioOpt = rs.getInt("id_usuario");
                fila[1] = "#BSL-" + String.format("%04d", idUsuarioOpt);
                
                fila[2] = rs.getString("titulo_consulta");
                fila[3] = rs.getString("nombre_estado");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error en obtenerTicketsBandeja: " + e.getMessage());
        }
        return lista;
    }

    /**
     * SELECT DETALLE: Devuelve el texto completo del reporte y la respuesta actual del Administrador.
     * Se sincroniza limpiamente usando el ID numérico de la fila seleccionada.
     */
    public static Object[] obtenerDetalleTicket(int idSolicitud) {
        String sql = "SELECT mensaje, dictamen_administrador FROM SOLICITUD_AYUDA WHERE id_solicitud = ?";
        
        // Corregido: Ahora apunta a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null) {

            if (ps == null) return null;
            ps.setInt(1, idSolicitud);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String msg = rs.getString("mensaje");
                    String dict = rs.getString("dictamen_administrador");
                    return new Object[]{
                        (msg != null) ? msg : "",
                        (dict != null) ? dict : ""
                    };
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al obtener detalle del ticket: " + e.getMessage());
        }
        return null;
    }

    /**
     * UPDATE: Guarda la contestación del administrador y cambia el ID de estado del ticket.
     * Primero busca el ID numérico del estado basándose en la selección del JComboBox de la interfaz.
     */
    public static boolean responderYActualizarTicket(int idSolicitud, String nombreEstadoNuevo, String contestacion) {
        String sqlIdEstado = "SELECT id_estado_solicitud FROM ESTADO_SOLICITUD WHERE nombre_estado = ?";
        String sqlUpdate = "UPDATE SOLICITUD_AYUDA SET id_estado_solicitud = ?, dictamen_administrador = ? WHERE id_solicitud = ?";

        // Corregido: Ahora apunta a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) return false;

            int idEstado = -1;
            try (PreparedStatement ps1 = con.prepareStatement(sqlIdEstado)) {
                ps1.setString(1, nombreEstadoNuevo);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) idEstado = rs.getInt("id_estado_solicitud");
                }
            }

            if (idEstado == -1) {
                System.err.println("[DAO ERROR] No se encontró el ID del estado para el nombre: " + nombreEstadoNuevo);
                return false;
            }

            try (PreparedStatement ps2 = con.prepareStatement(sqlUpdate)) {
                ps2.setInt(1, idEstado);
                ps2.setString(2, contestacion);
                ps2.setInt(3, idSolicitud);
                return ps2.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al actualizar estado e informe del ticket: " + e.getMessage());
            return false;
        }
    }
}