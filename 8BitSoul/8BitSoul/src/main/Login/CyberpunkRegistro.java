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
import main.Admin.AdminCuenta.PerfilDAO;

public class CyberpunkRegistro extends JFrame {

    // Paleta de colores unificada con CyberpunkLogin
    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(242, 5, 203);
    public static final Color COLOR_TEXT_LIGHT = new Color(220, 245, 255);
    public static final Color COLOR_BOX_BG = new Color(6, 12, 24, 230); // Fondo translúcido

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JTextField txtCorreo;
    private JButton btnVolver;
    private JButton btnRegistrar;
    private RegistroBackgroundPanel panelFondo;
    private Font pixelFont;
    private Font smallPixelFont;

    private int mouseX, mouseY; 

    public CyberpunkRegistro() {
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
        configurarEventos(); 
    }

    private void configurarVentana() {
        setUndecorated(true); // Ventana plana sin bordes de Windows/OSX
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        panelFondo = new RegistroBackgroundPanel();
        panelFondo.setLayout(null);
        setContentPane(panelFondo);
        
        // Fuentes retro tipográficas
        try {
            pixelFont = new Font("Pixel Emulator", Font.BOLD, 15);
            smallPixelFont = new Font("Pixel Emulator", Font.PLAIN, 12);
        } catch (Exception e) {
            pixelFont = new Font("Monospaced", Font.BOLD, 15);
            smallPixelFont = new Font("Monospaced", Font.PLAIN, 12);
        }

        // --- BARRA SUPERIOR RETRO (Idéntica a CyberpunkLogin) ---
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

        JLabel lblTituloBarra = new JLabel(" 8-BIT SOUL // SECURE REGISTRATION SYSTEM");
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
            @Override public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) { setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY); }
        });
    }

    private void inicializarComponentes() {
        // --- PANEL CENTRAL CONTENEDOR (Estilo CyberpunkLogin) ---
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
        // Se amplió la altura a 390 para acomodar los 3 inputs cómodamente
        contenedorCentral.setBounds(270, 50, 360, 390); 
        panelFondo.add(contenedorCentral);

        JLabel lblWelcome = new JLabel("REGISTRARSE");
        lblWelcome.setFont(pixelFont.deriveFont(Font.BOLD, 18f));
        lblWelcome.setForeground(COLOR_CYAN);
        lblWelcome.setBounds(30, 20, 300, 30);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        contenedorCentral.add(lblWelcome);

        // Input: Usuario
        JLabel lblUser = new JLabel("USUARIO:");
        lblUser.setFont(smallPixelFont);
        lblUser.setForeground(COLOR_TEXT_LIGHT);
        lblUser.setBounds(30, 65, 300, 20);
        contenedorCentral.add(lblUser);

        txtUser = new RetroTextField(32);
        txtUser.setBounds(30, 85, 300, 38);
        contenedorCentral.add(txtUser);

        // Input: Contraseña
        JLabel lblPass = new JLabel("CONTRASEÑA:");
        lblPass.setFont(smallPixelFont);
        lblPass.setForeground(COLOR_TEXT_LIGHT);
        lblPass.setBounds(30, 135, 300, 20);
        contenedorCentral.add(lblPass);

        txtPass = new RetroPasswordField(64);
        txtPass.setBounds(30, 155, 300, 38);
        contenedorCentral.add(txtPass);

        // Input: Correo Electrónico
        JLabel lblCorreo = new JLabel("CORREO ELECTRÓNICO:");
        lblCorreo.setFont(smallPixelFont);
        lblCorreo.setForeground(COLOR_TEXT_LIGHT);
        lblCorreo.setBounds(30, 205, 300, 20);
        contenedorCentral.add(lblCorreo);

        txtCorreo = new RetroTextField(64);
        txtCorreo.setBounds(30, 225, 300, 38);
        contenedorCentral.add(txtCorreo);

        // Botón: Registrar
        btnRegistrar = new RetroButton("REGISTRARSE");
        btnRegistrar.setBounds(30, 300, 300, 45);
        btnRegistrar.setFont(pixelFont);
        contenedorCentral.add(btnRegistrar);

        // --- BOTONES EXTERNOS EN LA BASE ---
        btnVolver = new JButton("← VOLVER AL MENU");
        btnVolver.setFont(pixelFont.deriveFont(12f));
        btnVolver.setForeground(COLOR_TEXT_LIGHT);
        btnVolver.setContentAreaFilled(false);
        btnVolver.setBorderPainted(false);
        btnVolver.setFocusPainted(false);
        btnVolver.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnVolver.setBounds(40, 455, 200, 30);
        btnVolver.setHorizontalAlignment(SwingConstants.LEFT);
        
        btnVolver.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btnVolver.setForeground(COLOR_CYAN); }
            @Override public void mouseExited(MouseEvent e) { btnVolver.setForeground(COLOR_TEXT_LIGHT); }
        });
        panelFondo.add(btnVolver);
    }

    private void configurarEventos() {
        // Evento volver
        btnVolver.addActionListener(e -> {
            System.out.println("[SYSTEM] Volviendo al menú raíz...");
            MenuPrincipal menu = new MenuPrincipal();
            menu.setVisible(true);
            this.dispose();
        });

        // Evento Registrarse
        btnRegistrar.addActionListener(e -> {
            String usuario = getUsuario().trim();
            String contrasena = getContrasena().trim();
            String correo = getCorreo().trim();

            if (usuario.isEmpty() || contrasena.isEmpty() || correo.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "ERROR: Todos los campos son obligatorios para el registro.",
                        "CRITICAL ERROR",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                boolean registrado = registrarUsuario(usuario, correo, contrasena);

                if (registrado) {
                    JOptionPane.showMessageDialog(this,
                            "Usuario registrado correctamente.",
                            "ÉXITO",
                            JOptionPane.INFORMATION_MESSAGE);

                    MenuPrincipal menu = new MenuPrincipal();
                    menu.setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al registrar usuario.",
                            "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private boolean registrarUsuario(String usuario, String correo, String contrasena) {
        try {
            PerfilDAO perfilDAO = new PerfilDAO();
            int rolDefault = 2; // Usuario estándar
            return perfilDAO.insertarUsuarioNuevo(usuario, correo, contrasena, rolDefault);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + e.getMessage(),
                    "ERROR",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // --- GETTERS ---
    public String getUsuario() { return txtUser.getText(); }
    public String getContrasena() { return new String(txtPass.getPassword()); }
    public String getCorreo() { return txtCorreo.getText(); }
    public JButton getBtnRegistrar() { return btnRegistrar; }
    public JButton getBtnVolver() { return btnVolver; }
}

// ============================================================
// COMPONENTES DE RENDERIZADO RETRO REUTILIZADOS
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
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkRegistro.COLOR_CYAN);
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
        g2d.setColor(new Color(12, 18, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (focusActivo) {
            g2d.setColor(CyberpunkRegistro.COLOR_MAGENTA);
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
        setOpaque(false);
        setForeground(Color.WHITE);
        setCaretColor(CyberpunkRegistro.COLOR_CYAN);
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
        g2d.setColor(new Color(12, 18, 30));
        g2d.fillRect(0, 0, getWidth(), getHeight());

        if (focusActivo) {
            g2d.setColor(CyberpunkRegistro.COLOR_MAGENTA);
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
        if (mouseEncima) {
            g2d.setColor(CyberpunkRegistro.COLOR_CYAN); 
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(18, 26, 44));
            g2d.fillRect(0, 0, getWidth(), getHeight());
            g2d.setColor(CyberpunkRegistro.COLOR_MAGENTA);
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
        g2d.dispose();
        super.paintComponent(g);
    }
}

class RegistroBackgroundPanel extends JPanel {
    private Image imagenFondo;
    public RegistroBackgroundPanel() {
        try {
            // Carga la misma imagen o una específica para el Registro si existe
            URL url = getClass().getResource("/imagenes/FondoLogin.png");
            if (url != null) imagenFondo = new ImageIcon(url).getImage();
        } catch (Exception e) {
            System.err.println("Error cargando el fondo del Registro.");
        }
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(6, 12, 24));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}