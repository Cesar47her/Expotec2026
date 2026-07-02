package main.Admin.Configuraciones;

import main.Conexion.ConexionSQL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;

public class ConfiguracionDAO {
    private final ConexionSQL conexionSQL;

    public ConfiguracionDAO() {
        this.conexionSQL = new ConexionSQL();
    }

    public ConfiguracionUsuario obtenerPorUsuario(int idUsuario) {
        String sql = "SELECT id_usuario, volumen_audio, tamano_botones, mapeo_controles " +
                     "FROM CONFIGURACION WHERE id_usuario = " + idUsuario;
        
        try (ResultSet rs = conexionSQL.consultarRegistros(sql)) {
            if (rs != null && rs.next()) {
                System.out.println("[DAO] Registro encontrado para ID: " + idUsuario);
                return new ConfiguracionUsuario(
                    rs.getInt("id_usuario"),
                    rs.getDouble("volumen_audio"),
                    rs.getString("tamano_botones"),
                    rs.getString("mapeo_controles")
                );
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR] Fallo al consultar configuración: " + e.getMessage());
        }
        
        System.out.println("[DAO] No se encontró configuración en BD.");
        return null; // CORRECCIÓN: Devolver null permite al controlador saber que debe crear una por defecto con el ID correcto
    }

    public boolean guardarConfiguracion(ConfiguracionUsuario config) {
        String volumenStr = String.format(Locale.US, "%.2f", config.getVolumenAudio());
        
        // Nota: Asegúrate de que tu base de datos sea MySQL/MariaDB para soportar "ON DUPLICATE KEY UPDATE"
        String sql = String.format(Locale.US,
            "INSERT INTO CONFIGURACION (id_usuario, volumen_audio, tamano_botones, mapeo_controles) " +
            "VALUES (%d, %s, '%s', '%s') " +
            "ON DUPLICATE KEY UPDATE volumen_audio = %s, tamano_botones = '%s', mapeo_controles = '%s'",
            config.getIdUsuario(), volumenStr, config.getTamanoBotones(), config.getMapeoControles(),
            volumenStr, config.getTamanoBotones(), config.getMapeoControles()
        );

        try {
            int resultado = conexionSQL.ejecutarsentenciaSQL(sql);
            System.out.println("[DAO SQL] Executed Query: " + sql);
            return resultado >= 0;
        } catch (Exception e) {
            System.err.println("[DAO CRITICAL] Error de ejecución SQL: " + e.getMessage());
            return false;
        }
    }
}