package main.Usuario;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;
import main.Admin.Configuraciones.*;
import main.Util.WindowManager;

public class MenuUsuario extends JFrame {

    private final Color COLOR_FONDO_NEGRO = Color.BLACK;
    private final Color COLOR_CIAN = new Color(0, 240, 255);
    private final Color COLOR_MAGENTA = new Color(242, 5, 203);
    private final Color COLOR_TEXTO_BASE = new Color(140, 165, 195);
    private final Color COLOR_GLITCH_TEXT = new Color(255, 0, 90);

    private final String[] OPCIONES = {"AYUDA", "NOVEDADES", "REGRESAR", "JUGAR", "PERSONALIZACIÓN", "CONFIGURACIÓN", "TIENDA"};
    private JButton[] botones = new JButton[OPCIONES.length];

    private int indiceSeleccionado = 3;

    private int xMenuBase = 55;
    private int yMenuInicio = 355;
    private int espaciadoY = 42;
    private final int ANCHO_BOTON = 340;
    private final int ALTO_BOTON = 38;

    private boolean errorActivo = false;
    private int glitchDuration = 0;
    private final Random random = new Random();
    private int mouseX, mouseY;
    private boolean esPantallaCompleta = false;
    private Rectangle dimensionesPrevias;

    private JPanel barraSuperior;
    private JButton btnCerrar, btnMaximizar, btnMinimizar;
    private PanelFondo panelPrincipal;

    private final int idUsuarioActual;

    // --- SOLUCIÓN: CONSTRUCTOR VACÍO AGREGADO PARA EVITAR ERRORES DE LLAMADO ---
    public MenuUsuario() {

        this(0); // Llama al constructor principal enviando un ID 0 por defecto
    }

    // CONSTRUCTOR PRINCIPAL: Exige el ID del usuario logueado
    public MenuUsuario(int idUsuarioLogueado) {
        this.idUsuarioActual = idUsuarioLogueado;

        setUndecorated(true);
        setSize(1200, 675);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(true);
        WindowManager.getInstance().register(this);
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        dimensionesPrevias = getBounds();

        panelPrincipal = new PanelFondo();
        panelPrincipal.setLayout(null);
        setContentPane(panelPrincipal);

        // --- BARRA SUPERIOR PERSONALIZADA ---
        barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(8, 12, 24));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(COLOR_CIAN);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        barraSuperior.setLayout(null);
        panelPrincipal.add(barraSuperior);

        JLabel lblTitulo = new JLabel(" 8-BIT SOUL // CORE SYSTEM MENU V.2.6");
        lblTitulo.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblTitulo.setForeground(COLOR_TEXTO_BASE);
        lblTitulo.setBounds(15, 0, 500, 30);
        barraSuperior.add(lblTitulo);

        btnCerrar = crearBotonControl("[X]", COLOR_MAGENTA);
        btnCerrar.addActionListener(e -> System.exit(0));
        barraSuperior.add(btnCerrar);

        btnMaximizar = crearBotonControl("[▢]", COLOR_CIAN);
        btnMaximizar.addActionListener(e -> alternarTamañoPantalla());
        barraSuperior.add(btnMaximizar);

        btnMinimizar = crearBotonControl("[_]", COLOR_CIAN);
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        barraSuperior.add(btnMinimizar);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!esPantallaCompleta) {
                    mouseX = e.getX();
                    mouseY = e.getY();
                }
            }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (!esPantallaCompleta) {
                    setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });

        // --- INICIALIZACIÓN DE BOTONES ---
        for (int i = 0; i < OPCIONES.length; i++) {
            final int index = i;
            botones[i] = new JButton(OPCIONES[i]);
            botones[i].setContentAreaFilled(false);
            botones[i].setBorderPainted(false);
            botones[i].setFocusable(false);
            botones[i].setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            botones[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    indiceSeleccionado = index;
                    actualizarEstadoVisual();
                    repaint();
                }

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        ejecutarAccion(indiceSeleccionado);
                    }
                }
            });
            panelPrincipal.add(botones[i]);
        }

        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                recalcularDimensiones();
            }
        });

        recalcularDimensiones();

        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        if (indiceSeleccionado > 3) {
                            indiceSeleccionado--;
                        } else if (indiceSeleccionado == 3) {
                            indiceSeleccionado = OPCIONES.length - 1;
                        }
                        actualizarEstadoVisual();
                        repaint();
                        break;
                    case KeyEvent.VK_DOWN:
                        if (indiceSeleccionado < OPCIONES.length - 1) {
                            indiceSeleccionado++;
                        } else if (indiceSeleccionado == OPCIONES.length - 1) {
                            indiceSeleccionado = 3;
                        }
                        actualizarEstadoVisual();
                        repaint();
                        break;
                    case KeyEvent.VK_ENTER:
                        ejecutarAccion(indiceSeleccionado);
                        break;
                }
            }
        });
    }

    private JButton crearBotonControl(String texto, Color colorNeon) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setForeground(colorNeon);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private void alternarTamañoPantalla() {
        GraphicsConfiguration config = getGraphicsConfiguration();
        Insets insets = Toolkit.getDefaultToolkit().getScreenInsets(config);
        Rectangle boundsPantalla = config.getBounds();

        if (!esPantallaCompleta) {
            dimensionesPrevias = getBounds();
            int x = boundsPantalla.x + insets.left;
            int y = boundsPantalla.y + insets.top;
            int ancho = boundsPantalla.width - (insets.left + insets.right);
            int alto = boundsPantalla.height - (insets.top + insets.bottom);

            setBounds(x, y, ancho, alto);
            esPantallaCompleta = true;
            btnMaximizar.setText("[⧈]");
        } else {
            setBounds(dimensionesPrevias);
            esPantallaCompleta = false;
            btnMaximizar.setText("[▢]");
        }
        recalcularDimensiones();
    }

    private void recalcularDimensiones() {
        int ancho = getWidth();
        int alto = getHeight();

        if (barraSuperior != null) {
            barraSuperior.setBounds(0, 0, ancho, 30);
            btnCerrar.setBounds(ancho - 60, 0, 60, 30);
            btnMaximizar.setBounds(ancho - 110, 0, 50, 30);
            btnMinimizar.setBounds(ancho - 160, 0, 50, 30);
        }

        xMenuBase = 55;

        if (alto > 600) {
            yMenuInicio = alto - 230;
            espaciadoY = 44;
        } else {
            yMenuInicio = 355;
            espaciadoY = 40;
        }

        if (panelPrincipal != null) {
            panelPrincipal.ajustarPosicionBotonesPorError(0, 0);
        }
        actualizarEstadoVisual();
    }

    private boolean validarFuente(String nombreFuente) {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        for (String f : ge.getAvailableFontFamilyNames()) {
            if (f.equalsIgnoreCase(nombreFuente)) {
                return true;
            }
        }
        return false;
    }

    private void actualizarEstadoVisual() {
        String fuenteMenu = validarFuente("Minecraft") ? "Minecraft" : "Monospaced";

        int tamanoJugar = (getHeight() > 600) ? 27 : 25;
        int tamanoOtros = (getHeight() > 600) ? 21 : 19;

        for (int i = 0; i < botones.length; i++) {
            if (botones[i] == null) {
                continue;
            }

            if (i == 3) {
                botones[i].setFont(new Font(fuenteMenu, Font.BOLD, tamanoJugar));
            } else {
                botones[i].setFont(new Font(fuenteMenu, Font.BOLD, tamanoOtros));
            }

            if (i == indiceSeleccionado) {
                botones[i].setForeground(COLOR_CIAN);
                if (errorActivo && random.nextInt(100) < 35) {
                    botones[i].setText(generarTextoGlitch(OPCIONES[i]));
                    botones[i].setForeground(COLOR_GLITCH_TEXT);
                } else if (i >= 3) {
                    botones[i].setText("> " + OPCIONES[i]);
                } else {
                    botones[i].setText(OPCIONES[i]);
                }
            } else {
                botones[i].setForeground(COLOR_TEXTO_BASE);
                botones[i].setText(OPCIONES[i]);
            }
        }
    }

    private String generarTextoGlitch(String original) {
        char[] chars = original.toCharArray();
        String caracteresGlitch = "$#@%&?01█▓▒░";
        for (int i = 0; i < chars.length / 3; i++) {
            int idx = random.nextInt(chars.length);
            chars[idx] = caracteresGlitch.charAt(random.nextInt(caracteresGlitch.length()));
        }
        return (indiceSeleccionado >= 3 ? "> " : "") + new String(chars);
    }

    private void ejecutarAccion(int indice) {
        switch (indice) {
            case 0:
                JOptionPane.showMessageDialog(this, "Sección de Ayuda.");
                break;
            case 1:
                JOptionPane.showMessageDialog(this, "Mostrando Novedades.");
                break;
            case 2:
                this.dispose();

                boolean ventanaEncontrada = false;
                for (Window w : Window.getWindows()) {
                    if (w instanceof JFrame && !w.isVisible()) {
                        w.setVisible(true);
                        ventanaEncontrada = true;
                        break;
                    }
                }

                if (!ventanaEncontrada) {
                    try {
                        Class<?> loginClass = Class.forName("main.Usuario.Login");
                        JFrame nuevaLogin = (JFrame) loginClass.getDeclaredConstructor().newInstance();
                        nuevaLogin.setVisible(true);
                    } catch (Exception e) {
                        System.out.println("No se pudo reabrir de forma dinámica: " + e.getMessage());
                    }
                }
                break;
            case 3:
                JOptionPane.showMessageDialog(this, "¡Iniciando el juego...!");
                break;
            case 4:
                JOptionPane.showMessageDialog(this, "Abriendo Personalización.");
                break;
            case 5:
                Window padre = SwingUtilities.getWindowAncestor(botones[5]);
                ConfiguracionVenta ventanaConfig = new ConfiguracionVenta(padre);
                ventanaConfig.setVisible(true);
                break;
            case 6:
                JOptionPane.showMessageDialog(this, "Abriendo la Tienda.");
                break;
        }
    }

    private class PanelFondo extends JPanel {

        private Image imagenFondo;
        private int shakeX = 0;
        private int shakeY = 0;

        public PanelFondo() {
            try {
                imagenFondo = ImageIO.read(new File("src/imagenes/MenuU.png"));
            } catch (IOException e) {
                System.out.println("Aviso: 'MenuU.png' no cargado. Usando renderizado básico.");
            }

            Timer timerError = new Timer(50, e -> {
                if (!errorActivo) {
                    if (random.nextInt(100) < 4) {
                        errorActivo = true;
                        glitchDuration = random.nextInt(4) + 2;
                    }
                } else {
                    glitchDuration--;
                    if (glitchDuration <= 0) {
                        errorActivo = false;
                        shakeX = 0;
                        shakeY = 0;
                        ajustarPosicionBotonesPorError(0, 0);
                    }
                }

                if (errorActivo) {
                    shakeX = random.nextInt(15) - 7;
                    shakeY = random.nextInt(7) - 3;
                    ajustarPosicionBotonesPorError(shakeX, shakeY);
                }

                actualizarEstadoVisual();
                repaint();
            });
            timerError.start();
        }

        public void ajustarPosicionBotonesPorError(int sx, int sy) {
            int ancho = getWidth();
            for (int i = 0; i < OPCIONES.length; i++) {
                if (botones[i] == null) {
                    continue;
                }

                if (i == 0) {
                    botones[i].setHorizontalAlignment(SwingConstants.RIGHT);
                    botones[i].setBounds(ancho - 180 + sx, 45 + sy, 150, 25);
                } else if (i == 1) {
                    botones[i].setHorizontalAlignment(SwingConstants.RIGHT);
                    botones[i].setBounds(ancho - 220 + sx, 75 + sy, 190, 25);
                } else if (i == 2) {
                    botones[i].setHorizontalAlignment(SwingConstants.RIGHT);
                    botones[i].setBounds(ancho - 220 + sx, 105 + sy, 190, 25);
                } else {
                    botones[i].setHorizontalAlignment(SwingConstants.LEFT);
                    int altoActual = (i == 3) ? ALTO_BOTON + 4 : ALTO_BOTON;
                    int finalY = yMenuInicio + ((i - 3) * espaciadoY);
                    botones[i].setBounds(xMenuBase + sx, finalY + sy, ANCHO_BOTON, altoActual);
                }
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int ancho = getWidth();
            int alto = getHeight();

            g2d.setColor(COLOR_FONDO_NEGRO);
            g2d.fillRect(0, 0, ancho, alto);

            if (imagenFondo != null) {
                g2d.drawImage(imagenFondo, shakeX, 30 + shakeY, ancho, alto - 30, this);

                if (errorActivo && random.nextBoolean()) {
                    int numBloques = random.nextInt(2) + 1;
                    for (int i = 0; i < numBloques; i++) {
                        int bx = random.nextInt(ancho - 150);
                        int by = random.nextInt(alto - 70) + 30;
                        int bw = random.nextInt(200) + 50;
                        int bh = random.nextInt(25) + 5;
                        int deltaX = random.nextInt(20) - 10;

                        g2d.drawImage(imagenFondo,
                                bx + deltaX + shakeX, by + shakeY, bx + bw + deltaX + shakeX, by + bh + shakeY,
                                bx, by - 30, bx + bw, by + bh - 30, this);
                    }
                }
            }

            if (errorActivo) {
                g2d.setColor(random.nextBoolean() ? COLOR_CIAN : COLOR_MAGENTA);
                for (int i = 0; i < 6; i++) {
                    int bugY = random.nextInt(alto);
                    int bugH = random.nextInt(12) + 2;
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
                    g2d.fillRect(0, bugY, ancho, bugH);
                }
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }

            if (indiceSeleccionado >= 3 && botones[indiceSeleccionado] != null) {
                Rectangle limitesBoton = botones[indiceSeleccionado].getBounds();

                int lineaY = limitesBoton.y + limitesBoton.height - 3;
                int inicioX = limitesBoton.x;
                int finX = inicioX + 235;

                g2d.setColor(errorActivo ? COLOR_GLITCH_TEXT : COLOR_CIAN);
                g2d.setStroke(new BasicStroke(2f));
                g2d.draw(new Line2D.Float(inicioX, lineaY, finX, lineaY));
                g2d.fillRect(finX - 3, lineaY - 2, 5, 5);
            }
        }
    }
}
