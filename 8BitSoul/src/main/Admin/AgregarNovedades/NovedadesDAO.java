package main.Admin.AgregarNovedades;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Importamos tu clase de conexión correcta
import main.Conexion.ConexionSQL;

public class NovedadesDAO {

    // Método para listar las noticias
    public List<NoticiaData> listarNoticias() {
        List<NoticiaData> lista = new ArrayList<>();
        String sql = "SELECT * FROM NOVEDAD ORDER BY fecha_publicacion DESC";

        // Cambiado a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                NoticiaData noticia = new NoticiaData();
                noticia.setIdNoticia(rs.getInt("id_noticia"));
                noticia.setIdAdministrador(rs.getInt("id_administrador"));
                noticia.setTitulo(rs.getString("titulo"));
                noticia.setContenido(rs.getString("contenido"));
                noticia.setFechaPublicacion(rs.getTimestamp("fecha_publicacion"));
                noticia.setColorHex(rs.getString("color_hex"));
                lista.add(noticia);
            }
        } catch (SQLException e) {
            System.err.println("[DAO LISTAR ERROR]: " + e.getMessage());
        }
        return lista;
    }

    // Método para insertar una nueva noticia
    public boolean insertarNoticia(NoticiaData noticia) {
        String sql = "INSERT INTO NOVEDAD (id_administrador, titulo, contenido, color_hex) VALUES (?, ?, ?, ?)";
        
        // Cambiado a ConexionSQL.obtenerConexion()
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, 1); // ID del administrador base por defecto
            ps.setString(2, noticia.getTitulo());
            ps.setString(3, noticia.getContenido());
            ps.setString(4, noticia.getColorHex());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[DAO INSERTAR ERROR]: " + e.getMessage());
            return false;
        }
    }
}