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

public class PanelNivel extends JPanel {

    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    // Mapeo global de campos para extracción de datos
    private JTextField txtIdNivel;
    private JTextField txtXpRequerida;
    private JTextField txtRecompensaMoneda;
    private JComboBox<String> cbCosmetico;

    private JTable tablaNiveles;
    private DefaultTableModel modeloTabla;

    public PanelNivel() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. ENCABEZADO DE CONFIGURACIÓN DEL CORE
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DISTRIBUIDA DE NIVELES
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaNiveles(), gbc);
        
        setFocusable(true);
        actualizarTablaProgresionReal();
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CONSOLA DE PARAMETRIZACIÓN DE NIVELES");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Configuración de la curva de experiencia del servidor. Define los requisitos de XP y recompensas por nivel.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaNiveles() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearFormularioEscala());
        panel.add(crearTablaEscalaNiveles());

        return panel;
    }

    private JPanel crearFormularioEscala() {
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

        JLabel title = new JLabel("🎯 CONFIGURAR ESCALÓN DE PROGRESIÓN (INSERT/UPDATE)");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("ID / NÚMERO DE NIVEL A CREAR O MODIFICAR (PK)"));
        txtIdNivel = crearCampoTexto("Ej: 5");
        fields.add(txtIdNivel);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("EXPERIENCIA REQUERIDA (XP TOTAL UMBRAL)"));
        txtXpRequerida = crearCampoTexto("5000");
        fields.add(txtXpRequerida);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("RECOMPENSA ECONÓMICA DE BILLETE DIRECTO (B$)"));
        txtRecompensaMoneda = crearCampoTexto("250");
        fields.add(txtRecompensaMoneda);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("TIPO DE RECOMPENSA COSMÉTICA ADICIONAL"));
        cbCosmetico = crearSelector(new String[]{"NINGUNA (SOLO DIVISAS)", "🎁 CAJA DE SUMINISTROS BÁSICA", "💎 EMBLEMA DE RANGO PREMIUM"});
        fields.add(cbCosmetico);

        card.add(fields, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("🎯 ESTABLECER REGLA DE NIVEL") {
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
            try {
                int idNivel = Integer.parseInt(txtIdNivel.getText().trim());
                int xp = Integer.parseInt(txtXpRequerida.getText().trim());
                int moneda = Integer.parseInt(txtRecompensaMoneda.getText().trim());
                String cosmetico = cbCosmetico.getSelectedItem().toString();

                if (idNivel <= 0 || xp < 0 || moneda < 0) {
                    JOptionPane.showMessageDialog(this, "Los valores numéricos no pueden ser negativos ni el nivel igual a cero.", "Error numérico", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                boolean exito = NivelDAO.guardarOActualizarNivel(idNivel, xp, moneda, cosmetico);
                if (exito) {
                    JOptionPane.showMessageDialog(this, "Curva de nivel actualizada. Los requisitos de XP han sido inyectados.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    actualizarTablaProgresionReal();
                    
                    txtIdNivel.setText("Ej: 5"); txtIdNivel.setForeground(TEXT_MUTED);
                    txtXpRequerida.setText("5000"); txtXpRequerida.setForeground(TEXT_MUTED);
                    txtRecompensaMoneda.setText("250"); txtRecompensaMoneda.setForeground(TEXT_MUTED);
                    cbCosmetico.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "Error de Datos: Compruebe las restricciones del servidor SQL.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Formato Inválido: Asegúrese de que el ID, la XP y la recompensa económica sean números enteros válidos sin letras.", "Error de Entrada", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnGuardar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaEscalaNiveles() {
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

        JLabel title = new JLabel("📊 CURVA DE EXPERIENCIA ACTUAL EN EL MOTOR");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"NIVEL (PK)", "XP REQUERIDA", "RECOMPENSA BASE", "COSMÉTICO RECOMPENSA"};

        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaNiveles = new JTable(modeloTabla);
        tablaNiveles.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaNiveles.setForeground(TEXT_WHITE);
        tablaNiveles.setBackground(INPUT_BG);
        tablaNiveles.setRowHeight(32);
        tablaNiveles.setGridColor(new Color(255, 255, 255, 8));
        tablaNiveles.setSelectionBackground(new Color(0, 240, 255, 30));
        tablaNiveles.setShowVerticalLines(false);

        JTableHeader header = tablaNiveles.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setBackground(new Color(10, 14, 30));
        header.setForeground(CYAN_NEON);
        header.setReorderingAllowed(false);
        header.setResizingAllowed(false);
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                cell.setBackground(new Color(12, 16, 35));
                cell.setForeground(CYAN_NEON);
                cell.setFont(new Font("Dialog", Font.BOLD, 11));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 240, 255, 50)));
                return cell;
            }
        });

        tablaNiveles.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 0) {
                    setForeground(CYAN_NEON);
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

        JScrollPane scroll = new JScrollPane(tablaNiveles);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        JButton btnEliminar = new JButton("❌ PURGAR ESCALÓN DE NIVEL (DELETE)") {
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
            int filaSeleccionada = tablaNiveles.getSelectedRow();
            if (filaSeleccionada == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un escalón en la tabla para eliminar.", "UMBRALES", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // CORRECCIÓN DE EXCEPCIÓN: Se extrae como String y se parsea limpiando el prefijo "Nivel "
            String nivelStr = tablaNiveles.getValueAt(filaSeleccionada, 0).toString();
            if (nivelStr.contains("--")) {
                JOptionPane.showMessageDialog(this, "No se puede eliminar un registro vacío.", "Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            try {
                int idNivel = Integer.parseInt(nivelStr.replace("Nivel ", "").trim());

                int confirm = JOptionPane.showConfirmDialog(this, 
                    "Advertencia extrema: Eliminar un nivel de la tabla de control puede corromper el cálculo de progreso de los usuarios que estén en ese rango. ¿Ejecutar DELETE?", 
                    "DANGER ZONE", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
                    
                if (confirm == JOptionPane.YES_OPTION) {
                    boolean exito = NivelDAO.eliminarNivel(idNivel);
                    if (exito) {
                        JOptionPane.showMessageDialog(this, "Escalón de dificultad purgado de la base de datos.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        actualizarTablaProgresionReal();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error Operativo: No se pudo despachar la orden de borrado.", "Error de Transacción", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error al procesar el identificador del nivel.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
            }
        });
        card.add(btnEliminar, BorderLayout.SOUTH);

        return card;
    }

    public void actualizarTablaProgresionReal() {
        modeloTabla.setRowCount(0);
        List<Object[]> datos = NivelDAO.obtenerNiveles();
        for (Object[] fila : datos) {
            String nivelFormateado = "Nivel " + String.format("%02d", (int) fila[0]);
            String xpFormateada = String.format("%,d XP", (int) fila[1]);
            String monedaFormateada = String.format("%,d B$", (int) fila[2]);
            String cosmetico = (fila[3] != null) ? fila[3].toString() : "NINGUNA (SOLO DIVISAS)";

            modeloTabla.addRow(new Object[]{nivelFormateado, xpFormateada, monedaFormateada, cosmetico});
        }
        if (modeloTabla.getRowCount() == 0) {
            modeloTabla.addRow(new Object[]{"Nivel --", "0 XP", "0 B$", "Sin registros activos"});
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
        c.setOpaque(false); 

        c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        Graphics2D g2d = (Graphics2D) g.create();
                        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2d.setColor(INPUT_BG);
                        g2d.fillRect(0, 0, getWidth(), getHeight());
                        
                        g2d.setColor(CYAN_NEON);
                        int[] xPoints = {getWidth() / 2 - 5, getWidth() / 2 + 5, getWidth() / 2};
                        int[] yPoints = {getHeight() / 2 - 2, getHeight() / 2 - 2, getHeight() / 2 + 4};
                        g2d.fillPolygon(xPoints, yPoints, 3);
                        g2d.dispose();
                    }
                };
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                return b;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2d.setColor(INPUT_BG);
                g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
                
                g2d.setColor(comboBox.hasFocus() ? CYAN_NEON : new Color(255, 255, 255, 15));
                g2d.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
                
                g2d.dispose();
            }
        });
        
        c.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                
                list.setBackground(INPUT_BG); 
                list.setForeground(TEXT_WHITE);
                
                if (index == -1) {
                    l.setOpaque(false); 
                    l.setForeground(TEXT_WHITE);
                } else {
                    l.setOpaque(true);
                    if (isSelected) {
                        l.setBackground(new Color(0, 240, 255, 35)); 
                        l.setForeground(CYAN_NEON);
                    } else {
                        l.setBackground(INPUT_BG);
                        l.setForeground(TEXT_WHITE);
                    }
                }
                return l;
            }
        });
        
        c.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1)); 
        
        c.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) { c.repaint(); }
            @Override
            public void focusLost(FocusEvent e) { c.repaint(); }
        });

        return c;
    }
}