package main.Admin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TarjetasAdmin extends JPanel {

    private String titulo1;
    private String titulo2;
    private String miniTag;
    private Color colorNeon;
    private String tipoIcono; 
    private boolean mouseEncima = false;
    
    // SOLUCCIÓN AL ERROR: Color cyberpunk por defecto para el fondo de la tarjeta
    private static final Color DEFAULT_CARD_BG = new Color(12, 18, 30);

    // Constructor manteniendo tu firma exacta
    public TarjetasAdmin(String titulo1, String titulo2, String miniTag, Color colorNeon, String tipoIcono) {
        this.titulo1 = titulo1;
        this.titulo2 = titulo2;
        this.miniTag = miniTag;
        this.colorNeon = colorNeon;
        this.tipoIcono = tipoIcono.toUpperCase(); 
        
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setLayout(new BorderLayout()); 

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { mouseEncima = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { mouseEncima = false; repaint(); }
            @Override
            public void mouseClicked(MouseEvent e) {
                // Acción al hacer click (Puedes implementar una interfaz Callback aquí si lo deseas)
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // CORRECCIÓN AQUÍ: Usamos un color seguro para evitar depender estrictamente de otra clase
        g2d.setColor(DEFAULT_CARD_BG);
        g2d.fillRect(0, 0, w, h);

        // Bordes y esquinas de enfoque (Hover)
        if (mouseEncima) {
            g2d.setColor(colorNeon);
            g2d.setStroke(new BasicStroke(2.5f));
            g2d.drawRect(1, 1, w - 3, h - 3);
            g2d.fillRect(0, 0, 8, 8); 
            g2d.fillRect(w - 8, 0, 8, 8);
        } else {
            g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 120));
            g2d.setStroke(new BasicStroke(1.2f));
            g2d.drawRect(0, 0, w - 1, h - 1);
        }

        // Línea divisoria dinámica
        g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 60));
        g2d.drawLine(10, 48, w - 10, 48);

        // Dibujo de Textos de Títulos
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Monospaced", Font.BOLD, Math.max(10, w / 11)));
        FontMetrics fm = g2d.getFontMetrics();
        
        g2d.drawString(titulo1, (w - fm.stringWidth(titulo1)) / 2, 22);
        if (titulo2 != null && !titulo2.isEmpty()) {
            g2d.drawString(titulo2, (w - fm.stringWidth(titulo2)) / 2, 36);
        }

        // Dibujo del MiniTag inferior
        g2d.setFont(new Font("SansSerif", Font.PLAIN, Math.max(8, w / 14)));
        FontMetrics fmTag = g2d.getFontMetrics();
        g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 200));
        g2d.drawString(miniTag, (w - fmTag.stringWidth(miniTag)) / 2, h - 12);

        // =========================================================================
        // 🎨 VECTOR DE ICONOS EN EL CENTRO EXACTO (Basado en la variable tipoIcono)
        // =========================================================================
        g2d.setColor(colorNeon);
        g2d.setStroke(new BasicStroke(2.0f));
        int centerX = w / 2;
        int centerY = h / 2 + 5; 

        switch (tipoIcono) {
            case "ADMIN":
                g2d.drawOval(centerX - 13, centerY - 18, 26, 26);
                g2d.drawArc(centerX - 22, centerY + 8, 44, 20, 0, 180);
                break;
            case "ACTUALIZAR":
                g2d.drawOval(centerX - 20, centerY - 20, 40, 40);
                g2d.drawOval(centerX - 20, centerY - 5, 40, 10);
                break;
            case "COMPROBAR":
                g2d.drawRect(centerX - 14, centerY - 18, 28, 36);
                g2d.drawLine(centerX - 5, centerY + 2, centerX - 1, centerY + 6);
                g2d.drawLine(centerX - 1, centerY + 6, centerX + 6, centerY - 4);
                break;
            case "REVISION":
                g2d.drawRoundRect(centerX - 18, centerY - 14, 36, 26, 6, 6);
                g2d.drawLine(centerX - 10, centerY - 4, centerX + 10, centerY - 4);
                break;
            case "TESTING":
                g2d.drawOval(centerX - 18, centerY - 18, 36, 36);
                g2d.drawLine(centerX, centerY, centerX, centerY - 10);
                g2d.drawLine(centerX, centerY, centerX + 8, centerY);
                break;
            case "AGREGAR":
                g2d.drawRect(centerX - 16, centerY - 4, 32, 24);
                g2d.drawRect(centerX - 19, centerY - 10, 38, 6);
                break;
            case "PERSONALIZAR":
                g2d.drawLine(centerX - 16, centerY - 8, centerX + 16, centerY - 8);
                g2d.fillRect(centerX - 4, centerY - 11, 6, 6);
                g2d.drawLine(centerX - 16, centerY + 6, centerX + 16, centerY + 6);
                g2d.fillRect(centerX + 4, centerY + 3, 6, 6);
                break;
        }
        g2d.dispose();
    }
}