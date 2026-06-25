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

public class PanelProgreso extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Componentes del formulario
    private JTextField txtUsername;
    private JTextField txtNivel;
    private JTextField txtXp;
    private JTextField txtRendimiento;
    private JComboBox<String> cbAccionGlobal;

    private JTable tablaLeaderboard;
    private DefaultTableModel modeloTabla;
    private JScrollPane scrollTabla;

    public PanelProgreso() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER DE CONTROL OPERATIVO
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DE PROGRESO DISTRIBUIDA
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaProgreso(), gbc);
        
        setFocusable(true);
        
        // Ejecución inicial de llenado de datos asegurando el hilo EDT
        SwingUtilities.invokeLater(this::actualizarLeaderboardReal);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE PROGRESO Y ESTADÍSTICAS");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Gestión del sistema de niveles y experiencia (XP). Modifica rangos y audita las tablas de rendimiento.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaProgreso() {
        // Se define un GridBagLayout para dar pesos proporcionales y evitar colapso de componentes
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // Panel izquierdo: Formulario
        gbc.gridx = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 15);
        panel.add(crearFormularioProgreso(), gbc);

        // Panel derecho: Tabla Monitor
        gbc.gridx = 1;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 15, 0, 0);
        panel.add(crearTablaLeaderboard(), gbc);

        return panel;
    }

    private JPanel crearFormularioProgreso() {
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

        JLabel title = new JLabel("📈 ALTERAR PARÁMETROS DE PROGRESO (UPDATE)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("USERNAME DEL USUARIO AFECTADO"));
        txtUsername = crearCampoTexto("UserName");
        fields.add(txtUsername);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("FORZAR NUEVO NIVEL / RANGO ACTUAL"));
        txtNivel = crearCampoTexto("50");
        fields.add(txtNivel);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("PUNTOS DE EXPERIENCIA A ASIGNAR (XP)"));
        txtXp = crearCampoTexto("12500");
        fields.add(txtXp);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("MODIFICADOR DE RENDIMIENTO (AMENAZAS DERROTADAS)"));
        txtRendimiento = crearCampoTexto("120");
        fields.add(txtRendimiento);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("ACCIÓN GLOBAL DE TEMPORADA"));
        cbAccionGlobal = crearSelector(new String[]{"MANTENER VALORES ACTUALES", "🔄 PRESTIGIO / REINICIAR EXPERIENCIA", "⚠️ PENALIZACIÓN DE NIVEL"});
        fields.add(cbAccionGlobal);

        card.add(fields, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("📈 ACTUALIZAR NIVEL Y ESTADÍSTICAS") {
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
        btnActualizar.setFont(new Font("Dialog", Font.BOLD, 12));
        btnActualizar.setForeground(Color.WHITE);
        btnActualizar.setPreferredSize(new Dimension(0, 38));
        btnActualizar.setContentAreaFilled(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnActualizar.addActionListener(e -> {
            String user = txtUsername.getText().trim();
            String lvlStr = txtNivel.getText().trim();
            String xpStr = txtXp.getText().trim();
            String rendStr = txtRendimiento.getText().trim();
            String accion = cbAccionGlobal.getSelectedItem().toString();

            if (user.isEmpty() || user.equals("UserName") || lvlStr.isEmpty() || xpStr.isEmpty() || rendStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debe completar todos los campos de progreso válidamente.", "Aviso de Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int nivel = Integer.parseInt(lvlStr);
                int xp = Integer.parseInt(xpStr);
                int rendimiento = Integer.parseInt(rendStr);

                boolean exito = ProgresoDAO.actualizarProgreso(user, nivel, xp, rendimiento, accion);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Progreso recalculado con éxito. Sincronizado con MySQL.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarLeaderboardReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Error: El username provisto no existe en la BD.", "Error de Destino", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Los campos de estadísticas deben ser exclusivamente números enteros.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnActualizar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaLeaderboard() {
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

        JLabel title = new JLabel("📊 MONITOR DE NIVELES Y LÍDERES EN BD");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"USERNAME", "NIVEL", "EXPERIENCIA (XP)", "THREATS KILLED"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaLeaderboard = new JTable(modeloTabla);
        tablaLeaderboard.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaLeaderboard.setForeground(TEXT_WHITE);
        tablaLeaderboard.setBackground(INPUT_BG);
        tablaLeaderboard.setRowHeight(32);
        tablaLeaderboard.setGridColor(new Color(255, 255, 255, 8));
        tablaLeaderboard.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaLeaderboard.setShowVerticalLines(false);

        // Cabecera estilizada integrada al sistema cyberpunk
        JTableHeader header = tablaLeaderboard.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setReorderingAllowed(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                lbl.setBackground(new Color(12, 16, 32)); 
                lbl.setForeground(CYAN_NEON); 
                lbl.setFont(new Font("Dialog", Font.BOLD, 11));
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(0, 240, 255, 40))); 
                return lbl;
            }
        });

        tablaLeaderboard.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 1) {
                    setForeground(CYAN_NEON);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                } else if (c == 3) {
                    setForeground(new Color(0, 255, 130));
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? PINK_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        // Evento para rellenar campos al seleccionar una fila
        tablaLeaderboard.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tablaLeaderboard.getSelectedRow();
                if (row != -1 && modeloTabla.getValueAt(row, 0) != null) {
                    String userVal = modeloTabla.getValueAt(row, 0).toString();
                    if(!userVal.equals("Sin Perfiles")) {
                        txtUsername.setText(userVal);
                        txtUsername.setForeground(TEXT_WHITE);
                        
                        String lvlClean = modeloTabla.getValueAt(row, 1).toString().replace("Nivel ", "");
                        txtNivel.setText(lvlClean);
                        txtNivel.setForeground(TEXT_WHITE);
                        
                        String xpClean = modeloTabla.getValueAt(row, 2).toString().replace(" XP", "").replace(",", "").replace(".", "");
                        txtXp.setText(xpClean);
                        txtXp.setForeground(TEXT_WHITE);
                        
                        String thClean = modeloTabla.getValueAt(row, 3).toString().replace(" Threats", "");
                        txtRendimiento.setText(thClean);
                        txtRendimiento.setForeground(TEXT_WHITE);
                    }
                }
            }
        });

        scrollTabla = new JScrollPane(tablaLeaderboard);
        scrollTabla.setOpaque(false);
        scrollTabla.getViewport().setOpaque(false);
        scrollTabla.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        
        // Forzamos dimensiones mínimas de renderizado para evitar que el scroll colapse a tamaño 0
        scrollTabla.setMinimumSize(new Dimension(300, 200));
        scrollTabla.setPreferredSize(new Dimension(400, 400));
        
        card.add(scrollTabla, BorderLayout.CENTER);
        return card;
    }

    public void actualizarLeaderboardReal() {
        // Garantizamos la ejecución limpia sobre el Event Dispatch Thread
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(this::actualizarLeaderboardReal);
            return;
        }

        modeloTabla.setRowCount(0);
        try {
            List<Object[]> datos = ProgresoDAO.obtenerLeaderboard();
            if (datos != null && !datos.isEmpty()) {
                for (Object[] fila : datos) {
                    modeloTabla.addRow(fila);
                }
            }
        } catch (Exception ex) {
            System.err.println("[ERROR UI] Error al actualizar la tabla: " + ex.getMessage());
        }

        // Si la tabla sigue vacía tras la consulta, inyectamos la fila por defecto
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"Sin Perfiles", "Nivel 0", "0 XP", "0 Threats"});
        }
        
        // Forzamos repintado estructural de la interfaz de usuario
        tablaLeaderboard.revalidate();
        tablaLeaderboard.repaint();
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