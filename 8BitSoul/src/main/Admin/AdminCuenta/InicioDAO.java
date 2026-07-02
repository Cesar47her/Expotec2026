package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import main.Conexion.ConexionSQL; // Importación unificada

public class InicioDAO {

    public static String obtenerTotalUsuarios() {
        return ejecutarConsultaEscalar("SELECT COUNT(*) FROM USUARIO");
    }

    public static String obtenerTotalCompras() {
        return ejecutarConsultaEscalar("SELECT COUNT(*) FROM HISTORIAL_COMPRA");
    }

    public static String obtenerTotalTickets() {
        return ejecutarConsultaEscalar("SELECT COUNT(*) FROM SOLICITUD_AYUDA");
    }

    public static String obtenerTotalInventario() {
        return ejecutarConsultaEscalar("SELECT COUNT(*) FROM INVENTARIO");
    }

    public static String obtenerTotalVentas() {
        double monto = obtenerSumaVentas("SELECT SUM(monto_core) FROM HISTORIAL_COMPRA");
        return String.format("$%,.2f", monto);
    }

    /**
     * Ejecuta las consultas de conteo conectándose a ConexionSQL.
     */
    private static String ejecutarConsultaEscalar(String sql) {
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) {
                throw new SQLException("La conexión devuelta por ConexionSQL es nula.");
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    int total = rs.getInt(1);
                    System.out.println("[SQL OK] Consulta: " + sql + " | Registro Real: " + total);
                    return String.format("%,d", total);
                }
            }
        } catch (Exception e) {
            System.err.println("[SQL ERROR] Error en la conexión/consulta de '" + sql + "': " + e.getMessage());
        }
        return "0"; 
    }

    /**
     * Calcula la sumatoria monetaria de las pasarelas financieras del software.
     */
    private static double obtenerSumaVentas(String sql) {
        try (Connection con = ConexionSQL.obtenerConexion()) {
            if (con == null) {
                throw new SQLException("La conexión devuelta por ConexionSQL es nula.");
            }
            
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {
                
                if (rs.next()) {
                    double total = rs.getDouble(1);
                    System.out.println("[SQL OK] Sumatoria Real Ventas: " + total);
                    return total;
                }
            }
        } catch (Exception e) {
            System.err.println("[SQL ERROR] No se pudo conectar/calcular la sumatoria: " + e.getMessage());
        }
        return 0.0;
    }
}