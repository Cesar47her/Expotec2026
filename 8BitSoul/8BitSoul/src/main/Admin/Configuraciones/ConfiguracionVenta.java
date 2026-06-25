package main.Admin.Configuraciones;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import main.Util.ReproducirSonido;
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

    private JSlider slVolumenGeneral, slMusica, slEfectos;
    private JRadioButton rbPequeno, rbMediano, rbGrande;
    private ButtonGroup grupoTamanoBotones;
    
    private JTextField txtArriba, txtAbajo, txtIzquierda, txtDerecha;
    private JTextField txtSaltar, txtAtacar, txtInventario, txtPausa;
    
    private JButton btnGuardarCambios, btnRestaurarPredeterminados;
    private Font pixelFont;
    private int mouseX, mouseY;

    private final ConfiguracionController controller;
    private final int idUsuarioActual = 1; 

    // Almacén de los botones del teclado virtual para poder cambiarles el color dinámicamente
    private final Map<String, JButton> mapaBotonesTeclado = new HashMap<>();

    public ConfiguracionVenta(Window padre) {
        super(padre, ModalityType.APPLICATION_MODAL);
        this.controller = new ConfiguracionController();
        configurarVentana();
        initComponentes();
        // 1. Carga desde la base de datos
        controller.cargarConfiguracionEnPantalla(
            idUsuarioActual, slVolumenGeneral, rbPequeno, rbMediano, rbGrande, 
            txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa
        );
        
        // 2. Escuchar cambios en los inputs para encender/apagar teclas
        añadirListenersMapeoTeclado();
        
        // 3. Forzar primer renderizado de luces en el teclado
        actualizarLucesTeclado();
        
        // 4. Enlazar otros eventos (Sliders)
        enlazarEventosDinamicos();
    }

    private void configurarVentana() {
        setUndecorated(true); 
        setSize(1020, 640); // Ajustado un poco el ancho para el teclado de forma cómoda
        setLocationRelativeTo(getOwner()); 
        setResizable(false);
        setBackground(new Color(0, 0, 0, 0)); 
        pixelFont = new Font("Monospaced", Font.BOLD, 12);
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

        JLabel lblTituloVentana = new JLabel(" ⚙ CORE_SYS // CONFIGURACIÓN GENERAL Y MAPEADO DE ENTORNO");
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
        btnCerrar.addActionListener(e -> this.dispose()); 
        barraSuperior.add(btnCerrar);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) { setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY); }
        });

        // --- COLUMNA IZQUIERDA (CONFIGURACIONES) ---

        // Sección Audio
        JPanel panelAudio = createContenedorCyber(COLOR_CYAN);
        panelAudio.setBounds(20, 50, 480, 140);
        panelPrincipal.add(panelAudio);

        JLabel lblAudioTitulo = new JLabel("⚡ MODIFICAR AUDIO");
        lblAudioTitulo.setFont(pixelFont);
        lblAudioTitulo.setForeground(COLOR_CYAN);
        lblAudioTitulo.setBounds(15, 10, 300, 20);
        panelAudio.add(lblAudioTitulo);

        slVolumenGeneral = createSliderCyber(); slVolumenGeneral.setBounds(150, 40, 260, 30); panelAudio.add(slVolumenGeneral);
        panelAudio.add(createLabelCyber("General", COLOR_TEXT_LIGHT)).setBounds(25, 42, 110, 20);
        JLabel lblValorVol = createLabelCyber("80%", COLOR_CYAN); lblValorVol.setBounds(420, 42, 50, 20); panelAudio.add(lblValorVol);

        slMusica = createSliderCyber(); slMusica.setBounds(150, 70, 260, 30); slMusica.setValue(70); panelAudio.add(slMusica);
        panelAudio.add(createLabelCyber("Música", COLOR_TEXT_LIGHT)).setBounds(25, 72, 110, 20);
        JLabel lblVM = createLabelCyber("70%", COLOR_CYAN); lblVM.setBounds(420, 72, 50, 20); panelAudio.add(lblVM);

        slEfectos = createSliderCyber(); slEfectos.setBounds(150, 100, 260, 30); slEfectos.setValue(90); panelAudio.add(slEfectos);
        panelAudio.add(createLabelCyber("Efectos", COLOR_TEXT_LIGHT)).setBounds(25, 102, 110, 20);
        JLabel lblVE = createLabelCyber("90%", COLOR_CYAN); lblVE.setBounds(420, 102, 50, 20); panelAudio.add(lblVE);

        // Sección Tamaño de Botones
        JPanel panelTamano = createContenedorCyber(COLOR_MAGENTA);
        panelTamano.setBounds(20, 200, 480, 90);
        panelPrincipal.add(panelTamano);

        JLabel lblTamanoTitulo = new JLabel("⚡ ESCALADO DEL HUD");
        lblTamanoTitulo.setFont(pixelFont);
        lblTamanoTitulo.setForeground(COLOR_MAGENTA);
        lblTamanoTitulo.setBounds(15, 10, 300, 20);
        panelTamano.add(lblTamanoTitulo);

        rbPequeno = createRadioCyber("PEQUEÑO", COLOR_MAGENTA); rbPequeno.setBounds(25, 45, 120, 30); panelTamano.add(rbPequeno);
        rbMediano = createRadioCyber("MEDIANO", COLOR_MAGENTA); rbMediano.setBounds(180, 45, 120, 30); panelTamano.add(rbMediano);
        rbGrande = createRadioCyber("GRANDE", COLOR_MAGENTA); rbGrande.setBounds(335, 45, 120, 30); panelTamano.add(rbGrande);

        grupoTamanoBotones = new ButtonGroup();
        grupoTamanoBotones.add(rbPequeno); grupoTamanoBotones.add(rbMediano); grupoTamanoBotones.add(rbGrande);

        // Sección Inputs Controles
        JPanel panelControles = createContenedorCyber(COLOR_CYAN);
        panelControles.setBounds(20, 300, 480, 260);
        panelPrincipal.add(panelControles);

        JLabel lblControlesTitulo = new JLabel("⚡ ASIGNACIÓN DE TECLAS");
        lblControlesTitulo.setFont(pixelFont);
        lblControlesTitulo.setForeground(COLOR_CYAN);
        lblControlesTitulo.setBounds(15, 10, 400, 20);
        panelControles.add(lblControlesTitulo);

        int xL1 = 20, xT1 = 120, xL2 = 240, xT2 = 350;
        panelControles.add(createLabelCyber("Arriba:", COLOR_TEXT_LIGHT)).setBounds(xL1, 45, 90, 25); txtArriba = createTextFieldControl(); txtArriba.setBounds(xT1, 45, 80, 25); panelControles.add(txtArriba);
        panelControles.add(createLabelCyber("Abajo:", COLOR_TEXT_LIGHT)).setBounds(xL1, 80, 90, 25); txtAbajo = createTextFieldControl(); txtAbajo.setBounds(xT1, 80, 80, 25); panelControles.add(txtAbajo);
        panelControles.add(createLabelCyber("Izquierda:", COLOR_TEXT_LIGHT)).setBounds(xL1, 115, 90, 25); txtIzquierda = createTextFieldControl(); txtIzquierda.setBounds(xT1, 115, 80, 25); panelControles.add(txtIzquierda);
        panelControles.add(createLabelCyber("Derecha:", COLOR_TEXT_LIGHT)).setBounds(xL1, 150, 90, 25); txtDerecha = createTextFieldControl(); txtDerecha.setBounds(xT1, 150, 80, 25); panelControles.add(txtDerecha);

        panelControles.add(createLabelCyber("Saltar:", COLOR_TEXT_LIGHT)).setBounds(xL2, 45, 90, 25); txtSaltar = createTextFieldControl(); txtSaltar.setBounds(xT2, 45, 100, 25); panelControles.add(txtSaltar);
        panelControles.add(createLabelCyber("Atacar:", COLOR_TEXT_LIGHT)).setBounds(xL2, 80, 90, 25); txtAtacar = createTextFieldControl(); txtAtacar.setBounds(xT2, 80, 100, 25); panelControles.add(txtAtacar);
        panelControles.add(createLabelCyber("Inventario:", COLOR_TEXT_LIGHT)).setBounds(xL2, 115, 90, 25); txtInventario = createTextFieldControl(); txtInventario.setBounds(xT2, 115, 100, 25); panelControles.add(txtInventario);
        panelControles.add(createLabelCyber("Pausa:", COLOR_TEXT_LIGHT)).setBounds(xL2, 150, 90, 25); txtPausa = createTextFieldControl(); txtPausa.setBounds(xT2, 150, 100, 25); panelControles.add(txtPausa);

        btnRestaurarPredeterminados = createBotonCyberEspecial("🔄 PREDETERMINADOS", COLOR_CYAN);
        btnRestaurarPredeterminados.setBounds(20, 205, 200, 35);
        btnRestaurarPredeterminados.addActionListener(e -> accionRestaurarPredeterminados());
        panelControles.add(btnRestaurarPredeterminados);

        btnGuardarCambios = createBotonCyberEspecial("💾 GUARDAR CONFIGURACIÓN", COLOR_MAGENTA);
        btnGuardarCambios.setBounds(240, 205, 210, 35);
        btnGuardarCambios.addActionListener(e -> accionGuardarCambios());
        panelControles.add(btnGuardarCambios);


        // --- COLUMNA DERECHA (TECLADO VIRTUAL NEÓN) ---
        JPanel panelTeclado = createContenedorCyber(COLOR_CYAN);
        panelTeclado.setBounds(515, 50, 485, 510);
        panelPrincipal.add(panelTeclado);

        JLabel lblTecladoTitulo = new JLabel("⌨️ MONITOR DE ENTORNO // VISTA DE HARDWARE");
        lblTecladoTitulo.setFont(pixelFont);
        lblTecladoTitulo.setForeground(COLOR_CYAN);
        lblTecladoTitulo.setBounds(15, 10, 400, 20);
        panelTeclado.add(lblTecladoTitulo);

        construirTecladoFisicoUI(panelTeclado);
    }

    /**
     * Dibuja los botones simulando la matriz de un teclado QWERTY estándar.
     */
    private void construirTecladoFisicoUI(JPanel contenedor) {
        // Fila 1
        String[] f1 = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
        int xStart = 20, yStart = 50, btnSize = 40, gap = 5;

        for (int i = 0; i < f1.length; i++) {
            crearTecla(f1[i], xStart + i * (btnSize + gap), yStart, btnSize, btnSize, contenedor);
        }
        // Fila 2 (QWERTY)
        String[] f2 = {"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
        int xF2 = xStart + 15; // Desfase típico de teclado
        for (int i = 0; i < f2.length; i++) {
            crearTecla(f2[i], xF2 + i * (btnSize + gap), yStart + 45, btnSize, btnSize, contenedor);
        }
        // Fila 3 (ASDF)
        String[] f3 = {"A", "S", "D", "F", "G", "H", "J", "K", "L", "Ñ"};
        int xF3 = xStart + 25;
        for (int i = 0; i < f3.length; i++) {
            crearTecla(f3[i], xF3 + i * (btnSize + gap), yStart + 90, btnSize, btnSize, contenedor);
        }
        // Fila 4 (ZXCV)
        String[] f4 = {"Z", "X", "C", "V", "B", "N", "M"};
        int xF4 = xStart + 35;
        for (int i = 0; i < f4.length; i++) {
            crearTecla(f4[i], xF4 + i * (btnSize + gap), yStart + 135, btnSize, btnSize, contenedor);
        }
        // Fila 5: Barra Espaciadora
        crearTecla("ESPACIO", xStart + 90, yStart + 180, 250, 35, contenedor);
    }

    private void crearTecla(String llave, int x, int y, int w, int h, JPanel panel) {
        JButton btnTecla = new JButton(llave) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Si el botón está marcado con el color Cyan, es una tecla activa/resaltada
                if (getForeground() == COLOR_CYAN) {
                    g2d.setColor(new Color(0, 100, 120, 200)); // Fondo neón opaco
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.setColor(COLOR_CYAN);
                    g2d.setStroke(new BasicStroke(2.0f));
                } else {
                    g2d.setColor(COLOR_KEY_DEFAULT); // Fondo apagado por defecto
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
        mapaBotonesTeclado.put(llave.toUpperCase(), btnTecla); // Registrar en el inventario de teclas
    }

    /**
     * Revisa el texto actual de los inputs y enciende las teclas correspondientes en la interfaz gráfica
     */
    private void actualizarLucesTeclado() {
        // 1. Apagar todas primero
        for (JButton btn : mapaBotonesTeclado.values()) {
            btn.setForeground(COLOR_TEXT_LIGHT);
        }

        // 2. Encender sólo las que están escritas en los campos de texto
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
        
        // Redibujar el contenedor del teclado para aplicar cambios visuales
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
        slMusica.addChangeListener(e -> ((JLabel)((JPanel)getContentPane().getComponent(1)).getComponent(6)).setText(slMusica.getValue() + "%"));
        slEfectos.addChangeListener(e -> ((JLabel)((JPanel)getContentPane().getComponent(1)).getComponent(9)).setText(slEfectos.getValue() + "%"));
    }

    private void accionGuardarCambios() {
        boolean exito = controller.recolectarYGuardar(
            idUsuarioActual, slVolumenGeneral, rbPequeno, rbGrande, 
            txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa
        );

        if (exito) {
            JOptionPane.showMessageDialog(this, "Configuración grabada con éxito en la BD.", "CORE STATUS", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Error crítico. Sentencia SQL rechazada.", "CORE ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void accionRestaurarPredeterminados() {
        slVolumenGeneral.setValue(80); slMusica.setValue(70); slEfectos.setValue(90);
        rbMediano.setSelected(true);
        txtArriba.setText("W"); txtAbajo.setText("S"); txtIzquierda.setText("A"); txtDerecha.setText("D");
        txtSaltar.setText("ESPACIO"); txtAtacar.setText("J"); txtInventario.setText("I"); txtPausa.setText("P");
        
        ReproducirSonido.asignarVolumen(0.80);
        actualizarLucesTeclado();
        controller.recolectarYGuardar(idUsuarioActual, slVolumenGeneral, rbPequeno, rbGrande, txtArriba, txtAbajo, txtIzquierda, txtDerecha, txtSaltar, txtAtacar, txtInventario, txtPausa);
    }

    // --- MÉTODOS AUXILIARES DE ESTILIZADO DE COMPONENTES ---

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

    private JRadioButton createRadioCyber(String texto, Color colorNeon) {
        JRadioButton rb = new JRadioButton(texto);
        rb.setFont(pixelFont); rb.setBackground(COLOR_BG_DARK); rb.setForeground(colorNeon);
        rb.setFocusPainted(false); rb.setOpaque(false);
        return rb;
    }

    private JTextField createTextFieldControl() {
        JTextField txt = new JTextField();
        txt.setBackground(new Color(12, 18, 32, 220)); txt.setForeground(COLOR_CYAN); txt.setCaretColor(COLOR_CYAN);
        txt.setBorder(new LineBorder(new Color(0, 150, 160), 1)); txt.setHorizontalAlignment(JTextField.CENTER);
        txt.setFont(pixelFont);
        return txt;
    }
}