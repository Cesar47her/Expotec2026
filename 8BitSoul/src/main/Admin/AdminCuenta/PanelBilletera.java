package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;
import main.Util.EstiloDiseno;

public class PanelBilletera extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    private JTextField txtUsuario;
    private JComboBox<String> cbTipoAjuste;
    private JTextField txtMonto;
    private JTextField txtJustificante;
    
    private JTable tablaLibro;
    private DefaultTableModel modeloTabla;
    private final BilleteraDAO billeteraDAO;

    public PanelBilletera() {
        this.billeteraDAO = new BilleteraDAO();
        
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. ENCABEZADO PRINCIPAL
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA DE OPERACIONES
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaBilletera(), gbc);
        
        setFocusable(true);
        
        // Ejecuta la carga inicial de datos desde la BD
        actualizarTablaSaldos();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA MAESTRA DE BILLETERA (FINANZAS CORE)");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Auditoría bancaria del servidor. Modifica balances, inyecta divisas y altera parámetros.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaBilletera() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioTransaccion());
        panel.add(crearMonitorLibroContable());

        return panel;
    }

    private JPanel crearFormularioTransaccion() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w, 0); path.lineTo(w, h - cut);
                path.lineTo(w - cut, h); path.lineTo(0, h); path.lineTo(0, cut);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("⚡ EJECUTAR AJUSTE DE BALANCE (UPDATE)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("USERNAME DEL JUGADOR AFECTADO"));
        txtUsuario = crearCampoTexto("Soul_Golden");
        fields.add(txtUsuario);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("TIPO DE AJUSTE BANCARIO"));
        cbTipoAjuste = crearSelector(new String[]{"➕ INYECTAR FONDOS (CRÉDITO)", "➖ DEBITAR / SANCIONAR (DÉBITO)"});
        fields.add(cbTipoAjuste);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("MONTO A PROCESAR (CANTIDAD EN B$)"));
        txtMonto = crearCampoTexto("500");
        fields.add(txtMonto);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("JUSTIFICANTE OPERATIVO (LOGS DE AUDITORÍA)"));
        txtJustificante = crearCampoTexto("Compensación por inestabilidad de base de datos");
        fields.add(txtJustificante);

        card.add(fields, BorderLayout.CENTER);

        JButton btnTransferir = new JButton("⚡ APLICAR AJUSTE FINANCIERO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(255, 0, 127, 20) : new Color(5, 8, 22));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnTransferir.setFont(new Font("Dialog", Font.BOLD, 12));
        btnTransferir.setForeground(Color.WHITE);
        btnTransferir.setPreferredSize(new Dimension(0, 38));
        btnTransferir.setContentAreaFilled(false);
        btnTransferir.setBorderPainted(false);
        btnTransferir.setFocusPainted(false);
        btnTransferir.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnTransferir.addActionListener(e -> intentarEnvioTransaccion());

        card.add(btnTransferir, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearMonitorLibroContable() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(0, h);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                g2d.setColor(new Color(255, 255, 255, 12));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📊 LIBRO DE BALANCES GENERALES");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"ID_WALLET (PK)", "USERNAME (DB)", "SALDO CORRIENTE", "PUNTOS APUESTA"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaLibro = new JTable(modeloTabla);
        tablaLibro.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaLibro.setForeground(TEXT_WHITE);
        tablaLibro.setBackground(INPUT_BG);
        tablaLibro.setRowHeight(32);
        tablaLibro.setGridColor(new Color(255, 255, 255, 8));
        tablaLibro.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaLibro.setShowVerticalLines(false);

        // --- ENCABEZADO DE LA TABLA PERSONALIZADO (INTEGRADO AL ENTORNO OSCO) ---
        JTableHeader header = tablaLibro.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setOpaque(false); // Evita heredar estilos nativos claros del SO

        // Aplicamos un renderizador propio para limpiar los bordes y pintar el fondo oscuro correcto
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                lbl.setFont(header.getFont());
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                
                // Forzamos el esquema oscuro cyberpunk
                lbl.setBackground(new Color(10, 15, 30)); 
                lbl.setForeground(CYAN_NEON);             
                
                // Línea inferior de separación estética sin destello blanco
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 15)));
                
                return lbl;
            }
        });

        tablaLibro.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 2 && val != null) {
                    if (val.toString().startsWith("0 ")) setForeground(PINK_NEON);
                    else setForeground(new Color(0, 255, 130));
                    setFont(new Font("Dialog", Font.BOLD, 12));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        // --- AJUSTES DEL SCROLLPANE PARA EVITAR BORDES Y ESQUINAS BLANCAS ---
        JScrollPane scroll = new JScrollPane(tablaLibro);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBackground(new Color(10, 15, 30)); 
        scroll.getViewport().setBackground(new Color(10, 15, 30));
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /**
     * Carga asincrónica para evitar congelamientos de la UI al interrogar a MySQL
     */
    public void actualizarTablaSaldos() {
        SwingWorker<List<Object[]>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Object[]> doInBackground() {
                return billeteraDAO.obtenerLibroContable();
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> filas = get();
                    modeloTabla.setRowCount(0);
                    if (filas.isEmpty()) {
                        modeloTabla.addRow(new Object[]{"WLT-000", "Sin datos en BD", "0 B$", "0 pts"});
                    } else {
                        for (Object[] fila : filas) {
                            modeloTabla.addRow(fila);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error al renderizar balances en la UI: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Envío y procesamiento de transacciones con la validación de vacíos corregida
     */
    private void intentarEnvioTransaccion() {
        String usuario = txtUsuario.getText().trim();
        String tipo = cbTipoAjuste.getSelectedItem().toString();
        String montoStr = txtMonto.getText().trim();

        if (usuario.isEmpty() || montoStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor introduzca un usuario y monto válidos.", "Campos Vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int monto = Integer.parseInt(montoStr);
            if (monto <= 0) throw new NumberFormatException();

            SwingWorker<Boolean, Void> transaccionWorker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return billeteraDAO.procesarAjusteFinanciero(usuario, tipo, monto);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(PanelBilletera.this, "Transacción inyectada con éxito. Balance recalculado.", "Éxito Finanzas", JOptionPane.INFORMATION_MESSAGE);
                            actualizarTablaSaldos(); 
                        } else {
                            JOptionPane.showMessageDialog(PanelBilletera.this, "Error: Verifique si el Username ingresado existe en el servidor.", "Usuario No Encontrado", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            transaccionWorker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El monto debe ser un valor numérico entero mayor a 0.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JLabel crearLabelInput(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JTextField crearCampoTexto(String placeholder) {
        JTextField f = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(hasFocus() ? CYAN_NEON : new Color(255, 255, 255, 15));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        f.setText(placeholder);
        f.setFont(new Font("Dialog", Font.PLAIN, 12));
        f.setForeground(TEXT_MUTED);
        f.setCaretColor(CYAN_NEON);
        f.setOpaque(false);
        f.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        f.setMaximumSize(new Dimension(1920, 36));
        f.setPreferredSize(new Dimension(0, 36));
        
        f.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (f.getText().equals(placeholder)) {
                    f.setText("");
                    f.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (f.getText().trim().isEmpty()) {
                    f.setText(placeholder);
                    f.setForeground(TEXT_MUTED);
                }
            }
        });
        return f;
    }

    private JComboBox<String> crearSelector(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Dialog", Font.PLAIN, 12));
        c.setForeground(TEXT_WHITE);
        c.setBackground(INPUT_BG);
        c.setMaximumSize(new Dimension(1920, 36));
        c.setPreferredSize(new Dimension(0, 36));
        c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton("▼");
                b.setFont(new Font("Dialog", Font.PLAIN, 10));
                b.setForeground(CYAN_NEON);
                b.setBackground(INPUT_BG);
                b.setContentAreaFilled(false);
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setFocusPainted(false);
                return b;
            }
        });
        c.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 15)));
        return c;
    }
}