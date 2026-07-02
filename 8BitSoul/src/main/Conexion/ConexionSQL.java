    package main.Conexion;

    import java.sql.*;

    public class ConexionSQL {
        private static final String URL = "jdbc:mysql://localhost:3306/BitSoul";
        private static final String USER = "root";
        private static final String PASS = "123456789";

        private static Connection con = null;

        /**
         * Método estático centralizado que solicita o reutiliza la conexión activa.
         */
        public static Connection obtenerConexion() {
            try {
                if (con == null || con.isClosed()) {
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    con = DriverManager.getConnection(URL, USER, PASS);
                    System.out.println("[DB LOG] Conexión establecida con éxito.");
                }
            } catch (ClassNotFoundException e) {
                System.err.println("[DB ERROR] Driver MySQL no localizado: " + e.getMessage());
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Error de conexión de red: " + e.getMessage());
            }
            return con;
        }

        /**
         * Ejecuta sentencias directas de tipo INSERT, UPDATE o DELETE.
         */
        public int ejecutarsentenciaSQL(String strSentenciaSQL) {
            try {
                Connection conexion = obtenerConexion();
                if (conexion != null) {
                    try (PreparedStatement pstm = conexion.prepareStatement(strSentenciaSQL)) {
                        pstm.execute();
                        return 1; 
                    }
                }
                return 0;
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Error al ejecutar sentencia write: " + e.getMessage());
                return 0; 
            }
        }

        /**
         * Retorna un juego de datos para consultas directas raw.
         */
        public ResultSet consultarRegistros(String strSentenciaSQL) {
            try {
                Connection conexion = obtenerConexion();
                if (conexion != null) {
                    PreparedStatement pstm = conexion.prepareStatement(strSentenciaSQL);
                    return pstm.executeQuery();
                }
                return null;
            } catch (SQLException e) {
                System.err.println("[DB ERROR] Error en query read: " + e.getMessage());
                return null;
            }
        }
    }