package main.Admin.ActualizacionMusica;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.sql.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import main.Admin.PanelAdmin;
import main.Util.ReproducirSonido;
import main.Util.WindowManager;

public class PanelMusicaAdmin extends JFrame {

    // Paleta de Colores de la Barra y Neones
    private final Color CYAN = new Color(0, 240, 255);
    private final Color MAGENTA = new Color(242, 5, 203);
    private final Color OSCURO = new Color(6, 12, 24);
    private final Color CONTENEDOR_BG = new Color(11, 19, 36, 180);
    private final Color COLOR_BARRA_BG = new Color(10, 20, 38, 230);
    private final Color TEXTO_MUTED = new Color(150, 160, 180);

    private DefaultTableModel modeloTabla;
    private JTable tablaMusica;
    private JScrollPane scrollTabla;

    // Campos de entrada
    private JTextField txtTitulo, txtArtista, txtCategoria;
    private JRadioButton rbAmbiental, rbEvento;

    // Variables de Barra Superior de Control
    private JPanel barraSuperior;
    private JLabel lblTituloBarra;
    private JButton btnCerrar, btnMaximizar, btnMinimizar;
    private int mouseX, mouseY;
    private boolean esMaximizado = false;
    private Rectangle dimensionesPrevias;
    private Image imagenFondo;

    // Componentes de la interfaz
    private JLabel lblMainTitulo, lblMainSubtitulo;
    private JPanel panelIzquierdo, panelTabla, panelControles;
    private JButton btnGuardar, btnReproducir, btnDesconectar;

    // Referencia al panel padre para evitar cierres de app y duplicación de memoria
    private PanelAdmin panelAdminPadre;

    public PanelMusicaAdmin() {
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
    }

    public PanelMusicaAdmin(PanelAdmin panelAdminPadre) {
        this.panelAdminPadre = panelAdminPadre; // Guardamos la referencia original
        configurarVentana();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        inicializarComponentes();
    }

    private void configurarVentana() {
        setUndecorated(true);
        setSize(1200, 675);
        setMinimumSize(new Dimension(1100, 650));
        setLocationRelativeTo(null);
        WindowManager.getInstance().register(this);

        // Cargar imagen de fondo
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
                    g2d.setColor(OSCURO);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                }
            }
        };
        mainBackground.setLayout(null);
        setContentPane(mainBackground);

        getRootPane().setBorder(BorderFactory.createLineBorder(new Color(0, 240, 255, 60), 1));
    }

    private void inicializarComponentes() {
        // --- BARRA SUPERIOR PERSONALIZADA ---
        construirBarraSuperior();
        add(barraSuperior);

        // --- TÍTULOS DE LA APLICACIÓN ---
        lblMainTitulo = new JLabel("GESTIÓN DE MÚSICA");
        lblMainTitulo.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblMainTitulo.setForeground(Color.WHITE);
        add(lblMainTitulo);

        lblMainSubtitulo = new JLabel("Panel de administrador para añadir y gestionar música");
        lblMainSubtitulo.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lblMainSubtitulo.setForeground(TEXTO_MUTED);
        add(lblMainSubtitulo);

        // --- PANEL IZQUIERDO: AÑADIR NUEVA PISTA ---
        panelIzquierdo = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CONTENEDOR_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(0, 240, 255, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        panelIzquierdo.setLayout(null);
        panelIzquierdo.setOpaque(false);
        add(panelIzquierdo);

        JLabel lblAnadir = new JLabel("AÑADIR NUEVA PISTA");
        lblAnadir.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblAnadir.setForeground(CYAN);
        lblAnadir.setBounds(25, 20, 300, 20);
        panelIzquierdo.add(lblAnadir);

        JLabel lblTitulo = new JLabel("Título de la Pista");
        lblTitulo.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBounds(25, 60, 200, 20);
        panelIzquierdo.add(lblTitulo);

        txtTitulo = new CyberTextField();
        txtTitulo.setBounds(25, 85, 370, 35);
        panelIzquierdo.add(txtTitulo);

        JLabel lblArtista = new JLabel("Artista");
        lblArtista.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lblArtista.setForeground(Color.WHITE);
        lblArtista.setBounds(25, 140, 200, 20);
        panelIzquierdo.add(lblArtista);

        txtArtista = new CyberTextField();
        txtArtista.setBounds(25, 165, 370, 35);
        panelIzquierdo.add(txtArtista);

        JLabel lblCat = new JLabel("Álbum/Categoría");
        lblCat.setFont(new Font("Monospaced", Font.PLAIN, 13));
        lblCat.setForeground(Color.WHITE);
        lblCat.setBounds(25, 220, 200, 20);
        panelIzquierdo.add(lblCat);

        txtCategoria = new CyberTextField();
        txtCategoria.setBounds(25, 245, 370, 35);
        panelIzquierdo.add(txtCategoria);

        JButton btnSubir = crearBotonCyber("⬆ SELECCIONAR E INYECTAR ARCHIVO (WAV)", CYAN);
        btnSubir.setBounds(25, 315, 370, 45);
        btnSubir.addActionListener(e -> seleccionarArchivo());
        panelIzquierdo.add(btnSubir);

        // --- PANEL DER. SUPERIOR: TABLA DE PISTAS ---
        panelTabla = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CONTENEDOR_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(242, 5, 203, 80));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        panelTabla.setLayout(null);
        panelTabla.setOpaque(false);
        add(panelTabla);

        JLabel lblLista = new JLabel("LISTA DE PISTAS EXISTENTES");
        lblLista.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblLista.setForeground(MAGENTA);
        lblLista.setBounds(25, 15, 300, 20);
        panelTabla.add(lblLista);

        String[] columnas = {"NÚMERO", "TÍTULO", "ARTISTA", "CATEGORÍA", "ESTADO", "ID_DB"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        tablaMusica = new JTable(modeloTabla);
        estilizarTabla();

        scrollTabla = new JScrollPane(tablaMusica);
        scrollTabla.getViewport().setBackground(new Color(11, 19, 36, 120));
        scrollTabla.setOpaque(false);
        scrollTabla.getViewport().setOpaque(false);
        scrollTabla.setBorder(BorderFactory.createEmptyBorder());
        panelTabla.add(scrollTabla);

        // --- PANEL DER. INFERIOR: CONTROLES ---
        panelControles = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(CONTENEDOR_BG);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 15, 15));
                g2.setColor(new Color(242, 5, 203, 50));
                g2.setStroke(new BasicStroke(1.5f));
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 15, 15));
                g2.dispose();
            }
        };
        panelControles.setLayout(null);
        panelControles.setOpaque(false);
        add(panelControles);

        JLabel lblControles = new JLabel("CONTROLES DE PISTA SELECCIONADA");
        lblControles.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblControles.setForeground(MAGENTA);
        lblControles.setBounds(25, 15, 400, 20);
        panelControles.add(lblControles);

        JPanel panelRadio = new JPanel(null);
        panelRadio.setBackground(new Color(6, 12, 24, 60));
        panelRadio.setBorder(BorderFactory.createLineBorder(new Color(242, 5, 203, 30)));
        panelRadio.setBounds(25, 50, 360, 75);
        panelControles.add(panelRadio);

        rbAmbiental = new JRadioButton("AMBIENTAL");
        rbAmbiental.setFont(new Font("Monospaced", Font.BOLD, 13));
        rbAmbiental.setForeground(Color.WHITE);
        rbAmbiental.setOpaque(false);
        rbAmbiental.setBounds(20, 15, 140, 25);
        rbAmbiental.setSelected(true);

        rbEvento = new JRadioButton("EVENTO ESPECIAL");
        rbEvento.setFont(new Font("Monospaced", Font.BOLD, 13));
        rbEvento.setForeground(Color.WHITE);
        rbEvento.setOpaque(false);
        rbEvento.setBounds(170, 15, 180, 25);

        ButtonGroup grupoRadio = new ButtonGroup();
        grupoRadio.add(rbAmbiental);
        grupoRadio.add(rbEvento);
        panelRadio.add(rbAmbiental);
        panelRadio.add(rbEvento);

        JLabel subAmbiental = new JLabel("TIPO");
        subAmbiental.setFont(new Font("Monospaced", Font.PLAIN, 10));
        subAmbiental.setForeground(TEXTO_MUTED);
        subAmbiental.setBounds(40, 40, 100, 15);
        panelRadio.add(subAmbiental);

        JLabel subEvento = new JLabel("TIPO");
        subEvento.setFont(new Font("Monospaced", Font.PLAIN, 10));
        subEvento.setForeground(TEXTO_MUTED);
        subEvento.setBounds(190, 40, 100, 15);
        panelRadio.add(subEvento);

        // --- BOTONES DE COMANDO INFERIORES ---
        btnGuardar = crearBotonCyber("💾 GUARDAR CAMBIOS ESTADO", CYAN);
        btnGuardar.addActionListener(e -> guardarCambiosEstado());
        add(btnGuardar);

        btnReproducir = crearBotonCyber("▶ REPRODUCIR SELECCIONADA", CYAN);
        btnReproducir.addActionListener(e -> reproducirSeleccionada());
        add(btnReproducir);

        // CORREGIDO: Retorna al panel padre existente en vez de fabricar uno en limpio
        btnDesconectar = crearBotonCyber("DESCONECTAR PANEL", MAGENTA);
        btnDesconectar.addActionListener(e -> {
            if (panelAdminPadre != null) {
                panelAdminPadre.setVisible(true);
            } else {
                new PanelAdmin().setVisible(true);
            }
            dispose();
        });
        add(btnDesconectar);

        tablaMusica.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaMusica.getSelectedRow() != -1) {
                String artista = tablaMusica.getValueAt(tablaMusica.getSelectedRow(), 2).toString();
                if (artista.equalsIgnoreCase("Evento Especial")) {
                    rbEvento.setSelected(true);
                } else {
                    rbAmbiental.setSelected(true);
                }
            }
        });

        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                recalcularPosicionesComponentes();
            }
        });

        recalcularPosicionesComponentes();
        cargarDatosActuales();
    }

    private void construirBarraSuperior() {
        barraSuperior = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(COLOR_BARRA_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(CYAN);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
                g2d.dispose();
            }
        };
        barraSuperior.setLayout(null);
        barraSuperior.setOpaque(false);

        lblTituloBarra = new JLabel("⚡ BITSOUL MUSIC PROTOCOL // CORE INJECTOR SYSTEM");
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

        // CORREGIDO: El botón de cerrar (X) ahora regresa ordenadamente al panel principal en vez de matar la JVM.
        btnCerrar = crearBotonControl("X", MAGENTA);
        btnCerrar.addActionListener(e -> {
            if (panelAdminPadre != null) {
                panelAdminPadre.setVisible(true);
            }
            dispose();
        });
        barraSuperior.add(btnCerrar);

        btnMaximizar = crearBotonControl("⬜", CYAN);
        btnMaximizar.addActionListener(e -> alternarMaximizacion());
        barraSuperior.add(btnMaximizar);

        btnMinimizar = crearBotonControl("_", CYAN);
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        barraSuperior.add(btnMinimizar);
    }

    private JButton crearBotonControl(String texto, Color colorHover) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Monospaced", Font.BOLD, 12));
        btn.setForeground(TEXTO_MUTED);
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
                btn.setForeground(TEXTO_MUTED);
            }
        });
        return btn;
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
            // CORREGIDO COMPLETAMENTE: El typo de "dimensionsPrevias" ya no dará error
            dimensionesPrevias = getBounds();
            Rectangle boundsMaximo = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            setBounds(boundsMaximo);
            esMaximizado = true;
            btnMaximizar.setText("🗗");
        }
        recalcularPosicionesComponentes();
    }

    private void recalcularPosicionesComponentes() {
        int w = getWidth();
        int h = getHeight();

        if (barraSuperior != null) {
            barraSuperior.setBounds(0, 0, w, 40);
            btnCerrar.setBounds(w - 45, 5, 35, 30);
            btnMaximizar.setBounds(w - 85, 5, 35, 30);
            btnMinimizar.setBounds(w - 125, 5, 35, 30);
        }

        lblMainTitulo.setBounds(30, 55, 400, 30);
        lblMainSubtitulo.setBounds(30, 85, 600, 20);

        panelIzquierdo.setBounds(30, 120, 420, h - 225);

        int panelDerW = w - 510;
        panelTabla.setBounds(480, 120, panelDerW, (int) ((h - 225) * 0.58));

        if (scrollTabla != null) {
            scrollTabla.setBounds(25, 45, panelDerW - 50, panelTabla.getHeight() - 65);
        }

        panelControles.setBounds(480, panelTabla.getY() + panelTabla.getHeight() + 20, panelDerW, (h - 225) - panelTabla.getHeight() - 20);

        btnDesconectar.setBounds(30, h - 65, 220, 40);
        btnGuardar.setBounds(w - 630, h - 65, 280, 40);
        btnReproducir.setBounds(w - 330, h - 65, 300, 40);

        revalidate();
        repaint();
    }

    private void estilizarTabla() {
        tablaMusica.setBackground(new Color(11, 19, 36, 150));
        tablaMusica.setForeground(Color.WHITE);
        tablaMusica.setGridColor(new Color(242, 5, 203, 30));
        tablaMusica.setFont(new Font("Monospaced", Font.PLAIN, 12));
        tablaMusica.setRowHeight(30);
        tablaMusica.setSelectionBackground(new Color(242, 5, 203, 50));
        tablaMusica.setSelectionForeground(Color.WHITE);
        tablaMusica.setShowVerticalLines(false);
        tablaMusica.setOpaque(false);

        tablaMusica.getColumnModel().getColumn(5).setMinWidth(0);
        tablaMusica.getColumnModel().getColumn(5).setMaxWidth(0);
        tablaMusica.getColumnModel().getColumn(5).setPreferredWidth(0);

        DefaultTableCellRenderer renderCentro = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (column == 4 && value != null) {
                    setForeground(value.toString().equalsIgnoreCase("ACTIVO") ? CYAN : TEXTO_MUTED);
                } else {
                    setForeground(Color.WHITE);
                }
                return c;
            }
        };
        renderCentro.setHorizontalAlignment(JLabel.CENTER);
        renderCentro.setOpaque(false);

        for (int i = 0; i < tablaMusica.getColumnCount() - 1; i++) {
            tablaMusica.getColumnModel().getColumn(i).setCellRenderer(renderCentro);
        }

        JTableHeader header = tablaMusica.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new Color(11, 19, 36, 230));
        header.setForeground(CYAN);
        header.setFont(new Font("Monospaced", Font.BOLD, 12));

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(11, 19, 36));
                setForeground(CYAN);
                setFont(new Font("Monospaced", Font.BOLD, 12));
                setHorizontalAlignment(JLabel.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 240, 255, 60)));
                return this;
            }
        };
        header.setDefaultRenderer(headerRenderer);
    }

    private void cargarDatosActuales() {
        modeloTabla.setRowCount(0);
        String query = "SELECT id_protocol, file_path, estado, nombre_pista, artista, categoria FROM MUSICA";

        try (Connection con = main.Conexion.ConexionSQL.obtenerConexion(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            int contador = 1;
            while (rs.next()) {
                String idProtocol = rs.getString("id_protocol");
                String ruta = rs.getString("file_path");
                String estado = rs.getString("estado");
                String nombrePista = rs.getString("nombre_pista");
                String artista = rs.getString("artista");
                String categoria = rs.getString("categoria");

                if (nombrePista == null || nombrePista.trim().isEmpty()) {
                    nombrePista = new File(ruta).getName().replace(".wav", "");
                }
                if (artista == null || artista.trim().isEmpty()) {
                    artista = "Ambiental";
                }
                if (categoria == null || categoria.trim().isEmpty()) {
                    categoria = "General";
                }
                if (estado == null) {
                    estado = "ACTIVO";
                }

                modeloTabla.addRow(new Object[]{
                    contador++,
                    nombrePista,
                    artista,
                    categoria,
                    estado,
                    idProtocol
                });

                if (!ReproducirSonido.getListaCanciones().contains(ruta)) {
                    ReproducirSonido.agregarCancionALista(ruta);
                }
            }
        } catch (Exception e) {
            System.err.println("[CORE ERROR] Sincronización de pistas fallida: " + e.getMessage());
        }
    }

    private void seleccionarArchivo() {
        final JFrame ventanaPadre = this;
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Audio Core (*.wav)", "wav"));

        if (selector.showOpenDialog(ventanaPadre) == JFileChooser.APPROVE_OPTION) {
            File archivoOriginal = selector.getSelectedFile();
            try {
                String subCarpeta = rbEvento.isSelected() ? "Eventos" : "General";
                File directorioDestino = new File("src/Music/" + subCarpeta);
                if (!directorioDestino.exists()) {
                    directorioDestino.mkdirs();
                }

                File archivoDestino = new File(directorioDestino, archivoOriginal.getName());
                java.nio.file.Files.copy(archivoOriginal.toPath(), archivoDestino.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                String rutaFormateadaDB = "src/Music/" + subCarpeta + "/" + archivoOriginal.getName();

                String tituloIngresado = txtTitulo.getText().trim();
                if (tituloIngresado.isEmpty()) {
                    tituloIngresado = archivoOriginal.getName().replace(".wav", "");
                }

                String artistaIngresado = txtArtista.getText().trim();
                if (artistaIngresado.isEmpty()) {
                    artistaIngresado = rbEvento.isSelected() ? "Evento Especial" : "Ambiental";
                }

                String catIngresada = txtCategoria.getText().trim();
                if (catIngresada.isEmpty()) {
                    catIngresada = subCarpeta;
                }

                String query = "INSERT INTO MUSICA (id_protocol, file_path, estado, nombre_pista, artista, categoria) VALUES (?, ?, ?, ?, ?, ?)";

                try (Connection con = main.Conexion.ConexionSQL.obtenerConexion(); PreparedStatement ps = con.prepareStatement(query)) {

                    int nuevoId = 100 + tablaMusica.getRowCount();

                    ps.setString(1, "PROT_" + nuevoId);
                    ps.setString(2, rutaFormateadaDB);
                    ps.setString(3, "ACTIVO");
                    ps.setString(4, tituloIngresado);
                    ps.setString(5, artistaIngresado);
                    ps.setString(6, catIngresada);

                    ps.executeUpdate();
                }

                ReproducirSonido.agregarCancionALista(rutaFormateadaDB);

                SwingUtilities.invokeLater(() -> {
                    txtTitulo.setText("");
                    txtArtista.setText("");
                    txtCategoria.setText("");
                    cargarDatosActuales();
                    JOptionPane.showMessageDialog(ventanaPadre, "⚡ AUDIO INYECTADO CORRECTAMENTE AL SISTEMA.", "SYSTEM CORE", JOptionPane.INFORMATION_MESSAGE);
                });

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(ventanaPadre, "❌ ERROR EN SISTEMA DE INYECCIÓN.\n" + ex.getMessage(), "ERROR CRÍTICO", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarCambiosEstado() {
        int filaSeleccionada = tablaMusica.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ SELECCIONE UNA PISTA DE LA TABLA PRIMERO.", "SYSTEM CORE", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProtocol = tablaMusica.getModel().getValueAt(filaSeleccionada, 5).toString();
        String nuevoArtista = rbEvento.isSelected() ? "Evento Especial" : "Ambiental";

        String query = "UPDATE MUSICA SET artista = ? WHERE id_protocol = ?";

        try (Connection con = main.Conexion.ConexionSQL.obtenerConexion(); PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, nuevoArtista);
            ps.setString(2, idProtocol);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "⚡ PROTOCOLO ACTUALIZADO EN LA BASE DE DATOS.", "SYSTEM CORE", JOptionPane.INFORMATION_MESSAGE);
            cargarDatosActuales();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ ERROR AL GUARDAR CAMBIOS:\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void reproducirSeleccionada() {
        int filaSeleccionada = tablaMusica.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "⚠️ SELECCIONE UNA PISTA PARA REPRODUCIR.", "SYSTEM CORE", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idProtocol = tablaMusica.getModel().getValueAt(filaSeleccionada, 5).toString();
        String query = "SELECT file_path FROM MUSICA WHERE id_protocol = ?";

        try (Connection con = main.Conexion.ConexionSQL.obtenerConexion(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, idProtocol);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String ruta = rs.getString("file_path");
                    File f = new File(ruta);
                    if (f.exists()) {
                        JOptionPane.showMessageDialog(this, "▶ REPRODUCIENDO CORE: " + f.getName(), "AUDIO PLAYER", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "❌ ARCHIVO FÍSICO NO ENCONTRADO EN: " + ruta, "ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "❌ ERROR DE REPRODUCCIÓN:\n" + ex.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton crearBotonCyber(String texto, Color neon) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setForeground(neon);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(neon, 1));
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setOpaque(true);
                btn.setBackground(new Color(neon.getRed(), neon.getGreen(), neon.getBlue(), 30));
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setOpaque(false);
            }
        });
        return btn;
    }

    private class CyberTextField extends JTextField {

        public CyberTextField() {
            setOpaque(false);
            setCaretColor(CYAN);
            setForeground(Color.WHITE);
            setFont(new Font("Monospaced", Font.PLAIN, 13));
            setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(6, 12, 24, 200));
            g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
            super.paintComponent(g);
            g2.dispose();
        }

        @Override
        protected void paintBorder(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gradiente = new GradientPaint(0, 0, CYAN, getWidth(), 0, MAGENTA);
            g2.setPaint(gradiente);
            g2.setStroke(new BasicStroke(1.2f));
            g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 1, getHeight() - 1, 8, 8));
            g2.dispose();
        }
    }
}