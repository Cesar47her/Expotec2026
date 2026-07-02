package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import main.Util.EstiloDiseno;
import java.util.List;

public class PanelTipoItem extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Capturadores de datos globales
    private JTextField txtNombreVisible; 
    private JTable tablaTipos;
    private DefaultTableModel modeloTabla;

    public PanelTipoItem() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. ENCABEZADO DE LA CONSOLA TAXONÓMICA
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA (FORMULARIO + TABLA MAESTRA)
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaTipoItem(), gbc);
        
        setFocusable(true);
        actualizarTablaTiposReal();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE TAXONOMÍA Y TIPOS DE ÍTEMS");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Diccionario maestro de categorías del sistema. Define y gestiona las estructuras de la tabla 'tipo_item'.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaTipoItem() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioRegistroTipo());
        panel.add(crearTablaTiposExistentes());

        return panel;
    }

    private JPanel crearFormularioRegistroTipo() {
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

        JLabel title = new JLabel("🧬 REGISTRAR NUEVA CATEGORÍA MADRE (INSERT)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("NOMBRE DE LA NUEVA CATEGORÍA"));
        txtNombreVisible = crearCampoTexto("Ej: MASCOTA CYBERNÉTICA");
        fields.add(txtNombreVisible);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        card.add(fields, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("🧬 GUARDAR CONFIGURACIÓN ESTRUCTURAL") {
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
        btnGuardar.setFont(new Font("Dialog", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(0, 38));
        btnGuardar.setContentAreaFilled(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardar.addActionListener(e -> {
            String nombre = txtNombreVisible.getText().trim();

            if (nombre.isEmpty() || nombre.equals("Ej: MASCOTA CYBERNÉTICA")) {
                JOptionPane.showMessageDialog(this, "Debe ingresar un nombre válido para la categoría estructural.", "Campos vacíos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Adaptado perfectamente a tu nuevo TipoItemDAO corregido
            boolean exito = TipoItemDAO.insertarTipo(nombre);
            if (exito) {
                JOptionPane.showMessageDialog(this, "Estructura inyectada con éxito. El motor ahora reconoce este tipo de ítem.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaTiposReal();
                
                // Limpieza limpia de contenedores
                txtNombreVisible.setText("Ej: MASCOTA CYBERNÉTICA");
                txtNombreVisible.setForeground(TEXT_MUTED);
            } else {
                JOptionPane.showMessageDialog(this, "Error de Datos: Compruebe las restricciones de persistencia en MySQL.", "Transacción Cancelada", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnGuardar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaTiposExistentes() {
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

        JLabel title = new JLabel("📊 MATRIZ DE TAXONOMÍAS DE ÍTEMS EN BD");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"ID_TIPO (PK)", "NOMBRE DEL TIPO DE ARTÍCULO", "ESTADO OPERATIVO"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTipos = new JTable(modeloTabla);
        tablaTipos.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaTipos.setForeground(TEXT_WHITE);
        tablaTipos.setBackground(INPUT_BG);
        tablaTipos.setRowHeight(32);
        tablaTipos.setGridColor(new Color(255, 255, 255, 8));
        tablaTipos.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaTipos.setShowVerticalLines(false);

        // ============================================================
        // 🔥 MODIFICACIÓN: ENCABEZADO CUSTOM CON ESTILO CIBERNÉTICO
        // ============================================================
        JTableHeader header = tablaTipos.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(5, 7, 20));  // Fondo oscuro integrado
                label.setForeground(CYAN_NEON);            // Texto Cian Neón
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PINK_NEON)); // Subrayado Rosa Neón
                return label;
            }
        });
        // ============================================================

        tablaTipos.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                
                if (c == 1) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(CYAN_NEON);
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else if (c == 0) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(PINK_NEON);
                } else {
                    setHorizontalAlignment(SwingConstants.CENTER);
                    setForeground(new Color(0, 255, 130)); // Verde neón para el estado
                    setFont(new Font("Dialog", Font.BOLD, 11));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaTipos);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnEliminar = new JButton("❌ DESTRUIR CATEGORÍA (DELETE)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(20, 10, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnEliminar.setFont(new Font("Dialog", Font.BOLD, 11));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(0, 34));
        btnEliminar.setContentAreaFilled(false);
        btnEliminar.setBorderPainted(false);
        btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnEliminar.addActionListener(e -> {
            int filaSeleccionada = tablaTipos.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona una categoría estructural para dar de baja.", "TAXONOMÍA", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String idRaw = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
            int idTipo = Integer.parseInt(idRaw.replace("#TYP-", ""));

            int confirm = JOptionPane.showConfirmDialog(this, 
                "Alerta: Borrar un tipo base desvinculará todos los ítems dependientes de esa categoría. ¿Proceder con el DELETE?", 
                "PELIGRO EN CASCADA", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean exito = TipoItemDAO.eliminarTipo(idTipo);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Sentencia SQL despachada. Categoría purgada.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaTiposReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Error de Integridad Referencial:\nNo se puede eliminar esta taxonomía porque hay artículos de inventario vinculados a ella.", "Restricción de MySQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        card.add(btnEliminar, BorderLayout.SOUTH);
        return card;
    }

    public void actualizarTablaTiposReal() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = TipoItemDAO.obtenerTipos();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"#TYP-00", "VACÍO", "INACTIVO"});
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
}