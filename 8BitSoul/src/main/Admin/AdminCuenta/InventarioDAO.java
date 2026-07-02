package main.Admin.AdminCuenta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import main.Conexion.ConexionSQL; // Importación de la conexión centralizada real

public class InventarioDAO {

    // Obtener catálogo combinando con TIPO_ITEM para el texto de la categoría
    public List<ItemData> obtenerCatalogo() {
        List<ItemData> lista = new ArrayList<>();
        String sql = "SELECT i.id_item, i.id_tipo_item, t.nombre_tipo, i.nombre_item, i.precio_monedas, i.descripcion "
                + "FROM ITEM_TIENDA i INNER JOIN TIPO_ITEM t ON i.id_tipo_item = t.id_tipo_item ORDER BY i.id_item DESC";

        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) {
                throw new SQLException("La conexión con la BD es nula.");
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql); 
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    lista.add(new ItemData(
                            rs.getInt("id_item"),
                            rs.getInt("id_tipo_item"),
                            rs.getString("nombre_tipo"),
                            rs.getString("nombre_item"),
                            rs.getInt("precio_monedas"),
                            rs.getString("descripcion")
                    ));
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al leer ITEM_TIENDA: " + e.getMessage());
        }
        return lista;
    }

    // Insertar un nuevo artículo al catálogo core
    public boolean insertarItem(String nombre, int idTipo, int precio, String desc) {
        String sql = "INSERT INTO ITEM_TIENDA (id_tipo_item, nombre_item, precio_monedas, descripcion) VALUES (?, ?, ?, ?)";
        
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) {
                throw new SQLException("La conexión con la BD es nula.");
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idTipo);
                ps.setString(2, nombre);
                ps.setInt(3, precio);
                ps.setString(4, desc);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al insertar en ITEM_TIENDA: " + e.getMessage());
            return false;
        }
    }

    // Eliminar físicamente un artículo por su PK
    public boolean eliminarItem(int idItem) {
        String sql = "DELETE FROM ITEM_TIENDA WHERE id_item = ?";
        
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) {
                throw new SQLException("La conexión con la BD es nula.");
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, idItem);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al ejecutar DELETE en ITEM_TIENDA: " + e.getMessage());
            return false;
        }
    }
}