package main.Login;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.util.Random;
import javax.swing.*;
import main.Util.WindowManager;

public class MenuPrincipal extends JFrame {
    
    private JButton btnCrearCuenta;
    private JButton btnIniciarSesion;
    private JButton btnInvitado;
    private Font pixelFont;
    private Font smallPixelFont;
    
    private int botonSeleccionado = 0; 
    private final int TOTAL_BOTONES = 3;

    private int botonXBase = 60;      
    private int botonYInicio = 360;   
    private final int ESPACIADO_Y = 50; 

    private int mouseX, mouseY;

    public MenuPrincipal() {
        setUndecorated(true);
        
        setSize(960, 540); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        

        try {
            pixelFont = new Font("Pixel Emulator", Font.BOLD, 20);
            smallPixelFont = new Font("Pixel Emulator", Font.PLAIN, 11);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 20);
            smallPixelFont = new Font("Monospaced", Font.PLAIN, 11);
        }

        BackgroundPanel panelFondo = new BackgroundPanel("/imagenes/FondoLogin.png"); 
        panelFondo.setLayout(null); 
        setContentPane(panelFondo);

        // --- BARRA SUPERIOR PERSONALIZADA ---
        JPanel barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(10, 14, 23), 0, getHeight(), new Color(4, 6, 10));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                g2d.setColor(new Color(0, 255, 255, 80));
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        barraSuperior.setLayout(null);
        barraSuperior.setBounds(0, 0, 960, 32); 
        panelFondo.add(barraSuperior);

        JLabel lblTitulo = new JLabel(" 8BIT SOUL OS V.1.0.4 // MAIN_MENU");
        lblTitulo.setFont(smallPixelFont);
        lblTitulo.setForeground(new Color(0, 255, 255)); 
        lblTitulo.setBounds(15, 0, 500, 32);
        barraSuperior.add(lblTitulo);

        JButton btnCerrar = new JButton("X");
        btnCerrar.setFont(pixelFont.deriveFont(Font.BOLD, 14f));
        btnCerrar.setForeground(new Color(242, 5, 203)); 
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBounds(915, 0, 45, 32);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btnCerrar.setForeground(Color.RED); }
            @Override
            public void mouseExited(MouseEvent e) { btnCerrar.setForeground(new Color(242, 5, 203)); }
            @Override
            public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        barraSuperior.add(btnCerrar);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
            }
        });

        // --- BOTONES ---
        btnCrearCuenta = crearBotonPersonalizado("CREAR CUENTA");
        panelFondo.add(btnCrearCuenta);

        btnIniciarSesion = crearBotonPersonalizado("INICIAR SESION");
        panelFondo.add(btnIniciarSesion);

        btnInvitado = crearBotonPersonalizado("INGRESAR COMO INVITADO");
        panelFondo.add(btnInvitado);

        panelFondo.actualizarDimensionesYComponentes();
        actualizarSeleccionVisual(false);

        this.setFocusable(true);
        this.requestFocusInWindow();
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_UP:
                        botonSeleccionado = (botonSeleccionado - 1 + TOTAL_BOTONES) % TOTAL_BOTONES;
                        actualizarSeleccionVisual(false);
                        break;
                    case KeyEvent.VK_DOWN:
                        botonSeleccionado = (botonSeleccionado + 1) % TOTAL_BOTONES;
                        actualizarSeleccionVisual(false);
                        break;
                    case KeyEvent.VK_ENTER:
                        ejecutarAccionBoton();
                        break;
                }
            }
        });

        agregarEventosMouse(btnCrearCuenta, 0);
        agregarEventosMouse(btnIniciarSesion, 1);
        agregarEventosMouse(btnInvitado, 2);
    }

    private JButton crearBotonPersonalizado(String texto) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                boolean esActivo = (this == getBotonesArray()[botonSeleccionado]);
                
                if (esActivo) {
                    g2d.setColor(new Color(242, 5, 203, 25)); 
                    g2d.fillRect(0, 0, getWidth() - 5, getHeight());
                    g2d.setColor(new Color(242, 5, 203, 180)); 
                    g2d.setStroke(new BasicStroke(2f));
                    g2d.drawRect(0, 0, getWidth() - 5, getHeight() - 1);
                } else {
                    g2d.setColor(new Color(0, 255, 255, 10)); 
                    g2d.fillRect(0, 0, getWidth() - 5, getHeight());
                    g2d.setColor(new Color(0, 255, 255, 40)); 
                    g2d.setStroke(new BasicStroke(1f));
                    g2d.drawRect(0, 0, getWidth() - 5, getHeight() - 1);
                }
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        boton.setFont(pixelFont);
        boton.setForeground(new Color(150, 255, 255));
        boton.setContentAreaFilled(false); 
        boton.setBorderPainted(false);     
        boton.setFocusPainted(false);         
        boton.setFocusable(false);      
        boton.setBorder(BorderFactory.createEmptyBorder(0, 35, 0, 0));
        boton.setHorizontalAlignment(SwingConstants.LEFT); 
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)); 
        return boton;
    }

    private JButton[] getBotonesArray() {
        return new JButton[]{btnCrearCuenta, btnIniciarSesion, btnInvitado};
    }

    private void agregarEventosMouse(JButton boton, int indice) {
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (botonSeleccionado != indice) {
                    botonSeleccionado = indice;
                    actualizarSeleccionVisual(false);
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    ejecutarAccionBoton();
                }
            }
        });
    }

    private void actualizarSeleccionVisual(boolean glitchActivo) {
        Color cianInactivo = new Color(140, 240, 240);
        Color neonActivo = new Color(255, 255, 255); 
        
        String t1 = glitchActivo ? "CR1#TI_CA0 CUE%" : "CREAR CUENTA";
        String t2 = glitchActivo ? "L0G_IN//ERR0R" : "INICIAR SESIÓN";
        String t3 = glitchActivo ? "GU3ST_BYPASS//0" : "INGRESAR COMO INVITADO";

        btnCrearCuenta.setText(botonSeleccionado == 0 ? (glitchActivo ? "CR1#TI_CA0 CUE%" : "CREAR CUENTA") : t1);
        btnCrearCuenta.setForeground(botonSeleccionado == 0 ? neonActivo : cianInactivo);
        
        btnIniciarSesion.setText(botonSeleccionado == 1 ? (glitchActivo ? "L0G_IN//ERR0R" : "INICIAR SESIÓN") : t2);
        btnIniciarSesion.setForeground(botonSeleccionado == 1 ? neonActivo : cianInactivo);
        
        btnInvitado.setText(botonSeleccionado == 2 ? (glitchActivo ? "GU3ST_BYPASS//0" : "INGRESAR COMO INVITADO") : t3);
        btnInvitado.setForeground(botonSeleccionado == 2 ? neonActivo : cianInactivo);

        getContentPane().repaint();
    }

    private void ejecutarAccionBoton() {
        switch (botonSeleccionado) {
            case 0: 
                CyberpunkRegistro registroVentana = new CyberpunkRegistro();
                main.Util.ContenedorVentana.pf_configurarVentana(registroVentana); // Mantiene el ícono en Registro
                registroVentana.setVisible(true);
                this.dispose(); 
                break;
            case 1: 
                CyberpunkLogin loginVentana = new CyberpunkLogin();
                main.Util.ContenedorVentana.pf_configurarVentana(loginVentana); // Mantiene el ícono en Login
                loginVentana.setVisible(true);
                this.dispose(); 
                break;
            case 2: 
                JOptionPane.showMessageDialog(this, "Acceso concedido en modo de auditoría (Invitado).", "Bypass", JOptionPane.WARNING_MESSAGE);
                break;
        }
    }

    class BackgroundPanel extends JPanel {
        private Image imagen;
        private Random random = new Random();
        private boolean errorActivo = false;
        private int shakeX = 0; private int shakeY = 0;
        private int parpadeoCursor = 0;

        public BackgroundPanel(String rutaImagen) {
            try {
                this.imagen = new ImageIcon(getClass().getResource(rutaImagen)).getImage();
            } catch (Exception e) {
                System.err.println("Error de renderizado de fondo.");
            }

            Timer timerError = new Timer(50, e -> {
                boolean anteriorEstadoError = errorActivo;
                parpadeoCursor = (parpadeoCursor + 1) % 10;
                
                if (!errorActivo && random.nextInt(100) < 12) { 
                    errorActivo = true;
                    shakeX = random.nextInt(8) - 4; 
                    shakeY = random.nextInt(6) - 3; 
                } else {
                    errorActivo = false; shakeX = 0; shakeY = 0;
                }
                if (errorActivo || anteriorEstadoError != errorActivo || parpadeoCursor % 5 == 0) {
                    actualizarPosicionComponentes(shakeX, shakeY);
                    actualizarSeleccionVisual(errorActivo);
                }
            });
            timerError.start();
        }

        public void actualizarDimensionesYComponentes() {
            botonXBase = 50; botonYInicio = 360; 
            actualizarPosicionComponentes(0, 0);
        }

        private void actualizarPosicionComponentes(int sx, int sy) {
            if (btnCrearCuenta == null || btnIniciarSesion == null || btnInvitado == null) return;
            int anchoBoton = 460; int altoBoton = 38;
            btnCrearCuenta.setBounds(botonXBase + sx, botonYInicio + sy, anchoBoton, altoBoton);
            btnIniciarSesion.setBounds(botonXBase + sx, (botonYInicio + ESPACIADO_Y) + sy, anchoBoton, altoBoton);
            btnInvitado.setBounds(botonXBase + sx, (botonYInicio + (ESPACIADO_Y * 2)) + sy, anchoBoton, altoBoton);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int ancho = getWidth(); int alto = getHeight();

            if (imagen != null) {
                g2d.drawImage(imagen, shakeX, shakeY, ancho, alto, this);
            } else {
                g2d.setColor(new Color(6, 10, 20)); g2d.fillRect(0, 0, ancho, alto);
            }

            g2d.setColor(new Color(0, 0, 0, 30));
            for (int y = 0; y < alto; y += 3) { g2d.fillRect(0, y, ancho, 1); }

            g2d.setFont(smallPixelFont);
            g2d.setColor(new Color(0, 255, 255, 90));
            g2d.drawString("NETWORK_STATUS: OVERLOADED", 50, 70);
            g2d.drawString("DECRYPTION_KEY: 0x8BF39A2", 50, 85);
            g2d.drawString("MEM_LOAD: 98.2%", 50, 100);

            if (errorActivo) {
                g2d.setColor(new Color(242, 5, 203, 15)); g2d.fillRect(0, 0, ancho, alto);
                g2d.setColor(new Color(0, 255, 255, 100)); g2d.fillRect(0, random.nextInt(alto), ancho, 2);
            }

            int lineaY = botonYInicio + (botonSeleccionado * ESPACIADO_Y) + 38;
            int finalLineX = botonXBase + shakeX; int finalLineY = lineaY + shakeY;
            int longitudLinea = 455;

            g2d.setColor(new Color(242, 5, 203, 40));
            g2d.setStroke(new BasicStroke(6.0f));
            g2d.draw(new Line2D.Float(finalLineX, finalLineY, finalLineX + longitudLinea, finalLineY));

            g2d.setColor(new Color(242, 5, 203, 120));
            g2d.setStroke(new BasicStroke(4.0f));
            g2d.draw(new Line2D.Float(finalLineX, finalLineY, finalLineX + longitudLinea, finalLineY));

            g2d.setColor(Color.WHITE); g2d.setStroke(new BasicStroke(1.5f));
            g2d.draw(new Line2D.Float(finalLineX, finalLineY, finalLineX + longitudLinea, finalLineY));

            if (parpadeoCursor < 6) {
                int cursorX = finalLineX + 12;
                int cursorY = botonYInicio + (botonSeleccionado * ESPACIADO_Y) + 12 + shakeY;
                g2d.setColor(new Color(242, 5, 203));
                int[] xPoints = {cursorX, cursorX + 8, cursorX};
                int[] yPoints = {cursorY, cursorY + 6, cursorY + 12};
                g2d.fillPolygon(xPoints, yPoints, 3);
            }

            g2d.setColor(new Color(0, 255, 255, 180)); 
            g2d.setStroke(new BasicStroke(3.0f)); 
            g2d.drawRect(0, 0, ancho, alto);
        }
    }
}