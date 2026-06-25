package main.Login;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import main.Util.*;

public class BackgroundPanelRegistrar extends JPanel {

    private Image imagenFondo;

    public BackgroundPanelRegistrar() {
        // Cargamos la imagen desde la ruta de recursos del proyecto
        // Asegúrate de guardar "fondo login.jpg" en tu carpeta de fuentes/recursos
        try {
            URL url = getClass().getResource("/imagenes/FondoLogin.png");
            if (url != null) {
                imagenFondo = new ImageIcon(url).getImage();
            } else {
                // Alternativa por si lo ejecutas directo en el directorio raíz del proyecto
                imagenFondo = new ImageIcon("fondo login.jpg").getImage();
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar la imagen de fondo: " + e.getMessage());
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int w = getWidth();
        int h = getHeight();

        // 1. Si la imagen cargó correctamente, la estiramos/escalamos para cubrir el panel
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, w, h, this);
        } else {
            // Fondo de respaldo por si falla la carga de la imagen
            g2d.setColor(EstiloDiseno.INPUT_BG);
            g2d.fillRect(0, 0, w, h);
            
            // Mensaje de advertencia visual en consola/pantalla en desarrollo
            g2d.setColor(Color.RED);
            g2d.drawString("Error: 'fondo login.jpg' no encontrado.", 20, 20);
        }

        // 2. Activamos Antialiasing por si añades más componentes vectoriales encima
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // NOTA: He removido las líneas de código antiguas (g2d.drawOval, drawLine) 
        // ya que la imagen que subiste ya trae la llave y los circuitos incorporados.
    }
}