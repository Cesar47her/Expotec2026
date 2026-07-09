package main.Login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import main.Admin.Configuraciones.ConfiguracionDAO;
import main.Admin.Configuraciones.ConfiguracionUsuario;
import main.AplicacionPrincipal;
import main.Conexion.UsuarioDAO;
import main.Util.*;

public class CyberpunkLogin extends JPanel {

    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(242, 5, 203);
    public static final Color COLOR_TEXT_LIGHT = new Color(220, 245, 255);
    public static final Color COLOR_BOX_BG = new Color(6, 12, 24, 230); 
    public static final Color COLOR_DARK_BG = new Color(6, 12, 24);

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnVolver;
    private JButton btnLogin;
    private JButton btnRecuperar;
    private LoginBackgroundPanel panelFondo;
    private Font pixelFont;
    private Font smallPixelFont;

    // Componentes del panel de recuperación integrado
    private JPanel contenedorCentral;
    private JPanel panelRecuperacion;
    private JLabel lblRecuperarTitulo;
    private JLabel lblRecuperarInstrucciones;
    private JTextField txtRecuperarInput;
    private JButton btnRecuperarAccion;
    private JButton btnRecuperarCancelar;
    
    // Estado del protocolo de recuperación: 1 = Email, 2 = Código Token, 3 = Nueva Clave, 4 = Fin Exitoso
    private int estadoRecuperacion = 1; 
    private String correoTemporal = "";
    private String tokenTemporal = "";
    private int idUsuarioTemporal = 0;

    private int mouseX, mouseY;
    private final AplicacionPrincipal aplicacion;

    public CyberpunkLogin() {
        this(null);
    }

    public CyberpunkLogin(AplicacionPrincipal aplicacion) {
        this.aplicacion = aplicacion;
        configurarVentana();
        inicializarComponentes();
        inicializarPanelRecuperacion();
        configureEventos();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        JRootPane root = SwingUtilities.getRootPane(this);
        if (root != null) {
            root.setDefaultButton(btnLogin);
        }
        if (txtUser != null) {
            txtUser.setText("");
            txtUser.requestFocusInWindow();
        }
        if (txtPass != null) {
            txtPass.setText("");
        }
        revalidate();
        repaint();
    }

    @Override
    public void setVisible(boolean aFlag) {
        super.setVisible(aFlag);
        if (aFlag) {
            if (txtUser != null) txtUser.setText("");
            if (txtPass != null) txtPass.setText("");
            ocultarPanelRecuperacion();
            revalidate();
            repaint();
        }
    }

    private void configurarVentana() {
        setLayout(null);
        setOpaque(false); 
        setPreferredSize(new Dimension(900, 500));

        panelFondo = new LoginBackgroundPanel();
        panelFondo.setLayout(null);
        panelFondo.setBounds(0, 0, 900, 500);
        add(panelFondo);

        JPanel barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(COLOR_DARK_BG);
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(COLOR_CYAN);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        barraSuperior.setLayout(null);
        barraSuperior.setBounds(0, 0, 900, 30);
        panelFondo.add(barraSuperior);

        try {
            pixelFont = new Font("Pixel Emulator", Font.BOLD, 15);
            smallPixelFont = new Font("Pixel Emulator", Font.PLAIN, 12);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 15);
            smallPixelFont = new Font("Monospaced", Font.PLAIN, 12);
        }

        JLabel lblTituloBarra = new JLabel(" 8-BIT SOUL // SECURE LOGIN SYSTEM");
        lblTituloBarra.setFont(smallPixelFont);
        lblTituloBarra.setForeground(COLOR_TEXT_LIGHT);
        lblTituloBarra.setBounds(15, 0, 500, 30);
        barraSuperior.add(lblTituloBarra);

        JButton btnCerrar = new JButton("[X]");
        btnCerrar.setFont(smallPixelFont);
        btnCerrar.setForeground(COLOR_MAGENTA);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBounds(850, 0, 50, 30);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnCerrar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnCerrar.setForeground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { btnCerrar.setForeground(COLOR_MAGENTA); }
            @Override public void mouseClicked(MouseEvent e) { System.exit(0); }
        });
        barraSuperior.add(btnCerrar);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                Window window = SwingUtilities.getWindowAncestor(CyberpunkLogin.this);
                if (window != null) {
                    window.setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });
    }

    private void inicializarComponentes() {
        contenedorCentral = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR_BOX_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(COLOR_CYAN);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        contenedorCentral.setLayout(null);
        contenedorCentral.setOpaque(false); 
        contenedorCentral.setBounds(270, 70, 360, 340);
        panelFondo.add(contenedorCentral);

        JLabel lblWelcome = new JLabel("INICIAR SESIÓN");
        lblWelcome.setFont(pixelFont.deriveFont(Font.BOLD, 18f));
        lblWelcome.setForeground(COLOR_CYAN);
        lblWelcome.setBounds(30, 25, 300, 30);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        contenedorCentral.add(lblWelcome);

        JLabel lblUser = new JLabel("USUARIO:");
        lblUser.setFont(smallPixelFont);
        lblUser.setForeground(COLOR_TEXT_LIGHT);
        lblUser.setBounds(30, 80, 300, 20);
        contenedorCentral.add(lblUser);

        txtUser = new LoginRetroTextField(32);
        txtUser.setBounds(30, 105, 300, 38);
        contenedorCentral.add(txtUser);

        JLabel lblPass = new JLabel("CONTRASEÑA:");
        lblPass.setFont(smallPixelFont);
        lblPass.setForeground(COLOR_TEXT_LIGHT);
        lblPass.setBounds(30, 160, 300, 20);
        contenedorCentral.add(lblPass);

        txtPass = new LoginRetroPasswordField(64);
        txtPass.setBounds(30, 185, 300, 38);
        contenedorCentral.add(txtPass);

        btnLogin = new LoginRetroButton("INGRESAR");
        btnLogin.setBounds(30, 255, 300, 45);
        btnLogin.setFont(pixelFont);
        contenedorCentral.add(btnLogin);

        btnVolver = new JButton("← VOLVER AL MENU");
        btnVolver.setFont(pixelFont.deriveFont(12f));
        btnVolver.setForeground(COLOR_TEXT_LIGHT);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setBounds(40, 440, 200, 30);
        btnVolver.setHorizontalAlignment(SwingConstants.LEFT);

        btnVolver.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnVolver.setForeground(COLOR_CYAN); }
            @Override public void mouseExited(MouseEvent e) { btnVolver.setForeground(COLOR_TEXT_LIGHT); }
        });
        panelFondo.add(btnVolver);

        btnRecuperar = new JButton("¿Olvidaste tu contraseña?");
        btnRecuperar.setFont(smallPixelFont.deriveFont(Font.ITALIC));
        btnRecuperar.setForeground(COLOR_TEXT_LIGHT);
        btnRecuperar.setContentAreaFilled(false);
        btnRecuperar.setBorderPainted(false);
        btnRecuperar.setFocusPainted(false);
        btnRecuperar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRecuperar.setBounds(620, 440, 260, 30);
        btnRecuperar.setHorizontalAlignment(SwingConstants.RIGHT);

        btnRecuperar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRecuperar.setForeground(COLOR_MAGENTA); }
            @Override public void mouseExited(MouseEvent e) { btnRecuperar.setForeground(COLOR_TEXT_LIGHT); }
        });
        panelFondo.add(btnRecuperar);
    }

    private void inicializarPanelRecuperacion() {
        panelRecuperacion = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR_BOX_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(COLOR_MAGENTA); 
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        panelRecuperacion.setLayout(null);
        panelRecuperacion.setOpaque(false);
        panelRecuperacion.setBounds(270, 70, 360, 340); 
        panelRecuperacion.setVisible(false); 
        panelFondo.add(panelRecuperacion);

        lblRecuperarTitulo = new JLabel("NÚCLEO DE RECUPERACIÓN");
        lblRecuperarTitulo.setFont(pixelFont.deriveFont(Font.BOLD, 14f));
        lblRecuperarTitulo.setForeground(COLOR_MAGENTA);
        lblRecuperarTitulo.setBounds(20, 25, 320, 25);
        lblRecuperarTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelRecuperacion.add(lblRecuperarTitulo);

        lblRecuperarInstrucciones = new JLabel("<html><center>Introduce tu correo registrado para generar un token efímero:</center></html>");
        lblRecuperarInstrucciones.setFont(smallPixelFont.deriveFont(11f));
        lblRecuperarInstrucciones.setForeground(COLOR_TEXT_LIGHT);
        lblRecuperarInstrucciones.setBounds(30, 65, 300, 50);
        lblRecuperarInstrucciones.setHorizontalAlignment(SwingConstants.CENTER);
        panelRecuperacion.add(lblRecuperarInstrucciones);

        txtRecuperarInput = new LoginRetroTextField(64);
        txtRecuperarInput.setBounds(30, 140, 300, 38);
        panelRecuperacion.add(txtRecuperarInput);

        btnRecuperarAccion = new LoginRetroButton("GENERAR LLAVE");
        btnRecuperarAccion.setBounds(30, 210, 300, 40);
        btnRecuperarAccion.setFont(pixelFont.deriveFont(13f));
        panelRecuperacion.add(btnRecuperarAccion);

        btnRecuperarCancelar = new JButton("[ CANCELAR PROTOCOLO ]");
        btnRecuperarCancelar.setFont(smallPixelFont.deriveFont(10f));
        btnRecuperarCancelar.setForeground(Color.GRAY);
        btnRecuperarCancelar.setContentAreaFilled(false);
        btnRecuperarCancelar.setBorderPainted(false);
        btnRecuperarCancelar.setFocusPainted(false);
        btnRecuperarCancelar.setBounds(30, 275, 300, 30);
        btnRecuperarCancelar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnRecuperarCancelar.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnRecuperarCancelar.setForeground(Color.RED); }
            @Override public void mouseExited(MouseEvent e) { btnRecuperarCancelar.setForeground(Color.GRAY); }
        });
        panelRecuperacion.add(btnRecuperarCancelar);
    }

    private void mostrarPanelRecuperacion() {
        contenedorCentral.setVisible(false);
        estadoRecuperacion = 1;
        correoTemporal = "";
        tokenTemporal = "";
        idUsuarioTemporal = 0;
        
        lblRecuperarTitulo.setText("NÚCLEO DE RECUPERACIÓN");
        lblRecuperarTitulo.setForeground(COLOR_MAGENTA);
        lblRecuperarInstrucciones.setText("<html><center>Introduce tu correo registrado para generar un token efímero:</center></html>");
        txtRecuperarInput.setText("");
        btnRecuperarAccion.setText("GENERAR LLAVE");
        btnRecuperarAccion.setEnabled(true);
        
        panelRecuperacion.setVisible(true);
        txtRecuperarInput.requestFocusInWindow();
        repaint();
    }

    private void ocultarPanelRecuperacion() {
        panelRecuperacion.setVisible(false);
        contenedorCentral.setVisible(true);
        txtUser.requestFocusInWindow();
        repaint();
    }

    private void configureEventos() {
        btnVolver.addActionListener(e -> {
            if (aplicacion != null) aplicacion.mostrarMenu();
        });

        configurarTeclado();

        btnRecuperar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                mostrarPanelRecuperacion();
            }
        });

        btnRecuperarCancelar.addActionListener(e -> ocultarPanelRecuperacion());

        btnRecuperarAccion.addActionListener(e -> {
            String input = txtRecuperarInput.getText().trim();
            if (input.isEmpty()) return;

            // ==========================================
            // ESTADO 1: Validar Correo y Enviar SMTP
            // ==========================================
            if (estadoRecuperacion == 1) { 
                btnRecuperarAccion.setEnabled(false);
                btnRecuperarAccion.setText("TRANSMITIENDO...");

                SwingWorker<Boolean, Void> recoveryWorker = new SwingWorker<>() {
                    private String username = null;
                    
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        UsuarioDAO usuarioDAO = new UsuarioDAO();
                        idUsuarioTemporal = usuarioDAO.obtenerIdPorCorreo(input); 
                        
                        if (idUsuarioTemporal <= 0) return false;

                        username = usuarioDAO.obtenerUsernamePorCorreo(input);
                        if (username == null || username.isEmpty()) username = "Operador";

                        int randomToken = (int) (Math.random() * 900000) + 100000;
                        tokenTemporal = String.valueOf(randomToken);
                        correoTemporal = input;

                        System.out.println("[SYSTEM] Token generado en memoria: " + tokenTemporal);
                        return ServicioCorreo.enviarCodigoRecuperacion(correoTemporal, username, tokenTemporal);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                estadoRecuperacion = 2; 
                                lblRecuperarTitulo.setText("VERIFICACIÓN DE LLAVE");
                                lblRecuperarTitulo.setForeground(COLOR_CYAN);
                                lblRecuperarInstrucciones.setText("<html><center><font color='#00f0ff'>ÉXITO SMTP.</font><br>Introduce el código de 6 dígitos enviado a tu terminal:</center></html>");
                                txtRecuperarInput.setText("");
                                btnRecuperarAccion.setText("VERIFICAR CÓDIGO");
                            } else {
                                lblRecuperarInstrucciones.setText("<html><center><font color='red'>ERROR:</font> Correo inexistente o falla de red.</center></html>");
                                btnRecuperarAccion.setText("GENERAR LLAVE");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            lblRecuperarInstrucciones.setText("<html><center><font color='red'>ERROR CRÍTICO:</font> Fallo de conexión hilos.</center></html>");
                            btnRecuperarAccion.setText("GENERAR LLAVE");
                        } finally {
                            btnRecuperarAccion.setEnabled(true);
                        }
                    }
                };
                recoveryWorker.execute();

            // ==========================================
            // ESTADO 2: Validar Token en memoria
            // ==========================================
            } else if (estadoRecuperacion == 2) { 
                if (tokenTemporal.equals(input)) {
                    estadoRecuperacion = 3; 
                    lblRecuperarTitulo.setText("REESCRITURA DE CREDENCIALES");
                    lblRecuperarTitulo.setForeground(Color.GREEN);
                    lblRecuperarInstrucciones.setText("<html><center>Código validado.<br>Ingresa tu nueva contraseña de acceso:</center></html>");
                    txtRecuperarInput.setText("");
                    btnRecuperarAccion.setText("ACTUALIZAR RED");
                } else {
                    lblRecuperarInstrucciones.setText("<html><center><font color='red'>ERROR:</font> Token inválido. Verifica de nuevo.</center></html>");
                }

            // ==========================================
            // ESTADO 3: Persistir Contraseña final
            // ==========================================
            } else if (estadoRecuperacion == 3) { 
                btnRecuperarAccion.setEnabled(false);
                btnRecuperarAccion.setText("GUARDANDO...");

                SwingWorker<Boolean, Void> updateWorker = new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        UsuarioDAO usuarioDAO = new UsuarioDAO();
                        return usuarioDAO.actualizarContrasena(idUsuarioTemporal, input);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                lblRecuperarInstrucciones.setText("<html><center><font color='green'>⚡ CAMBIO COMPLETADO.</font><br>Tu clave ha sido reescrita con éxito.</center></html>");
                                btnRecuperarAccion.setText("CERRAR PANEL");
                                estadoRecuperacion = 4; 
                            } else {
                                lblRecuperarInstrucciones.setText("<html><center><font color='red'>ERROR CRÍTICO:</font> Fallo de escritura en DB.</center></html>");
                                btnRecuperarAccion.setText("ACTUALIZAR RED");
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            btnRecuperarAccion.setText("ACTUALIZAR RED");
                        } finally {
                            btnRecuperarAccion.setEnabled(true);
                        }
                    }
                };
                updateWorker.execute();
                
            // ==========================================
            // ESTADO 4: Salida Limpia del Panel
            // ==========================================
            } else if (estadoRecuperacion == 4) {
                ocultarPanelRecuperacion(); 
            }
        });

        // EVENTO LOGIN NORMAL MODIFICADO PARA LLAMAR PANTALLA DE CARGA
        btnLogin.addActionListener(e -> {
            String usuario = txtUser.getText().trim();
            String contrasena = new String(txtPass.getPassword()).trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) return;

            btnLogin.setEnabled(false);
            btnLogin.setText("CONECTANDO AL NÚCLEO...");

            SwingWorker<String, Void> loginWorker = new SwingWorker<>() {
                private int idUsuarioDetectado = 0;

                @Override
                protected String doInBackground() throws Exception {
                    UsuarioDAO usuarioConsultor = new UsuarioDAO();
                    idUsuarioDetectado = usuarioConsultor.obtenerIdPorUsername(usuario); 
                    return usuarioConsultor.autenticarUsuario(usuario, contrasena);
                }

                @Override
                protected void done() {
                    try {
                        String rol = get();
                        if (rol != null) {
                            rol = rol.trim();
                            ConfiguracionDAO configDAO = new ConfiguracionDAO();
                            ConfiguracionUsuario configUsuario = configDAO.obtenerPorUsuario(idUsuarioDetectado);

                            double volumen = 0.5; 
                            if (configUsuario != null) {
                                volumen = configUsuario.getVolumenAudio();
                            }
                            ReproducirSonido.asignarVolumen(volumen);

                            // Obtener la ventana actual (JFrame) para ocultarla
                            Window ventanaActual = SwingUtilities.getWindowAncestor(CyberpunkLogin.this);
                            JFrame frameActual = (ventanaActual instanceof JFrame) ? (JFrame) ventanaActual : null;

                            final int idUsuario = idUsuarioDetectado;
                            final String rolFinal = rol;

                            if (frameActual != null) {
                                frameActual.setVisible(false);

                                // Crear ventana proxy intermedia que reaccionará al temporizador de la pantalla de carga
                                JFrame destinoFinal = new JFrame() {
                                    @Override
                                    public void setVisible(boolean b) {
                                        if (b) {
                                            if (rolFinal.equalsIgnoreCase("ADMINISTRADOR")) {
                                                if (aplicacion != null) aplicacion.mostrarAdmin();
                                            } else if (rolFinal.equalsIgnoreCase("USUARIO_CORE")) {
                                                if (aplicacion != null) aplicacion.mostrarUsuario(idUsuario);
                                            }
                                            this.dispose(); // Destrucción inmediata del proxy temporal
                                        }
                                    }
                                };

                                // Disparar la pantalla de carga pasándole el destino condicionalizado
                                main.PantalladeCarga.GlitchLoadingScreen1 pantallaCarga = 
                                    new main.PantalladeCarga.GlitchLoadingScreen1(destinoFinal);
                                pantallaCarga.setVisible(true);

                            } else {
                                // Flujo de respaldo directo si no se puede hallar el contenedor superior
                                if (rol.equalsIgnoreCase("ADMINISTRADOR")) {
                                    if (aplicacion != null) aplicacion.mostrarAdmin();
                                } else if (rol.equalsIgnoreCase("USUARIO_CORE")) {
                                    if (aplicacion != null) aplicacion.mostrarUsuario(idUsuarioDetectado);
                                }
                            }
                        } else {
                            JOptionPane.showMessageDialog(CyberpunkLogin.this, "Credenciales Inválidas o Error de Firma Hash.", "ERROR DE ACCESO", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        btnLogin.setEnabled(true);
                        btnLogin.setText("INGRESAR");
                    }
                }
            };
            loginWorker.execute();
        });
    }

    private void configurarTeclado() {
        Action loginAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (btnLogin != null && btnLogin.isEnabled() && contenedorCentral.isVisible()) {
                    btnLogin.doClick();
                } else if (panelRecuperacion.isVisible() && btnRecuperarAccion.isEnabled()) {
                    btnRecuperarAccion.doClick();
                }
            }
        };

        txtUser.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "login");
        txtUser.getActionMap().put("login", loginAction);
        txtPass.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "login");
        txtPass.getActionMap().put("login", loginAction);
        txtRecuperarInput.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("ENTER"), "login");
        txtRecuperarInput.getActionMap().put("login", loginAction);

        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "volver");
        getActionMap().put("volver", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (panelRecuperacion.isVisible()) {
                    ocultarPanelRecuperacion();
                } else if (btnVolver != null) {
                    btnVolver.doClick();
                }
            }
        });
    }
}

// ============================================================
// COMPONENTES DE RENDERIZADO RETRO
// ============================================================
class LoginLengthRestrictedDocumentFilter extends DocumentFilter {
    private final int maxLength;
    public LoginLengthRestrictedDocumentFilter(int maxLength) { this.maxLength = maxLength; }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) return;
        if ((fb.getDocument().getLength() + string.length()) <= maxLength) { super.insertString(fb, offset, string, attr); }
    }
    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) return;
        if ((fb.getDocument().getLength() - length + text.length()) <= maxLength) { super.replace(fb, offset, length, text, attrs); }
    }
}

class LoginRetroTextField extends JTextField {
    private boolean focusActivo = false;
    public LoginRetroTextField(int maxLength) {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkLogin.COLOR_CYAN);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        ((AbstractDocument) getDocument()).setDocumentFilter(new LoginLengthRestrictedDocumentFilter(maxLength));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focusActivo = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focusActivo = false; repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(12, 18, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(focusActivo ? CyberpunkLogin.COLOR_MAGENTA : new Color(0, 240, 255, 120));
        g2d.setStroke(new BasicStroke(focusActivo ? 2.0f : 1.0f));
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
        super.paintComponent(g);
    }
}

class LoginRetroPasswordField extends JPasswordField {
    private boolean focusActivo = false;
    public LoginRetroPasswordField(int maxLength) {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkLogin.COLOR_CYAN);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        ((AbstractDocument) getDocument()).setDocumentFilter(new LoginLengthRestrictedDocumentFilter(maxLength));
        addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) { focusActivo = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { focusActivo = false; repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setColor(new Color(12, 18, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setColor(focusActivo ? CyberpunkLogin.COLOR_MAGENTA : new Color(0, 240, 255, 120));
        g2d.setStroke(new BasicStroke(focusActivo ? 2.0f : 1.0f));
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
        super.paintComponent(g);
    }
}

class LoginRetroButton extends JButton {
    private boolean mouseEncima = false;
    public LoginRetroButton(String text) {
        super(text);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { mouseEncima = true; setForeground(new Color(6, 12, 24)); repaint(); }
            @Override public void mouseExited(MouseEvent e) { mouseEncima = false; setForeground(Color.WHITE); repaint(); }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        if (mouseEncima) {
            g2d.setColor(CyberpunkLogin.COLOR_CYAN);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(18, 26, 44));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(CyberpunkLogin.COLOR_MAGENTA);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        g2d.dispose();
        super.paintComponent(g);
    }
}

class LoginBackgroundPanel extends JPanel {
    private Image imagenFondo;
    public LoginBackgroundPanel() {
        super();
        try {
            URL url = getClass().getResource("/imagenes/FondoLogin.png");
            if (url != null) { imagenFondo = new ImageIcon(url).getImage(); }
        } catch (Exception e) { System.err.println("Error cargando el fondo del Login."); }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) { g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this); }
        else { g.setColor(CyberpunkLogin.COLOR_DARK_BG); g.fillRect(0, 0, getWidth(), getHeight()); }
    }
}