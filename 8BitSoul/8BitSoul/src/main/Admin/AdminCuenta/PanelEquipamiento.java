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

public class PanelEquipamiento extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Componentes interactivos del formulario
    private JTextField txtUsuario;
    private JTextField txtIdItem;
    private JComboBox<String> cbEstado;
    private JComboBox<String> cbSlot;

    private JTable tablaEquipamiento;
    private DefaultTableModel modeloTabla;

    public PanelEquipamiento() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER OPERATIVO
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaEquipamiento(), gbc);
        
        setFocusable(true);
        actualizarTablaReal();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE EQUIPAMIENTO Y ASIGNACIONES");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Administración de la tabla relacional de posesión de ítems. Asigna o revoca equipamiento.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaEquipamiento() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioAsignacion());
        panel.add(crearTablaAuditoríaEquipamiento());

        return panel;
    }

    private JPanel crearFormularioAsignacion() {
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

        JLabel title = new JLabel("⚔️ ASIGNAR / MODIFICAR EQUIPAMIENTO (INSERT)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("USERNAME DEL USUARIO OBJETIVO"));
        txtUsuario = crearCampoTexto("Soul_Golden");
        fields.add(txtUsuario);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("ID NUMÉRICO DEL ÍTEM A VINCULAR (1 al 3)"));
        txtIdItem = crearCampoTexto("1");
        fields.add(txtIdItem);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("ESTADO INICIAL DEL EQUIPAMIENTO"));
        cbEstado = crearSelector(new String[]{"EQUIPADO (ACTIVO)", "EN MOCHILA (DESEQUIPADO)"});
        fields.add(cbEstado);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("SLOT DE ACCESO RÁPIDO ASIGNADO"));
        cbSlot = crearSelector(new String[]{"SLOT_01 (ARMA/APÉNDICE)", "SLOT_02 (SKIN DE PERSONAJE)"});
        fields.add(cbSlot);

        card.add(fields, BorderLayout.CENTER);

        JButton btnAsignar = new JButton("⚔️ INYECTAR EQUIPAMIENTO A PERFIL") {
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
        btnAsignar.setFont(new Font("Dialog", Font.BOLD, 12));
        btnAsignar.setForeground(Color.WHITE);
        btnAsignar.setPreferredSize(new Dimension(0, 38));
        btnAsignar.setContentAreaFilled(false);
        btnAsignar.setBorderPainted(false);
        btnAsignar.setFocusPainted(false);
        btnAsignar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnAsignar.addActionListener(e -> {
            String user = txtUsuario.getText().trim();
            String itemStr = txtIdItem.getText().trim();
            String estado = cbEstado.getSelectedItem().toString();
            String slot = cbSlot.getSelectedItem().toString();

            if (user.isEmpty() || user.equals("Name") || itemStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Parámetros de inserción vacíos o inválidos.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int idItem = Integer.parseInt(itemStr);
                boolean exito = EquipamientoDAO.registrarAsignacion(user, idItem, estado, slot);
                
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Registro insertado en MySQL. El inventario se ha actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: El usuario no existe en la BD o el ID del ítem es incorrecto.", "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "El ID del Ítem debe ser un número entero.", "Formato Inválido", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnAsignar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaAuditoríaEquipamiento() {
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

        JLabel title = new JLabel("📊 HISTORIAL DE ASIGNACIONES ACTIVAS EN SERVIDOR");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"USERNAME (DB)", "ID_ITEM", "NOMBRE DEL OBJETO", "ESTADO"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaEquipamiento = new JTable(modeloTabla);
        tablaEquipamiento.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaEquipamiento.setForeground(TEXT_WHITE);
        tablaEquipamiento.setBackground(INPUT_BG);
        tablaEquipamiento.setRowHeight(32);
        tablaEquipamiento.setGridColor(new Color(255, 255, 255, 8));
        tablaEquipamiento.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaEquipamiento.setShowVerticalLines(false);

        // Modificación del JTableHeader para mimetizarlo con el tema cyberpunk
        JTableHeader header = tablaEquipamiento.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setForeground(CYAN_NEON);      
                cell.setBackground(new Color(5, 7, 20)); // Tono azul oscuro del fondo del sistema
                cell.setFont(new Font("Dialog", Font.BOLD, 11));
                
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255, 255, 255, 20))); 
                return cell;
            }
        });

        tablaEquipamiento.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 3 && val != null) {
                    if ("EQUIPADO".equals(val.toString())) setForeground(new Color(0, 255, 130));
                    else setForeground(TEXT_MUTED);
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaEquipamiento);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnRemover = new JButton("❌ REMOVER COMPONENTE DEL PERFIL (DELETE)") {
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
        btnRemover.setFont(new Font("Dialog", Font.BOLD, 11));
        btnRemover.setForeground(Color.WHITE);
        btnRemover.setPreferredSize(new Dimension(0, 34));
        btnRemover.setContentAreaFilled(false);
        btnRemover.setBorderPainted(false);
        btnRemover.setFocusPainted(false);
        btnRemover.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnRemover.addActionListener(e -> {
            int filaSeleccionada = tablaEquipamiento.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una fila en la tabla de asignaciones para remover.", "SISTEMA", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            String username = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
            String itemCoded = modeloTabla.getValueAt(filaSeleccionada, 1).toString();
            int idItem = Integer.parseInt(itemCoded.replace("ITM-", ""));

            int confirm = JOptionPane.showConfirmDialog(this, "¿Ejecutar sentencia DELETE? El usuario perderá el ítem de inmediato.", "WARN CONTROL", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean purgado = EquipamientoDAO.revocarEquipamiento(username, idItem);
                if (purgado) {
                    JOptionPane.showMessageDialog(this, "Vínculo de inventario purgado con éxito.");
                    actualizarTablaReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Error al eliminar el registro.");
                }
            }
        });
        card.add(btnRemover, BorderLayout.SOUTH);

        return card;
    }

    public void actualizarTablaReal() {
        modeloTabla.setRowCount(0);
        List<Object[]> registros = EquipamientoDAO.obtenerHistorialEquipamiento();
        for (Object[] fila : registros) {
            modeloTabla.addRow(fila);
        }
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"Sin asignaciones", "ITM-000", "Ninguno", "VACÍO"});
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