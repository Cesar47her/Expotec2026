package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import main.Util.EstiloDiseno;

public class PanelHistorial extends JPanel {

    private static final Color CARD_BG    = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG   = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON  = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON  = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED = EstiloDiseno.TEXT_MUTED;

    private HistorialDAO historialDAO;
    private DefaultTableModel modeloTabla;
    private JTextField txtBuscar;
    private JLabel lblInfoBanner;
    private JLabel lblMontoBanner;
    private final String placeholder = " Buscar por ID de usuario, transacción o ítem...";

    public PanelHistorial() {
        historialDAO = new HistorialDAO();

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER
        gbc.gridy = 0; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 15, 0);
        add(crearHeaderTop(), gbc);

        // 2. PANEL DE CONTROL (BUSCADOR DE REGISTROS)
        gbc.gridy = 1; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 15, 0);
        add(crearBarraBusqueda(), gbc);

        // 3. TABLA DE DATOS MASIVA (JTABLE CYBERPUNK)
        gbc.gridy = 2; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 15, 0);
        add(crearContenedorTabla(), gbc);

        // 4. RESUMEN DE COMPRAS INFERIOR
        gbc.gridy = 3; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 0, 0);
        add(crearBannerResumen(), gbc);
        
        // Carga inicial asíncrona de datos desde MySQL
        buscarDatosBaseDatos("");
        setFocusable(true);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("AUDITORÍA: HISTORIAL DE COMPRAS");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Monitoreo global de transacciones, transacciones de billetera e inserciones en la tabla 'historial_compra'.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title); left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearBarraBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(1920, 40));

        txtBuscar = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 15));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        txtBuscar.setText(placeholder);
        txtBuscar.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtBuscar.setForeground(TEXT_MUTED);
        txtBuscar.setCaretColor(CYAN_NEON);
        txtBuscar.setOpaque(false);
        txtBuscar.setBorder(new EmptyBorder(0, 10, 0, 10));
        txtBuscar.setPreferredSize(new Dimension(400, 36));

        txtBuscar.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtBuscar.getText().equals(placeholder)) {
                    txtBuscar.setText("");
                    txtBuscar.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtBuscar.getText().trim().isEmpty()) {
                    txtBuscar.setText(placeholder);
                    txtBuscar.setForeground(TEXT_MUTED);
                }
            }
        });

        txtBuscar.addActionListener(e -> ejecutarFiltroAccion());

        JButton btnFiltrar = new JButton("🔍 FILTRAR QUERY") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(0, 240, 255, 20) : new Color(5, 8, 22));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(CYAN_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnFiltrar.setFont(new Font("Dialog", Font.BOLD, 11));
        btnFiltrar.setForeground(Color.WHITE);
        btnFiltrar.setPreferredSize(new Dimension(150, 36));
        btnFiltrar.setContentAreaFilled(false); btnFiltrar.setBorderPainted(false); btnFiltrar.setFocusPainted(false);
        btnFiltrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnFiltrar.addActionListener(e -> ejecutarFiltroAccion());

        panel.add(txtBuscar, BorderLayout.CENTER);
        panel.add(btnFiltrar, BorderLayout.EAST);
        return panel;
    }

    private JComponent crearContenedorTabla() {
        String[] columnas = {"ID_TX (PK)", "ID_USUARIO (FK)", "ITEM ADQUIRIDO", "MONTO CORE", "TIMESTAMP", "ESTADO SQL"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; 
            }
        };

        JTable tabla = new JTable(modeloTabla);
        tabla.setFont(new Font("Dialog", Font.PLAIN, 12));
        tabla.setForeground(TEXT_WHITE);
        tabla.setBackground(CARD_BG);
        tabla.setRowHeight(32);
        tabla.setGridColor(new Color(255, 255, 255, 10));
        tabla.setSelectionBackground(new Color(255, 0, 127, 40)); 
        tabla.setSelectionForeground(Color.WHITE);
        tabla.setShowVerticalLines(false);

        // SOLUCIÓN AL FONDO BLANCO DE LA CABECERA:
        JTableHeader header = tabla.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        
        // Aplicamos un Renderizador Personalizado Celda por Celda al Header
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBackground(INPUT_BG); // Forzamos el color de fondo oscuro integrado
                setForeground(CYAN_NEON); // Texto neón
                setFont(new Font("Dialog", Font.BOLD, 12));
                setHorizontalAlignment(SwingConstants.CENTER);
                
                // Un borde estético que delimita las columnas usando opacidad sutil
                setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(255, 255, 255, 15)));
                return this;
            }
        });

        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 5) { // Columna ESTADO SQL
                    if ("SUCCESS".equalsIgnoreCase(String.valueOf(val))) {
                        setForeground(new Color(0, 255, 130));
                    } else {
                        setForeground(PINK_NEON);
                    }
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? CARD_BG : new Color(5, 8, 22, 180)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(0, 240, 255, 30)));
        return scroll;
    }

    private JPanel crearBannerResumen() {
        JPanel banner = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 10;
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(0, h);
                path.closePath();
                g2d.setColor(INPUT_BG); g2d.fill(path);
                g2d.setColor(PINK_NEON); g2d.setStroke(new BasicStroke(1f)); g2d.draw(path);
                g2d.dispose();
            }
        };
        banner.setOpaque(false);
        banner.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        banner.setMaximumSize(new Dimension(1920, 45));

        lblInfoBanner = new JLabel("📊 TOTAL TRANSACCIONADO EN BD: CARGANDO REGISTROS...");
        lblInfoBanner.setFont(new Font("Dialog", Font.BOLD, 11));
        lblInfoBanner.setForeground(TEXT_MUTED);

        lblMontoBanner = new JLabel("RECAUDACIÓN CORE: -- B$");
        lblMontoBanner.setFont(new Font("Dialog", Font.BOLD, 12));
        lblMontoBanner.setForeground(PINK_NEON);

        banner.add(lblInfoBanner, BorderLayout.WEST);
        banner.add(lblMontoBanner, BorderLayout.EAST);
        return banner;
    }

    private void ejecutarFiltroAccion() {
        String texto = txtBuscar.getText();
        if (texto.equals(placeholder)) {
            texto = "";
        }
        buscarDatosBaseDatos(texto);
    }

    // MEJORA AGREGADA: Carga asíncrona mediante SwingWorker para evitar congelamiento de UI
    private void buscarDatosBaseDatos(String criterio) {
        lblInfoBanner.setText("📊 INDEXANDO TRANSMISIONES... ESPERANDO RESPUESTA DE MYSQL...");
        
        SwingWorker<List<CompraData>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CompraData> doInBackground() {
                return historialDAO.obtenerHistorialFiltrado(criterio);
            }

            @Override
            protected void done() {
                try {
                    List<CompraData> compras = get();
                    modeloTabla.setRowCount(0); 
                    
                    double totalRecaudado = 0;
                    int registrosExitosos = 0;

                    for (CompraData c : compras) {
                        String montoString = String.format("%,.0f B$", c.monto);
                        
                        modeloTabla.addRow(new Object[]{
                            c.idTx,
                            c.idUsuario,
                            c.item,
                            montoString,
                            c.timestamp,
                            c.estado
                        });

                        if ("SUCCESS".equalsIgnoreCase(c.estado)) {
                            totalRecaudado += c.monto;
                            registrosExitosos++;
                        }
                    }

                    lblInfoBanner.setText("📊 TOTAL TRANSACCIONADO EN BD: " + compras.size() + " REGISTROS DETECTADOS (" + registrosExitosos + " SUCCESS)");
                    lblMontoBanner.setText(String.format("RECAUDACIÓN CORE: %,.0f B$ ", totalRecaudado));

                } catch (Exception e) {
                    lblInfoBanner.setText("⚠️ ERROR CRÍTICO: FALLA AL ENLAZAR EL HISTORIAL DE TRANSACCIONES");
                    lblMontoBanner.setText("RECAUDACIÓN CORE: ERR B$");
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }
}