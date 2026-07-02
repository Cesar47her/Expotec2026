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

public class PanelRol extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Componentes del formulario capturadores de datos
    private JTextField txtNombreRol;
    private JTextField txtCodigoToken;
    private JComboBox<String> cbJerarquia;

    private JTable tablaRoles;
    private DefaultTableModel modeloTabla;

    public PanelRol() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER DE CONTROL DE SEGURIDAD
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA DE ROLES
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaRoles(), gbc);
        
        setFocusable(true);
        actualizarTablaRolesReal();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE ROLES Y PRIVILEGIOS DE SEGURIDAD");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Gobierno de accesos. Modifica las políticas de la tabla 'rol', añade rangos y gestiona tokens de acceso.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaRoles() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioPoliticas());
        panel.add(crearTablaAuditoriaRoles());

        return panel;
    }

    private JPanel crearFormularioPoliticas() {
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

        JLabel title = new JLabel("🔑 DEFINIR NUEVA POLÍTICA DE ACCESO (INSERT)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("NOMBRE DEL ROL OPERATIVO"));
        txtNombreRol = crearCampoTexto("Ej: MODERADOR_SISTEMAS");
        fields.add(txtNombreRol);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("CÓDIGO ALFANUMÉRICO DE AUTORIZACIÓN"));
        txtCodigoToken = crearCampoTexto("ROLE_MOD_CORE");
        fields.add(txtCodigoToken);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("JERARQUÍA / NIVEL DE PRIVILEGIO (CLEARANCE)"));
        cbJerarquia = crearSelector(new String[]{
            "NIVEL 5 - Full Root Access (Super Admin)", 
            "NIVEL 4 - Write & Audit Access (Moderador)", 
            "NIVEL 3 - Write Only Access (Soporte)", 
            "NIVEL 1 - Read Only Access (Usuario Estándar)"
        });
        fields.add(cbJerarquia);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("ÁMBITO DE APLICACIÓN EN CONSOLA"));
        fields.add(crearSelector(new String[]{"ACCESO TOTAL A TODAS LAS TABLAS", "RESTRINGIDO A TABLA DE SOPORTE Y NOVEDADES", "SÓLO INTERFAZ DE CLIENTE (JUEGO)"}));

        card.add(fields, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("🔑 GUARDAR Y DESPLEGAR POLÍTICA") {
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
            String nombre = txtNombreRol.getText().trim();
            String token = txtCodigoToken.getText().trim();
            
            if (nombre.isEmpty() || nombre.equals("Ej: MODERADOR_SISTEMAS") || token.isEmpty() || token.equals("ROLE_MOD_CORE")) {
                JOptionPane.showMessageDialog(this, "Debe ingresar parámetros válidos para la nueva política de control.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Mapear el String de la jerarquía al valor numérico correspondiente
            int jerarquia = 1;
            String seleccionado = cbJerarquia.getSelectedItem().toString();
            if (seleccionado.contains("NIVEL 5")) jerarquia = 5;
            else if (seleccionado.contains("NIVEL 4")) jerarquia = 4;
            else if (seleccionado.contains("NIVEL 3")) jerarquia = 3;

            // CORRECCIÓN: Invocación adaptada de forma estricta a los 3 parámetros requeridos por el DAO
            boolean exito = RolDAO.insertarRol(nombre, token, jerarquia);
            
            if (exito) {
                JOptionPane.showMessageDialog(this, "Política de seguridad inyectada con éxito en la tabla 'rol'.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                actualizarTablaRolesReal();
                
                // Resetear campos limpiamente
                txtNombreRol.setText("Ej: MODERADOR_SISTEMAS");
                txtNombreRol.setForeground(TEXT_MUTED);
                txtCodigoToken.setText("ROLE_MOD_CORE");
                txtCodigoToken.setForeground(TEXT_MUTED);
            } else {
                JOptionPane.showMessageDialog(this, "Error de Persistencia: Verifique que el código token o nombre no estén duplicados.", "Transacción Fallida", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnGuardar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaAuditoriaRoles() {
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

        JLabel title = new JLabel("📊 NIVELES DE AUTORIZACIÓN EXISTENTES EN BD");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"ID_ROL (PK)", "NOMBRE DEL ROL", "CÓDIGO TOKEN", "JERARQUÍA"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaRoles = new JTable(modeloTabla);
        tablaRoles.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaRoles.setForeground(TEXT_WHITE);
        tablaRoles.setBackground(INPUT_BG);
        tablaRoles.setRowHeight(32);
        tablaRoles.setGridColor(new Color(255, 255, 255, 8));
        tablaRoles.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaRoles.setShowVerticalLines(false);

        // --- ENCABEZADO CUSTOM CON ESTILO CIBERNÉTICO ---
        JTableHeader header = tablaRoles.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(5, 8, 22)); // Fondo oscuro integrado
                label.setForeground(CYAN_NEON);           // Texto cian neón brillante
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, PINK_NEON)); // Subrayado neón rosa
                return label;
            }
        });
        // -----------------------------------------------------------------

        tablaRoles.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 1) {
                    if (val != null && val.toString().contains("ADMIN")) setForeground(PINK_NEON);
                    else if (val != null && val.toString().contains("MODERADOR")) setForeground(CYAN_NEON);
                    else setForeground(TEXT_WHITE);
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else if (c == 3) {
                    setForeground(new Color(0, 255, 130)); 
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaRoles);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnEliminar = new JButton("❌ ELIMINAR ROL DEL SISTEMA (DELETE)") {
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
            int filaSeleccionada = tablaRoles.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Por favor seleccione un rol para purgar.", "SEGURIDAD", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String idRaw = modeloTabla.getValueAt(filaSeleccionada, 0).toString();
            int idRol = Integer.parseInt(idRaw.replace("#ROL-", ""));

            int res = JOptionPane.showConfirmDialog(this, 
                "¿Está completamente seguro? Eliminar un rol puede dejar cuentas huérfanas en cascada si existen llaves foráneas activas.", 
                "ALERTA CRÍTICA", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                
            if (res == JOptionPane.YES_OPTION) {
                boolean exito = RolDAO.eliminarRol(idRol);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Sentencia SQL ejecutada con éxito. Rol revocado físicamente.", "Operación Completada", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaRolesReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Error de Integridad Referencial:\nNo se puede eliminar este rol porque existen usuarios asociados a él.", "Restricción de MySQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        card.add(btnEliminar, BorderLayout.SOUTH);
        return card;
    }

    public void actualizarTablaRolesReal() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = RolDAO.obtenerRoles();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"#ROL-000", "SIN REGISTROS", "ROLE_NULL", "Nivel 0"});
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