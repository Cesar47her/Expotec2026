package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;

public class PanelAyuda extends JPanel {

    private static final Color CARD_BG = new Color(3, 5, 16, 200);
    private static final Color INPUT_BG = new Color(7, 10, 26);
    private static final Color CYAN_NEON = new Color(0, 240, 255);
    private static final Color PINK_NEON = new Color(242, 5, 203);
    private static final Color TEXT_WHITE = new Color(255, 255, 255);
    private static final Color TEXT_MUTED = new Color(120, 130, 145);

    // Componentes del núcleo de datos
    private final SoporteDAO soporteDAO;
    private List<TicketData> ticketsCache;
    private TicketData ticketSeleccionado = null;

    // Componentes Swing interactivos
    private DefaultTableModel modeloTabla;
    private JTable tablaTickets;
    private JTextArea txtDetalle;
    private JTextArea txtRespuesta;
    private JComboBox<String> selectorEstado;

    public PanelAyuda() {
        this.soporteDAO = new SoporteDAO();
        this.ticketsCache = new ArrayList<>();

        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. HEADER DE CONTROL
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. CONSOLA DE SOPORTE TÉCNICO DIVIDIDA
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearConsolaSoporte(), gbc);

        // Carga inicial asíncrona de los datos de la base de datos
        cargarDatosDesdeBD();
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
        
        txtDetalle = new JTextArea("Selecciona un ticket de la tabla de la derecha para inspeccionar el problema reportado de manera detallada...");
        
        panel.add(crearFormularioResolucion(txtDetalle));
        panel.add(crearTablaTicketsSoporte(txtDetalle));
        
        return panel;
    }

    private JPanel crearFormularioResolucion(JTextArea txtDetalle) {
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

        JLabel title = new JLabel("🛠️ INFORME DE DIAGNÓSTICO Y RESPUESTA");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("DETALLE DE LA SOLICITUD ENVIADA POR EL USUARIO"));
        
        txtDetalle.setFont(new Font("Dialog", Font.ITALIC, 12));
        txtDetalle.setForeground(TEXT_MUTED);
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
        txtDetalle.setOpaque(false);
        txtDetalle.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        JScrollPane scrollDetalle = new JScrollPane(txtDetalle);
        scrollDetalle.setOpaque(false);
        scrollDetalle.getViewport().setOpaque(false);
        scrollDetalle.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 10)));
        scrollDetalle.setPreferredSize(new Dimension(0, 100));
        fields.add(scrollDetalle);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("ACTUALIZAR ESTADO DE LA SOLICITUD (UPDATE)"));
        selectorEstado = crearSelector(new String[]{"PENDIENTE", "EN REVISIÓN", "RESUELTO"});
        fields.add(selectorEstado);
        fields.add(Box.createRigidArea(new Dimension(0, 15)));

        fields.add(crearLabelInput("DICTAMEN FINAL / CONTESTACIÓN DEL ADMINISTRADOR"));
        txtRespuesta = new JTextArea() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
                
                super.paintComponent(g);
                
                Graphics2D g2dBorder = (Graphics2D) g.create();
                g2dBorder.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 15));
                g2dBorder.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2dBorder.dispose();
            }
        };
        txtRespuesta.setFont(new Font("Dialog", Font.PLAIN, 12));
        txtRespuesta.setForeground(TEXT_WHITE);
        txtRespuesta.setCaretColor(PINK_NEON);
        txtRespuesta.setLineWrap(true);
        txtRespuesta.setWrapStyleWord(true);
        txtRespuesta.setOpaque(false);
        txtRespuesta.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        JScrollPane scrollRespuesta = new JScrollPane(txtRespuesta);
        scrollRespuesta.setOpaque(false);
        scrollRespuesta.getViewport().setOpaque(false);
        scrollRespuesta.setBorder(BorderFactory.createEmptyBorder());
        scrollRespuesta.setPreferredSize(new Dimension(0, 100));
        fields.add(scrollRespuesta);

        card.add(fields, BorderLayout.CENTER);

        JButton btnResolver = new JButton("💾 ENVIAR RESPUESTA Y ACTUALIZAR BD") {
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
        btnResolver.setFont(new Font("Dialog", Font.BOLD, 12));
        btnResolver.setForeground(Color.WHITE);
        btnResolver.setPreferredSize(new Dimension(0, 38));
        btnResolver.setContentAreaFilled(false);
        btnResolver.setBorderPainted(false);
        btnResolver.setFocusPainted(false);
        btnResolver.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnResolver.addActionListener(e -> guardarResolucionTicket());

        card.add(btnResolver, BorderLayout.SOUTH);
        return card;
    }

    private JPanel crearTablaTicketsSoporte(JTextArea txtDetalle) {
        // Contenedor con corte diagonal (Chamfer Corner) simétrico al panel de la izquierda
        JPanel card = new JPanel(new BorderLayout(0, 15)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight(), cut = 15;
                
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0); 
                path.lineTo(w - cut, 0); 
                path.lineTo(w, cut);
                path.lineTo(w, h); 
                path.lineTo(0, h);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.setStroke(new BasicStroke(1.0f));
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

        String[] columnas = {"ID_TICKET", "JUGADOR", "ASUNTO", "ESTADO"};
        
        modeloTabla = new DefaultTableModel(null, columnas) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };

        tablaTickets = new JTable(modeloTabla);
        tablaTickets.setFont(new Font("Dialog", Font.PLAIN, 12));
        tablaTickets.setForeground(TEXT_WHITE);
        tablaTickets.setBackground(new Color(0, 0, 0, 0)); // Fondo transparente integrado
        tablaTickets.setRowHeight(36); // Espaciado premium para las filas
        tablaTickets.setGridColor(new Color(255, 255, 255, 8));
        tablaTickets.setSelectionBackground(new Color(0, 240, 255, 25)); // Glow Cian de selección
        tablaTickets.setShowVerticalLines(false);

        // Interactividad con caché de la base de datos
        tablaTickets.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int fila = tablaTickets.getSelectedRow();
                if (fila >= 0 && fila < ticketsCache.size()) {
                    ticketSeleccionado = ticketsCache.get(fila);
                    
                    txtDetalle.setFont(new Font("Dialog", Font.PLAIN, 12));
                    txtDetalle.setForeground(TEXT_WHITE);
                    txtDetalle.setText(ticketSeleccionado.mensaje);
                    
                    txtRespuesta.setText(ticketSeleccionado.dictamenAdministrador != null ? 
                                          ticketSeleccionado.dictamenAdministrador : "");
                    
                    selectorEstado.setSelectedItem(ticketSeleccionado.nombreEstado.toUpperCase());
                }
            }
        });

        // Configuración Cyberpunk para el Header (Previene el bloque blanco plano)
        JTableHeader header = tablaTickets.getTableHeader();
        header.setFont(new Font("Dialog", Font.BOLD, 11));
        header.setOpaque(false);
        header.setBackground(new Color(6, 11, 28)); 
        header.setForeground(CYAN_NEON);
        header.setPreferredSize(new Dimension(0, 32));
        
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBackground(new Color(6, 11, 28)); 
                setForeground(CYAN_NEON);
                setFont(new Font("Dialog", Font.BOLD, 11));
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, CYAN_NEON)); // Subrayado cian
                return this;
            }
        });

        // Renderizado estilizado con opacidad sutil para las celdas
        tablaTickets.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private final Border bordeVacio = BorderFactory.createEmptyBorder(0, 10, 0, 10);

            @Override
            public Component getTableCellRendererComponent(JTable table, Object val, boolean isSel, boolean hasFocus, int r, int c) {
                Component comp = super.getTableCellRendererComponent(table, val, isSel, hasFocus, r, c);
                setHorizontalAlignment(SwingConstants.CENTER);
                
                if (c == 3) {
                    String est = String.valueOf(val);
                    if ("PENDIENTE".equals(est)) setForeground(PINK_NEON);
                    else if ("EN REVISIÓN".equals(est)) setForeground(new Color(255, 170, 0));
                    else setForeground(new Color(0, 255, 130)); // RESUELTO
                    setFont(new Font("Dialog", Font.BOLD, 11));
                } else {
                    setForeground(isSel ? Color.WHITE : (c == 0 ? CYAN_NEON : TEXT_WHITE));
                }
                
                if (isSel) {
                    setBackground(table.getSelectionBackground());
                } else {
                    setBackground(r % 2 == 0 ? new Color(7, 10, 26, 120) : new Color(3, 5, 14, 40));
                }
                
                setBorder(bordeVacio);
                return comp;
            }
        });

        JScrollPane scroll = new JScrollPane(tablaTickets);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 8)));
        scroll.setBackground(new Color(0, 0, 0, 0));

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    /**
     * Carga de manera asíncrona todos los tickets de soporte desde la base de datos.
     */
    private void cargarDatosDesdeBD() {
        SwingWorker<List<TicketData>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<TicketData> doInBackground() {
                return soporteDAO.obtenerTodasLasSolicitudes();
            }

            @Override
            protected void done() {
                try {
                    ticketsCache = get();
                    modeloTabla.setRowCount(0); // Limpiar datos previos
                    
                    for (TicketData t : ticketsCache) {
                        modeloTabla.addRow(new Object[]{
                            "#TK-" + String.format("%03d", t.idSolicitud),
                            t.username,
                            t.tituloConsulta,
                            t.nombreEstado.toUpperCase()
                        });
                    }
                    
                    // Resetear inputs de edición limpia
                    ticketSeleccionado = null;
                    txtDetalle.setText("Selecciona un ticket de la tabla de la derecha para inspeccionar el problema reportado de manera detallada...");
                    txtDetalle.setFont(new Font("Dialog", Font.ITALIC, 12));
                    txtDetalle.setForeground(TEXT_MUTED);
                    txtRespuesta.setText("");
                    
                } catch (Exception e) {
                    System.err.println("Error al actualizar la UI de soporte: " + e.getMessage());
                }
            }
        };
        worker.execute();
    }

    /**
     * Envía las respuestas añadidas por el administrador hacia la base de datos MySQL de forma asíncrona.
     */
    private void guardarResolucionTicket() {
        if (ticketSeleccionado == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un ticket de la bandeja entrante antes de responder.", "CENTRAL CORE", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String dictamen = txtRespuesta.getText().trim();
        if (dictamen.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El dictamen o contestación del administrador no puede estar vacío.", "CENTRAL CORE", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int indexSeleccionado = selectorEstado.getSelectedIndex();
        int idEstadoDestino = indexSeleccionado + 1; // 1 = PENDIENTE, 2 = EN REVISIÓN, 3 = RESUELTO

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return soporteDAO.actualizarDictamenYEstado(ticketSeleccionado.idSolicitud, idEstadoDestino, dictamen);
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(PanelAyuda.this, "Ticket actualizado en el núcleo. Notificación enviada al buzón del usuario.");
                        cargarDatosDesdeBD(); // Recargar la tabla y refrescar la caché
                    } else {
                        JOptionPane.showMessageDialog(PanelAyuda.this, "Error de sincronización con la base de datos.", "DATABASE ERROR", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        worker.execute();
    }

    private JLabel crearLabelInput(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font("Dialog", Font.BOLD, 10));
        l.setForeground(TEXT_MUTED);
        l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
        return l;
    }

    private JComboBox<String> crearSelector(String[] items) {
        JComboBox<String> c = new JComboBox<>(items);
        c.setFont(new Font("Dialog", Font.PLAIN, 12));
        c.setForeground(TEXT_WHITE);
        c.setBackground(INPUT_BG);
        c.setMaximumSize(new Dimension(1920, 36));
        c.setPreferredSize(new Dimension(0, 36));
        c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override protected JButton createArrowButton() {
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