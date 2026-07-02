package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import main.Conexion.ConexionSQL; // Importación exacta de tu clase de conexión

public class SoporteDAO {

    /**
     * Obtiene todas las solicitudes de ayuda realizando un JOIN con el usuario y el estado.
     * Utiliza el método estático obtenerConexion() de tu clase ConexionSQL.
     */
    public List<TicketData> obtenerTodasLasSolicitudes() {
        List<TicketData> lista = new ArrayList<>();
        String sql = "SELECT s.id_solicitud, s.id_usuario, u.username, s.id_estado_solicitud, "
                   + "e.nombre_estado, s.titulo_consulta, s.mensaje, s.dictamen_administrador "
                   + "FROM SOLICITUD_AYUDA s "
                   + "INNER JOIN USUARIO u ON s.id_usuario = u.id_usuario "
                   + "INNER JOIN ESTADO_SOLICITUD e ON s.id_estado_solicitud = e.id_estado_solicitud "
                   + "ORDER BY s.id_solicitud DESC";

        // Obtenemos la instancia de conexión compartida de tu clase
        Connection con = ConexionSQL.obtenerConexion();
        
        if (con == null) {
            System.err.println("[DAO ERROR] No se pudo obtener la conexión a BitSoul.");
            return lista;
        }

        // Usamos try-with-resources solo para el statement y el resultset
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new TicketData(
                    rs.getInt("id_solicitud"),
                    rs.getInt("id_usuario"),
                    rs.getString("username"),
                    rs.getInt("id_estado_solicitud"),
                    rs.getString("nombre_estado"),
                    rs.getString("titulo_consulta"),
                    rs.getString("mensaje"),
                    rs.getString("dictamen_administrador")
                ));
            }
        } catch (Exception e) {
            System.err.println("[DAO ERROR] Error en obtenerTodasLasSolicitudes: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Actualiza el dictamen del administrador y el id del estado del ticket.
     * Mantiene los parámetros limpios y protegidos.
     */
    public boolean actualizarDictamenYEstado(int idSolicitud, int idEstado, String dictamen) {
        String sql = "UPDATE SOLICITUD_AYUDA SET id_estado_solicitud = ?, dictamen_administrador = ? "
                   + "WHERE id_solicitud = ?";

        Connection con = ConexionSQL.obtenerConexion();
        
        if (con == null) {
            System.err.println("[DAO ERROR] No se pudo obtener la conexión para actualizar.");
            return false;
        }

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstado);
            ps.setString(2, dictamen);
            ps.setInt(3, idSolicitud);

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            System.err.println("[DAO ERROR] Error en actualizarDictamenYEstado: " + e.getMessage());
            return false;
        }
    }
}