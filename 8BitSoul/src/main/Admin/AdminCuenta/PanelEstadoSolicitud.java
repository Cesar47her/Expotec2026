package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import main.Util.EstiloDiseno;

public class PanelEstadoSolicitud extends JPanel {

    // Paleta de Colores de Estilo Cyberpunk / Synthwave
    private static final Color CARD_BG     = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG    = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON   = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON   = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE  = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED  = EstiloDiseno.TEXT_MUTED;

    private JTextArea txtAreaDetalleSolicitud;
    private JTextArea txtAreaDictamenAdmin;
    private JComboBox<String> cbActualizarEstado;
    private JTable tablaTickets;
    private DefaultTableModel modeloTickets;

    public PanelEstadoSolicitud() {
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. TÍTULO SUPERIOR
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. PANEL CONTENEDOR CENTRAL DIVIDIDO EN DOS CONSOLAS
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaSoporte(), gbc);
        
        // Ejecución forzada en el hilo de la interfaz gráfica
        SwingUtilities.invokeLater(() -> {
            inicializarConsola();
        });
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("CENTRAL DE SOPORTE: SOLICITUDES DE AYUDA");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Consola de atención al cliente. Administra reportes de bugs, auditorías de cuentas y problemas del core.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearConsolaSoporte() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 25, 0));
        panel.setOpaque(false);

        panel.add(crearPanelDiagnosticoIzquierdo());
        panel.add(crearPanelBandejaDerecho());

        return panel;
    }

    private JPanel crearPanelDiagnosticoIzquierdo() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0); path.lineTo(w, 0); path.lineTo(w, h); path.lineTo(0, h); path.lineTo(0, cut);
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

        JLabel title = new JLabel("🛠️ INFORME DE DIAGNÓSTICO Y RESPUESTA");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel centro = new JPanel();
        centro.setOpaque(false);
        centro.setLayout(new BoxLayout(centro, BoxLayout.Y_AXIS));

        centro.add(crearLabelInput("DETALLE DE LA SOLICITUD ENVIADA POR EL USUARIO"));
        txtAreaDetalleSolicitud = crearAreaTexto("Selecciona un ticket de la tabla de la derecha para inspeccionar el problema reportado de manera detallada...", false);
        JScrollPane scrollDetalle = abrirScroll(txtAreaDetalleSolicitud);
        scrollDetalle.setPreferredSize(new Dimension(0, 140));
        centro.add(scrollDetalle);
        centro.add(Box.createRigidArea(new Dimension(0, 15)));

        centro.add(crearLabelInput("ACTUALIZAR ESTADO DE LA SOLICITUD (UPDATE)"));
        
        // 🎨 JCOMBOBOX CONFIGURADO CON ANULACIÓN DE FONDO BLANCO NATIVO
        cbActualizarEstado = new JComboBox<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        cbActualizarEstado.setFont(new Font("Dialog", Font.PLAIN, 12));
        cbActualizarEstado.setForeground(TEXT_WHITE);
        cbActualizarEstado.setBackground(INPUT_BG);
        cbActualizarEstado.setOpaque(false); // Evita que dibuje la base blanca del Look and Feel
        cbActualizarEstado.setMaximumSize(new Dimension(1920, 36));
        cbActualizarEstado.setPreferredSize(new Dimension(0, 36));
        
        // Customización estética de la flecha triangular neón
        cbActualizarEstado.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
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

        // Renderizado personalizado de las opciones internas del desplegable
        cbActualizarEstado.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                l.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                l.setFont(new Font("Dialog", Font.PLAIN, 12));
                l.setOpaque(true);
                
                if (isSelected) {
                    l.setBackground(new Color(0, 240, 255, 35)); // Selección neón translúcida
                    l.setForeground(CYAN_NEON);
                } else {
                    l.setBackground(new Color(12, 16, 32)); // Fondo oscuro integrado
                    l.setForeground(TEXT_WHITE);
                }
                return l;
            }
        });

        // 🔥 ANULACIÓN CRÍTICA: Hace transparente la celda seleccionada por defecto para ocultar el recuadro blanco
        ((JLabel)cbActualizarEstado.getRenderer()).setOpaque(false);

        // Forzado de bordes y fondo al menú desplegable abierto (Popup)
        cbActualizarEstado.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                Object popup = cbActualizarEstado.getUI().getAccessibleChild(cbActualizarEstado, 0);
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
        
        cbActualizarEstado.setBorder(BorderFactory.createEmptyBorder());
        centro.add(cbActualizarEstado);
        centro.add(Box.createRigidArea(new Dimension(0, 15)));

        centro.add(crearLabelInput("DICTAMEN FINAL / CONTESTACIÓN DEL ADMINISTRADOR"));
        txtAreaDictamenAdmin = crearAreaTexto("", true);
        JScrollPane scrollDictamen = abrirScroll(txtAreaDictamenAdmin);
        scrollDictamen.setPreferredSize(new Dimension(0, 120));
        centro.add(scrollDictamen);

        card.add(centro, BorderLayout.CENTER);

        JButton btnEnviar = new JButton("💾 ENVIAR RESPUESTA Y ACTUALIZAR BD") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(255, 0, 127, 30) : new Color(5, 8, 22));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnEnviar.setFont(new Font("Dialog", Font.BOLD, 12));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setPreferredSize(new Dimension(0, 40));
        btnEnviar.setContentAreaFilled(false);
        btnEnviar.setBorderPainted(false);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnEnviar.addActionListener(e -> {
            int filaSel = tablaTickets.getSelectedRow();
            if (filaSel == -1) {
                JOptionPane.showMessageDialog(this, "Por favor, selecciona un ticket de la bandeja derecha.", "AVISO", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String idTexto = modeloTickets.getValueAt(filaSel, 0).toString().replace("#TK-", "").trim();
                int idTicket = Integer.parseInt(idTexto);

                if(cbActualizarEstado.getSelectedItem() == null) {
                    JOptionPane.showMessageDialog(this, "No hay estados válidos cargados.", "ERROR", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String nuevoEstado = cbActualizarEstado.getSelectedItem().toString();
                String respuestaAdmin = txtAreaDictamenAdmin.getText().trim();

                if (respuestaAdmin.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "El campo de dictamen o contestación no puede estar vacío.", "DATO REQUERIDO", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (EstadoSolicitudDAO.responderYActualizarTicket(idTicket, nuevoEstado, respuestaAdmin)) {
                    JOptionPane.showMessageDialog(this, "¡Ticket #" + idTicket + " actualizado y respondido correctamente!", "ÉXITO", JOptionPane.INFORMATION_MESSAGE);
                    inicializarConsola(); 
                    
                    txtAreaDetalleSolicitud.setText("Selecciona un ticket de la tabla de la derecha para inspeccionar el problema reportado de manera detallada...");
                    txtAreaDictamenAdmin.setText("");
                } else {
                    JOptionPane.showMessageDialog(this, "Ocurrió un error en la base de datos al procesar el ticket.", "ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                System.err.println("[ERROR INTERFAZ] Error al procesar ID de ticket: " + ex.getMessage());
            }
        });

        card.add(btnEnviar, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearPanelBandejaDerecho() {
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); path.lineTo(w - cut, 0); path.lineTo(w, cut); path.lineTo(w, h); path.lineTo(0, h);
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

        JLabel title = new JLabel("📥 BANDEJA DE REPORTES ENTRANTE");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(PINK_NEON);
        card.add(title, BorderLayout.NORTH);

        String[] columnas = {"ID_TICKET", "ID_USER", "ASUNTO", "ESTADO"};
        modeloTickets = new DefaultTableModel(null, columnas) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTickets = new JTable();
        tablaTickets.setModel(modeloTickets); 
        
        tablaTickets.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaTickets.setForeground(TEXT_WHITE);
        tablaTickets.setBackground(INPUT_BG);
        tablaTickets.setRowHeight(35);
        tablaTickets.setGridColor(new Color(255, 255, 255, 8));
        tablaTickets.setSelectionBackground(new Color(255, 0, 127, 30));
        tablaTickets.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaTickets.setShowVerticalLines(false);

        tablaTickets.getColumnModel().getColumn(0).setPreferredWidth(80);
        tablaTickets.getColumnModel().getColumn(1).setPreferredWidth(90);
        tablaTickets.getColumnModel().getColumn(2).setPreferredWidth(220);
        tablaTickets.getColumnModel().getColumn(3).setPreferredWidth(100);

        // Control de selección de filas
        tablaTickets.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int filaSel = tablaTickets.getSelectedRow();
                if (filaSel == -1 || filaSel >= modeloTickets.getRowCount()) {
                    return; 
                }

                try {
                    String idTexto = modeloTickets.getValueAt(filaSel, 0).toString().replace("#TK-", "").trim();
                    int idTicket = Integer.parseInt(idTexto);

                    Object[] detalles = EstadoSolicitudDAO.obtenerDetalleTicket(idTicket);

                    if (detalles != null) {
                        String mensajeUsuario = (String) detalles[0];
                        String contestacionAdmin = (String) detalles[1];

                        txtAreaDetalleSolicitud.setText(mensajeUsuario);
                        txtAreaDetalleSolicitud.setCaretPosition(0);

                        txtAreaDictamenAdmin.setText(contestacionAdmin);

                        String estadoActual = modeloTickets.getValueAt(filaSel, 3).toString();
                        cbActualizarEstado.setSelectedItem(estadoActual);
                    }
                } catch (Exception ex) {
                    System.err.println("[ERROR SELECCIÓN] Error al leer datos de la fila: " + ex.getMessage());
                }
            }
        });

        // 🎨 CABECERA DE TABLA CORREGIDA CON ESTILO OSCURO DE MATRIZ COHERENTE
        JTableHeader header = tablaTickets.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setOpaque(false);
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(11, 15, 30)); // Fondo oscuro plano coherente
                setForeground(CYAN_NEON);            // Texto Cian Neón
                setFont(new Font("Dialog", Font.BOLD, 11));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CYAN_NEON)); // Subrayado técnico
                return this;
            }
        });

        tablaTickets.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 3 && val != null) { 
                    String est = val.toString();
                    if (est.equalsIgnoreCase("PENDIENTE")) {
                        setForeground(PINK_NEON);
                    } else if (est.equalsIgnoreCase("EN REVISIÓN")) {
                        setForeground(new Color(255, 170, 0));
                    } else {
                        setForeground(Color.GREEN);
                    }
                    setFont(getFont().deriveFont(Font.BOLD));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                setBackground(isSel ? table.getSelectionBackground() : (r % 2 == 0 ? INPUT_BG : new Color(3, 5, 14)));
                setBorder(noFocusBorder);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaTickets);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        card.add(scroll, BorderLayout.CENTER);

        return card;
    }

    public void inicializarConsola() {
        cargarComboEstados();
        actualizarTablaBandeja();
    }

    private void actualizarTablaBandeja() {
        if (modeloTickets != null) {
            modeloTickets.setRowCount(0); 
            
            ArrayList<Object[]> reportes = EstadoSolicitudDAO.obtenerTicketsBandeja();
            
            if (reportes != null && !reportes.isEmpty()) {
                for (Object[] fila : reportes) {
                    modeloTickets.addRow(fila);
                }
            } else {
                System.out.println("[ALERTA] No se leyeron registros reales o la base de datos está vacía.");
            }
            
            modeloTickets.fireTableDataChanged();
            tablaTickets.revalidate();
            tablaTickets.repaint();
        }
    }

    private void cargarComboEstados() {
        if (cbActualizarEstado != null) {
            cbActualizarEstado.removeAllItems();
            ArrayList<Object[]> estados = EstadoSolicitudDAO.obtenerEstados();
            if (estados.isEmpty()) {
                cbActualizarEstado.addItem("PENDIENTE");
                cbActualizarEstado.addItem("EN REVISIÓN");
                cbActualizarEstado.addItem("RESUELTO");
            } else {
                for (Object[] est : estados) {
                    cbActualizarEstado.addItem(est[1].toString()); 
                }
            }
        }
    }

    private JTextArea crearAreaTexto(String textoDefecto, boolean editable) {
        JTextArea area = new JTextArea(textoDefecto);
        area.setFont(new Font("Dialog", Font.PLAIN, 12));
        area.setForeground(editable ? TEXT_WHITE : TEXT_MUTED);
        area.setBackground(INPUT_BG);
        area.setCaretColor(CYAN_NEON);
        area.setEditable(editable);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        return area;
    }

    private JScrollPane abrirScroll(JTextArea area) {
        JScrollPane s = new JScrollPane(area);
        s.setOpaque(false);
        s.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 15)));
        return s;
    }

    private JLabel crearLabelInput(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        return l;
    }
}