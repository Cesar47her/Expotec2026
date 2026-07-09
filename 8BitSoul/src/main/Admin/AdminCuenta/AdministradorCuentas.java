package main.Admin.AdminCuenta;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import main.Admin.*;
import main.Util.WindowManager;

public class AdministradorCuentas extends JFrame {

    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(255, 0, 127);
    public static final Color BG_DARK = new Color(5, 7, 22);
    public static final Color CYAN_NEON = new Color(0, 240, 255);
    public static final Color PINK_NEON = new Color(255, 0, 127);
    public static final Color TEXT_WHITE = new Color(240, 240, 245);
    public static final Color TEXT_MUTED = new Color(130, 140, 160);
    public static final Color COLOR_BARRA_BG = new Color(10, 20, 38, 230);

    private JPanel containerPanel;
    private CardLayout cardLayout;
    private Image imagenFondo;
    private JPanel currentSelectedButton = null;

    // Variables de la barra superior y movimiento de ventana
    private JPanel barraSuperior;
    private JLabel lblTituloBarra;
    private JButton btnCerrar, btnMaximizar, btnMinimizar;
    private int mouseX, mouseY;
    private boolean esMaximizado = false;
    private Rectangle dimensionesPrevias;

    // Variables del menú retráctil
    private boolean menuColapsado = false;
    private JPanel sidebar;
    private JPanel logoPanel;
    private JLabel toggleButton;
    private JLabel btnRegresar; 

    private List<JPanel> listaBotones = new ArrayList<>();
    private List<JLabel> listaEtiquetasSeccion = new ArrayList<>();
    private List<Component> ordenNavegacionTeclado = new ArrayList<>();

    private JPanel subMenuCrud;
    private JPanel subMenuCR;
    private JPanel subMenuRU;
    private JPanel subMenuReadOnly;
    
    // CORRECCIÓN: Guardar la referencia al panel administrador padre
    private final PanelAdmin panelAdminPadre;

    public AdministradorCuentas() {
        this(null);
    }

    // CORRECCIÓN: Nuevo constructor que recibe y almacena el frame original
    public AdministradorCuentas(PanelAdmin panelAdminPadre) {
        this.panelAdminPadre = panelAdminPadre;
        
        setUndecorated(true);
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        setTitle("BIT SOUL - Cyberpunk Dashboard");
        setSize(1200, 675); 
        setMinimumSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(true);

        WindowManager.getInstance().register(this);

        imagenFondo = new ImageIcon("src/imagenes/FondoCrud.png").getImage();

        JPanel mainBackground = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                if (imagenFondo != null && imagenFondo.getWidth(null) > 0) {
                    int imgW = imagenFondo.getWidth(this);
                    int imgH = imagenFondo.getHeight(this);
                    double escala = Math.max((double) getWidth() / imgW, (double) getHeight() / imgH);
                    int anchoEscalado = (int) (imgW * escala);
                    int altoEscalado = (int) (imgH * escala);
                    int x = (getWidth() - anchoEscalado) / 2;
                    int y = (getHeight() - altoEscalado) / 2;
                    g2d.drawImage(imagenFondo, x, y, anchoEscalado, altoEscalado, this);
                } else {
                    GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(15, 10, 30));
                    g2d.setPaint(gp);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainBackground.setName("mainBackground");
        mainBackground.setLayout(new BorderLayout());
        setContentPane(mainBackground);

        construirBarraSuperior();
        mainBackground.add(barraSuperior, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        containerPanel = new JPanel(cardLayout);
        containerPanel.setOpaque(false);

        // Inyección real de tus subpaneles
        containerPanel.add(new PanelInicio(), "INICIO");
        containerPanel.add(new PanelUsuario(), "USUARIO");
        containerPanel.add(new PanelPerfil(), "PERFIL");
        containerPanel.add(new PanelConfiguracion(), "CONFIGURACION");
        containerPanel.add(new PanelNovedades(), "NOVEDADES");
        containerPanel.add(new PanelHistorial(), "HISTORIAL");
        containerPanel.add(new PanelInventario(), "INVENTARIO");
        containerPanel.add(new PanelAyuda(), "AYUDA");
        containerPanel.add(new PanelBilletera(), "BILLETERA");
        containerPanel.add(new PanelEquipamiento(), "EQUIPAMIENTO");
        containerPanel.add(new PanelProgreso(), "PROGRESO");
        containerPanel.add(new PanelRol(), "ROL");
        containerPanel.add(new PanelTipoItem(), "TIPO DE ÍTEM");
        containerPanel.add(new PanelNivel(), "NIVEL");
        containerPanel.add(new PanelDificultad(), "DIFICULTAD");
        containerPanel.add(new PanelEstadoSolicitud(), "ESTADO SOLICITUD");

        sidebar = buildSidebarMenu();

        mainBackground.add(sidebar, BorderLayout.WEST);
        mainBackground.add(containerPanel, BorderLayout.CENTER);

        actualizarOrdenNavegacion();
        configurarKeyBindings();

        SwingUtilities.invokeLater(() -> {
            if (!ordenNavegacionTeclado.isEmpty()) {
                ordenNavegacionTeclado.get(0).requestFocusInWindow();
            }
            recalcularBotonesControl(); 
        });
    }

    private void construirBarraSuperior() {
        barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BARRA_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        barraSuperior.setLayout(null);
        barraSuperior.setPreferredSize(new Dimension(getWidth(), 40));
        barraSuperior.setOpaque(false);

        lblTituloBarra = new JLabel("⚡ BITSOUL SYSTEM PROTOCOL // ACCOUNTS MANAGEMENT TERMINAL");
        lblTituloBarra.setForeground(new Color(0, 240, 255, 200));
        lblTituloBarra.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTituloBarra.setBounds(20, 0, 600, 40);
        barraSuperior.add(lblTituloBarra);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    alternarMaximizacion();
                }
            }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (esMaximizado) {
                    alternarMaximizacion();
                    mouseX = getWidth() / 2; 
                }
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });

        btnCerrar = crearBotonControl("X", COLOR_MAGENTA);
        btnCerrar.addActionListener(e -> System.exit(0));
        barraSuperior.add(btnCerrar);

        btnMaximizar = crearBotonControl("⬜", COLOR_CYAN);
        btnMaximizar.addActionListener(e -> alternarMaximizacion());
        barraSuperior.add(btnMaximizar);

        btnMinimizar = crearBotonControl("_", COLOR_CYAN);
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        barraSuperior.add(btnMinimizar);

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                recalcularBotonesControl();
            }
        });
    }

    private JButton crearBotonControl(String texto, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(TEXT_MUTED);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setForeground(colorHover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setForeground(TEXT_MUTED);
            }
        });
        return btn;
    }

    private void recalcularBotonesControl() {
        int w = getWidth();
        btnCerrar.setBounds(w - 45, 5, 35, 30);
        btnMaximizar.setBounds(w - 85, 5, 35, 30);
        btnMinimizar.setBounds(w - 125, 5, 35, 30);
    }

    private void alternarMaximizacion() {
        if (esMaximizado) {
            if (dimensionesPrevias != null) {
                setBounds(dimensionesPrevias);
            } else {
                setSize(1200, 675);
                setLocationRelativeTo(null);
            }
            esMaximizado = false;
            btnMaximizar.setText("⬜");
        } else {
            dimensionesPrevias = getBounds();
            Rectangle boundsMaximo = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            setBounds(boundsMaximo);
            esMaximizado = true;
            btnMaximizar.setText("🗗");
        }
        recalcularBotonesControl();
        revalidate();
        repaint();
    }

    private JPanel buildSidebarMenu() {
        JPanel side = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(3, 5, 16, 235));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2d.dispose();
            }
        };
        side.setOpaque(false);
        side.setPreferredSize(new Dimension(240, 768)); 
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(280, 85)); 

        JPanel subHeaderLeft = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        subHeaderLeft.setOpaque(false);

        toggleButton = new JLabel(" ☰ ", SwingConstants.CENTER);
        toggleButton.setFont(new Font("Dialog", Font.BOLD, 20));
        toggleButton.setForeground(CYAN_NEON);
        toggleButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        toggleButton.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 10));
        toggleButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toggleMenu();
            }
        });
        subHeaderLeft.add(toggleButton);

        btnRegresar = new JLabel(" <- ", SwingConstants.CENTER);
        btnRegresar.setFont(new Font("Dialog", Font.BOLD, 18));
        btnRegresar.setForeground(PINK_NEON);
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 15));
        btnRegresar.setToolTipText("Regresar al Panel de Admin");
        btnRegresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                SwingUtilities.invokeLater(() -> {
                    // CORRECCIÓN: Si el panel padre existe, lo volvemos a mostrar en lugar de crear uno nuevo vacío
                    if (panelAdminPadre != null) {
                        panelAdminPadre.setVisible(true);
                    } else {
                        PanelAdmin panelAdmin = new PanelAdmin();
                        panelAdmin.setVisible(true);
                    }
                    dispose();
                });
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegresar.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRegresar.setForeground(PINK_NEON);
            }
        });
        subHeaderLeft.add(btnRegresar);
        headerPanel.add(subHeaderLeft, BorderLayout.WEST);

        logoPanel = new JPanel() {
            private Image imgLogo = new ImageIcon("src/imagenes/LogoCompleto.png").getImage();
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (imgLogo != null && imgLogo.getWidth(null) > 0 && !menuColapsado) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                    double ratio = Math.min((double) (getWidth() - 10) / imgLogo.getWidth(this), (double) (getHeight() - 20) / imgLogo.getHeight(this));
                    int targetW = (int) (imgLogo.getWidth(this) * ratio);
                    int targetH = (int) (imgLogo.getHeight(this) * ratio);
                    g2d.drawImage(imgLogo, 10, (getHeight() - targetH) / 2, targetW, targetH, this);
                }
            }
        };
        logoPanel.setOpaque(false);
        headerPanel.add(logoPanel, BorderLayout.CENTER);
        side.add(headerPanel);
        side.add(Box.createRigidArea(new Dimension(0, 10)));

        JPanel btnInicio = createMenuButton("🏠", "INICIO", "INICIO");
        side.add(btnInicio);
        currentSelectedButton = btnInicio;
        ((JLabel) btnInicio.getComponent(0)).setForeground(CYAN_NEON);
        side.add(Box.createRigidArea(new Dimension(0, 15)));

        JLabel lblCrud = createSectionLabel("CRUD GESTIÓN  ▼", "C-G  ▼", "CRUD GESTIÓN");
        side.add(lblCrud);
        subMenuCrud = createSubMenuContainer();
        subMenuCrud.add(createMenuButton("👤", "USUARIO", "USUARIO"));
        subMenuCrud.add(createMenuButton("🖼️", "PERFIL", "PERFIL"));
        subMenuCrud.add(createMenuButton("⚙️", "CONFIGURACIÓN", "CONFIGURACION"));
        subMenuCrud.add(createMenuButton("📄", "NOVEDADES", "NOVEDADES"));
        side.add(subMenuCrud);
        configurarDesplegable(lblCrud, subMenuCrud, "CRUD GESTIÓN");

        JLabel lblCR = createSectionLabel("CREAR Y LEER (C-R)  ▼", "C-R  ▼", "CREAR Y LEER (C-R)");
        side.add(lblCR);
        subMenuCR = createSubMenuContainer();
        subMenuCR.add(createMenuButton("🛒", "HISTORIAL DE COMPRA", "HISTORIAL"));
        subMenuCR.add(createMenuButton("📦", "INVENTARIO", "INVENTARIO"));
        subMenuCR.add(createMenuButton("❓", "SOLICITUD DE AYUDA", "AYUDA"));
        side.add(subMenuCR);
        configurarDesplegable(lblCR, subMenuCR, "CREAR Y LEER (C-R)");

        JLabel lblRU = createSectionLabel("LEER ACTUALIZAR  ▼", "R-U  ▼", "LEER ACTUALIZAR");
        side.add(lblRU);
        subMenuRU = createSubMenuContainer();
        subMenuRU.add(createMenuButton("💳", "BILLETERA", "BILLETERA"));
        subMenuRU.add(createMenuButton("⚔️", "EQUIPAMIENTO", "EQUIPAMIENTO"));
        subMenuRU.add(createMenuButton("📈", "PROGRESO", "PROGRESO"));
        side.add(subMenuRU);
        configurarDesplegable(lblRU, subMenuRU, "LEER ACTUALIZAR");

        JLabel lblReadOnly = createSectionLabel("READ ONLY  ▼", "R-O  ▼", "READ ONLY");
        side.add(lblReadOnly);
        subMenuReadOnly = createSubMenuContainer();
        subMenuReadOnly.add(createMenuButton("👑", "ROL", "ROL"));
        subMenuReadOnly.add(createMenuButton("💎", "TIPO DE ÍTEM", "TIPO DE ÍTEM"));
        subMenuReadOnly.add(createMenuButton("📊", "NIVEL", "NIVEL"));
        subMenuReadOnly.add(createMenuButton("🎯", "DIFICULTAD", "DIFICULTAD"));
        subMenuReadOnly.add(createMenuButton("📝", "ESTADO SOLICITUD", "ESTADO SOLICITUD"));
        side.add(subMenuReadOnly);
        configurarDesplegable(lblReadOnly, subMenuReadOnly, "READ ONLY");

        return side;
    }

    private void configurarDesplegable(JLabel label, JPanel subMenu, String nombreBase) {
        label.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                alternarSubMenu(label, subMenu, nombreBase);
                label.requestFocusInWindow();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                label.setForeground(Color.WHITE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!label.hasFocus()) {
                    label.setForeground(PINK_NEON);
                }
            }
        });
    }

    private void alternarSubMenu(JLabel label, JPanel subMenu, String nombreBase) {
        if (menuColapsado) return;
        boolean estaVisible = subMenu.isVisible();
        subMenu.setVisible(!estaVisible);
        label.setText(nombreBase + (!estaVisible ? "  ▲" : "  ▼"));
        actualizarOrdenNavegacion();
        sidebar.revalidate();
        sidebar.repaint();
    }

    private JPanel createSubMenuContainer() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        return panel;
    }

    private JLabel createSectionLabel(String textoLargo, String textoCorto, String nombreBase) {
        JLabel label = new JLabel(textoLargo) {
            @Override
            public void setText(String text) {
                putClientProperty("largo", text.contains("▲") || text.contains("▼") ? text : textoLargo);
                putClientProperty("corto", textoCorto);
                super.setText(text);
            }
        };
        label.setFocusable(true);
        label.putClientProperty("largo", textoLargo);
        label.putClientProperty("corto", textoCorto);
        label.putClientProperty("nombreBase", nombreBase);
        label.setForeground(PINK_NEON);
        label.setFont(new Font("Dialog", Font.BOLD, 11));
        label.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        label.setMaximumSize(new Dimension(280, 30));
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));

        label.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { label.setForeground(Color.WHITE); }
            @Override
            public void focusLost(FocusEvent e) { label.setForeground(PINK_NEON); }
        });
        listaEtiquetasSeccion.add(label);
        return label;
    }

    private JPanel createMenuButton(String icono, String texto, String targetCard) {
        JPanel btn = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentSelectedButton == this || hasFocus()) {
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    int w = getWidth(), h = getHeight(), cut = 8;
                    Path2D path = new Path2D.Double();
                    path.moveTo(10, 2);
                    path.lineTo(w - cut - 10, 2);
                    path.lineTo(w - 10, cut);
                    path.lineTo(w - 10, h - cut);
                    path.lineTo(w - cut - 10, h - 2);
                    path.lineTo(10, h - 2);
                    path.closePath();

                    g2d.setColor(new Color(255, 0, 127, 25));
                    g2d.fill(path);
                    g2d.setColor(PINK_NEON);
                    g2d.setStroke(new BasicStroke(1.2f));
                    g2d.draw(path);
                    g2d.dispose();
                }
            }
        };
        btn.setFocusable(true);
        btn.setOpaque(false);
        btn.setMaximumSize(new Dimension(280, 38));
        btn.setPreferredSize(new Dimension(280, 38));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));

        btn.putClientProperty("icono", icono);
        btn.putClientProperty("textoFull", icono + "  " + texto);
        btn.putClientProperty("targetCard", targetCard);

        JLabel label = new JLabel(icono + "  " + texto);
        label.setForeground(TEXT_MUTED);
        label.setFont(new Font("Dialog", Font.PLAIN, 12));
        btn.add(label, BorderLayout.WEST);

        btn.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (currentSelectedButton != btn) label.setForeground(Color.WHITE);
                btn.repaint();
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (currentSelectedButton != btn) label.setForeground(TEXT_MUTED);
                btn.repaint();
            }
        });

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                if (currentSelectedButton != btn) label.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                if (currentSelectedButton != btn && !btn.hasFocus()) label.setForeground(TEXT_MUTED);
            }
            @Override
            public void mousePressed(MouseEvent e) {
                btn.requestFocusInWindow();
                seleccionarBoton(btn);
            }
        });

        listaBotones.add(btn);
        return btn;
    }

    private void seleccionarBoton(JPanel btn) {
        if (currentSelectedButton != null && currentSelectedButton != btn) {
            JPanel prev = currentSelectedButton;
            ((JLabel) prev.getComponent(0)).setForeground(TEXT_MUTED);
            prev.repaint();
        }
        currentSelectedButton = btn;
        JLabel lbl = (JLabel) btn.getComponent(0);
        lbl.setForeground(CYAN_NEON);
        btn.repaint();

        String targetCard = (String) btn.getClientProperty("targetCard");
        cardLayout.show(containerPanel, targetCard);
    }

    private void toggleMenu() {
        menuColapsado = !menuColapsado;

        if (menuColapsado) {
            sidebar.setPreferredSize(new Dimension(70, 768));
            toggleButton.setText(" ☰ ");
            btnRegresar.setVisible(false);

            subMenuCrud.setVisible(false);
            subMenuCR.setVisible(false);
            subMenuRU.setVisible(false);
            subMenuReadOnly.setVisible(false);

            for (JPanel btn : listaBotones) {
                JLabel lbl = (JLabel) btn.getComponent(0);
                lbl.setText((String) btn.getClientProperty("icono"));
                btn.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));
            }
            for (JLabel section : listaEtiquetasSeccion) {
                section.setText((String) section.getClientProperty("corto"));
                section.setBorder(BorderFactory.createEmptyBorder(8, 15, 5, 0));
            }
        } else {
            sidebar.setPreferredSize(new Dimension(240, 768));
            toggleButton.setText(" ☰ ");
            btnRegresar.setVisible(true);

            for (JPanel btn : listaBotones) {
                JLabel lbl = (JLabel) btn.getComponent(0);
                lbl.setText((String) btn.getClientProperty("textoFull"));
                btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
            }
            for (JLabel section : listaEtiquetasSeccion) {
                section.setText((String) section.getClientProperty("largo"));
                section.setBorder(BorderFactory.createEmptyBorder(8, 20, 5, 0));
            }
        }

        actualizarOrdenNavegacion();
        sidebar.revalidate();
        sidebar.repaint();
    }

    private void actualizarOrdenNavegacion() {
        ordenNavegacionTeclado.clear();
        for (Component comp : sidebar.getComponents()) {
            if (comp instanceof JPanel && listaBotones.contains(comp)) {
                if (comp.isVisible()) ordenNavegacionTeclado.add(comp);
            } else if (comp instanceof JLabel && listaEtiquetasSeccion.contains(comp)) {
                if (comp.isVisible()) ordenNavegacionTeclado.add(comp);
            } else if (comp instanceof JPanel && !comp.isOpaque()) {
                JPanel subContainer = (JPanel) comp;
                if (subContainer.isVisible()) {
                    for (Component subComp : subContainer.getComponents()) {
                        if (subComp instanceof JPanel && listaBotones.contains(subComp)) {
                            ordenNavegacionTeclado.add(subComp);
                        }
                    }
                }
            }
        }
    }

    private void configurarKeyBindings() {
        JComponent root = getRootPane();
        InputMap im = root.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = root.getActionMap();

        im.put(KeyStroke.getKeyStroke("DOWN"), "navegarAbajo");
        am.put("navegarAbajo", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { moverFocoTeclado(1); }
        });

        im.put(KeyStroke.getKeyStroke("UP"), "navegarArriba");
        am.put("navegarArriba", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { moverFocoTeclado(-1); }
        });

        im.put(KeyStroke.getKeyStroke("RIGHT"), "abrirOExpandir");
        am.put("abrirOExpandir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuColapsado) {
                    toggleMenu();
                } else {
                    Component actual = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (actual instanceof JLabel && listaEtiquetasSeccion.contains(actual)) {
                        gestionarSubmenuPorTeclado((JLabel) actual, true);
                    }
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("LEFT"), "cerrarOColapsar");
        am.put("cerrarOColapsar", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!menuColapsado) {
                    Component actual = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                    if (actual instanceof JLabel && listaEtiquetasSeccion.contains(actual)) {
                        gestionarSubmenuPorTeclado((JLabel) actual, false);
                    } else {
                        toggleMenu();
                    }
                }
            }
        });

        im.put(KeyStroke.getKeyStroke("ENTER"), "ejecutarEnter");
        am.put("ejecutarEnter", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Component actual = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                if (actual instanceof JLabel && listaEtiquetasSeccion.contains(actual)) {
                    gestionarSubmenuPorTeclado((JLabel) actual, null);
                }
            }
        });
    }

    private void moverFocoTeclado(int direccion) {
        if (ordenNavegacionTeclado.isEmpty()) return;

        Component focoActual = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        int indexActual = ordenNavegacionTeclado.indexOf(focoActual);

        if (indexActual == -1 && currentSelectedButton != null) {
            indexActual = ordenNavegacionTeclado.indexOf(currentSelectedButton);
        }

        int siguienteIndex = indexActual + direccion;
        if (siguienteIndex >= ordenNavegacionTeclado.size()) siguienteIndex = 0;
        if (siguienteIndex < 0) siguienteIndex = ordenNavegacionTeclado.size() - 1;

        Component objetivo = ordenNavegacionTeclado.get(siguienteIndex);
        if (objetivo != null) {
            objetivo.requestFocusInWindow();
            if (objetivo instanceof JPanel && listaBotones.contains(objetivo)) {
                seleccionarBoton((JPanel) objetivo);
            }
        }
    }

    private void gestionarSubmenuPorTeclado(JLabel label, Boolean forzarAbrir) {
        JPanel subMenuObjetivo = null;
        String nombre = (String) label.getClientProperty("nombreBase");
        if ("CRUD GESTIÓN".equals(nombre)) subMenuObjetivo = subMenuCrud;
        if ("CREAR Y LEER (C-R)".equals(nombre)) subMenuObjetivo = subMenuCR;
        if ("LEER ACTUALIZAR".equals(nombre)) subMenuObjetivo = subMenuRU;
        if ("READ ONLY".equals(nombre)) subMenuObjetivo = subMenuReadOnly;

        if (subMenuObjetivo != null) {
            boolean visible = subMenuObjetivo.isVisible();
            if (forzarAbrir == null || (forzarAbrir && !visible) || (!forzarAbrir && visible)) {
                alternarSubMenu(label, subMenuObjetivo, nombre);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Error Look and Feel");
            }
            new AdministradorCuentas().setVisible(true);
        });
    }
}