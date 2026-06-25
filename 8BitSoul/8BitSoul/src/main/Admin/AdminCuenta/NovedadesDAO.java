package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import main.Conexion.*; // Se agrega la importación correcta de tu puente de base de datos

public class NovedadesDAO {

    public List<NoticiaData> obtenerNoticias() {
        List<NoticiaData> lista = new ArrayList<>();
        String sql = "SELECT titulo, contenido, fecha_publicacion, color_hex FROM NOVEDAD ORDER BY fecha_publicacion DESC";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        // 1. Obtenemos la conexión fuera del try-with-resources para validar el nulo con seguridad
        Connection con = null;
        try {
            con = ConexionSQL.obtenerConexion();
            
            if (con == null) {
                throw new SQLException("La conexión devuelta por ConexionDB es nula.");
            }

            // 2. Una vez seguros de que no es nulo, preparamos y ejecutamos la consulta
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                while (rs.next()) {
                    String fechaFormateada = "FECHA_DESCONOCIDA";
                    if (rs.getTimestamp("fecha_publicacion") != null) {
                        fechaFormateada = sdf.format(rs.getTimestamp("fecha_publicacion"));
                    }

                    // VALIDACIÓN DE SEGURIDAD PARA EL COLOR HEXADECIMAL
                    String rawColor = rs.getString("color_hex");
                    if (rawColor == null || rawColor.trim().isEmpty()) {
                        rawColor = "#00FFFF"; // Color por defecto si está vacío en la BD
                    } else {
                        rawColor = rawColor.trim();
                        // Si olvidaste ponerle el '#' en la inyección de la BD, se lo ponemos aquí
                        if (!rawColor.startsWith("#")) {
                            rawColor = "#" + rawColor;
                        }
                    }

                    lista.add(new NoticiaData(
                        rs.getString("titulo"),
                        rs.getString("contenido"),
                        fechaFormateada,
                        rawColor
                    ));
                }
            }

        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Fallo crítico en tabla NOVEDAD: " + e.getMessage());
            
            // Respaldo visual en la UI para que no se quede congelada la pantalla neón
            if (lista.isEmpty()) {
                lista.add(new NoticiaData(
                    "📡 MODO DE RESPALDO ACTIVADO", 
                    "No se pudo conectar con MySQL. Revisa que el servicio de la BD esté corriendo.", 
                    "ERROR_CON", 
                    "#FF0055"
                ));
            }
        } finally {
            // Cerramos manualmente la conexión ya que la abrimos fuera del try-with-resources principal
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException ex) {
                    System.err.println("[DAO ERROR]: No se pudo cerrar la conexión: " + ex.getMessage());
                }
            }
        }
        return lista;
    }
}