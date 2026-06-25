package main.Login;

import java.awt.*;
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
import main.Admin.PanelAdmin;
import main.Conexion.PasswordRecoveryDAO;
import main.Conexion.UsuarioDAO;
import main.PantalladeCarga.GlitchLoadingScreen1;
import main.Usuario.*;
import main.Util.*;

public class CyberpunkLogin extends JFrame {

    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(242, 5, 203);
    public static final Color COLOR_TEXT_LIGHT = new Color(220, 245, 255);
    public static final Color COLOR_BOX_BG = new Color(6, 12, 24, 230);

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnVolver;
    private JButton btnLogin;
    private JButton btnRecuperar;
    private LoginBackgroundPanel panelFondo;
    private Font pixelFont;
    private Font smallPixelFont;

    private int mouseX, mouseY;

    public CyberpunkLogin() {
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
        configureEventos();
    }

    private void configurarVentana() {
        setUndecorated(true);
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        panelFondo = new LoginBackgroundPanel();
        panelFondo.setLayout(null);
        setContentPane(panelFondo);

        JPanel barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(6, 12, 24));
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
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrar.setForeground(Color.RED);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrar.setForeground(COLOR_MAGENTA);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }
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
    }

    private void inicializarComponentes() {
        JPanel contenedorCentral = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BOX_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(COLOR_CYAN);
                g2d.setStroke(new BasicStroke(2.0f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        contenedorCentral.setLayout(null);
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

        txtUser = new RetroTextField(32);
        txtUser.setBounds(30, 105, 300, 38);
        contenedorCentral.add(txtUser);

        JLabel lblPass = new JLabel("CONTRASEÑA:");
        lblPass.setFont(smallPixelFont);
        lblPass.setForeground(COLOR_TEXT_LIGHT);
        lblPass.setBounds(30, 160, 300, 20);
        contenedorCentral.add(lblPass);

        txtPass = new RetroPasswordField(64);
        txtPass.setBounds(30, 185, 300, 38);
        contenedorCentral.add(txtPass);

        btnLogin = new RetroButton("INGRESAR");
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
            @Override
            public void mouseEntered(MouseEvent e) {
                btnVolver.setForeground(COLOR_CYAN);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnVolver.setForeground(COLOR_TEXT_LIGHT);
            }
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
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRecuperar.setForeground(COLOR_MAGENTA);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btnRecuperar.setForeground(COLOR_TEXT_LIGHT);
            }
        });
        panelFondo.add(btnRecuperar);
    }

    private void configureEventos() {
        btnVolver.addActionListener(e -> {
            System.out.println("[SYSTEM] Interrumpiendo protocolo de autenticación. Regresando al menú raíz...");
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
            this.dispose();
        });

        btnRecuperar.addActionListener(e -> {
            String correoInput = JOptionPane.showInputDialog(this,
                    "SISTEMA DE RECOVERY:\nEscribe tu correo electrónico registrado para restaurar acceso:",
                    "RECUPERAR CREDENCIALES", JOptionPane.QUESTION_MESSAGE);

            if (correoInput != null && !correoInput.trim().isEmpty()) {
                String correoFinal = correoInput.trim();

                btnRecuperar.setEnabled(false);
                System.out.println("[SYSTEM] Iniciando protocolo de recuperación de credenciales para: " + correoFinal);

                SwingWorker<Boolean, Void> recoveryWorker = new SwingWorker<>() {
                    private String username = null;
                    private String codigo = null;
                    private int idUsuario = 0;

                    @Override
                    protected Boolean doInBackground() throws Exception {
                        PasswordRecoveryDAO recoveryDAO = new PasswordRecoveryDAO();
                        var datosRecuperacion = recoveryDAO.solicitarCodigoRecuperacion(correoFinal);

                        if (datosRecuperacion != null) {
                            username = datosRecuperacion.get("username");
                            codigo = datosRecuperacion.get("codigo");
                            idUsuario = Integer.parseInt(datosRecuperacion.get("idUsuario"));
                            return ServicioCorreo.enviarCodigoRecuperacion(correoFinal, username, codigo);
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean exitoProtocolo = get();
                            if (exitoProtocolo) {
                                String codigoIngresado = JOptionPane.showInputDialog(CyberpunkLogin.this,
                                        "Se ha enviado un código de recuperación a tu correo. Introduce ese código para continuar:",
                                        "VALIDAR CÓDIGO DE RECUPERACION", JOptionPane.QUESTION_MESSAGE);

                                if (codigoIngresado != null && !codigoIngresado.trim().isEmpty()) {
                                    PasswordRecoveryDAO recoveryDAO = new PasswordRecoveryDAO();
                                    boolean codigoValido = recoveryDAO.verificarCodigo(correoFinal, codigoIngresado.trim());

                                    if (codigoValido) {
                                        String nuevaContrasena = JOptionPane.showInputDialog(CyberpunkLogin.this,
                                                "Código validado. Ingresa tu nueva contraseña:",
                                                "RESTABLECER CONTRASEÑA", JOptionPane.QUESTION_MESSAGE);
                                        if (nuevaContrasena != null && !nuevaContrasena.trim().isEmpty()) {
                                            if (recoveryDAO.actualizarContrasena(idUsuario, nuevaContrasena.trim())) {
                                                JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                                        "⚡ Tu contraseña ha sido restablecida correctamente.",
                                                        "RECOVERY EXITOSO", JOptionPane.INFORMATION_MESSAGE);
                                            } else {
                                                JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                                        "No se pudo actualizar la contraseña en el servidor.",
                                                        "ERROR DE ACTUALIZACIÓN", JOptionPane.ERROR_MESSAGE);
                                            }
                                        } else {
                                            JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                                    "No se ingresó una nueva contraseña.",
                                                    "RECOVERY CANCELADO", JOptionPane.WARNING_MESSAGE);
                                        }
                                    } else {
                                        JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                                "El código ingresado no es válido o expiró.",
                                                "CODIGO INVALIDO", JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                            } else {
                                JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                        "❌ [FALLO DE ENLACE DE CODIGO]\nEl correo electrónico no se encuentra registrado o el puerto SMTP falló.",
                                        "ERROR DE VALIDACIÓN", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            System.err.println("[CRITICAL] Error capturado en el hilo de recuperación asíncrona:");
                            ex.printStackTrace();
                            JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                    "Error crítico en la secuencia de recuperación asíncrona.\nRevisa la terminal de salida.",
                                    "NET_CORE_EXCEPTION", JOptionPane.ERROR_MESSAGE);
                        } finally {
                            btnRecuperar.setEnabled(true);
                        }
                    }
                };
                recoveryWorker.execute();
            }
        });

        btnLogin.addActionListener(e -> {
            String usuario = txtUser.getText().trim();
            String contrasena = new String(txtPass.getPassword()).trim();

            if (usuario.isEmpty() || contrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor complete todos los campos.", "AVISO", JOptionPane.WARNING_MESSAGE);
                return;
            }

            btnLogin.setEnabled(false);
            btnLogin.setText("CONECTANDO AL NÚCLEO...");

            SwingWorker<String, Void> loginWorker = new SwingWorker<>() {
                private int idUsuarioDetectado = 0; // Almacenará el ID dinámico real

                @Override
                protected String doInBackground() throws Exception {
                    UsuarioDAO usuarioConsultor = new UsuarioDAO();
                    
                    // --- MODIFICACIÓN CLAVE: Obtenemos el ID real del usuario desde la BD ---
                    // Nota: Si tu método 'autenticarUsuario' solo devuelve el String del rol, deberías tener
                    // un método alternativo en tu UsuarioDAO como 'obtenerIdPorUsername(usuario)' para guardar el ID real.
                    idUsuarioDetectado = usuarioConsultor.obtenerIdPorUsername(usuario); 
                    
                    return usuarioConsultor.autenticarUsuario(usuario, contrasena);
                }

                @Override
                protected void done() {
                    try {
                        String rol = get();

                        if (rol != null) {
                            rol = rol.trim();
                            System.out.println("[SYSTEM] Acceso autorizado. Inicializando entorno para ID: " + idUsuarioDetectado + " con Rol: '" + rol + "'");

                            ConfiguracionDAO configDAO = new ConfiguracionDAO();
                            // --- ARREGLO AQUÍ: Ahora busca dinámicamente usando el ID real obtenido ---
                            ConfiguracionUsuario configUsuario = configDAO.obtenerPorUsuario(idUsuarioDetectado);

                            double volumen = 0.5; // Valor por defecto
                            if (configUsuario != null) {
                                volumen = configUsuario.getVolumenAudio();
                                System.out.println("[AUDIO SYSTEM] Volumen ajustado según persistencia a: " + (volumen * 100) + "%");
                            } else {
                                System.out.println("[AUDIO SYSTEM] Configuración no encontrada para ID " + idUsuarioDetectado + ". Usando volumen por defecto (50%).");
                            }

                            ReproducirSonido.asignarVolumen(volumen);

                            JFrame ventanaDestino = null;

                            if (rol.equalsIgnoreCase("ADMINISTRADOR")) {
                                ventanaDestino = new PanelAdmin();
                            } else if (rol.equalsIgnoreCase("USUARIO_CORE")) {
                                // --- ARREGLO AQUÍ: Enviamos el ID detectado directamente al constructor para que herede las propiedades ---
                                ventanaDestino = new MenuUsuario(idUsuarioDetectado);
                            }

                            if (ventanaDestino != null) {
                                GlitchLoadingScreen1 loadingScreen = new GlitchLoadingScreen1(ventanaDestino);
                                loadingScreen.setVisible(true);
                                CyberpunkLogin.this.dispose();
                            } else {
                                JOptionPane.showMessageDialog(CyberpunkLogin.this,
                                        "El rol asignado ('" + rol + "') no tiene definida una interfaz destino.",
                                        "ERROR DE ENTORNO", JOptionPane.ERROR_MESSAGE);
                                btnLogin.setEnabled(true);
                                btnLogin.setText("INGRESAR");
                            }

                        } else {
                            JOptionPane.showMessageDialog(CyberpunkLogin.this, "Usuario o contraseña incorrectos, o cuenta inactiva.", "ERROR", JOptionPane.ERROR_MESSAGE);
                            btnLogin.setEnabled(true);
                            btnLogin.setText("INGRESAR");
                        }
                    } catch (Exception ex) {
                        System.err.println("[CRITICAL] Error en loginWorker:");
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(CyberpunkLogin.this, "Fallo crítico al inicializar el entorno de usuario.", "SYSTEM ERROR", JOptionPane.ERROR_MESSAGE);
                        btnLogin.setEnabled(true);
                        btnLogin.setText("INGRESAR");
                    }
                }
            };
            loginWorker.execute();
        });
    }
}

// ============================================================
// COMPONENTES DE RENDERIZADO RETRO (Se mantienen intactos)
// ============================================================
class LengthRestrictedDocumentFilter extends DocumentFilter {
    private final int maxLength;

    public LengthRestrictedDocumentFilter(int maxLength) {
        this.maxLength = maxLength;
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
        if (string == null) {
            return;
        }
        int currentLength = fb.getDocument().getLength();
        int newLength = currentLength + string.length();
        if (newLength <= maxLength) {
            super.insertString(fb, offset, string, attr);
        } else if (currentLength < maxLength) {
            super.insertString(fb, offset, string.substring(0, maxLength - currentLength), attr);
        }
    }

    @Override
    public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
        if (text == null) {
            return;
        }
        int currentLength = fb.getDocument().getLength();
        int newLength = currentLength - length + text.length();
        if (newLength <= maxLength) {
            super.replace(fb, offset, length, text, attrs);
        } else if (currentLength < maxLength) {
            super.replace(fb, offset, length, text.substring(0, maxLength - (currentLength - length)), attrs);
        }
    }
}

class RetroTextField extends JTextField {
    private boolean focusActivo = false;

    public RetroTextField() {
        this(32);
    }

    public RetroTextField(int maxLength) {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkLogin.COLOR_CYAN);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        ((AbstractDocument) getDocument()).setDocumentFilter(new LengthRestrictedDocumentFilter(maxLength));
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
        if (focusActivo) {
            g2d.setColor(CyberpunkLogin.COLOR_MAGENTA);
            g2d.setStroke(new BasicStroke(2.0f));
        } else {
            g2d.setColor(new Color(0, 240, 255, 120));
            g2d.setStroke(new BasicStroke(1.0f));
        }
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
        super.paintComponent(g);
    }
}

class RetroPasswordField extends JPasswordField {
    private boolean focusActivo = false;

    public RetroPasswordField() {
        this(64);
    }

    public RetroPasswordField(int maxLength) {
        super();
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkLogin.COLOR_CYAN);
        setFont(new Font("Monospaced", Font.PLAIN, 14));
        setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        ((AbstractDocument) getDocument()).setDocumentFilter(new LengthRestrictedDocumentFilter(maxLength));
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
        if (focusActivo) {
            g2d.setColor(CyberpunkLogin.COLOR_MAGENTA);
            g2d.setStroke(new BasicStroke(2.0f));
        } else {
            g2d.setColor(new Color(0, 240, 255, 120));
            g2d.setStroke(new BasicStroke(1.0f));
        }
        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        g2d.dispose();
        super.paintComponent(g);
    }
}

class RetroButton extends JButton {
    private boolean mouseEncima = false;
    public RetroButton(String text) {
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
        else { g.setColor(new Color(6, 12, 24)); g.fillRect(0, 0, getWidth(), getHeight()); }
    }
}