package main.Admin.AdminCuenta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import main.Conexion.ConexionSQL; // Importación de tu clase de conexión real
import main.Util.PasswordUtil;

public class PerfilDAO {

    // Estructura intermedia (Wrapper) para el JComboBox cyberpunk
    public static class ObjetoRol {
        public int idRol;
        public String nombreRol;

        public ObjetoRol(int idRol, String nombreRol) {
            this.idRol = idRol;
            this.nombreRol = nombreRol;
        }

        @Override
        public String toString() {
            return "ID: " + idRol + " | " + nombreRol;
        }
    }

    // =========================================================================
    // 1. LEER ROLES REALES (CORREGIDO: Enlace a ConexionSQL)
    // =========================================================================
    public List<ObjetoRol> obtenerRolesDisponibles() {
        List<ObjetoRol> lista = new ArrayList<>();
        String sql = "SELECT id_rol, nombre_rol FROM ROL"; 

        // CORREGIDO: Llamada cambiada a ConexionSQL
        try (Connection con = ConexionSQL.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new ObjetoRol(
                    rs.getInt("id_rol"),
                    rs.getString("nombre_rol")
                ));
            }

        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Error al mapear la tabla ROL: " + e.getMessage());
            // Datos de contingencia por seguridad
            if (lista.isEmpty()) {
                lista.add(new ObjetoRol(1, "Administrador"));
                lista.add(new ObjetoRol(2, "Usuario"));
            }
        }
        return lista;
    }

    // =========================================================================
    // 2. INSERCIÓN TRANSACCIONAL (CORREGIDO: Enlace a ConexionSQL)
    // =========================================================================
    public boolean insertarUsuarioNuevo(String username, String correo, String password, int idRol) {
        String sql = "INSERT INTO USUARIO (id_rol, username, correo, contrasena) VALUES (?, ?, ?, ?)";

        try (Connection con = ConexionSQL.obtenerConexion(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (con == null) {
                System.err.println("[DAO ERROR]: Conexión nula hacia BitSoul.");
                return false;
            }

            String hashedPassword = PasswordUtil.hashPassword(password);
            ps.setInt(1, idRol);
            ps.setString(2, username);
            ps.setString(3, correo);
            ps.setString(4, hashedPassword);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException e) {
            System.err.println("[DAO ERROR]: Fallo al ejecutar INSERT en USUARIO: " + e.getMessage());
            return false; 
        }
    }
}