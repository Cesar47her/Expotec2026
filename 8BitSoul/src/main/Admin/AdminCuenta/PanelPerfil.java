package main.Admin.AdminCuenta;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.List;

public class PanelPerfil extends JPanel {

    private static final Color CARD_BG = new Color(3, 5, 16, 200);   // Azul profundo transparente
    private static final Color INPUT_BG = new Color(7, 10, 26);       // Fondo de inputs oscuro
    private static final Color CYAN_NEON = new Color(0, 243, 255);    // Celeste Neón Cyberpunk
    private static final Color PINK_NEON = new Color(255, 0, 127);    // Rosado/Fucsia Neón
    private static final Color TEXT_WHITE = new Color(240, 244, 255); // Blanco brillante para texto
    private static final Color TEXT_MUTED = new Color(100, 115, 148); // Texto secundario/deshabilitado

    private JTextField txtUsername;
    private JTextField txtCorreo;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtPuntos;
    private JComboBox<PerfilDAO.ObjetoRol> cmbRol;
    private JTextField txtIdUsuario;
    private JToggleButton btnSwitchEstado;

    private PerfilDAO perfilDAO;

    public PanelPerfil() {
        perfilDAO = new PerfilDAO();
        setOpaque(false);
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. Header Superior de la Consola
        gbc.gridy = 0;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(0, 0, 20, 0);
        add(crearHeaderTop(), gbc);

        // 2. Tarjeta del Formulario Estilo Cyberpunk
        gbc.gridy = 1;
        gbc.weighty = 1.0;
        gbc.insets = new Insets(0, 0, 0, 0);
        add(crearFormularioCyberpunk(), gbc);

        setFocusable(true);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);

        JLabel title = new JLabel("MÓDULO DE INSERCIÓN Y EDICIÓN (CRUD)");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);

        JLabel subtitle = new JLabel("Consola operativa para la alteración de entidades, asignación de llaves foráneas y estados de cuentas.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);

        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    @SuppressWarnings("unchecked")
    private JPanel crearFormularioCyberpunk() {
        JPanel formCard = new JPanel(new BorderLayout(0, 25)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight(), cut = 20;
                Path2D path = new Path2D.Double();
                path.moveTo(cut, 0);
                path.lineTo(w - cut, 0);
                path.lineTo(w, cut);
                path.lineTo(w, h - cut);
                path.lineTo(w - cut, h);
                path.lineTo(cut, h);
                path.lineTo(0, h - cut);
                path.lineTo(0, cut);
                path.closePath();

                g2d.setColor(CARD_BG);
                g2d.fill(path);
                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        formCard.setOpaque(false);
        formCard.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Malla Grid Layout de 4 Filas x 2 Columnas para los Campos Integrados
        JPanel fieldsGrid = new JPanel(new GridLayout(4, 2, 35, 20));
        fieldsGrid.setOpaque(false);

        JPanel pUser = crearCampoTexto("COL: USERNAME / ALIAS", "COL: USERNAME / ALIAS", false);
        txtUsername = (JTextField) pUser.getComponent(1);
        fieldsGrid.add(pUser);

        JPanel pCorr = crearCampoTexto("COL: CORREO ELECTRÓNICO", "usuario@bitsoul.com", false);
        txtCorreo = (JTextField) pCorr.getComponent(1);
        fieldsGrid.add(pCorr);

        JPanel pPass = crearCampoContraseña("COL: CONTRASENA (PASSWORD)", "********");
        txtPassword = (JPasswordField) pPass.getComponent(1);
        fieldsGrid.add(pPass);

        JPanel pConf = crearCampoContraseña("CONFIRMAR PASSWORD", "********");
        txtConfirmPassword = (JPasswordField) pConf.getComponent(1);
        fieldsGrid.add(pConf);

        JPanel pPunt = crearCampoTexto("COMPONENTE: PUNTOS DISPONIBLES (INFO)", "0", true);
        txtPuntos = (JTextField) pPunt.getComponent(1);
        fieldsGrid.add(pPunt);

        // Llenado dinámico desde la tabla ROL de MySQL
        List<PerfilDAO.ObjetoRol> roles = perfilDAO.obtenerRolesDisponibles();
        JPanel pSel = crearCampoSelector("FK: ID_ROL (NIVEL DE ACCESO)", roles.toArray(new PerfilDAO.ObjetoRol[0]));
        cmbRol = (JComboBox<PerfilDAO.ObjetoRol>) pSel.getComponent(1);
        fieldsGrid.add(pSel);

        JPanel pId = crearCampoTexto("PK: ID_USUARIO (LLAVE PRIMARIA)", "AUTO_INCREMENT", true);
        txtIdUsuario = (JTextField) pId.getComponent(1);
        fieldsGrid.add(pId);

        JPanel pSw = crearCampoSwitch("COMPONENTE: ESTADO_CONEXION");
        btnSwitchEstado = (JToggleButton) pSw.getComponent(1);
        fieldsGrid.add(pSw);

        formCard.add(fieldsGrid, BorderLayout.CENTER);

        // Barra inferior de botones operacionales
        JPanel actionsBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        actionsBar.setOpaque(false);

        JButton btnCancelar = crearBotonFormulario("CANCELAR OPERACIÓN", PINK_NEON);
        btnCancelar.addActionListener(e -> limpiarFormularioOperativo());
        actionsBar.add(btnCancelar);

        JButton btnGuardar = crearBotonFormulario("EJECUTAR SQL (SAVE)", CYAN_NEON);
        btnGuardar.addActionListener(e -> {
            String uName = txtUsername.getText().trim();
            String mail = txtCorreo.getText().trim();
            String p1 = String.valueOf(txtPassword.getPassword()).trim();
            String p2 = String.valueOf(txtConfirmPassword.getPassword()).trim();

            // CORREGIDO: Validación estricta para evitar inyecciones de los textos por defecto (Placeholders)
            if (uName.isEmpty() || uName.equals("COL: USERNAME / ALIAS")
                    || mail.isEmpty() || mail.equals("usuario@bitsoul.com")
                    || p1.isEmpty() || p1.equals("********")) {
                JOptionPane.showMessageDialog(this, "Por favor complete todas las credenciales básicas del usuario.", "DATOS INCOMPLETOS", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!p1.equals(p2)) {
                JOptionPane.showMessageDialog(this, "Las contraseñas de verificación no coinciden.", "INTEGRIDAD ERROR", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Extraer el objeto mapeado del combo box relacional
            PerfilDAO.ObjetoRol rolSeleccionado = (PerfilDAO.ObjetoRol) cmbRol.getSelectedItem();
            int idRol = (rolSeleccionado != null) ? rolSeleccionado.idRol : 2; // Por defecto 2 (Usuario)

            // Envía la carga hacia tu tabla USUARIO relacional de forma segura
            if (perfilDAO.insertarUsuarioNuevo(uName, mail, p1, idRol)) {
                JOptionPane.showMessageDialog(this, "¡Sentencia INSERT ejecutada con éxito en la tabla USUARIO!", "SQL TRANSACTION SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                limpiarFormularioOperativo();
            } else {
                JOptionPane.showMessageDialog(this, "Error de consistencia SQL. Revisa la consola para más detalles.", "DB TRANSACTION FAILED", JOptionPane.ERROR_MESSAGE);
            }
        });
        actionsBar.add(btnGuardar);

        formCard.add(actionsBar, BorderLayout.SOUTH);
        return formCard;
    }

    private void limpiarFormularioOperativo() {
        txtUsername.setText("COL: USERNAME / ALIAS");
        txtUsername.setForeground(TEXT_MUTED);
        txtCorreo.setText("usuario@bitsoul.com");
        txtCorreo.setForeground(TEXT_MUTED);
        txtPassword.setText("********");
        txtConfirmPassword.setText("********");
        txtPuntos.setText("0");
        if (cmbRol.getItemCount() > 0) {
            cmbRol.setSelectedIndex(0);
        }
        btnSwitchEstado.setSelected(true);
    }

    private JPanel crearCampoTexto(String titulo, String valorDefecto, boolean deshabilitado) {
        JPanel container = new JPanel(new BorderLayout(0, 6));
        container.setOpaque(false);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Dialog", Font.BOLD, 11));
        label.setForeground(CYAN_NEON);
        container.add(label, BorderLayout.NORTH);

        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(deshabilitado ? new Color(15, 20, 38) : INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 20));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        field.setText(valorDefecto);
        field.setFont(new Font("Dialog", Font.PLAIN, 13));
        field.setForeground(deshabilitado ? TEXT_MUTED : (valorDefecto.equals(field.getText()) && !valorDefecto.equals("0") ? TEXT_MUTED : TEXT_WHITE));
        field.setCaretColor(PINK_NEON);
        field.setEditable(!deshabilitado);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8, 12, 8, 12));
        field.setPreferredSize(new Dimension(0, 38));

        if (!deshabilitado) {
            field.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (field.getText().equals(valorDefecto)) {
                        field.setText("");
                        field.setForeground(TEXT_WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (field.getText().isEmpty()) {
                        field.setText(valorDefecto);
                        field.setForeground(TEXT_MUTED);
                    }
                }
            });
        }

        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private JPanel crearCampoContraseña(String titulo, String valorDefecto) {
        JPanel container = new JPanel(new BorderLayout(0, 6));
        container.setOpaque(false);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Dialog", Font.BOLD, 11));
        label.setForeground(CYAN_NEON);
        container.add(label, BorderLayout.NORTH);

        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 20));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();

                super.paintComponent(g);
            }
        };
        field.setText(valorDefecto);
        field.setFont(new Font("Dialog", Font.PLAIN, 13));
        field.setForeground(TEXT_MUTED);
        field.setCaretColor(PINK_NEON);
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(8, 12, 8, 12));
        field.setPreferredSize(new Dimension(0, 38));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(valorDefecto)) {
                    field.setText("");
                    field.setForeground(TEXT_WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).isEmpty()) {
                    field.setText(valorDefecto);
                    field.setForeground(TEXT_MUTED);
                }
            }
        });

        container.add(field, BorderLayout.CENTER);
        return container;
    }

    private JPanel crearCampoSelector(String titulo, PerfilDAO.ObjetoRol[] opciones) {
        JPanel container = new JPanel(new BorderLayout(0, 6));
        container.setOpaque(false);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Dialog", Font.BOLD, 11));
        label.setForeground(CYAN_NEON);
        container.add(label, BorderLayout.NORTH);

        JComboBox<PerfilDAO.ObjetoRol> combo = new JComboBox<>(opciones);
        combo.setFont(new Font("Dialog", Font.PLAIN, 12));
        combo.setForeground(TEXT_WHITE);
        combo.setBackground(INPUT_BG);
        combo.setPreferredSize(new Dimension(0, 38));
        combo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton btn = super.createArrowButton();
                btn.setBackground(INPUT_BG);
                btn.setBorder(BorderFactory.createEmptyBorder());
                return btn;
            }
        });
        combo.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 20)));

        container.add(combo, BorderLayout.CENTER);
        return container;
    }

    private JPanel crearCampoSwitch(String titulo) {
        JPanel container = new JPanel(new BorderLayout(0, 6));
        container.setOpaque(false);

        JLabel label = new JLabel(titulo);
        label.setFont(new Font("Dialog", Font.BOLD, 11));
        label.setForeground(CYAN_NEON);
        container.add(label, BorderLayout.NORTH);

        JToggleButton toggle = new JToggleButton("ACTIVO") {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight();
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, w, h);
                g2d.drawRect(0, 0, w - 1, h - 1);

                int swW = 50, swH = 22;
                int swX = 15;
                int swY = (h - swH) / 2;

                g2d.setColor(new Color(10, 15, 35));
                g2d.fillRoundRect(swX, swY, swW, swH, 10, 10);
                g2d.setColor(new Color(255, 255, 255, 15));
                g2d.drawRoundRect(swX, swY, swW, swH, 10, 10);

                if (isSelected()) {
                    g2d.setColor(new Color(0, 255, 130));
                    g2d.fillOval(swX + swW - swH + 2, swY + 2, swH - 4, swH - 4);
                    setText("ESTADO: EN LÍNEA (1)");
                    setForeground(new Color(0, 255, 130));
                } else {
                    g2d.setColor(PINK_NEON);
                    g2d.fillOval(swX + 2, swY + 2, swH - 4, swH - 4);
                    setText("ESTADO: DESCONECTADO (0)");
                    setForeground(TEXT_MUTED);
                }
                g2d.dispose();
            }
        };
        toggle.setSelected(true);
        toggle.setFont(new Font("Dialog", Font.BOLD, 11));
        toggle.setOpaque(false);
        toggle.setContentAreaFilled(false);
        toggle.setBorderPainted(false);
        toggle.setFocusPainted(false);
        toggle.setHorizontalAlignment(SwingConstants.LEFT);
        toggle.setBorder(BorderFactory.createEmptyBorder(0, 80, 0, 0));
        toggle.setPreferredSize(new Dimension(0, 38));
        toggle.setCursor(new Cursor(Cursor.HAND_CURSOR));

        container.add(toggle, BorderLayout.CENTER);
        return container;
    }

    private JButton crearBotonFormulario(String texto, Color colorNeon) {
        JButton btn = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth(), h = getHeight(), cut = 8;
                Path2D path = new Path2D.Double();
                path.moveTo(0, 0);
                path.lineTo(w - cut, 0);
                path.lineTo(w, cut);
                path.lineTo(w, h);
                path.lineTo(cut, h);
                path.lineTo(0, h - cut);
                path.closePath();

                if (getModel().isPressed()) {
                    g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 40));
                } // Corrección estética menor: Se eliminó redundancia de chequeo de GetModel
                else if (getModel().isRollover()) {
                    g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), 15));
                } else {
                    g2d.setColor(new Color(5, 8, 22, 220));
                }

                g2d.fill(path);
                g2d.setColor(colorNeon);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(path);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Dialog", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setPreferredSize(new Dimension(180, 38));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
