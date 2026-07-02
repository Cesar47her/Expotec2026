package main;

import main.Login.*;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;
import main.Util.*;
import java.util.*;
import java.sql.*;
import main.PantalladeCarga.*;

public class Main {

    public static void main(String[] args) {
        try {
            // Establecer el estilo visual nativo del sistema operativo
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("No se pudo establecer el Look and Feel nativo: " + e.getMessage());
        }

        // --- SISTEMA CORE DE AUDIO: EXTRACCIÓN DE PISTAS ---
        List<String> listaCanciones = new ArrayList<>();
        String querySelect = "SELECT file_path FROM MUSICA";
        main.Conexion.ConexionSQL conexionInstancia = new main.Conexion.ConexionSQL();

        try (ResultSet rs = conexionInstancia.consultarRegistros(querySelect)) {
            // Si la tabla contiene registros, los extraemos todos
            if (rs != null) {
                while (rs.next()) {
                    listaCanciones.add(rs.getString("file_path"));
                }
            }
        } catch (Exception e) {
            System.err.println("[CORE ERROR] No se pudo leer la tabla MUSICA: " + e.getMessage());
        }

        // Si la base de datos estaba vacía, inyectamos el catálogo por defecto de respaldo
        if (listaCanciones.isEmpty()) {
            System.out.println("[DB LOG] Tabla MUSICA vacía. Inicializando pistas base de respaldo...");
            listaCanciones.add("src/Music/General/fondo1.wav");
            listaCanciones.add("src/Music/General/fondo2.wav");
            listaCanciones.add("src/Music/General/fondo3.wav");
            listaCanciones.add("src/Music/General/fondo4.wav");
            listaCanciones.add("src/Music/General/fondo5.wav");
            listaCanciones.add("src/Music/General/fondo6.wav");

            // Registrar estas 6 canciones base en la BD para que ya existan formalmente
            Connection con = main.Conexion.ConexionSQL.obtenerConexion();
            String queryInsert = "INSERT IGNORE INTO MUSICA (id_protocol, file_path, nombre_pista) VALUES (?, ?, ?)";
            try (PreparedStatement ps = con.prepareStatement(queryInsert)) {
                for (int i = 0; i < listaCanciones.size(); i++) {
                    ps.setString(1, "PROT_" + (100 + i));
                    ps.setString(2, listaCanciones.get(i));
                    ps.setString(3, "Fondo Core " + (i + 1));
                    ps.addBatch();
                }
                ps.executeBatch();
            } catch (SQLException ex) {
                System.err.println("[DB ERROR] No se pudo precargar el catálogo base en SQL: " + ex.getMessage());
            }
        }

        // --- REPRODUCCIÓN INICIAL CON VOLUMEN BASE ---
        ReproducirSonido.configurarLista(listaCanciones); 
        ReproducirSonido.asignarVolumen(0.80); // Volumen predeterminado al 80%
        ReproducirSonido.reproducirSiguiente(); 
        // ---------------------------------------------------------------

        // --- LANZAMIENTO SEGURO DEL HILO DE LA PANTALLA DE CARGA ---
        SwingUtilities.invokeLater(() -> {
            // 1. Instanciamos la ventana destino.
            MenuPrincipal menuDestino = new MenuPrincipal();

            // 2. Iniciamos la pantalla de carga pasándole el menú de destino
            GlitchLoadingScreen inicio = new GlitchLoadingScreen(menuDestino);

            // 3. PROPAGACIÓN DE IDENTIDAD: Aplicamos nombre e ícono usando la clase de utilidad
            // (Asegúrate de haber creado primero la clase ContenedorVentana en main.Util)
            main.Util.ContenedorVentana.pf_configurarVentana(inicio);
            main.Util.ContenedorVentana.pf_configurarVentana(menuDestino);

            // 4. Mostramos la pantalla de carga inicial
            inicio.setVisible(true);
        });
    }
}