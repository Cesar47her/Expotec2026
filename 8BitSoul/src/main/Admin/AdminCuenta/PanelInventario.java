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

public class PanelInventario extends JPanel {

    private static final Color CARD_BG    = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG   = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON  = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON  = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED = EstiloDiseno.TEXT_MUTED;

    private InventarioDAO inventarioDAO;
    private DefaultTableModel modeloTabla;
    private JTable tablaItems;
    
    // Componentes de captura del formulario
    private JTextField txtNombre;
    private JComboBox<String> cmbTipo;
    private JTextField txtPrecio;
    private JTextArea txtDesc;

    private final String placeNombre = "Ej: Katana Cyber-Soul X";
    private final String placePrecio = "1200";
    private final String placeDesc = "Inserte especificaciones técnicas del ítem...";

    public PanelInventario() {
        inventarioDAO = new InventarioDAO();

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER GENERAL DE CONTROL
        gbc.gridy = 0; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA (FORMULARIO CRUD + TABLA DE ÍTEMS)
        gbc.gridy = 1; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaInventario(), gbc);
        
        // Sincronizar datos iniciales de forma asíncrona
        recargarTablaDesdeBD();
        setFocusable(true);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE INVENTARIO Y CATÁLOGO GLOBAL");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Panel de administración para la tabla 'ITEM_TIENDA'. Modifica precios, añade equipamiento y gestiona existencias.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title); left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaInventario() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);
        panel.add(crearFormularioRegistroItem());
        panel.add(crearTablaInventarioMaestra());
        return panel;
    }

    private JPanel crearFormularioRegistroItem() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w, 0); path.lineTo(w, h - cut);
                path.lineTo(w - cut, h); path.lineTo(0, h); path.lineTo(0, cut);
                path.closePath();
                g2d.setColor(CARD_BG); g2d.fill(path);
                g2d.setColor(CYAN_NEON); g2d.setStroke(new BasicStroke(1.2f)); g2d.draw(path);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel title = new JLabel("📦 REGISTRAR NUEVO ÍTEM (INSERT INTO)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        // Input Nombre
        fields.add(crearLabelInput("NOMBRE DEL ARTÍCULO / ITEM_NAME"));
        txtNombre = crearCampoTexto(placeNombre);
        fields.add(txtNombre);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        // Selector Categoría
        fields.add(crearLabelInput("TIPO DE COMPONENTE (CATEGORÍA)"));
        cmbTipo = crearSelector(new String[]{"SKIN DE PERSONAJE", "ARMA / APÉNDICE", "PAQUETE / BUNDLE", "POTENCIADOR (BOOSTER)"});
        fields.add(cmbTipo);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        // Input Precio
        fields.add(crearLabelInput("VALOR EN ECONOMÍA CORE (PRECIO B$)"));
        txtPrecio = crearCampoTexto(placePrecio);
        fields.add(txtPrecio);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        // Input Descripción Operativa
        fields.add(crearLabelInput("DESCRIPCIÓN OPERATIVA DEL OBJETO"));
        txtDesc = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG); g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 15));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        txtDesc.setText(placeDesc);
        txtDesc.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtDesc.setForeground(TEXT_MUTED);
        txtDesc.setCaretColor(PINK_NEON);
        txtDesc.setLineWrap(true); txtDesc.setWrapStyleWord(true);
        txtDesc.setOpaque(false);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        txtDesc.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtDesc.getText().equals(placeDesc)) {
                    txtDesc.setText(""); txtDesc.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (txtDesc.getText().trim().isEmpty()) {
                    txtDesc.setText(placeDesc); txtDesc.setForeground(TEXT_MUTED);
                }
            }
        });
        
        JScrollPane scrollArea = new JScrollPane(txtDesc);
        scrollArea.setOpaque(false); scrollArea.getViewport().setOpaque(false);
        scrollArea.setBorder(BorderFactory.createEmptyBorder());
        scrollArea.setPreferredSize(new Dimension(0, 100));
        fields.add(scrollArea);

        card.add(fields, BorderLayout.CENTER);

        // Botón Guardar Acción
        JButton btnGuardar = new JButton("💾 GUARDAR CAMBIOS EN CATÁLOGO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(255, 0, 127, 20) : new Color(5, 8, 22));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON); g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnGuardar.setFont(new Font("Dialog", Font.BOLD, 12));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setPreferredSize(new Dimension(0, 38));
        btnGuardar.setContentAreaFilled(false); btnGuardar.setBorderPainted(false); btnGuardar.setFocusPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnGuardar.addActionListener(e -> ejecutarInsertarItem());

        card.add(btnGuardar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaInventarioMaestra() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut);
                path.lineTo(w, h); path.lineTo(0, h);
                path.closePath();
                g2d.setColor(CARD_BG); g2d.fill(path);
                g2d.setColor(new Color(255, 255, 255, 12)); g2d.draw(path);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("📊 CATÁLOGO DE ÍTEMS EN BASE DE DATOS");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"ID_ITEM (PK)", "NOMBRE DEL ÍTEM", "TIPO", "PRECIO"};
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaItems = new JTable(modeloTabla);
        tablaItems.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaItems.setForeground(TEXT_WHITE);
        tablaItems.setBackground(INPUT_BG);
        tablaItems.setRowHeight(32);
        tablaItems.setGridColor(new Color(255, 255, 255, 8));
        tablaItems.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaItems.setShowVerticalLines(false);

        // SOLUCIÓN AL FONDO BLANCO DEL HEADER:
        JTableHeader header = tablaItems.getTableHeader();
        header.setPreferredSize(new Dimension(0, 36));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBackground(new Color(5, 7, 20)); // Color ultra oscuro integrado
                setForeground(CYAN_NEON);         // Títulos neón cyberpunk
                setFont(new Font("Dialog", Font.BOLD, 11));
                setHorizontalAlignment(SwingConstants.CENTER);
                
                // Separador sutil para las columnas
                setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(255, 255, 255, 15)));
                return this;
            }
        });

        tablaItems.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 2) {
                    String tipo = String.valueOf(val);
                    if (tipo.contains("SKIN")) setForeground(CYAN_NEON);
                    else if (tipo.contains("ARMA") || tipo.contains("WEAPON")) setForeground(PINK_NEON);
                    else if (tipo.contains("BUNDLE") || tipo.contains("PAQUETE")) setForeground(new Color(160, 32, 240));
                    else setForeground(new Color(0, 255, 130));
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaItems);
        scroll.setOpaque(false); scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        // Botón Eliminar Acción
        JButton btnEliminar = new JButton("❌ ELIMINAR ARTÍCULO DEL INVENTARIO (DELETE)") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(20, 10, 20)); g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON); g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnEliminar.setFont(new Font("Dialog", Font.BOLD, 11));
        btnEliminar.setForeground(Color.WHITE);
        btnEliminar.setPreferredSize(new Dimension(0, 34));
        btnEliminar.setContentAreaFilled(false); btnEliminar.setBorderPainted(false); btnEliminar.setFocusPainted(false);
        btnEliminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnEliminar.addActionListener(e -> ejecutarEliminarItem());
        
        card.add(btnEliminar, BorderLayout.SOUTH);
        return card;
    }

    // MODIFICADO: Sincronización asíncrona para no congelar la UI al consultar MySQL
    private void recargarTablaDesdeBD() {
        SwingWorker<List<ItemData>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ItemData> doInBackground() {
                return inventarioDAO.obtenerCatalogo();
            }

            @Override
            protected void done() {
                try {
                    List<ItemData> items = get();
                    modeloTabla.setRowCount(0);
                    for (ItemData i : items) {
                        modeloTabla.addRow(new Object[]{
                            "#ITM-" + String.format("%03d", i.idItem),
                            i.nombreItem,
                            i.nombreTipo,
                            String.format("%,d B$", i.precio)
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    // MODIFICADO: Inserción asíncrona segura
    private void ejecutarInsertarItem() {
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim();
        String desc = txtDesc.getText().trim();
        
        if (nombre.isEmpty() || nombre.equals(placeNombre) || precioStr.isEmpty() || precioStr.equals(placePrecio)) {
            JOptionPane.showMessageDialog(this, "Por favor complete los campos obligatorios.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int precio = Integer.parseInt(precioStr);
            int idTipo = cmbTipo.getSelectedIndex() + 1; 
            
            final String descripcionFinal = desc.equals(placeDesc) ? "Sin especificaciones técnicas." : desc;

            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return inventarioDAO.insertarItem(nombre, idTipo, precio, descripcionFinal);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(PanelInventario.this, "Ítem inyectado exitosamente en el catálogo central.");
                            recargarTablaDesdeBD();
                            
                            txtNombre.setText(placeNombre); txtNombre.setForeground(TEXT_MUTED);
                            txtPrecio.setText(placePrecio); txtPrecio.setForeground(TEXT_MUTED);
                            txtDesc.setText(placeDesc); txtDesc.setForeground(TEXT_MUTED);
                        } else {
                            JOptionPane.showMessageDialog(PanelInventario.this, "Error crítico al guardar en la base de datos.", "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El precio debe ser un valor numérico entero válido.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
        }
    }

    // MODIFICADO: Eliminación asíncrona segura con verificación de claves foráneas
    private void ejecutarEliminarItem() {
        int filaSeleccionada = tablaItems.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un ítem del catálogo para dar de baja.", "SISTEMA", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String idString = String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 0));
        int idReal = Integer.parseInt(idString.replace("#ITM-", ""));

        int confirm = JOptionPane.showConfirmDialog(this, "¿Está seguro de borrar este ítem del catálogo permanente?", "AVISO DE CONTROL", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            
            SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                @Override
                protected Boolean doInBackground() {
                    return inventarioDAO.eliminarItem(idReal);
                }

                @Override
                protected void done() {
                    try {
                        if (get()) {
                            JOptionPane.showMessageDialog(PanelInventario.this, "Registro purgado físicamente del sistema.");
                            recargarTablaDesdeBD();
                        } else {
                            JOptionPane.showMessageDialog(PanelInventario.this, "No se puede eliminar: El ítem está referenciado en un inventario activo de jugador.", "ALERTA INTEGRIDAD", JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            worker.execute();
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
                g2d.setColor(INPUT_BG); g2d.fillRect(0, 0, getWidth(), getHeight());
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
                    f.setText(""); f.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (f.getText().isEmpty()) {
                    f.setText(placeholder); f.setForeground(TEXT_MUTED);
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
                JButton b = super.createArrowButton();
                b.setBackground(INPUT_BG);
                b.setBorder(BorderFactory.createEmptyBorder());
                return b;
            }
        });
        c.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 15)));
        return c;
    }
}