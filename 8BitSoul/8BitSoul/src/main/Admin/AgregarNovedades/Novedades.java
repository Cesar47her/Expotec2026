package main.Admin.AgregarNovedades;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.List;

// Importamos la clase del menú de administración
import main.Admin.PanelAdmin;

public class Novedades extends JFrame {

    // Paleta de colores Neón del diseño
    private static final Color COLOR_CIAN = new Color(0, 240, 255);
    private static final Color COLOR_MAGENTA = new Color(255, 0, 128);
    private static final Color COLOR_BG_PANEL = new Color(2, 14, 26, 200);
    private static final Color COLOR_TEXTO = new Color(200, 220, 240);
    private static final Color COLOR_TEXT_MUTED = new Color(100, 130, 150);
    private static final Color COLOR_BARRA_BG = new Color(10, 20, 38, 230); 

    private Image imagenFondo;
    
    // Variables para el arrastre de la ventana sin bordes
    private int mouseX, mouseY;
    private boolean esMaximizado = false;
    private Rectangle dimensionesPrevias;
    
    // Componentes globales de control
    private JTextField txtTitulo;
    private JTextArea txtCuerpo;
    private JPanel listaScrollContent;
    private JLabel lblDrop; 
    private JButton btnMaximizar; 
    
    // Almacenamiento temporal de la imagen seleccionada
    private File imagenSeleccionada = null; 
    
    private final NovedadesDAO novedadesDAO = new NovedadesDAO();

    public Novedades() {
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
        actualizarListaNoticias(); 
    }

    // --- 1. CONFIGURACIÓN GENERAL ---
    private void configurarVentana() {
        setTitle("BitSoul - Agregar Noticias");
        setSize(1200, 675);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true); 
        setLocationRelativeTo(null);

        try {
            imagenFondo = new ImageIcon("src/imagenes/FondoCrud.png").getImage();
            if (imagenFondo.getWidth(null) == -1) {
                imagenFondo = new ImageIcon("v.png").getImage();
            }
        } catch (Exception e) {
            System.out.println("Fondo dinámico ausente, aplicando sólido alternativo.");
        }
    }

    // --- 2. DISPOSICIÓN DE CAPAS (Layouts) ---
    private void inicializarComponentes() {
        // Panel contenedor principal (Sin márgenes internos para que la barra vaya de extremo a extremo)
        JPanel panelPrincipal = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imagenFondo != null && imagenFondo.getWidth(null) > 0) {
                    g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
                } else {
                    g.setColor(new Color(5, 10, 20));
                    g.fillRect(0, 0, getWidth(), getHeight());
                }
                
                // Borde neón exterior de punta a punta de la ventana
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(COLOR_CIAN);
                g2.setStroke(new BasicStroke(3f));
                g2.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        setContentPane(panelPrincipal);

        // 1. Añadimos la Barra Superior de control directamente al Norte (Punta a punta)
        panelPrincipal.add(crearBarraVentanaPersonalizada(), BorderLayout.NORTH);

        // 2. Creamos un contenedor interno para el resto de elementos que SÍ necesitan margen lateral
        JPanel panelCentralContenedor = new JPanel(new BorderLayout(20, 15));
        panelCentralContenedor.setOpaque(false);
        // Aquí aplicamos los márgenes que quitamos del contenedor raíz (30 a los lados, 20 abajo, 15 arriba)
        panelCentralContenedor.setBorder(BorderFactory.createEmptyBorder(15, 30, 20, 30));

        // Cabecera dentro del contenedor con margen
        panelCentralContenedor.add(crearCabecera(), BorderLayout.NORTH);

        // Formulario y Listado distribuidos equitativamente
        JPanel panelContenido = new JPanel(new GridLayout(1, 2, 30, 0));
        panelContenido.setOpaque(false);
        panelContenido.add(crearPanelFormulario());
        panelContenido.add(crearPanelListaNoticias());
        panelCentralContenedor.add(panelContenido, BorderLayout.CENTER);

        // Barra de botones inferior
        panelCentralContenedor.add(crearBarraInferior(), BorderLayout.SOUTH);

        // Finalmente añadimos el contenedor intermedio en el centro del layout principal
        panelPrincipal.add(panelCentralContenedor, BorderLayout.CENTER);
    }

    // --- BARRA SUPERIOR PERSONALIZADA (MINIMIZAR, MAXIMIZAR, CERRAR) ---
    private JPanel crearBarraVentanaPersonalizada() {
        JPanel barraControl = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BARRA_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Línea cian inferior de separación
                g2d.setColor(COLOR_CIAN);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        barraControl.setOpaque(false);
        barraControl.setPreferredSize(new Dimension(getWidth(), 35));

        // Lógica de arrastre
        barraControl.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                mouseX = evt.getX();
                mouseY = evt.getY();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    alternarMaximizacion();
                }
            }
        });
        barraControl.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent evt) {
                if (!esMaximizado) {
                    int x = evt.getXOnScreen();
                    int y = evt.getYOnScreen();
                    setLocation(x - mouseX, y - mouseY);
                }
            }
        });

        // Título izquierdo alineado correctamente con un pequeño margen
        JLabel lblTag = new JLabel("  ⚡ BITSOUL NEWS TERMINAL // parches_y_novedades.log");
        lblTag.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTag.setForeground(new Color(0, 240, 255, 180));
        barraControl.add(lblTag, BorderLayout.WEST);

        // Panel de botones derecho
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 2));
        panelBotones.setOpaque(false);

        BotonControlVentana btnMinimizar = new BotonControlVentana("—", COLOR_CIAN);
        btnMaximizar = new BotonControlVentana("⬜", COLOR_CIAN); 
        BotonControlVentana btnCerrar = new BotonControlVentana("X", COLOR_MAGENTA);

        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        btnMaximizar.addActionListener(e -> alternarMaximizacion());
        btnCerrar.addActionListener(e -> System.exit(0));

        panelBotones.add(btnMinimizar);
        panelBotones.add(btnMaximizar);
        panelBotones.add(btnCerrar);

        barraControl.add(panelBotones, BorderLayout.EAST);
        return barraControl;
    }
    
    private void alternarMaximizacion() {
        if (!esMaximizado) {
            dimensionesPrevias = getBounds();
            
            GraphicsConfiguration config = getGraphicsConfiguration();
            Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
            Rectangle bounds = config.getBounds();
            
            int x = bounds.x + insets.left;
            int y = bounds.y + insets.top;
            int ancho = bounds.width - insets.left - insets.right;
            int alto = bounds.height - insets.top - insets.bottom;
            
            setBounds(x, y, ancho, alto);
            
            btnMaximizar.setText("🗗"); 
            esMaximizado = true;
        } else {
            if (dimensionesPrevias != null) {
                setBounds(dimensionesPrevias);
            } else {
                setSize(1200, 675);
                setLocationRelativeTo(null);
            }
            btnMaximizar.setText("⬜"); 
            esMaximizado = false;
        }
        repaint();
    }

    // --- 3. MÓDULOS DE CONSTRUCCIÓN DE COMPONENTES ---
    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel textoHeader = new JPanel(new GridLayout(2, 1));
        textoHeader.setOpaque(false);
        
        JLabel lblTitulo = new JLabel("PANEL DE NOVEDADES Y NOTICIAS");
        lblTitulo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 26));
        lblTitulo.setForeground(COLOR_CIAN);
        
        JLabel lblSubtitulo = new JLabel("Inyección de parches de actualización y transmisiones globales de BitSoul");
        lblSubtitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 13));
        lblSubtitulo.setForeground(COLOR_TEXTO);

        textoHeader.add(lblTitulo);
        textoHeader.add(lblSubtitulo);
        header.add(textoHeader, BorderLayout.WEST);

        JLabel lblLogo = new JLabel("CORE_SYS ", SwingConstants.CENTER);
        lblLogo.setFont(new Font(Font.MONOSPACED, Font.BOLD, 22));
        lblLogo.setForeground(COLOR_CIAN);
        header.add(lblLogo, BorderLayout.EAST);

        return header;
    }

    private JPanel crearPanelFormulario() {
        JPanel form = new PanelCyberpunk();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createCompoundBorder(
                new BorderNeon(COLOR_CIAN), BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblSeccion = new JLabel("NUEVA NOTICIA");
        lblSeccion.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        lblSeccion.setForeground(COLOR_CIAN);
        lblSeccion.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(lblSeccion);
        form.add(Box.createVerticalStrut(15)); 

        form.add(crearEtiquetaForm("TÍTULO DEL PARCHE"));
        txtTitulo = new JTextField("Ej: v1.2.0 - Firewall Fortificado");
        txtTitulo.setBackground(new Color(1, 24, 38));
        txtTitulo.setForeground(COLOR_TEXT_MUTED);
        txtTitulo.setCaretColor(COLOR_CIAN);
        txtTitulo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtTitulo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CIAN, 1), BorderFactory.createEmptyBorder(5, 8, 5, 8)));
        txtTitulo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(txtTitulo);
        form.add(Box.createVerticalStrut(15));

        form.add(crearEtiquetaForm("CUERPO DEL MENSAJE"));
        form.add(crearToolbarSimulada());

        txtCuerpo = new JTextArea("Describe los cambios, mejoras, correcciones y novedades...");
        txtCuerpo.setBackground(new Color(1, 24, 38));
        txtCuerpo.setForeground(COLOR_TEXT_MUTED);
        txtCuerpo.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        txtCuerpo.setLineWrap(true);
        txtCuerpo.setWrapStyleWord(true);
        
        JScrollPane scrollCuerpo = new JScrollPane(txtCuerpo);
        scrollCuerpo.setBorder(BorderFactory.createLineBorder(COLOR_CIAN, 1));
        scrollCuerpo.setAlignmentX(Component.LEFT_ALIGNMENT);
        form.add(scrollCuerpo);
        form.add(Box.createVerticalStrut(15));

        form.add(crearEtiquetaForm("ILUSTRACIÓN / IMAGEN DESTACADA"));
        form.add(crearZonaCargaImagen());
        
        form.add(Box.createVerticalStrut(20));

        JPanel panelBotones = new JPanel(new GridLayout(1, 2, 20, 0));
        panelBotones.setOpaque(false);
        panelBotones.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        panelBotones.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnLimpiar = new BotonCyberpunk("LIMPIAR", COLOR_MAGENTA);
        JButton btnPublicar = new BotonCyberpunk("PUBLICAR NOTICIA", COLOR_CIAN);

        btnLimpiar.addActionListener(e -> restaurarFormulario());
        btnPublicar.addActionListener(e -> ejecutarPublicacion());

        panelBotones.add(btnLimpiar);
        panelBotones.add(btnPublicar);
        form.add(panelBotones);

        return form;
    }

    private JPanel crearZonaCargaImagen() {
        JPanel dropZone = new JPanel(new GridBagLayout());
        dropZone.setBackground(new Color(1, 24, 38));
        dropZone.setBorder(BorderFactory.createDashedBorder(COLOR_CIAN, 2, 5, 2, true));
        dropZone.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        dropZone.setAlignmentX(Component.LEFT_ALIGNMENT);
        dropZone.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        lblDrop = new JLabel("Arrastra una imagen o haz clic para seleccionar (Máx. 5MB)");
        lblDrop.setForeground(COLOR_TEXT_MUTED);
        lblDrop.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        dropZone.add(lblDrop);

        dropZone.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                seleccionarImagen();
            }
        });
        
        return dropZone;
    }

    private JPanel crearToolbarSimulada() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        toolbar.setBackground(new Color(1, 35, 54));
        toolbar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        toolbar.setAlignmentX(Component.LEFT_ALIGNMENT);
        String[] herramientas = {"B", "I", "U", "S", "🔗", "📷"};
        for (String h : herramientas) {
            JLabel btnH = new JLabel(h);
            btnH.setForeground(COLOR_CIAN);
            btnH.setCursor(new Cursor(Cursor.HAND_CURSOR));
            toolbar.add(btnH);
        }
        return toolbar;
    }

    private JPanel crearPanelListaNoticias() {
        JPanel listaContainer = new PanelCyberpunk();
        listaContainer.setLayout(new BorderLayout());
        listaContainer.setBorder(BorderFactory.createCompoundBorder(
                new BorderNeon(COLOR_CIAN), BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel lblSeccion = new JLabel("LISTADO DE NOTICIAS");
        lblSeccion.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
        lblSeccion.setForeground(COLOR_CIAN);
        listaContainer.add(lblSeccion, BorderLayout.NORTH);

        listaScrollContent = new JPanel();
        listaScrollContent.setLayout(new BoxLayout(listaScrollContent, BoxLayout.Y_AXIS));
        listaScrollContent.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(listaScrollContent);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(null);
        
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        scrollPane.getVerticalScrollBar().setUI(new javax.swing.plaf.basic.BasicScrollBarUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.setOpaque(false);
            }

            @Override
            protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(1, 24, 38, 200));
                g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
                g2.setColor(COLOR_CIAN);
                g2.drawLine(trackBounds.x, trackBounds.y, trackBounds.x, trackBounds.y + trackBounds.height);
                g2.dispose();
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(COLOR_CIAN);
                g2.fillRoundRect(thumbBounds.x + 3, thumbBounds.y + 2, thumbBounds.width - 6, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) { return crearBotonVacio(); }
            @Override
            protected JButton createIncreaseButton(int orientation) { return crearBotonVacio(); }
            
            private JButton crearBotonVacio() {
                JButton b = new JButton();
                b.setPreferredSize(new Dimension(0, 0));
                b.setMinimumSize(new Dimension(0, 0));
                b.setMaximumSize(new Dimension(0, 0));
                return b;
            }
        });

        listaContainer.add(scrollPane, BorderLayout.CENTER);
        return listaContainer;
    }

    private JPanel crearCardNoticia(String version, String titulo, String fecha, Color colorNeon) {
        JPanel card = new JPanel(new BorderLayout(10, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(2, 25, 44, 230));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(colorNeon);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel lblVer = new JLabel(version);
        lblVer.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
        lblVer.setForeground(colorNeon);
        
        JLabel lblTit = new JLabel(titulo.toUpperCase());
        lblTit.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
        lblTit.setForeground(Color.WHITE);

        JLabel lblDetalle = new JLabel("• Nodo verificado e indexado en el servidor.");
        lblDetalle.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        lblDetalle.setForeground(COLOR_TEXTO);

        infoPanel.add(lblVer);
        infoPanel.add(Box.createVerticalStrut(3)); 
        infoPanel.add(lblTit);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblDetalle);

        card.add(infoPanel, BorderLayout.CENTER);

        JPanel statusPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        statusPanel.setOpaque(false);

        JLabel lblFecha = new JLabel("📅 " + fecha, SwingConstants.RIGHT);
        lblFecha.setForeground(COLOR_TEXT_MUTED);
        lblFecha.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 11));

        JLabel lblPublicada = new JLabel("PUBLICADA", SwingConstants.CENTER);
        lblPublicada.setForeground(COLOR_CIAN);
        lblPublicada.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        lblPublicada.setBorder(BorderFactory.createLineBorder(COLOR_CIAN, 1));

        statusPanel.add(lblFecha);
        statusPanel.add(lblPublicada);
        card.add(statusPanel, BorderLayout.EAST);

        return card;
    }

    private JLabel crearEtiquetaForm(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
        label.setForeground(COLOR_CIAN);
        label.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JPanel crearBarraInferior() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);

        JButton btnVolver = new BotonCyberpunk("← VOLVER", COLOR_CIAN);
        btnVolver.setPreferredSize(new Dimension(120, 35));
        
        btnVolver.addActionListener(e -> {
            PanelAdmin panelAdmin = new PanelAdmin();
            panelAdmin.setVisible(true);
            this.dispose();
        });

        JLabel lblConsejo = new JLabel("Consejo: Mantén a la comunidad informada sobre cada mejora.", SwingConstants.CENTER);
        lblConsejo.setForeground(COLOR_TEXT_MUTED);
        lblConsejo.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 12));

        JButton btnAyuda = new BotonCyberpunk("AYUDA ?", COLOR_CIAN);
        btnAyuda.setPreferredSize(new Dimension(100, 35));
        btnAyuda.addActionListener(e -> JOptionPane.showMessageDialog(this, 
                "Terminal de Parches de BitSoul v1.0.\nPermite publicar notificaciones globales persistentes.", 
                "Módulo de Ayuda", JOptionPane.INFORMATION_MESSAGE));

        footer.add(btnVolver, BorderLayout.WEST);
        footer.add(lblConsejo, BorderLayout.CENTER);
        footer.add(btnAyuda, BorderLayout.EAST);

        return footer;
    }

    // --- 4. CONTROLADORES Y LOGICA DE CARGA (Funciones) ---
    private void seleccionarImagen() {
        JFileChooser selector = new JFileChooser();
        selector.setDialogTitle("Seleccionar Ilustración BitSoul");
        
        FileNameExtensionFilter filtro = new FileNameExtensionFilter("Archivos de Imagen (*.png, *.jpg)", "png", "jpg", "jpeg");
        selector.setFileFilter(filtro);
        
        int resultado = selector.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            imagenSeleccionada = selector.getSelectedFile();
            lblDrop.setText("✔ IMAGEN INYECTADA: " + imagenSeleccionada.getName().toUpperCase());
            lblDrop.setForeground(COLOR_CIAN);
        }
    }

    private void restaurarFormulario() {
        txtTitulo.setText("");
        txtCuerpo.setText("");
        imagenSeleccionada = null;
        lblDrop.setText("Arrastra una imagen o haz clic para seleccionar (Máx. 5MB)");
        lblDrop.setForeground(COLOR_TEXT_MUTED);
    }

    public void actualizarListaNoticias() {
        if (listaScrollContent == null) return;
        
        listaScrollContent.removeAll();
        listaScrollContent.add(Box.createVerticalStrut(15));
        
        List<NoticiaData> noticias = novedadesDAO.listarNoticias();

        if (noticias != null && !noticias.isEmpty()) {
            for (NoticiaData n : noticias) {
                String fechaStr = (n.getFechaPublicacion() != null) ? n.getFechaPublicacion().toString().substring(0, 10) : "Reciente";
                
                Color colorBorde = COLOR_CIAN;
                if (n.getColorHex() != null) {
                    try { 
                        colorBorde = Color.decode(n.getColorHex()); 
                    } catch (Exception e) { 
                        colorBorde = COLOR_CIAN; 
                    }
                }
                
                listaScrollContent.add(crearCardNoticia("POST-ID: " + n.getIdNoticia(), n.getTitulo(), fechaStr, colorBorde));
                listaScrollContent.add(Box.createVerticalStrut(15));
            }
        } else {
            JLabel lblVacio = new JLabel("NINGÚN REGISTRO EN EL FEED GLOBAL", SwingConstants.CENTER);
            lblVacio.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
            lblVacio.setForeground(COLOR_TEXT_MUTED);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            listaScrollContent.add(lblVacio);
        }

        listaScrollContent.revalidate();
        listaScrollContent.repaint();
    }

    private void ejecutarPublicacion() {
        String titulo = txtTitulo.getText().trim();
        String contenido = txtCuerpo.getText().trim();

        if (titulo.isEmpty() || titulo.equals("Ej: v1.2.0 - Firewall Fortificado")) {
            JOptionPane.showMessageDialog(this, "Debe ingresar un título válido antes de la transmisión.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (contenido.isEmpty() || contenido.equals("Describe los cambios, mejoras, correcciones y novedades...")) {
            JOptionPane.showMessageDialog(this, "El cuerpo del mensaje no puede estar vacío.", "Campos Incompletos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String hex = (Math.random() > 0.5) ? "#00F0FF" : "#FF0080";
        NoticiaData nuevaNoticia = new NoticiaData(titulo, contenido, hex);

        if (novedadesDAO.insertarNoticia(nuevaNoticia)) {
            JOptionPane.showMessageDialog(this, "¡Noticia inyectada en la red de BitSoul correctamente!", "Enlace Establecido", JOptionPane.INFORMATION_MESSAGE);
            restaurarFormulario();
            actualizarListaNoticias();
        } else {
            JOptionPane.showMessageDialog(this, "Fallo crítico al sincronizar con el nodo MySQL. Verifica tu conexión.", "Error de Servidor", JOptionPane.ERROR_MESSAGE);
        }
    }

    // --- 5. COMPONENTES ESTÉTICOS PERSONALIZADOS ---
    private static class PanelCyberpunk extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(COLOR_BG_PANEL);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            g2.dispose();
        }
        public PanelCyberpunk() { setOpaque(false); }
    }

    private static class BorderNeon extends AbstractBorder {
        private final Color color;
        public BorderNeon(Color color) { this.color = color; }
        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(x, y, width - 1, height - 1, 15, 15);
            g2.dispose();
        }
    }

    private static class BotonCyberpunk extends JButton {
        private final Color colorNeon;

        public BotonCyberpunk(String texto, Color colorNeon) {
            super(texto);
            this.colorNeon = colorNeon;
            setFont(new Font(Font.MONOSPACED, Font.BOLD, 13));
            setForeground(colorNeon); 
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isRollover()) {
                g2.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 60));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
            }

            g2.setColor(colorNeon);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            
            g2.setColor(colorNeon);
            g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);

            g2.dispose();
        }
    }

    private static class BotonControlVentana extends JButton {
        private final Color colorNeon;

        public BotonControlVentana(String simbolo, Color colorNeon) {
            super(simbolo);
            this.colorNeon = colorNeon;
            setPreferredSize(new Dimension(30, 22));
            setFont(new Font(Font.MONOSPACED, Font.BOLD, 12));
            setForeground(colorNeon);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isRollover()) {
                g2.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 45));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(colorNeon);
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 1);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Novedades().setVisible(true);
        });
    }
}