package main.Admin.AdminCuenta;

import main.Conexion.ConexionSQL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProgresoDAO {

    /**
     * Obtiene el listado unificado de los niveles, XP y rendimiento de los usuarios de la BD.
     * Corregido con LEFT JOIN hacia PERFIL para mitigar la ausencia de filas iniciales.
     */
    public static List<Object[]> obtenerLeaderboard() {
        List<Object[]> lista = new ArrayList<>();
        String sql = "SELECT u.username, COALESCE(p.nivel_cuenta, 1) AS nivel_cuenta, " +
                     "COALESCE(p.experiencia, 0) AS experiencia, " +
                     "COALESCE(SUM(pj.amenazas_derrotadas), 0) AS amenazas_totales " +
                     "FROM USUARIO u " +
                     "LEFT JOIN PERFIL p ON u.id_usuario = p.id_usuario " +
                     "LEFT JOIN PROGRESO_JUGADOR pj ON u.id_usuario = pj.id_usuario " +
                     "GROUP BY u.id_usuario, u.username, p.nivel_cuenta, p.experiencia " +
                     "ORDER BY nivel_cuenta DESC, experiencia DESC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = (con != null) ? con.prepareStatement(sql) : null;
             ResultSet rs = (ps != null) ? ps.executeQuery() : null) {

            if (rs == null) return lista;

            while (rs.next()) {
                lista.add(new Object[]{
                    // Cambiado a String plano para que coincida con la captura de eventos JTable
                    rs.getString("username"),
                    "Nivel " + rs.getInt("nivel_cuenta"),
                    String.format("%,d XP", rs.getInt("experiencia")),
                    rs.getInt("amenazas_totales") + " Threats"
                });
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Error al cargar monitor de progreso: " + e.getMessage());
        }
        return lista;
    }

    /**
     * Modifica los parámetros de nivel, experiencia y rendimiento de forma transaccional y segura.
     * Incorpora soporte de inyección automática si el perfil no existía previamente.
     */
    public static boolean actualizarProgreso(String username, int nuevoNivel, int nuevaXp, int rendimiento, String accionGlobal) {
        String sqlBuscar = "SELECT id_usuario FROM USUARIO WHERE username = ?";
        int idUsuario = -1;

        // 1. Obtener el ID del usuario de forma aislada
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

        if (idUsuario == -1) return false;

        // 2. Procesar modificadores lógicos según la acción del panel administrativo
        if ("🔄 PRESTIGIO / REINICIAR EXPERIENCIA".equals(accionGlobal)) {
            nuevoNivel = 1;
            nuevaXp = 0;
            rendimiento = 0;
        } else if ("⚠️ PENALIZACIÓN DE NIVEL".equals(accionGlobal)) {
            nuevoNivel = Math.max(1, nuevoNivel - 5);
            nuevaXp = (int) (nuevaXp * 0.5); 
        }

        // Sentencias preparadas robustas
        String sqlVerificarPerfil = "SELECT id_perfil FROM PERFIL WHERE id_usuario = ?";
        String sqlInsertPerfil = "INSERT INTO PERFIL (id_usuario, nivel_cuenta, experiencia) VALUES (?, ?, ?)";
        String sqlUpdatePerfil = "UPDATE PERFIL SET nivel_cuenta = ?, experiencia = ? WHERE id_usuario = ?";
        
        String sqlVerificarProgreso = "SELECT id_progreso FROM PROGRESO_JUGADOR WHERE id_usuario = ? AND id_nivel = 1";
        String sqlInsertProgreso = "INSERT INTO PROGRESO_JUGADOR (id_usuario, id_nivel, id_dificultad, amenazas_derrotadas) VALUES (?, 1, 2, ?)";
        String sqlUpdateProgreso = "UPDATE PROGRESO_JUGADOR SET amenazas_derrotadas = ? WHERE id_usuario = ? AND id_nivel = 1";

        // 3. Ejecución de la transacción unificada
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) return false;
            
            try {
                con.setAutoCommit(false); // Activamos bloque transaccional ACID

                // Ejecución I: Gestionar la existencia de la tabla PERFIL
                boolean tienePerfil = false;
                try (PreparedStatement psCheckP = con.prepareStatement(sqlVerificarPerfil)) {
                    psCheckP.setInt(1, idUsuario);
                    try (ResultSet rsP = psCheckP.executeQuery()) {
                        if (rsP.next()) tienePerfil = true;
                    }
                }

                if (tienePerfil) {
                    try (PreparedStatement psUpP = con.prepareStatement(sqlUpdatePerfil)) {
                        psUpP.setInt(1, nuevoNivel);
                        psUpP.setInt(2, nuevaXp);
                        psUpP.setInt(3, idUsuario);
                        psUpP.executeUpdate();
                    }
                } else {
                    // CORRECCIÓN AQUÍ: Cambiado el punto '.' por espacio para declaración válida
                    try (PreparedStatement psInP = con.prepareStatement(sqlInsertPerfil)) {
                        psInP.setInt(1, idUsuario);
                        psInP.setInt(2, nuevoNivel);
                        psInP.setInt(3, nuevaXp);
                        psInP.executeUpdate();
                    }
                }

                // Ejecución II: Gestionar amenazas derrotadas en la tabla PROGRESO_JUGADOR
                boolean tieneProgreso = false;
                try (PreparedStatement psCheckPr = con.prepareStatement(sqlVerificarProgreso)) {
                    psCheckPr.setInt(1, idUsuario);
                    try (ResultSet rsPr = psCheckPr.executeQuery()) {
                        if (rsPr.next()) tieneProgreso = true;
                    }
                }

                if (tieneProgreso) {
                    try (PreparedStatement psUpPr = con.prepareStatement(sqlUpdateProgreso)) {
                        psUpPr.setInt(1, rendimiento);
                        psUpPr.setInt(2, idUsuario);
                        psUpPr.executeUpdate();
                    }
                } else {
                    try (PreparedStatement psInPr = con.prepareStatement(sqlInsertProgreso)) {
                        psInPr.setInt(1, idUsuario);
                        psInPr.setInt(2, rendimiento);
                        psInPr.executeUpdate();
                    }
                }

                con.commit(); // Consolidar cambios simultáneos en MySQL
                return true;
                
            } catch (SQLException e) {
                con.rollback(); // Deshacer todo si algo falla
                System.err.println("[TRANSACTION ROLLBACK] Error en actualización de progreso: " + e.getMessage());
                return false;
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Fallo crítico de conexión en la transacción: " + e.getMessage());
            return false;
        }
    }
}