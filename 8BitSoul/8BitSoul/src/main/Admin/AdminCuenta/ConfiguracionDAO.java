package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import main.Conexion.ConexionSQL; // CORREGIDO: Importación de tu clase de conexión real

public class ConfiguracionDAO {

    // =========================================================================
    // CARGAR CONFIGURACIÓN: Lee desde MySQL y reconstruye el Mapa de la UI
    // =========================================================================
    public Map<String, String> cargarConfiguracionUsuario(int idUsuario) {
        Map<String, String> mapa = obtenerMapaPorDefecto();
        String sql = "SELECT volumen_audio, mapeo_botones, mapeo_controles FROM CONFIGURACION WHERE id_usuario = ?";

        // CORREGIDO: Enlace directo a ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // 1. Extraer y convertir volumen (De DECIMAL 0.80 a entero de JSlider 80)
                    double volDecimal = rs.getDouble("volumen_audio");
                    int volEntero = (int) (volDecimal * 100);
                    mapa.put("AUDIO_VOLUMEN", String.valueOf(volEntero));

                    // 2. Procesar mapeo_botones (Estructura: KEY:VALOR;KEY:VALOR)
                    String mapeoBotones = rs.getString("mapeo_botones");
                    deserializarEstructuraAlMapa(mapeoBotones, mapa);

                    // 3. Procesar mapeo_controles (Estados de UI y Gráficos)
                    String mapeoControles = rs.getString("mapeo_controles");
                    deserializarEstructuraAlMapa(mapeoControles, mapa);
                } else {
                    // Si el usuario es nuevo y no tiene fila, la creamos con los valores base
                    inyectarFilaConfiguracionInicial(idUsuario, mapa);
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al leer CONFIGURACION: " + e.getMessage());
        }
        return mapa;
    }

    // =========================================================================
    // GUARDAR CONFIGURACIÓN: Empaqueta el mapa de la UI y actualiza la BD
    // =========================================================================
    public void guardarConfiguracionUsuario(int idUsuario, Map<String, String> mapa) {
        String sql = "UPDATE CONFIGURACION SET volumen_audio = ?, mapeo_botones = ?, mapeo_controles = ? WHERE id_usuario = ?";

        // Convertir volumen de JSlider (0-100) a formato Decimal de BD (0.00 - 1.00)
        int volEntero = Integer.parseInt(mapa.getOrDefault("AUDIO_VOLUMEN", "80"));
        double volDecimal = volEntero / 100.0;

        // Serializar grupos de propiedades para guardarlos en strings únicos
        String mapeoBotones = "AUDIO_MUSICA:" + mapa.get("AUDIO_MUSICA") + ";" +
                             "AUDIO_EFECTOS:" + mapa.get("AUDIO_EFECTOS");

        String mapeoControles = "GRAFICOS_GLOW:" + mapa.get("GRAFICOS_GLOW") + ";" +
                               "GRAFICOS_GPU:" + mapa.get("GRAFICOS_GPU") + ";" +
                               "GRAFICOS_FPS:" + mapa.get("GRAFICOS_FPS") + ";" +
                               "UI_ALERTAS:" + mapa.get("UI_ALERTAS") + ";" +
                               "UI_COMPACTO:" + mapa.get("UI_COMPACTO") + ";" +
                               "UI_LOGS:" + mapa.get("UI_LOGS");

        // CORREGIDO: Enlace directo a ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setDouble(1, volDecimal);
            ps.setString(2, mapeoBotones);
            ps.setString(3, mapeoControles);
            ps.setInt(4, idUsuario);

            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al actualizar CONFIGURACION: " + e.getMessage());
        }
    }

    // =========================================================================
    // MÉTODOS AUXILIARES DE SOPORTE E INYECCIÓN
    // =========================================================================
    private void inyectarFilaConfiguracionInicial(int idUsuario, Map<String, String> defaults) {
        String sql = "INSERT INTO CONFIGURACION (id_usuario, volumen_audio, mapeo_botones, mapeo_controles) VALUES (?, 0.80, ?, ?)";
        String mapeoB = "AUDIO_MUSICA:true;AUDIO_EFECTOS:true";
        String mapeoC = "GRAFICOS_GLOW:true;GRAFICOS_GPU:true;GRAFICOS_FPS:60;UI_ALERTAS:true;UI_COMPACTO:false;UI_LOGS:false";

        // CORREGIDO: Enlace directo a ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            ps.setString(2, mapeoB);
            ps.setString(3, mapeoC);
            ps.executeUpdate();
            System.out.println("[CORE]: Fila de configuración inicial inyectada para el id_usuario: " + idUsuario);
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Fallo al创建 registro de configuración base: " + e.getMessage());
        }
    }

    public Map<String, String> obtenerMapaPorDefecto() {
        Map<String, String> defts = new HashMap<>();
        defts.put("AUDIO_VOLUMEN", "80");
        defts.put("AUDIO_MUSICA", "true");
        defts.put("AUDIO_EFECTOS", "true");
        defts.put("GRAFICOS_GLOW", "true");
        defts.put("GRAFICOS_GPU", "true");
        defts.put("GRAFICOS_FPS", "60");
        defts.put("UI_ALERTAS", "true");
        defts.put("UI_COMPACTO", "false");
        defts.put("UI_LOGS", "false");
        return defts;
    }

    private void deserializarEstructuraAlMapa(String cadenaData, Map<String, String> destino) {
        if (cadenaData == null || cadenaData.trim().isEmpty()) return;
        try {
            String[] tokens = cadenaData.split(";");
            for (String token : tokens) {
                String[] par = token.split(":");
                if (par.length == 2) {
                    destino.put(par[0].trim(), par[1].trim());
                }
            }
        } catch (Exception e) {
            System.err.println("[CORE ERROR]: Fallo al parsear string de configuración serializado.");
        }
    }
}