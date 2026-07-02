package main.Admin.AdminCuenta;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import main.Util.EstiloDiseno; 

public class PanelInicio extends JPanel {

    private static final Color CARD_BG    = EstiloDiseno.CARD_BG;
    private static final Color CYAN_NEON  = EstiloDiseno.CYAN_NEON; 
    private static final Color PINK_NEON  = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED = EstiloDiseno.TEXT_MUTED;

    private JLabel lblBienvenida;
    private JLabel lblFechaReloj;
    private JPanel panelContenedorEstadisticas;
    private Timer timerReloj;

    public PanelInicio() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER (Título y Reloj)
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        add(crearHeaderTop(), gbc);

        // 2. BANNER CENTRAL
        gbc.gridy = 1;
        gbc.weighty = 0.35;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearBannerPrincipal(), gbc);

        // 3. CONTENEDOR DE TARJETAS ESTADÍSTICAS
        gbc.gridy = 2;
        gbc.weighty = 0.30;
        gbc.insets = new Insets(0, 0, 20, 0);
        
        panelContenedorEstadisticas = new JPanel(new BorderLayout(0, 8));
        panelContenedorEstadisticas.setOpaque(false);
        add(panelContenedorEstadisticas, gbc);

        // 4. SECCIÓN INFERIOR (Misión, Visión, Valores)
        gbc.gridy = 3;
        gbc.weighty = 0.35;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearSeccionInferior(), gbc);

        inicializarReloj();
        actualizarDatosPantalla();
    }   

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        JLabel title = new JLabel("INICIO");
        title.setFont(new Font("Monospaced", Font.BOLD, 24));
        title.setForeground(TEXT_WHITE);
        
        lblBienvenida = new JLabel("Cargando sesión de seguridad...");
        lblBienvenida.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblBienvenida.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(lblBienvenida);

        JPanel right = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g); 
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2d.setColor(new Color(255, 255, 255, 20));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);
                g2d.dispose();
            }
        };
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(240, 38));
        right.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        lblFechaReloj = new JLabel("📅 -- / -- / ---- --:-- --");
        lblFechaReloj.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblFechaReloj.setForeground(CYAN_NEON);
        right.add(lblFechaReloj);

        p.add(left, BorderLayout.WEST);
        p.add(right, BorderLayout.EAST);
        return p;
    }

    private JPanel crearBannerPrincipal() {
        JPanel banner = new JPanel(new BorderLayout(20, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 20;
                
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(0, h); path.lineTo(0, cut);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                g2d.setColor(PINK_NEON);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel textContainer = new JPanel();
        textContainer.setOpaque(false);
        textContainer.setLayout(new BoxLayout(textContainer, BoxLayout.Y_AXIS));

        JLabel mainTitle = new JLabel("BIT SOUL SYSTEM");
        mainTitle.setFont(new Font("Monospaced", Font.BOLD, 34));
        mainTitle.setForeground(CYAN_NEON);

        JLabel subTitle = new JLabel("CORE INTERFACE // PROTOCOLO DE ADMINISTRACIÓN GLOBAL");
        subTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        subTitle.setForeground(TEXT_WHITE);

        JTextArea desc = new JTextArea(
            "BIT SOUL es una plataforma enfocada en la gestión de usuarios, inventarios, compras y " +
            "progresión dentro de videojuegos. Nuestro objetivo es brindar una experiencia segura, " +
            "organizada y eficiente para jugadores y administradores de la red central."
        );
        desc.setFont(new Font("SansSerif", Font.PLAIN, 12));
        desc.setForeground(TEXT_MUTED);
        desc.setEditable(false);
        desc.setFocusable(false);
        desc.setOpaque(false);
        desc.setLineWrap(true);
        desc.setWrapStyleWord(true);
        desc.setMaximumSize(new Dimension(650, 70));

        textContainer.add(mainTitle);
        textContainer.add(Box.createRigidArea(new Dimension(0, 2)));
        textContainer.add(subTitle);
        textContainer.add(Box.createRigidArea(new Dimension(0, 10)));
        textContainer.add(desc);

        JPanel shieldVector = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int cx = getWidth() / 2;
                int cy = getHeight() / 2;
                
                int[] xs = {cx-45, cx+45, cx+60, cx+60, cx, cx-60, cx-60};
                int[] ys = {cy-55, cy-55, cy-25, cy+35, cy+60, cy+35, cy-25};
                g2d.setColor(new Color(5, 12, 30));
                g2d.fillPolygon(xs, ys, 7);
                
                g2d.setStroke(new BasicStroke(1.8f));
                g2d.setColor(CYAN_NEON);
                g2d.drawPolygon(xs, ys, 7);
                
                g2d.setColor(PINK_NEON);
                g2d.drawRoundRect(cx-15, cy-2, 30, 24, 6, 6);
                g2d.drawOval(cx-10, cy-16, 20, 16);
                g2d.fillOval(cx-4, cy+4, 8, 8);
                g2d.dispose();
            }
        };
        shieldVector.setOpaque(false);
        shieldVector.setPreferredSize(new Dimension(160, 130));

        banner.add(textContainer, BorderLayout.CENTER);
        banner.add(shieldVector, BorderLayout.EAST);
        return banner;
    }

    private JPanel crearCardMetrica(String icono, String valor, String titulo, String porcentaje, boolean esPositivo) {
        final String valFinal = (valor == null || valor.trim().isEmpty()) ? "0" : valor;

        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 12;
                
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h - cut); path.lineTo(w - cut, h); path.lineTo(0, h);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.draw(path);

                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(esPositivo ? CYAN_NEON : PINK_NEON);
                int[] gx = {12, w/4, w/2, (3*w)/4, w-12};
                int[] gy = esPositivo ? 
                    new int[]{h-15, h-18, h-12, h-22, h-28} : 
                    new int[]{h-28, h-20, h-25, h-14, h-12};
                g2d.drawPolyline(gx, gy, 5);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 12, 32, 12));
        card.setPreferredSize(new Dimension(160, 110));
        card.setMinimumSize(new Dimension(160, 110));
        card.setMaximumSize(new Dimension(160, 110));

        JPanel center = new JPanel(new GridBagLayout());
        center.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        JLabel lblIcon = new JLabel(icono);
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 22));
        lblIcon.setForeground(CYAN_NEON);
        
        JLabel lblVal = new JLabel(valFinal, SwingConstants.RIGHT);
        lblVal.setFont(new Font("Monospaced", Font.BOLD, 20));
        lblVal.setForeground(Color.WHITE);

        c.gridx = 0; c.gridy = 0; center.add(lblIcon, c);
        c.gridx = 1; center.add(lblVal, c);

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("SansSerif", Font.BOLD, 9));
        lblTit.setForeground(CYAN_NEON);

        JLabel lblPerc = new JLabel(porcentaje + " vs mes anterior");
        lblPerc.setFont(new Font("SansSerif", Font.PLAIN, 9));
        lblPerc.setForeground(esPositivo ? EstiloDiseno.GREEN_NEON : PINK_NEON);

        card.add(center, BorderLayout.NORTH);
        
        JPanel bottomText = new JPanel(new GridLayout(2, 1, 0, 1));
        bottomText.setOpaque(false);
        bottomText.add(lblTit);
        bottomText.add(lblPerc);
        card.add(bottomText, BorderLayout.CENTER);

        return card;
    }

    private JPanel crearSeccionInferior() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.weightx = 0.28; gbc.insets = new Insets(0, 0, 0, 15);
        p.add(crearCardBloqueTexto("🚀", "MISIÓN", "Ofrecer herramientas tecnológicas innovadoras para la administración de recursos y perfiles de jugadores."), gbc);

        gbc.gridx = 1; gbc.weightx = 0.28; gbc.insets = new Insets(0, 0, 0, 15);
        p.add(crearCardBloqueTexto("👁️", "VISIÓN", "Convertirnos en una plataforma líder en la gestión de comunidades y sistemas para videojuegos."), gbc);

        gbc.gridy = 0; gbc.gridx = 2; gbc.weightx = 0.44; gbc.insets = new Insets(0, 0, 0, 0);
        p.add(crearCardValores(), gbc);

        return p;
    }

    private JPanel crearCardBloqueTexto(String icono, String titulo, String cuerpo) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel lblIcon = new JLabel(icono, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(5, 15, 35));
                g2d.fillOval(0, 0, getWidth(), getHeight());
                g2d.setColor(CYAN_NEON);
                g2d.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setPreferredSize(new Dimension(44, 44));
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 18));

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTit.setForeground(PINK_NEON);

        JTextArea txtCuerpo = new JTextArea(cuerpo);
        txtCuerpo.setFont(new Font("SansSerif", Font.PLAIN, 11));
        txtCuerpo.setForeground(TEXT_MUTED);
        txtCuerpo.setEditable(false);
        txtCuerpo.setFocusable(false);
        txtCuerpo.setOpaque(false);
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);

        right.add(lblTit);
        right.add(Box.createRigidArea(new Dimension(0, 4)));
        right.add(txtCuerpo);

        card.add(lblIcon, BorderLayout.WEST);
        card.add(right, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearCardValores() {
        JPanel card = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTit = new JLabel("VALORES CORE");
        lblTit.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTit.setForeground(CYAN_NEON);
        card.add(lblTit, BorderLayout.NORTH);

        JPanel row = new JPanel(new GridLayout(1, 5, 8, 0));
        row.setOpaque(false);
        
        row.add(crearItemValor("💡", "INNOVACIÓN", CYAN_NEON));
        row.add(crearItemValor("🛡️", "SEGURIDAD", CYAN_NEON));
        row.add(crearItemValor("💎", "CALIDAD", PINK_NEON));
        row.add(crearItemValor("👥", "COMUNIDAD", CYAN_NEON));
        row.add(crearItemValor("📄", "TRANSPARENCIA", CYAN_NEON));

        card.add(row, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearItemValor(String icono, String texto, Color colorNeon) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

        JLabel lblIcon = new JLabel(icono, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(5, 12, 28));
                
                int w = getWidth(), h = getHeight();
                int[] hx = {w/2, w-2, w-2, w/2, 2, 2};
                int[] hy = {2, h/3, (2*h)/3, h-2, (2*h)/3, h/3};
                g2d.fillPolygon(hx, hy, 6);
                g2d.setColor(colorNeon);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.drawPolygon(hx, hy, 6);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        lblIcon.setPreferredSize(new Dimension(40, 40));
        lblIcon.setMaximumSize(new Dimension(40, 40));
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 15));
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTxt = new JLabel(texto, SwingConstants.CENTER);
        lblTxt.setFont(new Font("SansSerif", Font.BOLD, 8));
        lblTxt.setForeground(TEXT_MUTED);
        lblTxt.setAlignmentX(Component.CENTER_ALIGNMENT);

        p.add(lblIcon);
        p.add(Box.createRigidArea(new Dimension(0, 6)));
        p.add(lblTxt);
        return p;
    }

    private void inicializarReloj() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd / MM / yyyy    hh:mm a");
        timerReloj = new Timer(1000, e -> {
            lblFechaReloj.setText("📅 " + sdf.format(new Date()));
        });
        timerReloj.start();
    }

    public void actualizarDatosPantalla() {
        String usuarioActual = "Soul_Golden"; 
        lblBienvenida.setText("Protocolo de acceso verificado. Bienvenido, " + usuarioActual);

        if (panelContenedorEstadisticas == null) return;
        panelContenedorEstadisticas.removeAll();
        
        JLabel sectionTitle = new JLabel("⟨ ESTADÍSTICAS OPERATIVAS DEL TERMINAL ⟩   »»»");
        sectionTitle.setFont(new Font("Monospaced", Font.BOLD, 12));
        sectionTitle.setForeground(CYAN_NEON);
        panelContenedorEstadisticas.add(sectionTitle, BorderLayout.NORTH);

        // Fallbacks por si el DAO retorna nulls
        String totalUsuarios = main.Admin.AdminCuenta.InicioDAO.obtenerTotalUsuarios();
        String totalCompras = main.Admin.AdminCuenta.InicioDAO.obtenerTotalCompras();
        String totalTickets = main.Admin.AdminCuenta.InicioDAO.obtenerTotalTickets();
        String totalInventario = main.Admin.AdminCuenta.InicioDAO.obtenerTotalInventario();
        String totalVentas = main.Admin.AdminCuenta.InicioDAO.obtenerTotalVentas();

        // CONTENEDOR NUEVO usando GridBagLayout para tener control milimétrico del tamaño
        JPanel gridEstable = new JPanel(new GridBagLayout());
        gridEstable.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.NONE; // Evita el estiramiento automático
        c.anchor = GridBagConstraints.CENTER;
        c.weighty = 1.0;
        c.weightx = 1.0;
        c.insets = new Insets(0, 6, 0, 6); // Espaciado horizontal controlado entre tarjetas

        c.gridx = 0; gridEstable.add(crearCardMetrica("👥", totalUsuarios, "USUARIOS REGISTRADOS", "+1.2%", true), c);
        c.gridx = 1; gridEstable.add(crearCardMetrica("🛒", totalCompras, "COMPRAS REALIZADAS", "+4.5%", true), c);
        c.gridx = 2; gridEstable.add(crearCardMetrica("🎧", totalTickets, "SOLICITUDES DE AYUDA", "Estable", true), c);
        c.gridx = 3; gridEstable.add(crearCardMetrica("📦", totalInventario, "OBJETOS EN INVENTARIO", "Sincronizado", true), c);
        c.gridx = 4; gridEstable.add(crearCardMetrica("💲", totalVentas, "VENTAS TOTALES", "Live", true), c);

        panelContenedorEstadisticas.add(gridEstable, BorderLayout.CENTER);
        
        panelContenedorEstadisticas.revalidate();
        panelContenedorEstadisticas.repaint();
    }
}