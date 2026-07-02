package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import main.Conexion.ConexionSQL; // Importación limpia de la clase específica

public class HistorialDAO {

    // Obtener todo el historial sin filtros
    public List<CompraData> obtenerHistorialCompleto() {
        return obtenerHistorialFiltrado("");
    }

    // Obtener historial aplicando filtro dinámico por ID de transacción, usuario o ítem
    public List<CompraData> obtenerHistorialFiltrado(String criterio) {
        List<CompraData> lista = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT id_transaccion, id_usuario, item_adquirido, monto_core, fecha_transaccion, estado_sql FROM historial_compra");

        boolean tieneFiltro = criterio != null && !criterio.trim().isEmpty();
        if (tieneFiltro) {
            sql.append(" WHERE id_transaccion LIKE ? OR id_usuario LIKE ? OR item_adquirido LIKE ?");
        }
        sql.append(" ORDER BY fecha_transaccion DESC");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm a");

        // Al usar try-with-resources abriendo la conexión aquí, Java se encarga de cerrarla 
        // automáticamente al terminar, pase lo que pase (incluso si ocurre una excepción).
        try (Connection con = ConexionSQL.obtenerConexion()) {
            
            if (con == null) {
                throw new SQLException("La conexión devuelta por ConexionSQL es nula.");
            }

            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                if (tieneFiltro) {
                    String param = "%" + criterio.trim() + "%";
                    ps.setString(1, param);
                    ps.setString(2, param);
                    ps.setString(3, param);
                }

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String fechaFmt = "CULT_DATE";
                        if (rs.getTimestamp("fecha_transaccion") != null) {
                            fechaFmt = sdf.format(rs.getTimestamp("fecha_transaccion"));
                        }

                        lista.add(new CompraData(
                                rs.getString("id_transaccion"),
                                rs.getString("id_usuario"),
                                rs.getString("item_adquirido"),
                                rs.getDouble("monto_core"),
                                fechaFmt,
                                rs.getString("estado_sql")
                        ));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al auditar tabla historial_compra: " + e.getMessage());
        }
        
        return lista;
    }
}