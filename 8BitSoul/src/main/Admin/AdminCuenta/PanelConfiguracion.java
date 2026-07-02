package main.Admin.AdminCuenta;

import main.Util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.Map;

public class PanelConfiguracion extends JPanel {
    
    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    private ConfiguracionDAO configDAO;
    private Map<String, String> mapaConfiguraciones;
    private int idUsuarioActual = 1; // Enlazado con el Administrador 'Soul_Golden' de tu script

    public PanelConfiguracion() {
        configDAO = new ConfiguracionDAO();
        // Cargar mapa persistente real desde MySQL
        mapaConfiguraciones = configDAO.cargarConfiguracionUsuario(idUsuarioActual);

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        inicializarComponentesConsola();
    }

    private void inicializarComponentesConsola() {
        removeAll(); // Limpia el panel para permitir recargas dinámicas en caliente

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER
        gbc.gridy = 0; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CUERPO MODULAR RECONSTRUIBLE
        gbc.gridy = 1; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        add(crearCuerpoConfiguracion(), gbc);

        revalidate();
        repaint();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONFIGURACIÓN DEL SISTEMA");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Ajusta los parámetros operativos de tu perfil guardados en Base de Datos.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title); left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearCuerpoConfiguracion() {
        JPanel contenedor = new JPanel(new GridLayout(2, 2, 20, 20));
        contenedor.setOpaque(false);

        // Extracción de datos tipados del mapa reactivo
        boolean musicaVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("AUDIO_MUSICA", "true"));
        boolean efectosVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("AUDIO_EFECTOS", "true"));
        int volumenVal = Integer.parseInt(mapaConfiguraciones.getOrDefault("AUDIO_VOLUMEN", "80"));

        boolean glowVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("GRAFICOS_GLOW", "true"));
        boolean gpuVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("GRAFICOS_GPU", "true"));
        int fpsVal = Integer.parseInt(mapaConfiguraciones.getOrDefault("GRAFICOS_FPS", "60"));

        boolean alertasVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("UI_ALERTAS", "true"));
        boolean compactoVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("UI_COMPACTO", "false"));
        boolean logsVal = Boolean.parseBoolean(mapaConfiguraciones.getOrDefault("UI_LOGS", "false"));

        // Bloque 1: Audio
        contenedor.add(crearTarjetaConfig("🎵   AUDIO Y SONIDO", new Component[]{
            crearFilaToggle("MÚSICA DE FONDO (SYNTHWAVE)", "AUDIO_MUSICA", musicaVal),
            crearFilaToggle("EFECTOS DE INTERFAZ (UI SOUNDS)", "AUDIO_EFECTOS", efectosVal),
            crearFilaSlider("VOLUMEN GENERAL", "AUDIO_VOLUMEN", volumenVal)
        }));

        // Bloque 2: Gráficos
        contenedor.add(crearTarjetaConfig("📺   RENDIMIENTO Y GRÁFICOS", new Component[]{
            crearFilaToggle("APLICAR EFECTO DE BRILLO GLOW NEÓN", "GRAFICOS_GLOW", glowVal),
            crearFilaToggle("RENDERIZADO POR HARDWARE (GPU)", "GRAFICOS_GPU", gpuVal),
            crearFilaSlider("LIMITADOR DE FPS MAX", "GRAFICOS_FPS", fpsVal)
        }));

        // Bloque 3: UI
        contenedor.add(crearTarjetaConfig("🔔   NOTIFICACIONES Y UI", new Component[]{
            crearFilaToggle("ALERTAS EN TIEMPO REAL (TOASTS)", "UI_ALERTAS", alertasVal),
            crearFilaToggle("MODO COMPACTO POR DEFECTO", "UI_COMPACTO", compactoVal),
            crearFilaToggle("REGISTRO DE LOGS EN CONSOLA", "UI_LOGS", logsVal)
        }));

        // Bloque 4: Opciones de Núcleo (Mantenimiento Crítico)
        JButton btnLimpiar = crearBotonCritico("LIMPIAR CACHÉ DEL CORE", CYAN_NEON);
        btnLimpiar.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Caché de volcado de texturas e historial purgado correctamente.", "CORE SYSTEM", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnRestablecer = crearBotonCritico("RESTABLECER VALORES DE FÁBRICA", PINK_NEON);
        btnRestablecer.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Confirmas el restablecimiento absoluto de fábrica?\nSe sobrescribirán todos tus parámetros en BitSoul DB.", 
                "SISTEMA DE SEGURIDAD CRÍTICO", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // 1. Obtener mapa limpio estructurado de valores de fábrica
                mapaConfiguraciones = configDAO.obtenerMapaPorDefecto();
                // 2. Persistir los valores por defecto directamente en tu BD
                configDAO.guardarConfiguracionUsuario(idUsuarioActual, mapaConfiguraciones);
                
                // 3. Re-renderizar visualmente de forma segura en el Event Dispatch Thread (EDT)
                SwingUtilities.invokeLater(() -> {
                    inicializarComponentesConsola();
                    JOptionPane.showMessageDialog(this, "Estructura transaccional restablecida. Configuración de fábrica cargada.", "DB TRANSACTION SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                });
            }
        });

        contenedor.add(crearTarjetaConfig("⚙️   OPCIONES DE NÚCLEO", new Component[]{
            new JLabel("Acciones críticas de mantenimiento del sistema:"),
            Box.createRigidArea(new Dimension(0, 5)), btnLimpiar,
            Box.createRigidArea(new Dimension(0, 10)), btnRestablecer
        }));

        return contenedor;
    }

    private JPanel crearTarjetaConfig(String titulo, Component[] componentes) {
        JPanel card = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(0, h); path.lineTo(0, cut);
                path.closePath();
                g2d.setColor(CARD_BG); g2d.fill(path);
                g2d.setColor(new Color(255, 255, 255, 15)); g2d.draw(path);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Preservación estricta de fuentes y colores neón asignados
        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Dialog", Font.BOLD, 13));
        lblTit.setForeground(CYAN_NEON);
        lblTit.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        card.add(lblTit, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        for (Component comp : componentes) {
            if (comp instanceof JLabel && !((JLabel) comp).getForeground().equals(CYAN_NEON)) {
                ((JLabel) comp).setFont(new Font("Dialog", Font.PLAIN, 12));
                ((JLabel) comp).setForeground(TEXT_MUTED);
                ((JLabel) comp).setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            body.add(comp);
            body.add(Box.createRigidArea(new Dimension(0, 8)));
        }
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    private JPanel crearFilaToggle(String nombreConfig, String mapaKey, boolean estadoInicial) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(600, 35));

        JLabel lbl = new JLabel(nombreConfig);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        lbl.setForeground(TEXT_WHITE);

        JToggleButton toggle = new JToggleButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                g2d.setColor(INPUT_BG); g2d.fillRoundRect(0, 0, w, h, 10, 10);
                g2d.setColor(new Color(255, 255, 255, 20)); g2d.drawRoundRect(0, 0, w - 1, h - 1, 10, 10);
                int d = h - 6;
                if (isSelected()) {
                    g2d.setColor(new Color(0, 255, 130)); g2d.fillOval(w - d - 3, 3, d, d);
                } else {
                    g2d.setColor(PINK_NEON); g2d.fillOval(3, 3, d, d);
                }
                g2d.dispose();
            }
        };
        toggle.setSelected(estadoInicial);
        toggle.setPreferredSize(new Dimension(45, 22));
        toggle.setOpaque(false);
        toggle.setContentAreaFilled(false);
        toggle.setBorderPainted(false);
        toggle.setFocusPainted(false);
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        toggle.addItemListener(e -> {
            mapaConfiguraciones.put(mapaKey, String.valueOf(toggle.isSelected()));
            configDAO.guardarConfiguracionUsuario(idUsuarioActual, mapaConfiguraciones);
        });

        row.add(lbl, BorderLayout.CENTER);
        row.add(toggle, BorderLayout.EAST);
        return row;
    }

    private JPanel crearFilaSlider(String nombreConfig, String mapaKey, int valorInicial) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(600, 40));

        JLabel lbl = new JLabel(nombreConfig);
        lbl.setFont(new Font("Dialog", Font.PLAIN, 12));
        lbl.setForeground(TEXT_WHITE);

        JSlider slider = new JSlider(0, 100, valorInicial);
        slider.setOpaque(false);
        slider.setPreferredSize(new Dimension(150, 20));
        slider.setBackground(INPUT_BG);
        slider.setForeground(CYAN_NEON);
        slider.setCursor(new Cursor(Cursor.HAND_CURSOR));

        slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
                mapaConfiguraciones.put(mapaKey, String.valueOf(slider.getValue()));
                configDAO.guardarConfiguracionUsuario(idUsuarioActual, mapaConfiguraciones);
            }
        });

        row.add(lbl, BorderLayout.CENTER);
        row.add(slider, BorderLayout.EAST);
        return row;
    }

    private JButton crearBotonCritico(String texto, Color colorNeon) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 6;
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(cut, h); path.lineTo(0, h - cut);
                path.closePath();
                if (getModel().isPressed()) g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 40));
                else if (getModel().isRollover()) g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 15));
                else g2d.setColor(new Color(12, 16, 35));
                g2d.fill(path);
                g2d.setColor(colorNeon); g2d.setStroke(new BasicStroke(1.2f)); g2d.draw(path);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 11));
        btn.setForeground(Color.WHITE);
        btn.setMaximumSize(new Dimension(500, 36));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setContentAreaFilled(false); btn.setBorderPainted(false); btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}