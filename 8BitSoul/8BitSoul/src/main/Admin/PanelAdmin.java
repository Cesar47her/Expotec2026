package main.Admin;

import main.Login.*;
import main.Admin.AdminCuenta.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import main.Admin.RevisionComentarios.*;
import main.Admin.Configuraciones.*;
import main.Util.ReproducirSonido;
import main.Admin.ActualizacionMusica.*;
import main.Admin.AgregarNovedades.*;
import main.Usuario.*;

public class PanelAdmin extends JFrame {

    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(242, 5, 203);
    public static final Color COLOR_CARD_BG = new Color(6, 16, 32, 180);
    public static final Color COLOR_BARRA_BG = new Color(10, 20, 38, 230);

    private FondoAdminPanel panelFondo;
    private JPanel barraSuperior;
    private JLabel lblTituloBarra, lblTitulo, lblSubtitulo;
    private JButton btnVolver, btnCerrar, btnMaximizar, btnMinimizar;

    private TarjetasAdmin card1, card2, card3, card4, card5, card6;

    private PanelInicio vistaPanelInicio = null; 
    private boolean mostrandoSubPanel = false;

    private int mouseX, mouseY;
    private boolean esMaximizado = false;
    private Rectangle dimensionesPrevias;

    public PanelAdmin() {
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
        registrarEventosRedimension();
        recalcularPosiciones();
    }

    private void configurarVentana() {
        setUndecorated(true);
        setSize(1200, 675);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);

        panelFondo = new FondoAdminPanel();
        panelFondo.setLayout(null);
        setContentPane(panelFondo);

        barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BARRA_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(COLOR_CYAN);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        barraSuperior.setLayout(null);
        barraSuperior.setOpaque(false);

        lblTituloBarra = new JLabel("⚡ BITSOUL SYSTEM PROTOCOL // ADMIN TERMINAL");
        lblTituloBarra.setForeground(new Color(0, 240, 255, 200));
        lblTituloBarra.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTituloBarra.setBounds(20, 0, 400, 40);
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
                if (!esMaximizado) {
                    setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });
        panelFondo.add(barraSuperior);
    }

    private void inicializarComponentes() {
        lblTitulo = new JLabel("INGRESAR CREDENCIALES");
        lblTitulo.setForeground(COLOR_CYAN);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelFondo.add(lblTitulo);

        lblSubtitulo = new JLabel("CONEXIÓN SEGURA // 8BIT SOUL DATABASE PROTOCOL");
        lblSubtitulo.setForeground(Color.WHITE);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelFondo.add(lblSubtitulo);

        btnCerrar = crearBotonControl("X", COLOR_MAGENTA);
        btnCerrar.addActionListener(e -> System.exit(0));
        barraSuperior.add(btnCerrar);

        btnMaximizar = crearBotonControl("⬜", COLOR_CYAN);
        btnMaximizar.addActionListener(e -> alternarMaximizacion());
        barraSuperior.add(btnMaximizar);

        btnMinimizar = crearBotonControl("_", COLOR_CYAN);
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        barraSuperior.add(btnMinimizar);

        btnVolver = new JButton("<-");
        btnVolver.setFont(new Font("Arial", Font.BOLD, 30));
        btnVolver.setForeground(new Color(120, 130, 145));
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnVolver.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnVolver.setForeground(COLOR_CYAN); }
            @Override
            public void mouseExited(MouseEvent e) { btnVolver.setForeground(new Color(120, 130, 145)); }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mostrandoSubPanel) {
                    conmutarSubPanelAdministrador(false);
                } else {
                    CyberpunkLogin login = new CyberpunkLogin();
                    login.setVisible(true);
                    dispose();
                }
            }
        });
        panelFondo.add(btnVolver);

        // Se corrigió agregando el parámetro 'tipoIcono' requerido por tu nuevo constructor
        card1 = new TarjetasAdmin("ADMINISTRADOR", "DE CUENTAS", "GESTIÓN TOTAL DE USUARIOS", COLOR_CYAN, "ADMIN");
        card2 = new TarjetasAdmin("ACTUALIZACION", "DE MUSICA", "SISTEMA DE MUSIC CORE", COLOR_CYAN, "ACTUALIZAR");
        card3 = new TarjetasAdmin("REVISIÓN DE", "COMENTARIOS", "MODERACIÓN TERMINAL", COLOR_CYAN, "REVISION");
        card4 = new TarjetasAdmin("TESTING", "", "DEBUG & GRAPH COMPILER", COLOR_MAGENTA, "TESTING");
        card5 = new TarjetasAdmin("AGREGAR", "NOVEDADES", "PATCH INJECTOR SYSTEM", COLOR_MAGENTA, "AGREGAR");
        card6 = new TarjetasAdmin("CONFIGURACIONES", "", "UI CUSTOMIZER ENGINE", COLOR_MAGENTA, "PERSONALIZAR");

        card1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                AdministradorCuentas AdminCuentas = new AdministradorCuentas();
                AdminCuentas.setVisible(true);
                setVisible(false); 
            }
        });

        card2.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PanelMusicaAdmin musicaAdmin = new PanelMusicaAdmin();
                musicaAdmin.setVisible(true);
                setVisible(false); 
            }
        });

        card3.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                PanelRevisionComentarios edicionMapas = new PanelRevisionComentarios();
                edicionMapas.setVisible(true);
                setVisible(false);
            }
        });
        
        card4.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                MenuUsuario edicionMapas = new MenuUsuario();
                edicionMapas.setVisible(true);
                setVisible(false);
            }
        });
        
        card5.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Novedades edicionMapas = new Novedades();
                edicionMapas.setVisible(true);
                setVisible(false);
            }
        });

        card6.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Window padre = SwingUtilities.getWindowAncestor(card6);
                ConfiguracionVenta ventanaConfig = new ConfiguracionVenta(padre);
                ventanaConfig.setVisible(true);
            }
        });

        panelFondo.add(card1);
        panelFondo.add(card2);
        panelFondo.add(card3);
        panelFondo.add(card4);
        panelFondo.add(card5);
        panelFondo.add(card6);
    }

    private void conmutarSubPanelAdministrador(boolean activarSubPanel) {
        this.mostrandoSubPanel = activarSubPanel;
        panelFondo.setOmitirCables(activarSubPanel);

        lblTitulo.setVisible(!activarSubPanel);
        lblSubtitulo.setVisible(!activarSubPanel);

        card1.setVisible(!activarSubPanel);
        card2.setVisible(!activarSubPanel);
        card3.setVisible(!activarSubPanel);
        card4.setVisible(!activarSubPanel);
        card5.setVisible(!activarSubPanel);
        card6.setVisible(!activarSubPanel);

        if (activarSubPanel) {
            if (vistaPanelInicio == null) {
                vistaPanelInicio = new PanelInicio();
                panelFondo.add(vistaPanelInicio);
            }
            vistaPanelInicio.setVisible(true);
            vistaPanelInicio.actualizarDatosPantalla();
        } else {
            if (vistaPanelInicio != null) {
                vistaPanelInicio.setVisible(false);
            }
        }

        recalcularPosiciones();
    }

    private JButton crearBotonControl(String texto, Color colorNeon) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Monospaced", Font.BOLD, 14));
        btn.setForeground(colorNeon);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void alternarMaximizacion() {
        if (esMaximizado) {
            setBounds(dimensionesPrevias);
            esMaximizado = false;
        } else {
            dimensionesPrevias = getBounds(); 
            GraphicsConfiguration gc = getGraphicsConfiguration();
            Rectangle boundsPantalla = gc.getBounds();
            Insets insetsSujetos = Toolkit.getDefaultToolkit().getScreenInsets(gc);

            int x = boundsPantalla.x + insetsSujetos.left;
            int y = boundsPantalla.y + insetsSujetos.top;
            int anchoUtil = boundsPantalla.width - (insetsSujetos.left + insetsSujetos.right);
            int altoUtil = boundsPantalla.height - (insetsSujetos.top + insetsSujetos.bottom);

            setBounds(x, y, anchoUtil, altoUtil);
            esMaximizado = true;
        }
        recalcularPosiciones();
    }

    private void registrarEventosRedimension() {
        this.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) { recalcularPosiciones(); }
        });
        ControladorBordesResizer resizer = new ControladorBordesResizer(this);
        addMouseListener(resizer);
        addMouseMotionListener(resizer);
    }

    private void recalcularPosiciones() {
        int W = getWidth();
        int H = getHeight();

        if (barraSuperior != null) {
            barraSuperior.setBounds(0, 0, W, 40);
            btnCerrar.setBounds(W - 50, 5, 45, 30);
            btnMaximizar.setBounds(W - 95, 5, 45, 30);
            btnMinimizar.setBounds(W - 140, 5, 45, 30);
        }

        if (mostrandoSubPanel && vistaPanelInicio != null) {
            vistaPanelInicio.setBounds(25, 45, W - 50, H - 110);
            btnVolver.setBounds(40, H - 65, 70, 45);
            return;
        }

        try {
            int tamTitulo = Math.max(20, W / 42);
            lblTitulo.setFont(new Font("Minecraft", Font.BOLD, tamTitulo));
            lblSubtitulo.setFont(new Font("Minecraft", Font.PLAIN, Math.max(10, W / 95)));
        } catch (Exception e) {
            lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 24));
        }

        if (lblTitulo != null) lblTitulo.setBounds(0, (int) (H * 0.12), W, 35);
        if (lblSubtitulo != null) lblSubtitulo.setBounds(0, (int) (H * 0.12) + 38, W, 20);
        if (btnVolver != null) btnVolver.setBounds(40, H - 80, 70, 50);

        int cardW = Math.max(130, W / 7);
        int cardH = Math.max(180, (int) (H / 3.2));

        int spacing = (W - (6 * cardW)) / 7;
        int altoFilaAlta = (int) (H * 0.26);
        int altoFilaBaja = (int) (H * 0.42);

        if (card1 != null) card1.setBounds(spacing, altoFilaBaja, cardW, cardH);
        if (card2 != null) card2.setBounds(spacing * 2 + cardW, altoFilaAlta, cardW, cardH);
        if (card3 != null) card3.setBounds(spacing * 3 + (cardW * 2), altoFilaAlta, cardW, cardH);
        if (card4 != null) card4.setBounds(spacing * 4 + (cardW * 3), altoFilaAlta, cardW, cardH);
        if (card5 != null) card5.setBounds(spacing * 5 + (cardW * 4), altoFilaAlta, cardW, cardH);
        if (card6 != null) card6.setBounds(spacing * 6 + (cardW * 5), altoFilaBaja, cardW, cardH);

        panelFondo.repaint();
    }

    private class ControladorBordesResizer extends MouseAdapter {
        private final JFrame frame;
        private final int DISTANCIA_DETECCION = 6;
        private int direccionResizado = 0;

        public ControladorBordesResizer(JFrame frame) { this.frame = frame; }

        @Override
        public void mouseMoved(MouseEvent e) {
            int x = e.getX(); int y = e.getY();
            int w = frame.getWidth(); int h = frame.getHeight();

            if (x > w - DISTANCIA_DETECCION && y > h - DISTANCIA_DETECCION) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
                direccionResizado = Cursor.SE_RESIZE_CURSOR;
            } else if (x > w - DISTANCIA_DETECCION) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
                direccionResizado = Cursor.E_RESIZE_CURSOR;
            } else if (y > h - DISTANCIA_DETECCION) {
                frame.setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
                direccionResizado = Cursor.S_RESIZE_CURSOR;
            } else {
                frame.setCursor(Cursor.getDefaultCursor());
                direccionResizado = 0;
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (direccionResizado == 0) return;
            int x = e.getX(); int y = e.getY();
            int w = frame.getWidth(); int h = frame.getHeight();

            if (direccionResizado == Cursor.E_RESIZE_CURSOR) {
                frame.setSize(Math.max(x, frame.getMinimumSize().width), h);
            } else if (direccionResizado == Cursor.S_RESIZE_CURSOR) {
                frame.setSize(w, Math.max(y, frame.getMinimumSize().height));
            } else if (direccionResizado == Cursor.SE_RESIZE_CURSOR) {
                frame.setSize(Math.max(x, frame.getMinimumSize().width), Math.max(y, frame.getMinimumSize().height));
            }
        }
    }
}

class FondoAdminPanel extends JPanel {
    private Image imagenFondo;
    private boolean omitirCables = false;

    public FondoAdminPanel() {
        try {
            URL url = getClass().getResource("/imagenes/FondoMAdmin.png");
            if (url != null) {
                imagenFondo = new ImageIcon(url).getImage();
            }
        } catch (Exception e) {}
    }

    public void setOmitirCables(boolean omitir) {
        this.omitirCables = omitir;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int W = getWidth();
        int H = getHeight();

        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, W, H, this);
        } else {
            g2d.setColor(new Color(10, 16, 28));
            g2d.fillRect(0, 0, W, H);
        }

        g2d.setColor(PanelAdmin.COLOR_CYAN);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.drawRect(15, 40, W - 30, H - 55);

        if (omitirCables) {
            g2d.dispose();
            return;
        }

        int targetX = W / 2;
        int targetY = (int) (H * 0.725);

        float[] patronTrazado = {5.0f, 4.0f};
        g2d.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, patronTrazado, 0.0f));

        int cardW = Math.max(130, W / 7);
        int spacing = (W - (6 * cardW)) / 7;

        int yFilaAlta = (int) (H * 0.26) + (int) (H / 3.2);
        int yFilaBaja = (int) (H * 0.42) + (int) (H / 3.2);

        g2d.setColor(new Color(0, 240, 255, 160));
        g2d.drawLine(spacing + (cardW / 2), yFilaBaja, targetX, targetY);
        g2d.drawLine(spacing * 2 + cardW + (cardW / 2), yFilaAlta, targetX, targetY);
        g2d.drawLine(spacing * 3 + (cardW * 2) + (cardW / 2), yFilaAlta, targetX, targetY);

        g2d.setColor(PanelAdmin.COLOR_MAGENTA);
        g2d.drawLine(spacing * 4 + (cardW * 3) + (cardW / 2), yFilaAlta, targetX, targetY);
        g2d.drawLine(spacing * 5 + (cardW * 4) + (cardW / 2), yFilaAlta, targetX, targetY);
        g2d.drawLine(spacing * 6 + (cardW * 5) + (cardW / 2), yFilaBaja, targetX, targetY);

        g2d.dispose();
    }
}