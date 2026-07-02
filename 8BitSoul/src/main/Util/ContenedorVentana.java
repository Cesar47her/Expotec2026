package main.Util;

import javax.swing.JFrame;
import javax.swing.ImageIcon;
import java.awt.Image;

public class ContenedorVentana {
    
    // Definimos el nombre y la ruta del ícono de forma global
    public static final String NOMBRE_APP = "8 Bits Soul";
    public static final String RUTA_ICONO = "src/imagenes/icono.png";

    // Este método aplicará el formato a cualquier ventana (JFrame) que le pases
    public static void pf_configurarVentana(JFrame ventana) {
        ventana.setTitle(NOMBRE_APP);
        try {
            Image icono = new ImageIcon(RUTA_ICONO).getImage();
            ventana.setIconImage(icono);
        } catch (Exception e) {
            System.err.println("[GUI ERROR] No se pudo cargar el ícono en la ventana: " + e.getMessage());
        }
    }
}