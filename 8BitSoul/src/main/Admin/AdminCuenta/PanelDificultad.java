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

public class PanelDificultad extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Campos de datos globales para la extracción limpia
    private JTextField txtIdDificultad;
    private JTextField txtMultDanio;
    private JTextField txtBonusXp;
    private JTextField txtDropRate;
    private JComboBox<String> cbModificador;

    private JTable tablaDificultad;
    private DefaultTableModel modeloTabla;

    public PanelDificultad() {
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

        // 2. CONSOLA DISTRIBUIDA DE DIFICULTAD
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaDificultad(), gbc);
        
        setFocusable(true);
        actualizarTablaMatrizReal();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE CONTROL Y CONFIGURACIÓN DE DIFICULTAD");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Calibración del motor matemático. Modifica los multiplicadores de daño, escalado de enemigos y tasas de drop.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaDificultad() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioCoeficientes());
        panel.add(crearTablaMatrizDificultad());

        return panel;
    }

    private JPanel crearFormularioCoeficientes() {
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

        JLabel title = new JLabel("⚙️ ALTERAR MULTIPLICADORES LÓGICOS (INSERT/UPDATE)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("ID / NOMBRE DE LA DIFICULTAD (PK_UPPERCASE)"));
        txtIdDificultad = crearCampoTexto("NIGHTMARE_V2");
        fields.add(txtIdDificultad);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("MULTIPLICADOR DE ESCALADO GENERAL (HP / DAÑO)"));
        txtMultDanio = crearCampoTexto("2.50");
        fields.add(txtMultDanio);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("BONIFICACIÓN DE XP EXTRA (PORCENTAJE DE RENDIMIENTO)"));
        txtBonusXp = crearCampoTexto("150%");
        fields.add(txtBonusXp);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("TASA DE DROP DE ÍTEMS RAROS (PROBABILIDAD MÁXIMA)"));
        txtDropRate = crearCampoTexto("0.08");
        fields.add(txtDropRate);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("MODIFICADOR DE ENTRADA AL SERVIDOR"));
        cbModificador = crearSelector(new String[]{"DISPONIBLE PARA TODO PÚBLICO", "🔒 RESTRINGIDO POR NIVEL MÍNIMO", "⚠️ MODO TEMPORAL / EVENTO"});
        fields.add(cbModificador);

        card.add(fields, BorderLayout.CENTER);

        JButton btnActualizar = new JButton("⚙️ INYECTAR PARÁMETROS AL MOTOR") {
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
            try {
                String id = txtIdDificultad.getText().trim().toUpperCase();
                double mult = Double.parseDouble(txtMultDanio.getText().replace("x", "").trim());
                int xp = Integer.parseInt(txtBonusXp.getText().replace("%", "").replace("+", "").trim());
                double drop = Double.parseDouble(txtDropRate.getText().trim());
                String entrada = cbModificador.getSelectedItem().toString();

                if (id.isEmpty() || id.contains("NIGHTMARE_V2")) {
                    JOptionPane.showMessageDialog(this, "Por favor ingrese un ID de dificultad válido.", "Error de ID", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean exito = DificultadDAO.guardarOActualizarDificultad(id, mult, xp, drop, entrada);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Variables de dificultad modificadas con éxito.", "Sincronizado", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaMatrizReal();
                    
                    txtIdDificultad.setText("NIGHTMARE_V2"); txtIdDificultad.setForeground(TEXT_MUTED);
                    txtMultDanio.setText("2.50"); txtMultDanio.setForeground(TEXT_MUTED);
                    txtBonusXp.setText("150%"); txtBonusXp.setForeground(TEXT_MUTED);
                    txtDropRate.setText("0.08"); txtDropRate.setForeground(TEXT_MUTED);
                    cbModificador.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "El motor SQL rechazó los valores asignados.", "Error en Transacción", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Sintaxis Incorrecta: Compruebe que los multiplicadores y tasas de drop utilicen puntos decimales válidos.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnActualizar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaMatrizDificultad() {
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

        JLabel title = new JLabel("📊 MATRIZ DE REGLAS DE ESCALADO EN BD");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"DIFICULTAD (PK)", "MULT. DAÑO", "BONUS XP", "DROP RATE"};

        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaDificultad = new JTable(modeloTabla);
        tablaDificultad.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaDificultad.setForeground(TEXT_WHITE);
        tablaDificultad.setBackground(INPUT_BG);
        tablaDificultad.setRowHeight(32);
        tablaDificultad.setGridColor(new Color(255, 255, 255, 8));
        tablaDificultad.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaDificultad.setShowVerticalLines(false);

        // 🎨 CABECERA DE TABLA CORREGIDA (FONDO OSCURO Y TEXTO CIAN NEÓN)
        JTableHeader header = tablaDificultad.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setOpaque(false);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(11, 15, 30)); // Fondo oscuro integrado al sistema
                setForeground(CYAN_NEON);            // Texto Neón
                setFont(new Font("Dialog", Font.BOLD, 11));
                setHorizontalAlignment(SwingConstants.CENTER);
                // Subrayado tecnológico inferior
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CYAN_NEON)); 
                return this;
            }
        });

        tablaDificultad.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 0) {
                    if ("NIGHTMARE".equals(val) || (val != null && val.toString().contains("NIGHT")) || "HARDCORE".equals(val)) {
                        setForeground(PINK_NEON);
                    } else {
                        setForeground(CYAN_NEON);
                    }
                    setFont(new Font("Dialog", Font.BOLD, 12));
                } else if (c == 2) {
                    setForeground(new Color(0, 255, 130)); 
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(TEXT_WHITE);
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaDificultad);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnEliminar = new JButton("❌ DESTRUIRE ALGORITMO DE DIFICULTAD (DELETE)") {
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
            int filaSeleccionada = tablaDificultad.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un modo de juego en la tabla para purgar.", "BALANCEO", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String idDificultad = modeloTabla.getValueAt(filaSeleccionada, 0).toString();

            int confirm = JOptionPane.showConfirmDialog(this, 
                "¡Cuidado! Si eliminas una dificultad maestra (" + idDificultad + ") de la base de datos, las misiones vinculadas quedarán inconsistentes. ¿Proceder con el DELETE?", 
                "CRITICAL REASONING", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                boolean exito = DificultadDAO.eliminarDificultad(idDificultad);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Sentencia SQL despachada. Parámetro purgado del núcleo operativo.");
                    actualizarTablaMatrizReal();
                } else {
                    JOptionPane.showMessageDialog(this, "Fallo del Sistema: No se pudo eliminar el registro seleccionado.", "Error SQL", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        card.add(btnEliminar, BorderLayout.SOUTH);

        return card;
    }

    public void actualizarTablaMatrizReal() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = DificultadDAO.obtenerDificultades();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"Vacío", "x 0.0", "0%", "0%"});
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

    // 🎨 JCOMBOBOX TOTALMENTE REDISEÑADO SIN CAJA BLANCA REBELDE
    private JComboBox<String> crearSelector(String[] items) {
        JComboBox<String> c = new JComboBox<>(items) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG); // Color oscuro del sistema
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 15)); // Borde sutil por defecto
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        
        c.setFont(new Font("Dialog", Font.PLAIN, 12));
        c.setForeground(TEXT_WHITE);
        c.setBackground(INPUT_BG);
        c.setOpaque(false); // Previene que el LookAndFeel dibuje su propia caja gris/blanca
        c.setMaximumSize(new Dimension(1920, 36));
        c.setPreferredSize(new Dimension(0, 36));
        
        // Customizar la flecha desplegable para que sea un triángulo Cian Neón
        c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(CYAN_NEON);
                        int[] xPoints = {getWidth()/2 - 5, getWidth()/2 + 5, getWidth()/2};
                        int[] yPoints = {getHeight()/2 - 3, getHeight()/2 - 3, getHeight()/2 + 4};
                        g2d.fillPolygon(xPoints, yPoints, 3);
                        g2d.dispose();
                    }
                };
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                return b;
            }
        });
        
        // Renderizador personalizado de celdas internas
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                l.setFont(new Font("Dialog", Font.PLAIN, 12));
                l.setOpaque(true);
                
                if (isSelected) {
                    l.setBackground(new Color(0, 240, 255, 35)); // Selección translúcida
                    l.setForeground(CYAN_NEON);
                } else {
                    l.setBackground(new Color(12, 16, 32)); // Fondo desplegable oscuro profundo
                    l.setForeground(TEXT_WHITE);
                }
                return l;
            }
        });
        
        // 🔥 ANULACIÓN CRÍTICA: Desactiva la opacidad de la celda seleccionada principal para que se vea el fondo oscuro trasero
        ((JLabel)c.getRenderer()).setOpaque(false);
        
        // Control de PopUp Flotante para forzar bordes y fondos oscuros en el menú desplegable abierto
        c.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = c.getUI().getAccessibleChild(c, 0);
                if (popup instanceof JPopupMenu) {
                    JPopupMenu pm = (JPopupMenu) popup;
                    pm.setBorder(BorderFactory.createLineBorder(CYAN_NEON, 1));
                    pm.setBackground(new Color(12, 16, 32));
                    
                    Component cScroll = pm.getComponent(0);
                    if (cScroll instanceof JScrollPane) {
                        JScrollPane scroll = (JScrollPane) cScroll;
                        scroll.getViewport().setBackground(new Color(12, 16, 32));
                        scroll.setBorder(BorderFactory.createEmptyBorder());
                    }
                }
            }
            @Override public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
        });
        
        c.setBorder(BorderFactory.createEmptyBorder()); // El recuadro exterior se maneja en paintComponent
        return c;
    }
}