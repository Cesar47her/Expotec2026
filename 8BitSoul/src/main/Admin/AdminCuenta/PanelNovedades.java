package main.Admin.AdminCuenta;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.List;
import main.Main;
import main.Util.EstiloDiseno;

public class PanelNovedades extends JPanel {

    private static final Color CARD_BG = new Color(3, 5, 16, 200);
    private static final Color TEXT_WHITE = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED = EstiloDiseno.TEXT_MUTED;

    public PanelNovedades() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONTENEDOR CON SCROLLBAR PARA LAS PUBLICACIONES
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        
        JPanel feedContainer = new JPanel();
        feedContainer.setOpaque(false);
        feedContainer.setLayout(new BoxLayout(feedContainer, BoxLayout.Y_AXIS));

        // INSTANCIA DEL DAO Y CARGA DESDE MYSQL
        NovedadesDAO novedadesDAO = new NovedadesDAO();
        List<NoticiaData> listaNoticias = novedadesDAO.obtenerNoticias();

        // Si la base de datos está vacía, muestra un aviso Cyberpunk en pantalla
        if (listaNoticias.isEmpty()) {
            JLabel lblVacio = new JLabel("📡 CANAL DE NOVEDADES FUERA DE LÍNEA / SIN REGISTROS");
            lblVacio.setFont(new Font("Dialog", Font.BOLD, 12));
            lblVacio.setForeground(EstiloDiseno.PINK_NEON);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            feedContainer.add(lblVacio);
        } else {
            // Pintar tarjetas interactivas
            for (int i = 0; i < listaNoticias.size(); i++) {
                NoticiaData n = listaNoticias.get(i);
                
                // Convertir el texto Hexadecimal (#00FFFF) a un objeto java.awt.Color seguro
                Color colorNoticia;
                try {
                    colorNoticia = Color.decode(n.colorHex);
                } catch (NumberFormatException e) {
                    colorNoticia = EstiloDiseno.CYAN_NEON; // Color de respaldo por si el hex está mal escrito
                }

                feedContainer.add(crearTarjetaNoticia(n.titulo, n.fecha, n.contenido, colorNoticia));
                
                if (i < listaNoticias.size() - 1) {
                    feedContainer.add(Box.createRigidArea(new Dimension(0, 15)));
                }
            }
        }

        // Hacer la lista deslizable con un ScrollPane transparente
        JScrollPane scroll = new JScrollPane(feedContainer);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        add(scroll, gbc);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("TERMINAL DE NOVEDADES");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Registro de notas de parches, anuncios oficiales y eventos activos en el ecosistema.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearTarjetaNoticia(String titulo, String fecha, String contenido, Color colorTematico) {
        JPanel card = new JPanel(new BorderLayout(0, 12)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                int w = getWidth(), h = getHeight(), cut = 15;
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w, 0);
                path.lineTo(w, h - cut); path.lineTo(w - cut, h);
                path.lineTo(0, h); path.lineTo(0, cut);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                
                // Línea decorativa izquierda dinámica con el color traído de la Base de Datos
                g2d.setColor(colorTematico);
                g2d.setStroke(new BasicStroke(3f));
                g2d.drawLine(0, 0, 0, h);
                
                // Borde exterior estético
                g2d.setColor(new Color(255, 255, 255, 12));
                g2d.setStroke(new BasicStroke(1f));
                g2d.draw(path);
                
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        card.setMaximumSize(new Dimension(1920, 140));

        JPanel topRow = new JPanel(new BorderLayout());
        topRow.setOpaque(false);

        JLabel lblTitle = new JLabel(titulo);
        lblTitle.setFont(new Font("Dialog", Font.BOLD, 14));
        lblTitle.setForeground(colorTematico);

        JLabel lblFecha = new JLabel("📅  " + fecha);
        lblFecha.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblFecha.setForeground(TEXT_MUTED);

        topRow.add(lblTitle, BorderLayout.WEST);
        topRow.add(lblFecha, BorderLayout.EAST);

        JTextArea txtCuerpo = new JTextArea(contenido);
        txtCuerpo.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtCuerpo.setForeground(TEXT_WHITE);
        txtCuerpo.setEditable(false);
        txtCuerpo.setFocusable(false);
        txtCuerpo.setOpaque(false);
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);

        card.add(topRow, BorderLayout.NORTH);
        card.add(txtCuerpo, BorderLayout.CENTER);

        return card;
    }
}