package main.Util;

import java.awt.*;
import java.awt.print.*;
import java.net.URL;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import main.Util.UsuarioDAO; // Importamos el DAO que usa tu conexión

public class ImpresoraTicket {

    private static final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public static void imprimirConfiguracion(int idUsuario) {
        Map<String, Object> datos = usuarioDAO.obtenerConfiguracionImpresion(idUsuario);
        
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No se encontraron datos para el usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombreUsuario = (String) datos.get("username");
        String tipoUsuario = (String) datos.get("nombre_rol");
        int volPorcentaje = (int) datos.get("volumen_porcentaje");
        String controlesRaw = (String) datos.get("mapeo_controles");

        String arriba = "W", abajo = "S", izquierda = "A", derecha = "D";
        String saltar = "ESPACIO", atacar = "J", inventario = "I", pausa = "P";

        if (controlesRaw != null && !controlesRaw.isEmpty()) {
            String[] pares = controlesRaw.split(";");
            for (String par : pares) {
                String[] deconstruct = par.split(":");
                if (deconstruct.length == 2) {
                    switch (deconstruct[0].trim()) {
                        case "Arriba": arriba = deconstruct[1]; break;
                        case "Abajo": abajo = deconstruct[1]; break;
                        case "Izquierda": izquierda = deconstruct[1]; break;
                        case "Derecha": derecha = deconstruct[1]; break;
                        case "Saltar": saltar = deconstruct[1]; break;
                        case "Atacar": atacar = deconstruct[1]; break;
                        case "Inventario": inventario = deconstruct[1]; break;
                        case "Pausa": pausa = deconstruct[1]; break;
                    }
                }
            }
        }

        ejecutarImpresionConfig(nombreUsuario, tipoUsuario, volPorcentaje, arriba, abajo, izquierda, derecha, saltar, atacar, inventario, pausa);
    }

    public static void imprimirProgresoNivel(int idUsuario) {
        Map<String, Object> datos = usuarioDAO.obtenerProgresoJugadorImpresion(idUsuario);
        
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "El jugador no registra historial de progreso.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = (String) datos.get("username");
        String rango = (String) datos.get("nombre_rol");
        String email = (String) datos.get("correo");
        String fecha = (String) datos.get("fecha_registro");
        int nivel = (int) datos.get("nivel_cuenta");
        int score = (int) datos.get("max_score");

        ejecutarImpresionProgreso(nombre, rango, email, nivel, score, fecha);
    }

    private static void ejecutarImpresionConfig(String nombre, String rango, int vol, String arr, String abj, String izq, String der, String sal, String atq, String inv, String pau) {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        paper.setSize(164.0, 700.0);
        paper.setImageableArea(0, 0, 164.0, 550.0);
        pf.setPaper(paper);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            Font normal = new Font("Monospaced", Font.BOLD, 7);
            Font grande = new Font("Monospaced", Font.BOLD, 11);
            int y = 12;

            try {
                URL url = ImpresoraTicket.class.getResource("/imagenes/LogoFact.png");
                if (url != null) { g2d.drawImage(new ImageIcon(url).getImage(), 15, y, 120, 70, null); y += 75; }
            } catch (Exception e) {}

            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString("   8 BIT SOUL", 10, y + 3); y += 14;
            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString(" CONFIGURACIÓN", 12, y + 3); y += 14;
            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            
            g2d.drawString("Usuario: " + nombre, 2, y); y += 9;
            g2d.drawString("Rango:   " + rango, 2, y); y += 9;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString("Volumen Audio: " + vol + "%", 2, y); y += 9;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString("CONTROLES ASIGNADOS:", 2, y); y += 9;
            g2d.drawString("Arriba:    " + arr, 2, y); y += 9;
            g2d.drawString("Abajo:     " + abj, 2, y); y += 9;
            g2d.drawString("Izquierda: " + izq, 2, y); y += 9;
            g2d.drawString("Derecha:   " + der, 2, y); y += 9;
            g2d.drawString("Saltar:    " + sal, 2, y); y += 9;
            g2d.drawString("Atacar:    " + atq, 2, y); y += 9;
            g2d.drawString("Inventario:" + inv, 2, y); y += 9;
            g2d.drawString("Pausa:     " + pau, 2, y); y += 9;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString(" EXPOTEC 2026", 12, y + 3); y += 14;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            return Printable.PAGE_EXISTS;
        }, pf);

        try { job.print(); } catch (PrinterException e) { dectectarError(e.getMessage()); }
    }

    private static void ejecutarImpresionProgreso(String nombre, String rango, String email, int nivel, int score, String fecha) {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.defaultPage();
        Paper paper = new Paper();
        paper.setSize(164.0, 450.0);
        paper.setImageableArea(0, 0, 164.0, 450.0);
        pf.setPaper(paper);

        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) return Printable.NO_SUCH_PAGE;
            Graphics2D g2d = (Graphics2D) graphics;
            g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            
            Font normal = new Font("Monospaced", Font.BOLD, 7);
            Font grande = new Font("Monospaced", Font.BOLD, 11);
            Font gigante = new Font("Monospaced", Font.BOLD, 15);
            int y = 12;

            try {
                URL url = ImpresoraTicket.class.getResource("/imagenes/LogoFact.png");
                if (url != null) { g2d.drawImage(new ImageIcon(url).getImage(), 20, y, 120, 70, null); y += 75; }
            } catch (Exception e) {}

            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString("   8 BIT SOUL", 10, y + 3); y += 14;
            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString(" ESTADO JUGADOR", 10, y + 3); y += 14;
            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            
            g2d.drawString("Nombre: " + nombre, 2, y); y += 9;
            g2d.drawString("Rango:  " + rango, 2, y); y += 9;
            g2d.drawString("Email:  " + email, 2, y); y += 9;
            g2d.drawString("Desde:  " + fecha, 2, y); y += 9;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString("Max Score: " + score + " pts", 2, y); y += 9;
            g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString("NIVEL EN CUENTA:", 2, y); y += 11;
            
            g2d.setFont(gigante);
            g2d.drawString(">> LVL " + nivel + " <<", 12, y + 4); y += 22;
            
            g2d.setFont(normal); g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.setFont(grande); g2d.drawString(" EXPOTEC 2026", 12, y + 3); y += 14;
            g2d.setFont(normal);g2d.drawString("------------------------------", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            g2d.drawString(" ", 2, y); y += 9;
            return Printable.PAGE_EXISTS;
        }, pf);

        try { job.print(); } catch (PrinterException e) { dectectarError(e.getMessage()); }
    }

    private static void dectectarError(String msg) {
        JOptionPane.showMessageDialog(null, "Error físico en la PR-100B: " + msg, "ERROR HARDWARE", JOptionPane.ERROR_MESSAGE);
    }
}