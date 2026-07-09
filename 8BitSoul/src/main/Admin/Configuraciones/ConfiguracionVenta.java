package main.Admin.Configuraciones;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import main.Util.ReproducirSonido;
import main.Util.ImpresoraTicket; 
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionVenta extends JDialog {

    public static final Color COLOR_CYAN = new Color(0, 240, 255);
    public static final Color COLOR_MAGENTA = new Color(242, 5, 203);
    public static final Color COLOR_TEXT_LIGHT = new Color(220, 245, 255);
    public static final Color COLOR_BG_DARK = new Color(6, 12, 24);
    
    public static final Color COLOR_BOX_BG = new Color(6, 12, 24, 180); 
    public static final Color COLOR_PANEL_TRANSPARENTE = new Color(4, 8, 16, 210);
    public static final Color COLOR_BTN_BG = new Color(12, 24, 44, 230);
    public static final Color COLOR_KEY_DEFAULT = new Color(20, 35, 60, 200);

    private JSlider slVolumenGeneral;
    
    private JTextField txtArriba, txtAbajo, txtIzquierda, txtDerecha;
    private JTextField txtSaltar, txtAtacar, txtInventario, txtPausa;
    
    private JButton btnGuardarCambios, btnRestaurarPredeterminados;
    private Font pixelFont;
    private int mouseX, mouseY;

    private final ConfiguracionController controller;
    private final int idUsuarioActual; 

    private final Map<String, JButton> mapaBotonesTeclado = new HashMap<>();

    public ConfiguracionVenta(Window padre, int idUsuarioLogueado) {
        super(padre, ModalityType.DOCUMENT_MODAL);
        this.idUsuarioActual = idUsuarioLogueado; 
        this.controller = new ConfiguracionController();
        
        configurarVentana();
        initComponentes();
        
        // Se pasan objetos nulos o simulados para los componentes removidos para no romper la firma del controller
        controller.cargarConfiguracionEnPantalla(
            idUsuarioActual, slVolumenGeneral, new JRadioButton(), new JRadioButton(), new JRadioButton(), 
            txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa
        );
        
        añadirListenersMapeoTeclado();
        actualizarLucesTeclado();
        enlazarEventosDinamicos();
    }

    private void configurarVentana() {
        setUndecorated(true); 
        setSize(1020, 540); // Altura optimizada al remover el HUD y sliders extras
        setLocationRelativeTo(getOwner()); 
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0)); 
        pixelFont = new Font("Monospaced", Font.BOLD, 12);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    private void initComponentes() {
        JPanel panelPrincipal = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR_PANEL_TRANSPARENTE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(COLOR_CYAN);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        panelPrincipal.setOpaque(false);
        setContentPane(panelPrincipal);

        // Barra Superior
        JPanel barraSuperior = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(new Color(4, 8, 16, 230));
                g.fillRect(0, 0, getWidth(), getHeight());
                g.setColor(COLOR_CYAN);
                g.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            }
        };
        barraSuperior.setOpaque(false);
        barraSuperior.setBounds(0, 0, 1020, 30);
        panelPrincipal.add(barraSuperior);

        JLabel lblTituloVentana = new JLabel(" ⚙ CORE_SYS // CONFIGURACIÓN DE AUDIO Y CONTROLES");
        lblTituloVentana.setFont(pixelFont);
        lblTituloVentana.setForeground(COLOR_TEXT_LIGHT);
        lblTituloVentana.setBounds(15, 0, 600, 30);
        barraSuperior.add(lblTituloVentana);

        JButton btnCerrar = new JButton("[X]");
        btnCerrar.setFont(pixelFont);
        btnCerrar.setForeground(COLOR_MAGENTA);
        btnCerrar.setContentAreaFilled(false);
        btnCerrar.setBorderPainted(false);
        btnCerrar.setFocusPainted(false);
        btnCerrar.setBounds(970, 0, 50, 30);
        btnCerrar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btnCerrar.addActionListener(e -> cerrarVentanaLimpio()); 
        barraSuperior.add(btnCerrar);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) { setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY); }
        });

        // --- COLUMNA IZQUIERDA ---
        // Sección Audio (Solo Volumen General)
        JPanel panelAudio = createContenedorCyber(COLOR_CYAN);
        panelAudio.setBounds(20, 50, 480, 90);
        panelPrincipal.add(panelAudio);

        JLabel lblAudioTitulo = new JLabel("⚡ CONTROL DE AUDIO MASTER");
        lblAudioTitulo.setFont(pixelFont);
        lblAudioTitulo.setForeground(COLOR_CYAN);
        lblAudioTitulo.setBounds(15, 10, 300, 20);
        panelAudio.add(lblAudioTitulo);

        slVolumenGeneral = createSliderCyber(); 
        slVolumenGeneral.setBounds(150, 42, 260, 30); 
        panelAudio.add(slVolumenGeneral);
        
        panelAudio.add(createLabelCyber("Vol. General", COLOR_TEXT_LIGHT)).setBounds(25, 44, 110, 20);
        JLabel lblValorVol = createLabelCyber("80%", COLOR_CYAN); 
        lblValorVol.setBounds(420, 44, 50, 20); 
        panelAudio.add(lblValorVol);

        // Sección Inputs Controles (Alineada hacia arriba)
        JPanel panelControles = createContenedorCyber(COLOR_CYAN);
        panelControles.setBounds(20, 155, 480, 355);
        panelPrincipal.add(panelControles);

        JLabel lblControlesTitulo = new JLabel("⚡ ASIGNACIÓN DE TECLAS DE ENTORNO");
        lblControlesTitulo.setFont(pixelFont);
        lblControlesTitulo.setForeground(COLOR_CYAN);
        lblControlesTitulo.setBounds(15, 15, 400, 20);
        panelControles.add(lblControlesTitulo);

        int xL1 = 25, xT1 = 125, xL2 = 245, xT2 = 355;
        
        panelControles.add(createLabelCyber("Arriba:", COLOR_TEXT_LIGHT)).setBounds(xL1, 55, 90, 25); 
        txtArriba = createTextFieldControl(); txtArriba.setBounds(xT1, 55, 80, 25); panelControles.add(txtArriba);
        
        panelControles.add(createLabelCyber("Abajo:", COLOR_TEXT_LIGHT)).setBounds(xL1, 100, 90, 25); 
        txtAbajo = createTextFieldControl(); txtAbajo.setBounds(xT1, 100, 80, 25); panelControles.add(txtAbajo);
        
        panelControles.add(createLabelCyber("Izquierda:", COLOR_TEXT_LIGHT)).setBounds(xL1, 145, 90, 25); 
        txtIzquierda = createTextFieldControl(); txtIzquierda.setBounds(xT1, 145, 80, 25); panelControles.add(txtIzquierda);
        
        panelControles.add(createLabelCyber("Derecha:", COLOR_TEXT_LIGHT)).setBounds(xL1, 190, 90, 25); 
        txtDerecha = createTextFieldControl(); txtDerecha.setBounds(xT1, 190, 80, 25); panelControles.add(txtDerecha);

        panelControles.add(createLabelCyber("Saltar:", COLOR_TEXT_LIGHT)).setBounds(xL2, 55, 90, 25); 
        txtSaltar = createTextFieldControl(); txtSaltar.setBounds(xT2, 55, 100, 25); panelControles.add(txtSaltar);
        
        panelControles.add(createLabelCyber("Atacar:", COLOR_TEXT_LIGHT)).setBounds(xL2, 100, 90, 25); 
        txtAtacar = createTextFieldControl(); txtAtacar.setBounds(xT2, 100, 100, 25); panelControles.add(txtAtacar);
        
        panelControles.add(createLabelCyber("Inventario:", COLOR_TEXT_LIGHT)).setBounds(xL2, 145, 90, 25); 
        txtInventario = createTextFieldControl(); txtInventario.setBounds(xT2, 145, 100, 25); panelControles.add(txtInventario);
        
        panelControles.add(createLabelCyber("Pausa:", COLOR_TEXT_LIGHT)).setBounds(xL2, 190, 90, 25); 
        txtPausa = createTextFieldControl(); txtPausa.setBounds(xT2, 190, 100, 25); panelControles.add(txtPausa);

        btnRestaurarPredeterminados = createBotonCyberEspecial("🔄 PREDETERMINADOS", COLOR_CYAN);
        btnRestaurarPredeterminados.setBounds(20, 290, 210, 40);
        btnRestaurarPredeterminados.addActionListener(e -> accionRestaurarPredeterminados());
        panelControles.add(btnRestaurarPredeterminados);

        btnGuardarCambios = createBotonCyberEspecial("💾 GUARDAR CAMBIOS", COLOR_MAGENTA);
        btnGuardarCambios.setBounds(245, 290, 215, 40);
        btnGuardarCambios.addActionListener(e -> accionGuardarCambios());
        panelControles.add(btnGuardarCambios);

        // --- COLUMNA DERECHA ---
        JPanel panelTeclado = createContenedorCyber(COLOR_CYAN);
        panelTeclado.setBounds(515, 50, 485, 460);
        panelPrincipal.add(panelTeclado);

        JLabel lblTecladoTitulo = new JLabel("⌨️ MONITOR DE HARDWARE Y MAPEADO");
        lblTecladoTitulo.setFont(pixelFont);
        lblTecladoTitulo.setForeground(COLOR_CYAN);
        lblTecladoTitulo.setBounds(15, 10, 400, 20);
        panelTeclado.add(lblTecladoTitulo);

        construirTecladoFisicoUI(panelTeclado);
    }

    private void cerrarVentanaLimpio() {
        try {
            ReproducirSonido.asignarVolumen(0.0);
        } catch (Exception ignored) {}

        if (getOwner() != null) {
            getOwner().setEnabled(true);
            getOwner().toFront();
        }
        
        this.setVisible(false);
        this.dispose();
    }

    private void construirTecladoFisicoUI(JPanel contenedor) {
        String[] f1 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        int xStart = 20, yStart = 50, btnSize = 40, gap = 5;

        for (int i = 0; i < f1.length; i++) {
            crearTecla(f1[i], xStart + i * (btnSize + gap), yStart, btnSize, btnSize, contenedor);
        }
        String[] f2 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        int xF2 = xStart + 15;
        for (int i = 0; i < f2.length; i++) {
            crearTecla(f2[i], xF2 + i * (btnSize + gap), yStart + 45, btnSize, btnSize, contenedor);
        }
        String[] f3 = {"A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ"};
        int xF3 = xStart + 25;
        for (int i = 0; i < f3.length; i++) {
            crearTecla(f3[i], xF3 + i * (btnSize + gap), yStart + 90, btnSize, btnSize, contenedor);
        }
        String[] f4 = {"Z", "X", "C", "V", "B", "N", "M"};
        int xF4 = xStart + 35;
        for (int i = 0; i < f4.length; i++) {
            crearTecla(f4[i], xF4 + i * (btnSize + gap), yStart + 135, btnSize, btnSize, contenedor);
        }
        crearTecla("ESPACIO", xStart + 90, yStart + 180, 250, 35, contenedor);
    }

    private void crearTecla(String llave, int x, int y, int w, int h, JPanel panel) {
        JButton btnTecla = new JButton(llave) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                if (getForeground() == COLOR_CYAN) {
                    g2d.setColor(new Color(0, 100, 120, 200)); 
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(COLOR_CYAN);
                    g2d.setStroke(new BasicStroke(2.0f));
                } else {
                    g2d.setColor(COLOR_KEY_DEFAULT); 
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(new Color(40, 70, 100));
                    g2d.setStroke(new BasicStroke(1.0f));
                }
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnTecla.setFont(new Font("Monospaced", Font.BOLD, 11));
        btnTecla.setForeground(COLOR_TEXT_LIGHT);
        btnTecla.setContentAreaFilled(false);
        btnTecla.setBorderPainted(false);
        btnTecla.setFocusPainted(false);
        btnTecla.setBounds(x, y, w, h);
        
        panel.add(btnTecla);
        mapaBotonesTeclado.put(llave.toUpperCase(), btnTecla); 
    }

    private void actualizarLucesTeclado() {
        for (JButton btn : mapaBotonesTeclado.values()) {
            btn.setForeground(COLOR_TEXT_LIGHT);
        }

        String[] llavesAEncender = {
            txtArriba.getText().trim().toUpperCase(),
            txtAbajo.getText().trim().toUpperCase(),
            txtIzquierda.getText().trim().toUpperCase(),
            txtDerecha.getText().trim().toUpperCase(),
            txtSaltar.getText().trim().toUpperCase(),
            txtAtacar.getText().trim().toUpperCase(),
            txtInventario.getText().trim().toUpperCase(),
            txtPausa.getText().trim().toUpperCase()
        };

        for (String llave : llavesAEncender) {
            if (mapaBotonesTeclado.containsKey(llave)) {
                mapaBotonesTeclado.get(llave).setForeground(COLOR_CYAN);
            }
        }
        
        if (!mapaBotonesTeclado.isEmpty()) {
            mapaBotonesTeclado.values().iterator().next().getParent().repaint();
        }
    }

    private void añadirListenersMapeoTeclado() {
        DocumentListener dl = new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { actualizarLucesTeclado(); }
            @Override public void removeUpdate(DocumentEvent e) { actualizarLucesTeclado(); }
            @Override public void changedUpdate(DocumentEvent e) { actualizarLucesTeclado(); }
        };

        txtArriba.getDocument().addDocumentListener(dl);
        txtAbajo.getDocument().addDocumentListener(dl);
        txtIzquierda.getDocument().addDocumentListener(dl);
        txtDerecha.getDocument().addDocumentListener(dl);
        txtSaltar.getDocument().addDocumentListener(dl);
        txtAtacar.getDocument().addDocumentListener(dl);
        txtInventario.getDocument().addDocumentListener(dl);
        txtPausa.getDocument().addDocumentListener(dl);
    }

    private void enlazarEventosDinamicos() {
        slVolumenGeneral.addChangeListener(e -> {
            ((JLabel)((JPanel)getContentPane().getComponent(1)).getComponent(3)).setText(slVolumenGeneral.getValue() + "%");
            if (!slVolumenGeneral.getValueIsAdjusting()) {
                ReproducirSonido.asignarVolumen(slVolumenGeneral.getValue() / 100.0);
            }
        });
    }

    private void accionGuardarCambios() {
        // Mantiene la compatibilidad con el controlador enviando RadioButtons vacíos en los campos quitados
        boolean exito = controller.recolectarYGuardar(
            idUsuarioActual, slVolumenGeneral, new JRadioButton(), new JRadioButton(), 
            txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "Configuración grabada con éxito en la BD.", "CORE STATUS", JOptionPane.INFORMATION_MESSAGE);
            ImpresoraTicket.imprimirConfiguracion(idUsuarioActual);
        } else {
            JOptionPane.showMessageDialog(this, "Error crítico. Sentencia SQL rechazada.", "CORE ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRestaurarPredeterminados() {
        slVolumenGeneral.setValue(80);
        txtArriba.setText("W"); txtAbajo.setText("S"); txtIzquierda.setText("A"); txtDerecha.setText("D");
        txtSaltar.setText("ESPACIO"); txtAtacar.setText("J"); txtInventario.setText("I"); txtPausa.setText("P");
        
        ReproducirSonido.asignarVolumen(0.80);
        actualizarLucesTeclado();
        
        controller.recolectarYGuardar(
            idUsuarioActual, slVolumenGeneral, new JRadioButton(), new JRadioButton(), 
            txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa
        );
    }

    // --- MÉTODOS AUXILIARES ---
    private JButton createBotonCyberEspecial(String texto, Color colorNeon) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(COLOR_BTN_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(colorNeon);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(pixelFont);
        btn.setForeground(colorNeon);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setOpaque(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(Color.WHITE); }
            @Override public void mouseExited(MouseEvent e) { btn.setForeground(colorNeon); }
        });
        return btn;
    }

    private JPanel createContenedorCyber(Color colorBorde) {
        JPanel panel = new JPanel(null) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BOX_BG); g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(colorBorde); g2d.setStroke(new BasicStroke(1.5f)); g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        panel.setOpaque(false);
        return panel;
    }

    private JLabel createLabelCyber(String texto, Color colorTexto) {
        JLabel label = new JLabel(texto);
        label.setFont(pixelFont); label.setForeground(colorTexto);
        return label;
    }

    private JSlider createSliderCyber() {
        JSlider slider = new JSlider(0, 100);
        slider.setBackground(COLOR_BG_DARK); slider.setForeground(COLOR_CYAN); slider.setOpaque(false);
        return slider;
    }

    private JTextField createTextFieldControl() {
        JTextField txt = new JTextField();
        txt.setBackground(new Color(12, 18, 32, 220)); txt.setForeground(COLOR_CYAN); txt.setCaretColor(COLOR_CYAN);
        txt.setBorder(new LineBorder(new Color(0, 150, 160), 1)); txt.setHorizontalAlignment(JTextField.CENTER);
        txt.setFont(pixelFont);
        return txt;
    }
}