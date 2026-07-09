package main.Util;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import main.Conexion.ConexionSQL; // Importamos exactamente tu clase de conexión

public class UsuarioDAO {

    /**
     * Busca los datos unificados de configuración y mapeo de controles de un usuario.
     */
    public Map<String, Object> obtenerConfiguracionImpresion(int idUsuario) {
        Map<String, Object> datos = new HashMap<>();
        String query = "SELECT u.username, r.nombre_rol, c.volumen_audio, c.mapeo_controles " +
                       "FROM USUARIO u " +
                       "INNER JOIN ROL r ON u.id_rol = r.id_rol " +
                       "LEFT JOIN CONFIGURACION c ON u.id_usuario = c.id_usuario " +
                       "WHERE u.id_usuario = ?";

        // Usamos exactamente tu método estático para pedir la conexión
        try (Connection con = ConexionSQL.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos.put("username", rs.getString("username"));
                    datos.put("nombre_rol", rs.getString("nombre_rol"));
                    
                    double volAudio = rs.getDouble("volumen_audio");
                    datos.put("volumen_porcentaje", (int) (volAudio * 100));
                    datos.put("mapeo_controles", rs.getString("mapeo_controles"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("[DAO ERROR] Error al consultar configuración: " + ex.getMessage());
        }
        return datos;
    }

    /**
     * Busca el perfil completo, nivel y la puntuación máxima histórica de un jugador.
     */
    public Map<String, Object> obtenerProgresoJugadorImpresion(int idUsuario) {
        Map<String, Object> datos = new HashMap<>();
        String query = "SELECT u.username, r.nombre_rol, u.correo, DATE(u.fecha_registro) as registro, " +
                       "p.nivel_cuenta, COALESCE(MAX(pj.puntuacion_maxima), 0) as max_score " +
                       "FROM USUARIO u " +
                       "INNER JOIN ROL r ON u.id_rol = r.id_rol " +
                       "LEFT JOIN PERFIL p ON u.id_usuario = p.id_usuario " +
                       "LEFT JOIN PROGRESO_JUGADOR pj ON u.id_usuario = pj.id_usuario " +
                       "WHERE u.id_usuario = ? " +
                       "GROUP BY u.id_usuario";

        // Usamos exactamente tu método estático para pedir la conexión
        try (Connection con = ConexionSQL.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(query)) {
            
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    datos.put("username", rs.getString("username"));
                    datos.put("nombre_rol", rs.getString("nombre_rol"));
                    datos.put("correo", rs.getString("correo"));
                    datos.put("fecha_registro", rs.getString("registro"));
                    datos.put("nivel_cuenta", rs.getInt("nivel_cuenta"));
                    datos.put("max_score", rs.getInt("max_score"));
                }
            }
        } catch (SQLException ex) {
            System.err.println("[DAO ERROR] Error al consultar progreso: " + ex.getMessage());
        }
        return datos;
    }
}