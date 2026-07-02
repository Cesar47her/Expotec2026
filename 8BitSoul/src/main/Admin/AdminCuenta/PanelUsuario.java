package main.Admin.AdminCuenta;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import main.Util.EstiloDiseno; // IMPORTANTE: Enlace a tu paleta centralizada global

public class PanelUsuario extends JPanel {

    // CORREGIDO: Consumo directo y estandarizado desde tu clase utilitaria
    private static final Color CARD_BG    = EstiloDiseno.CARD_BG;
    private static final Color INPUT_BG   = EstiloDiseno.INPUT_BG;
    private static final Color CYAN_NEON  = EstiloDiseno.CYAN_NEON;
    private static final Color PINK_NEON  = EstiloDiseno.PINK_NEON;
    private static final Color TEXT_WHITE = EstiloDiseno.TEXT_WHITE;
    private static final Color TEXT_MUTED = EstiloDiseno.TEXT_MUTED;
    
    private JTextField txtBusqueda;
    private JLabel lblAlias, lblUid, lblEstadoValor;
    private JLabel lblRangoValor, lblNivelValor, lblBilleteraValor;

    private JTextField txtEmail;
    private JPasswordField pass;
    private JTextField txtFecha;
    private JTextField txtIp; 

    private UsuarioDAO usuarioDAO;
    private int idUsuarioActualcargado = -1;

    public PanelUsuario() {
        usuarioDAO = new UsuarioDAO();
        setOpaque(false);
        setLayout(new BorderLayout());

        JPanel contenidoReal = new JPanel(new GridBagLayout());
        contenidoReal.setOpaque(false);
        contenidoReal.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;

        // 1. ENCABEZADO
        gbc.gridy = 0; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 15, 0);
        contenidoReal.add(crearHeaderTop(), gbc);

        // 2. MÓDULO BÚSQUEDA
        gbc.gridy = 1; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 20, 0);
        contenidoReal.add(crearModuloBusqueda(), gbc);

        // 3. GRID CENTRAL
        gbc.gridy = 2; gbc.weighty = 0.0; gbc.insets = new Insets(0, 0, 0, 0);
        contenidoReal.add(crearGridCentral(), gbc);
        
        // 4. RESET MUELLE
        gbc.gridy = 3; gbc.weighty = 1.0;
        contenidoReal.add(Box.createVerticalGlue(), gbc);

        JScrollPane scrollPane = new JScrollPane(contenidoReal);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        add(scrollPane, BorderLayout.CENTER);
        setFocusable(true);
    }

    private JPanel crearHeaderTop() {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);

        JPanel left = new JPanel(new GridLayout(2, 1, 0, 2));
        left.setOpaque(false);
        
        JLabel title = new JLabel("SISTEMA CENTRAL DE AUDITORÍA Y CONTROL DE USUARIOS");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setForeground(TEXT_WHITE);
        
        JLabel subtitle = new JLabel("Consola de control de accesos. Busca perfiles en la red local, altera credenciales y ejecuta suspensiones de nivel Root.");
        subtitle.setFont(new Font("Dialog", Font.PLAIN, 12));
        subtitle.setForeground(TEXT_MUTED);
        
        left.add(title);
        left.add(subtitle);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private JPanel crearModuloBusqueda() {
        JPanel card = new JPanel(new BorderLayout(15, 0)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(CARD_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(CYAN_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        JLabel icon = new JLabel("🔍 INSTANCIA DE BÚSQUEDA (SELECT):");
        icon.setFont(new Font("Dialog", Font.BOLD, 11));
        icon.setForeground(CYAN_NEON);
        card.add(icon, BorderLayout.WEST);

        txtBusqueda = crearCampoTexto("Introduce UID, Alias o Email...");
        txtBusqueda.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent evt) {
                if (txtBusqueda.getText().equals("Introduce UID, Alias o Email...")) {
                    txtBusqueda.setText("");
                    txtBusqueda.setForeground(TEXT_WHITE);
                }
            }
            @Override
            public void focusLost(FocusEvent evt) {
                if (txtBusqueda.getText().isEmpty()) {
                    txtBusqueda.setText("Introduce UID, Alias o Email...");
                    txtBusqueda.setForeground(TEXT_MUTED);
                }
            }
        });
        card.add(txtBusqueda, BorderLayout.CENTER);

        JButton btnBuscar = new JButton("LOCALIZAR REGISTRO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(0, 240, 255, 30) : INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(CYAN_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnBuscar.setFont(new Font("Dialog", Font.BOLD, 11));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setPreferredSize(new Dimension(160, 0));
        btnBuscar.setContentAreaFilled(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnBuscar.addActionListener(e -> {
            String query = txtBusqueda.getText().trim();
            if(!query.isEmpty() && !query.equals("Introduce UID, Alias o Email...")) {
                
                PerfilCompleto usuario = usuarioDAO.buscarUsuarioReal(query);
                
                if (usuario != null) {
                    idUsuarioActualcargado = usuario.idUsuario;
                    
                    lblAlias.setText(usuario.username.toUpperCase());
                    lblUid.setText("UID: #BSL-" + String.format("%04d", usuario.idUsuario) + "-X");
                    lblRangoValor.setText(usuario.nombreRol.toUpperCase());
                    lblNivelValor.setText("Nivel " + usuario.nivelCuenta);
                    lblBilleteraValor.setText(String.format("%,d", usuario.cantidadMonedas) + " B$");
                    
                    lblEstadoValor.setText("ACTIVE");
                    lblEstadoValor.setForeground(EstiloDiseno.GREEN_NEON); // Corregido para usar el color reactivo de éxito
                    
                    txtEmail.setText(usuario.correo);
                    pass.setText(usuario.contrasena);
                    txtFecha.setText(usuario.fechaRegistro);
                    txtIp.setText("10.0.4.125 // PROXY");
                    
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró ningún registro.", "CORE ERROR", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Por favor, digite un parámetro válido.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        card.add(btnBuscar, BorderLayout.EAST);
        return card;
    }

    private JPanel crearGridCentral() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.45;
        gbc.insets = new Insets(0, 0, 0, 15);
        panel.add(crearTarjetaAvatar(), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.55;
        gbc.insets = new Insets(0, 15, 0, 0);
        panel.add(crearTarjetaFormulario(), gbc);

        return panel;
    }

    private JPanel crearTarjetaAvatar() {
        JPanel card = new JPanel(new GridBagLayout()) {
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
                g2d.setColor(PINK_NEON);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 20, 20, 20));
        card.setPreferredSize(new Dimension(320, 440));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.NONE;

        JPanel avatarFrame = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(2f));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
            }
        };
        avatarFrame.setOpaque(false);
        avatarFrame.setPreferredSize(new Dimension(110, 110));
        avatarFrame.setLayout(new BorderLayout());
        
        JLabel lblIcon = new JLabel("👤", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Dialog", Font.PLAIN, 52));
        lblIcon.setForeground(CYAN_NEON);
        avatarFrame.add(lblIcon, BorderLayout.CENTER);

        gbc.gridy = 0; gbc.weighty = 0.1;
        card.add(avatarFrame, gbc);

        lblAlias = new JLabel("SISTEMA_IDLE", SwingConstants.CENTER);
        lblAlias.setFont(new Font("Dialog", Font.BOLD, 18));
        lblAlias.setForeground(TEXT_WHITE);
        gbc.gridy = 1; gbc.weighty = 0.02; gbc.insets = new Insets(10, 0, 2, 0);
        card.add(lblAlias, gbc);

        lblUid = new JLabel("UID: #BSL-0000-X", SwingConstants.CENTER);
        lblUid.setFont(new Font("Dialog", Font.PLAIN, 11));
        lblUid.setForeground(CYAN_NEON);
        gbc.gridy = 2; gbc.weighty = 0.02; gbc.insets = new Insets(0, 0, 10, 0);
        card.add(lblUid, gbc);

        JPanel meta = new JPanel(new GridLayout(2, 2, 10, 10));
        meta.setOpaque(false);
        
        JPanel pRango = crearMiniDato("RANGO", "N/A");
        lblRangoValor = (JLabel) pRango.getComponent(1);
        meta.add(pRango);
        
        JPanel pNivel = crearMiniDato("NIVEL", "Nivel 0");
        lblNivelValor = (JLabel) pNivel.getComponent(1);
        meta.add(pNivel);
        
        JPanel pBilletera = crearMiniDato("BILLETERA", "0 B$");
        lblBilleteraValor = (JLabel) pBilletera.getComponent(1);
        meta.add(pBilletera);
        
        JPanel pEstado = new JPanel(new GridLayout(2, 1, 0, 2));
        pEstado.setOpaque(false);
        JLabel lblEst = new JLabel("ESTADO EN RED");
        lblEst.setFont(new Font("Dialog", Font.BOLD, 9));
        lblEst.setForeground(TEXT_MUTED);
        lblEstadoValor = new JLabel("STANDBY");
        lblEstadoValor.setFont(new Font("Dialog", Font.BOLD, 12));
        lblEstadoValor.setForeground(TEXT_MUTED);
        pEstado.add(lblEst); pEstado.add(lblEstadoValor);
        meta.add(pEstado);

        gbc.gridy = 3; gbc.weighty = 0.2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 10, 15, 10);
        card.add(meta, gbc);

        JButton btnBanear = new JButton("⚠️ TERMINAR ACCESO Y ELIMINAR REGISTRO") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(255, 0, 127, 25) : new Color(24, 6, 16));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(PINK_NEON);
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        btnBanear.setFont(new Font("Dialog", Font.BOLD, 11));
        btnBanear.setForeground(Color.WHITE);
        btnBanear.setPreferredSize(new Dimension(0, 36));
        btnBanear.setContentAreaFilled(false);
        btnBanear.setBorderPainted(false);
        btnBanear.setFocusPainted(false);
        btnBanear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnBanear.addActionListener(e -> {
            if (idUsuarioActualcargado == -1) {
                JOptionPane.showMessageDialog(this, "Primero debe localizar un usuario de la base de datos.", "SISTEMA", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, 
                "¿Confirmar purga del registro? El usuario y sus perfiles vinculados serán borrados permanentemente de MySQL.", 
                "🔥 MEDIDA DISCIPLINARIA CRÍTICA", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (usuarioDAO.eliminarUsuario(idUsuarioActualcargado)) {
                    lblEstadoValor.setText("PURGED");
                    lblEstadoValor.setForeground(PINK_NEON);
                    limpiarFormulario();
                    JOptionPane.showMessageDialog(this, "Sentencia DELETE despachada con éxito en cascada.", "SECURITY LOG", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });

        gbc.gridy = 4; gbc.weighty = 0.05;
        gbc.insets = new Insets(0, 10, 0, 10);
        card.add(btnBanear, gbc);

        return card;
    }

    private JPanel crearTarjetaFormulario() {
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
                g2d.setColor(CYAN_NEON);
                g2d.setStroke(new BasicStroke(1.2f));
                g2d.draw(path);
                g2d.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        card.setPreferredSize(new Dimension(380, 440));

        JLabel title = new JLabel("📝 CREDENCIALES Y CONFIGURACIÓN DE CUENTA");
        title.setFont(new Font("Dialog", Font.BOLD, 13));
        title.setForeground(CYAN_NEON);
        card.add(title, BorderLayout.NORTH);

        JPanel fields = new JPanel();
        fields.setOpaque(false);
        fields.setLayout(new BoxLayout(fields, BoxLayout.Y_AXIS));

        fields.add(crearLabelInput("DIRECCIÓN DE CORREO ELECTRÓNICO (EMAIL)"));
        txtEmail = crearCampoTexto("");
        fields.add(txtEmail);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("HASH DE CONTRASEÑA DE SEGURIDAD (PASSWORD)"));
        pass = new JPasswordField("") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(INPUT_BG);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(hasFocus() ? PINK_NEON : new Color(255, 255, 255, 15));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        pass.setFont(new Font("Dialog", Font.PLAIN, 12));
        pass.setForeground(TEXT_WHITE);
        pass.setCaretColor(PINK_NEON);
        pass.setOpaque(false);
        pass.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        pass.setMaximumSize(new Dimension(1920, 36));
        pass.setPreferredSize(new Dimension(0, 36));
        fields.add(pass);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("FECHA DE REGISTRO EN EL SISTEMA"));
        txtFecha = crearCampoTexto("");
        txtFecha.setEditable(false); 
        fields.add(txtFecha);
        fields.add(Box.createRigidArea(new Dimension(0, 12)));

        fields.add(crearLabelInput("DIRECCIÓN IP DE ÚLTIMA CONEXIÓN"));
        txtIp = crearCampoTexto("127.0.0.1");
        fields.add(txtIp);

        card.add(fields, BorderLayout.CENTER);

        JButton btnGuardar = new JButton("💾 GUARDAR MODIFICACIONES DE PERFIL") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getModel().isRollover() ? new Color(0, 240, 255, 15) : new Color(5, 8, 22));
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
            if (idUsuarioActualcargado == -1) {
                JOptionPane.showMessageDialog(this, "No hay ningún registro cargado para modificar.", "ERROR", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String nuevoEmail = txtEmail.getText().trim();
            String nuevaContrasena = String.valueOf(pass.getPassword()).trim();
            
            if(nuevoEmail.isEmpty() || nuevaContrasena.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Los campos no pueden guardarse vacíos.", "SISTEMA", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (usuarioDAO.actualizarCredenciales(idUsuarioActualcargado, nuevoEmail, nuevaContrasena)) {
                JOptionPane.showMessageDialog(this, "Credenciales en red actualizadas correctamente en MySQL.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Error al escribir en la base de datos.", "DATABASE ERROR", JOptionPane.ERROR_MESSAGE);
            }
        });

        card.add(btnGuardar, BorderLayout.SOUTH);
        return card;
    }

    private void limpiarFormulario() {
        idUsuarioActualcargado = -1;
        txtBusqueda.setText("Introduce UID, Alias o Email...");
        txtBusqueda.setForeground(TEXT_MUTED);
        lblAlias.setText("SISTEMA_IDLE");
        lblUid.setText("UID: #BSL-0000-X");
        lblRangoValor.setText("N/A");
        lblNivelValor.setText("Nivel 0");
        lblBilleteraValor.setText("0 B$");
        txtEmail.setText("");
        pass.setText("");
        txtFecha.setText("");
        txtIp.setText("127.0.0.1");
    }

    private JPanel crearMiniDato(String label, String valor) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 2));
        p.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Dialog", Font.BOLD, 9));
        lbl.setForeground(TEXT_MUTED);
        
        JLabel val = new JLabel(valor);
        val.setFont(new Font("Dialog", Font.BOLD, 12));
        val.setForeground(TEXT_WHITE);
        
        p.add(lbl); p.add(val);
        return p;
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
        return f;
    }
}