package main.Admin.RevisionComentarios;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

import main.Conexion.ConexionSQL;
import main.Admin.PanelAdmin;

public class PanelRevisionComentarios extends JFrame {

    public static final Color CYAN_NEON     = new Color(0, 240, 255);
    public static final Color MAGENTA_NEON  = new Color(242, 5, 203);
    public static final Color AMARILLO_WARN = new Color(255, 170, 0);
    public static final Color VERDE_OK      = new Color(0, 230, 115);
    public static final Color TEXTO_VALOR   = new Color(220, 240, 255);
    public static final Color TEXTO_MUTED   = new Color(120, 130, 145);
    public static final Color FONDO_DARK    = new Color(2, 6, 14);
    public static final Color CAPA_NEGRA_TRANSPARENTE = new Color(6, 11, 22, 225);

    private FondoHUDPanel panelFondo;
    private JPanel barraSuperior;
    private int mouseX, mouseY;

    private JLabel lblTituloMain, lblSubtituloMain;
    private JButton btnMinimizar, btnMaximizar, btnCerrar;

    private JLabel lblResultadosContador;
    private JPanel panelListaComentarios;
    private JScrollPane scrollComentarios;
    private JButton btnPagAnt, btnPagSig;
    private JLabel lblPaginacionTxt;

    private JLabel lblDetalleUsuario, lblDetalleNivel, lblDetalleMiembroDesde;
    private JTextArea txtCompletoComentario;
    private JTextArea txtResumenIA;
    
    private JLabel lblMisionVal, lblLeccionVal, lblModuloVal, lblFechaVal;
    private JLabel lblIaLenguajeVal, lblIaContenidoVal, lblIaSpamVal, lblIaDiscursoVal, lblIaAcosoVal;
    
    private JLabel lblConfianzaIA;
    private JProgressBar barraConfianzaIA;

    private BotonNeonHUD btnAprobar, btnRechazar, btnReportar, btnVolver;

    private List<ComentarioDTO> listaComentariosMemoria = new ArrayList<>();
    private ComentarioDTO comentarioSeleccionado = null;
    private int paginaActual = 1;
    private final int FILAS_POR_PAGINA = 7; // Incrementado ligeramente al haber más espacio vertical

    public PanelRevisionComentarios() {
        configurarVentana();
        inicializarEstructura();
        configurarAtajosTeclado();
        main.Util.ContenedorVentana.pf_configurarVentana(this);
        cargarComentariosDesdeBD();
        construirListaUI();
    }

    private void configurarVentana() {
        setUndecorated(true);
        setSize(1200, 675); 
        setMinimumSize(new Dimension(950, 600));
        setLocationRelativeTo(null);

        panelFondo = new FondoHUDPanel("src/imagenes/FondoCrud.png");
        panelFondo.setLayout(new BorderLayout());
        setContentPane(panelFondo);
    }

    private void inicializarEstructura() {
        // --- BARRA SUPERIOR ---
        barraSuperior = new JPanel(new BorderLayout());
        barraSuperior.setOpaque(false);
        barraSuperior.setPreferredSize(new Dimension(getWidth(), 65));
        barraSuperior.setBorder(new EmptyBorder(12, 25, 5, 25));

        JPanel panelTextosTitulo = new JPanel(new GridLayout(2, 1));
        panelTextosTitulo.setOpaque(false);
        lblTituloMain = new JLabel("REVISIÓN DE COMENTARIOS");
        lblTituloMain.setFont(new Font("Monospaced", Font.BOLD, 24));
        lblTituloMain.setForeground(Color.WHITE);
        lblSubtituloMain = new JLabel("Sistema de Moderación y Análisis Automatizado por IA // MASTER LOG");
        lblSubtituloMain.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblSubtituloMain.setForeground(CYAN_NEON);
        panelTextosTitulo.add(lblTituloMain);
        panelTextosTitulo.add(lblSubtituloMain);

        JPanel panelControlesVentana = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        panelControlesVentana.setOpaque(false);
        btnMinimizar = crearBotonControl("--");
        btnMinimizar.addActionListener(e -> setExtendedState(JFrame.ICONIFIED));
        btnMaximizar = crearBotonControl("⬜");
        btnMaximizar.addActionListener(e -> {
            if(getExtendedState() == JFrame.MAXIMIZED_BOTH) setExtendedState(JFrame.NORMAL);
            else setExtendedState(JFrame.MAXIMIZED_BOTH);
        });
        btnCerrar = crearBotonControl("X");
        btnCerrar.addActionListener(e -> System.exit(0));
        panelControlesVentana.add(btnMinimizar);
        panelControlesVentana.add(btnMaximizar);
        panelControlesVentana.add(btnCerrar);

        barraSuperior.add(panelTextosTitulo, BorderLayout.WEST);
        barraSuperior.add(panelControlesVentana, BorderLayout.EAST);
        panelFondo.add(barraSuperior, BorderLayout.NORTH);

        barraSuperior.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { mouseX = e.getX(); mouseY = e.getY(); }
        });
        barraSuperior.addMouseMotionListener(new MouseAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if(getExtendedState() != JFrame.MAXIMIZED_BOTH) {
                    setLocation(e.getXOnScreen() - mouseX, e.getYOnScreen() - mouseY);
                }
            }
        });

        // --- PANEL DE CONTROL HUD CENTRAL CON CONTRASTE OSCURO ---
        JPanel panelGridCentral = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(CAPA_NEGRA_TRANSPARENTE);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(new Color(0, 240, 255, 35));
                g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        panelGridCentral.setOpaque(false);
        panelGridCentral.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 8, 5, 8);

        // SECCIÓN IZQUIERDA: LISTA DE COMENTARIOS (Ocupa ahora el 45% de la pantalla)
        JPanel colCentral = new JPanel(new BorderLayout(0, 12));
        colCentral.setOpaque(false);

        JPanel headerCentral = new JPanel(new BorderLayout());
        headerCentral.setOpaque(false);
        headerCentral.add(crearLabelSeccion("HISTORIAL DE ENTRADAS DEL SISTEMA"), BorderLayout.WEST);
        lblResultadosContador = new JLabel("0 REGISTROS", SwingConstants.RIGHT);
        lblResultadosContador.setFont(new Font("Monospaced", Font.BOLD, 12));
        lblResultadosContador.setForeground(CYAN_NEON);
        headerCentral.add(lblResultadosContador, BorderLayout.EAST);
        colCentral.add(headerCentral, BorderLayout.NORTH);

        panelListaComentarios = new JPanel();
        panelListaComentarios.setLayout(new BoxLayout(panelListaComentarios, BoxLayout.Y_AXIS));
        panelListaComentarios.setOpaque(false);

        scrollComentarios = new JScrollPane(panelListaComentarios);
        scrollComentarios.setOpaque(false);
        scrollComentarios.getViewport().setOpaque(false);
        scrollComentarios.setBorder(null);
        scrollComentarios.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override protected void configureScrollBarColors() {
                this.thumbColor = new Color(0, 240, 255, 65);
                this.trackColor = new Color(0, 0, 0, 0);
            }
        });
        colCentral.add(scrollComentarios, BorderLayout.CENTER);

        JPanel panelPaginador = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        panelPaginador.setOpaque(false);
        btnPagAnt = new JButton("◀"); ConfigurarBotonFlecha(btnPagAnt);
        btnPagSig = new JButton("▶"); ConfigurarBotonFlecha(btnPagSig);
        lblPaginacionTxt = new JLabel("1 / 1", SwingConstants.CENTER);
        lblPaginacionTxt.setFont(new Font("Monospaced", Font.BOLD, 13));
        lblPaginacionTxt.setForeground(TEXTO_VALOR);
        panelPaginador.add(btnPagAnt); panelPaginador.add(lblPaginacionTxt); panelPaginador.add(btnPagSig);
        colCentral.add(panelPaginador, BorderLayout.SOUTH);

        btnPagAnt.addActionListener(e -> cambiarPagina(-1));
        btnPagSig.addActionListener(e -> cambiarPagina(1));

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.45; gbc.weighty = 1.0;
        panelGridCentral.add(colCentral, gbc);

        // SECCIÓN DERECHA: INSPECTOR DE DETALLES IA (Ocupa el 55% restante)
        JPanel colDerecha = new JPanel(new BorderLayout(0, 12));
        colDerecha.setOpaque(false);

        JPanel headerDerecho = new JPanel(new BorderLayout());
        headerDerecho.setOpaque(false);
        headerDerecho.add(crearLabelSeccion("INSPECTOR DE AUDITORÍA DETALLADA"), BorderLayout.NORTH);
        
        JPanel datosMetaUser = new JPanel(new BorderLayout());
        datosMetaUser.setOpaque(false);
        datosMetaUser.setBorder(new EmptyBorder(6, 0, 0, 0));
        lblDetalleUsuario = new JLabel("SELECCIONE UNA ENTRADA");
        lblDetalleUsuario.setFont(new Font("Monospaced", Font.BOLD, 17));
        lblDetalleUsuario.setForeground(CYAN_NEON);
        lblDetalleNivel = new JLabel("NIVEL --  [ -- XP ]");
        lblDetalleNivel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        lblDetalleNivel.setForeground(TEXTO_MUTED);
        lblDetalleMiembroDesde = new JLabel("REGISTRO DE CUENTA: -- / -- / ----", SwingConstants.RIGHT);
        lblDetalleMiembroDesde.setFont(new Font("Monospaced", Font.PLAIN, 11));
        lblDetalleMiembroDesde.setForeground(TEXTO_MUTED);

        JPanel subIzq = new JPanel(new GridLayout(2, 1)); subIzq.setOpaque(false);
        subIzq.add(lblDetalleUsuario); subIzq.add(lblDetalleNivel);
        datosMetaUser.add(subIzq, BorderLayout.WEST);
        datosMetaUser.add(lblDetalleMiembroDesde, BorderLayout.EAST);
        headerDerecho.add(datosMetaUser, BorderLayout.SOUTH);
        colDerecha.add(headerDerecho, BorderLayout.NORTH);

        txtCompletoComentario = new JTextArea();
        txtCompletoComentario.setLineWrap(true);
        txtCompletoComentario.setWrapStyleWord(true);
        txtCompletoComentario.setEditable(false);
        txtCompletoComentario.setOpaque(false);
        txtCompletoComentario.setForeground(TEXTO_VALOR);
        txtCompletoComentario.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtCompletoComentario.setBorder(new CompoundBorder(new LineBorder(new Color(0,240,255,45), 1), new EmptyBorder(12,12,12,12)));
        
        JScrollPane scrollTextoFull = new JScrollPane(txtCompletoComentario);
        scrollTextoFull.setOpaque(false);
        scrollTextoFull.getViewport().setOpaque(false);
        scrollTextoFull.setBorder(null);
        colDerecha.add(scrollTextoFull, BorderLayout.CENTER);

        JPanel splitSubPaneles = new JPanel(new GridLayout(1, 2, 20, 0));
        splitSubPaneles.setOpaque(false);
        splitSubPaneles.setPreferredSize(new Dimension(350, 150));

        JPanel subContexto = new JPanel(new GridLayout(5, 1, 0, 4));
        subContexto.setOpaque(false);
        subContexto.add(crearLabelSubSeccion("CONTEXTO DE LA TRIVIA"));
        subContexto.add(crearFilaDetalleMini("MISIÓN:", lblMisionVal = crearLabelValorMini("--")));
        subContexto.add(crearFilaDetalleMini("LECCIÓN:", lblLeccionVal = crearLabelValorMini("--")));
        subContexto.add(crearFilaDetalleMini("MÓDULO:", lblModuloVal = crearLabelValorMini("--")));
        subContexto.add(crearFilaDetalleMini("FECHA EMISIÓN:", lblFechaVal = crearLabelValorMini("--")));

        JPanel subIA = new JPanel(new GridLayout(7, 1, 0, 2));
        subIA.setOpaque(false);
        subIA.add(crearLabelSubSeccion("ANÁLISIS DE MODERACIÓN IA"));
        subIA.add(crearFilaDetalleMini("Lenguaje Inapropiado", lblIaLenguajeVal = crearLabelIaStatus(false)));
        subIA.add(crearFilaDetalleMini("Contenido Ofensivo", lblIaContenidoVal = crearLabelIaStatus(false)));
        subIA.add(crearFilaDetalleMini("Spam / Publicidad", lblIaSpamVal = crearLabelIaStatus(false)));
        subIA.add(crearFilaDetalleMini("Discurso de Odio", lblIaDiscursoVal = crearLabelIaStatus(false)));
        subIA.add(crearFilaDetalleMini("Acoso / Toxicidad", lblIaAcosoVal = crearLabelIaStatus(false)));
        
        JPanel containerProgreso = new JPanel(new BorderLayout(5,0));
        containerProgreso.setOpaque(false);
        lblConfianzaIA = new JLabel("CONFIANZA: 0%");
        lblConfianzaIA.setFont(new Font("Monospaced", Font.BOLD, 10));
        lblConfianzaIA.setForeground(CYAN_NEON);
        barraConfianzaIA = new JProgressBar(0, 100);
        barraConfianzaIA.setValue(0);
        barraConfianzaIA.setForeground(CYAN_NEON);
        barraConfianzaIA.setBackground(new Color(4, 26, 44));
        barraConfianzaIA.setBorderPainted(false);
        containerProgreso.add(lblConfianzaIA, BorderLayout.WEST);
        containerProgreso.add(barraConfianzaIA, BorderLayout.CENTER);
        subIA.add(containerProgreso);

        splitSubPaneles.add(subContexto);
        splitSubPaneles.add(subIA);

        JPanel panelInferiorDerecho = new JPanel(new BorderLayout(0, 8));
        panelInferiorDerecho.setOpaque(false);
        panelInferiorDerecho.add(splitSubPaneles, BorderLayout.NORTH);

        JPanel panelResumenWrap = new JPanel(new BorderLayout());
        panelResumenWrap.setOpaque(false);
        panelResumenWrap.add(crearLabelSubSeccion("RESUMEN DEL DICTAMEN EMITIDO POR IA"), BorderLayout.NORTH);
        txtResumenIA = new JTextArea("No se ha seleccionado ningún comentario para revisión.");
        txtResumenIA.setLineWrap(true);
        txtResumenIA.setWrapStyleWord(true);
        txtResumenIA.setEditable(false);
        txtResumenIA.setOpaque(false);
        txtResumenIA.setForeground(TEXTO_VALOR);
        txtResumenIA.setFont(new Font("SansSerif", Font.ITALIC, 12));
        txtResumenIA.setBorder(new EmptyBorder(5,5,5,5));
        panelResumenWrap.add(txtResumenIA, BorderLayout.CENTER);
        panelInferiorDerecho.add(panelResumenWrap, BorderLayout.SOUTH);

        colDerecha.add(panelInferiorDerecho, BorderLayout.SOUTH);

        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.55; gbc.weighty = 1.0;
        panelGridCentral.add(colDerecha, gbc);

        // Margen exterior estético
        JPanel panelContenedorConMargen = new JPanel(new BorderLayout());
        panelContenedorConMargen.setOpaque(false);
        panelContenedorConMargen.setBorder(new EmptyBorder(5, 25, 10, 25));
        panelContenedorConMargen.add(panelGridCentral, BorderLayout.CENTER);
        
        panelFondo.add(panelContenedorConMargen, BorderLayout.CENTER);

        // --- BOTONERA INFERIOR (BOTONES GRANDES DE ALTO CONTRASTE) ---
        JPanel barraBotonesAcciones = new JPanel(new BorderLayout());
        barraBotonesAcciones.setOpaque(false);
        barraBotonesAcciones.setBorder(new EmptyBorder(10, 25, 20, 25));

        btnVolver = new BotonNeonHUD("◀ VOLVER (ESC)", TEXTO_MUTED, false, 180, 48, 13);
        btnVolver.addActionListener(e -> regresarMenuPrincipal());
        barraBotonesAcciones.add(btnVolver, BorderLayout.WEST);

        JPanel subAccionesDerecha = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        subAccionesDerecha.setOpaque(false);
        
        btnAprobar = new BotonNeonHUD("✔ APROBAR (F1)", VERDE_OK, true, 180, 48, 13);
        btnRechazar = new BotonNeonHUD("✖ RECHAZAR (F2)", MAGENTA_NEON, true, 180, 48, 13);
        btnReportar = new BotonNeonHUD("🏳 REPORTAR (F3)", AMARILLO_WARN, true, 180, 48, 13);

        btnAprobar.addActionListener(e -> procesarEstadoComentario("APROBADO"));
        btnRechazar.addActionListener(e -> procesarEstadoComentario("RECHAZADO"));
        btnReportar.addActionListener(e -> procesarEstadoComentario("REPORTADO"));

        subAccionesDerecha.add(btnAprobar);
        subAccionesDerecha.add(btnRechazar);
        subAccionesDerecha.add(btnReportar);
        barraBotonesAcciones.add(subAccionesDerecha, BorderLayout.EAST);

        panelFondo.add(barraBotonesAcciones, BorderLayout.SOUTH);
    }

    private void configurarAtajosTeclado() {
        JComponent root = getRootPane();
        registrarAccionTeclado(root, "F1", KeyEvent.VK_F1, () -> btnAprobar.doClick());
        registrarAccionTeclado(root, "F2", KeyEvent.VK_F2, () -> btnRechazar.doClick());
        registrarAccionTeclado(root, "F3", KeyEvent.VK_F3, () -> btnReportar.doClick());
        registrarAccionTeclado(root, "ESCAPE", KeyEvent.VK_ESCAPE, () -> btnVolver.doClick());
        registrarAccionTeclado(root, "LEFT_ARROW", KeyEvent.VK_LEFT, () -> btnPagAnt.doClick());
        registrarAccionTeclado(root, "RIGHT_ARROW", KeyEvent.VK_RIGHT, () -> btnPagSig.doClick());
    }

    private void registrarAccionTeclado(JComponent comp, String identificador, int keyCode, Runnable accion) {
        comp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(keyCode, 0), identificador);
        comp.getActionMap().put(identificador, new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { accion.run(); }
        });
    }

    private void cambiarPagina(int direccion) {
        int proximapagina = paginaActual + direccion;
        if(proximapagina >= 1 && proximapagina <= calcularTotalPaginas()) {
            paginaActual = proximapagina;
            construirListaUI();
        }
    }

    private void regresarMenuPrincipal() {
        PanelAdmin menuAdmin = new PanelAdmin();
        menuAdmin.setVisible(true);
        this.dispose();
    }

    private void cargarComentariosDesdeBD() {
        listaComentariosMemoria.clear();
        String query = "SELECT m.*, u.username, p.nivel_cuenta, p.experiencia, u.fecha_registro " +
                       "FROM MODERACION_COMENTARIO m " +
                       "JOIN USUARIO u ON m.id_usuario = u.id_usuario " +
                       "LEFT JOIN PERFIL p ON u.id_usuario = p.id_usuario " +
                       "ORDER BY m.fecha_comentario DESC";

        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ComentarioDTO c = new ComentarioDTO();
                c.idModeracion = rs.getInt("id_moderacion");
                c.username = rs.getString("username");
                c.nivel = rs.getInt("nivel_cuenta");
                c.experiencia = rs.getInt("experiencia");
                c.fechaRegistro = rs.getTimestamp("fecha_registro");
                c.textoComentario = rs.getString("texto_comentario");
                c.estado = rs.getString("estado_comentario");
                c.mision = rs.getString("mision_contexto");
                c.leccion = rs.getString("leccion_contexto");
                c.modulo = rs.getString("modulo_contexto");
                c.fechaComentario = rs.getTimestamp("fecha_comentario");
                c.iaLenguaje = rs.getBoolean("ia_lenguaje_inapropiado");
                c.iaContenido = rs.getBoolean("ia_contenido_ofensivo");
                c.iaSpam = rs.getBoolean("ia_spam");
                c.iaDiscurso = rs.getBoolean("ia_discurso_odio");
                c.iaAcoso = rs.getBoolean("ia_acoso");
                c.iaConfianza = rs.getInt("ia_confianza");
                c.iaResumen = rs.getString("ia_resumen");
                listaComentariosMemoria.add(c);
            }
        } catch (SQLException ex) {
            System.err.println("[DB READ ERROR]: " + ex.getMessage());
        }
        lblResultadosContador.setText(listaComentariosMemoria.size() + " REGISTROS TOTALES");
    }

    private void construirListaUI() {
        panelListaComentarios.removeAll();
        int totalFilas = listaComentariosMemoria.size();
        int paginasTotales = calcularTotalPaginas();
        
        if(paginaActual > paginasTotales) paginaActual = Math.max(1, paginasTotales);
        lblPaginacionTxt.setText(paginaActual + " / " + Math.max(1, paginasTotales));

        int inicio = (paginaActual - 1) * FILAS_POR_PAGINA;
        int fin = Math.min(inicio + FILAS_POR_PAGINA, totalFilas);

        for (int i = inicio; i < fin; i++) {
            ComentarioDTO dto = listaComentariosMemoria.get(i);
            Color colEst = AMARILLO_WARN;
            if ("APROBADO".equalsIgnoreCase(dto.estado)) colEst = VERDE_OK;
            if ("RECHAZADO".equalsIgnoreCase(dto.estado)) colEst = MAGENTA_NEON;

            String extracto = dto.textoComentario.length() > 50 ? dto.textoComentario.substring(0, 47) + "..." : dto.textoComentario;

            TarjetaComentario tarjeta = new TarjetaComentario(dto.username, "LVL " + dto.nivel, extracto, dto.estado, colEst);
            tarjeta.setFocusable(true);
            
            tarjeta.addMouseListener(new MouseAdapter() {
                @Override public void mousePressed(MouseEvent e) { 
                    tarjeta.requestFocusInWindow();
                    cargarDetalleEnEspejo(dto); 
                }
            });
            panelListaComentarios.add(tarjeta);
            panelListaComentarios.add(Box.createVerticalStrut(6));
        }
        panelListaComentarios.revalidate();
        panelListaComentarios.repaint();
    }

    private void cargarDetalleEnEspejo(ComentarioDTO dto) {
        this.comentarioSeleccionado = dto;
        lblDetalleUsuario.setText(dto.username.toUpperCase());
        lblDetalleNivel.setText("NIVEL " + dto.nivel + "  [ " + String.format("%,d", dto.experiencia) + " XP ]");
        lblDetalleMiembroDesde.setText("REGISTRO DE CUENTA: " + new java.text.SimpleDateFormat("dd/MM/yyyy").format(dto.fechaRegistro));
        txtCompletoComentario.setText(dto.textoComentario);
        
        lblMisionVal.setText(dto.mision);
        lblLeccionVal.setText(dto.leccion);
        lblModuloVal.setText(dto.modulo);
        lblFechaVal.setText(new java.text.SimpleDateFormat("dd/MM/yy HH:mm").format(dto.fechaComentario));

        cambiarStatusIaLabel(lblIaLenguajeVal, dto.iaLenguaje);
        cambiarStatusIaLabel(lblIaContenidoVal, dto.iaContenido);
        cambiarStatusIaLabel(lblIaSpamVal, dto.iaSpam);
        cambiarStatusIaLabel(lblIaDiscursoVal, dto.iaDiscurso);
        cambiarStatusIaLabel(lblIaAcosoVal, dto.iaAcoso);

        lblConfianzaIA.setText("CONFIDENCIA: " + dto.iaConfianza + "%");
        barraConfianzaIA.setValue(dto.iaConfianza);
        txtResumenIA.setText(dto.iaResumen);
    }

    private void procesarEstadoComentario(String nuevoEstado) {
        if (comentarioSeleccionado == null) {
            Toolkit.getDefaultToolkit().beep();
            return;
        }
        String updateQuery = "UPDATE MODERACION_COMENTARIO SET estado_comentario = ? WHERE id_moderacion = ?";
        try (Connection con = ConexionSQL.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(updateQuery)) {
            ps.setString(1, nuevoEstado);
            ps.setInt(2, comentarioSeleccionado.idModeracion);
            ps.executeUpdate();
            
            cargarComentariosDesdeBD();
            construirListaUI();
            limpiarEspejoDetalles();
        } catch (SQLException ex) {
            System.err.println("[DB WRITE ERROR]: " + ex.getMessage());
        }
    }

    private void limpiarEspejoDetalles() {
        lblDetalleUsuario.setText("ESTADO ACTUALIZADO");
        txtCompletoComentario.setText("");
        lblMisionVal.setText("--"); lblLeccionVal.setText("--"); lblModuloVal.setText("--"); lblFechaVal.setText("--");
        txtResumenIA.setText("Seleccione una nueva entrada para auditar.");
        barraConfianzaIA.setValue(0);
        lblConfianzaIA.setText("CONFIDENCIA: 0%");
        comentarioSeleccionado = null;
    }

    private int calcularTotalPaginas() {
        return (int) Math.ceil((double) listaComentariosMemoria.size() / FILAS_POR_PAGINA);
    }

    private void cambiarStatusIaLabel(JLabel lbl, boolean detectado) {
        lbl.setText(detectado ? "DETECTADO" : "LIMPIO");
        lbl.setForeground(detectado ? MAGENTA_NEON : VERDE_OK);
    }

    private JPanel crearFilaDetalleMini(String title, JLabel valLabel) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel titleL = new JLabel(title);
        titleL.setFont(new Font("Monospaced", Font.PLAIN, 11));
        titleL.setForeground(TEXTO_MUTED);
        p.add(titleL, BorderLayout.WEST);
        p.add(valLabel, BorderLayout.CENTER);
        return p;
    }

    private JButton crearBotonControl(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setForeground(TEXTO_MUTED);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { b.setForeground(CYAN_NEON); }
            @Override public void mouseExited(MouseEvent e) { b.setForeground(TEXTO_MUTED); }
        });
        return b;
    }

    private JLabel crearLabelSeccion(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Monospaced", Font.BOLD, 14)); l.setForeground(CYAN_NEON);
        return l;
    }
    private JLabel crearLabelSubSeccion(String t) {
        JLabel l = new JLabel(t); l.setFont(new Font("Monospaced", Font.BOLD, 12)); l.setForeground(CYAN_NEON);
        l.setBorder(new EmptyBorder(0,0,5,0));
        return l;
    }
    private JLabel crearLabelValorMini(String t) {
        JLabel l = new JLabel(t, SwingConstants.RIGHT); l.setFont(new Font("SansSerif", Font.PLAIN, 12)); l.setForeground(TEXTO_VALOR);
        return l;
    }
    private JLabel crearLabelIaStatus(boolean d) {
        JLabel l = new JLabel(d ? "DETECTADO" : "LIMPIO", SwingConstants.RIGHT);
        l.setFont(new Font("Monospaced", Font.BOLD, 11)); l.setForeground(d ? MAGENTA_NEON : VERDE_OK);
        return l;
    }
    private void ConfigurarBotonFlecha(JButton b) {
        b.setFont(new Font("Monospaced", Font.BOLD, 18)); b.setForeground(CYAN_NEON);
        b.setContentAreaFilled(false); b.setBorderPainted(false); b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PanelRevisionComentarios().setVisible(true));
    }
}

class ComentarioDTO {
    int idModeracion;
    String username;
    int nivel;
    int experiencia;
    Timestamp fechaRegistro;
    String textoComentario;
    String estado;
    String mision;
    String leccion;
    String modulo;
    Timestamp fechaComentario;
    boolean iaLenguaje;
    boolean iaContenido;
    boolean iaSpam;
    boolean iaDiscurso;
    boolean iaAcoso;
    int iaConfianza;
    String iaResumen;
}

class FondoHUDPanel extends JPanel {
    private Image imagenFondo;

    public FondoHUDPanel(String rutaImagen) {
        try {
            File fichero = new File(rutaImagen);
            if(fichero.exists()) this.imagenFondo = ImageIO.read(fichero);
        } catch (Exception e) {
            System.out.println("Fondo principal HUD ausente.");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (imagenFondo != null) {
            g2d.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g2d.setColor(PanelRevisionComentarios.FONDO_DARK);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }

        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(new Color(0, 240, 255, 45));
        g2d.drawRect(12, 12, getWidth() - 24, getHeight() - 24);
        
        g2d.dispose();
    }
}

class TarjetaComentario extends JPanel {
    private final Color colorEstado;
    private boolean tieneFoco = false;

    public TarjetaComentario(String user, String lvl, String texto, String estado, Color colorEstado) {
        this.colorEstado = colorEstado;
        setLayout(new BorderLayout(15, 0));
        setOpaque(false);
        setBorder(new CompoundBorder(new LineBorder(new Color(0, 240, 255, 40), 1), new EmptyBorder(8, 12, 8, 12)));
        setPreferredSize(new Dimension(340, 58));
        setMaximumSize(new Dimension(2000, 58));

        addFocusListener(new FocusListener() {
            @Override public void focusGained(FocusEvent e) { tieneFoco = true; repaint(); }
            @Override public void focusLost(FocusEvent e) { tieneFoco = false; repaint(); }
        });

        JPanel panelIzquierdo = new JPanel(new GridLayout(2, 1));
        panelIzquierdo.setOpaque(false);
        JLabel name = new JLabel(user);
        name.setFont(new Font("Monospaced", Font.BOLD, 13));
        name.setForeground(PanelRevisionComentarios.CYAN_NEON);
        JLabel txt = new JLabel(texto);
        txt.setFont(new Font("SansSerif", Font.PLAIN, 12));
        txt.setForeground(PanelRevisionComentarios.TEXTO_VALOR);
        panelIzquierdo.add(name);
        panelIzquierdo.add(txt);

        JPanel panelDerecho = new JPanel(new GridBagLayout());
        panelDerecho.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();

        JLabel level = new JLabel(lvl);
        level.setFont(new Font("Monospaced", Font.BOLD, 10));
        level.setForeground(PanelRevisionComentarios.TEXTO_MUTED);
        c.gridx = 0; c.gridy = 0; c.insets = new Insets(0,0,0,12);
        panelDerecho.add(level, c);

        JLabel badge = new JLabel(" " + estado.toUpperCase() + " ") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setColor(new Color(colorEstado.getRed(), colorEstado.getGreen(), colorEstado.getBlue(), 30));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.setColor(colorEstado);
                g2d.drawRect(0, 0, getWidth()-1, getHeight()-1);
                g2d.dispose();
                super.paintComponent(g);
            }
        };
        badge.setFont(new Font("Monospaced", Font.BOLD, 10));
        badge.setForeground(colorEstado);
        c.gridx = 1; c.insets = new Insets(0,0,0,0);
        panelDerecho.add(badge, c);

        add(panelIzquierdo, BorderLayout.CENTER);
        add(panelDerecho, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        if (tieneFoco) {
            g2d.setColor(new Color(0, 240, 255, 35));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        } else {
            g2d.setColor(new Color(4, 12, 28, 220));
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
        g2d.dispose();
        super.paintComponent(g);
    }
}

class BotonNeonHUD extends JButton {
    private final String stringLabel;
    private final Color colorNeon;
    private final boolean tieneCapsula;
    private boolean mouseEncima = false;
    private final int tamanoFuente;

    public BotonNeonHUD(String texto, Color colorNeon, boolean tieneCapsula, int ancho, int alto, int tamanoFuente) {
        this.stringLabel = texto;
        this.colorNeon = colorNeon;
        this.tieneCapsula = tieneCapsula;
        this.tamanoFuente = tamanoFuente;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setPreferredSize(new Dimension(ancho, alto));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { mouseEncima = true; repaint(); }
            @Override public void mouseExited(MouseEvent e) { mouseEncima = false; repaint(); }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(); int h = getHeight();

        if (tieneCapsula) {
            g2d.setColor(new Color(colorNeon.getRed(), colorNeon.getGreen(), colorNeon.getBlue(), mouseEncima ? 55 : 20));
            g2d.fillRect(0, 0, w, h);
            g2d.setColor(colorNeon);
            g2d.setStroke(new BasicStroke(mouseEncima ? 2.5f : 1.5f));
            g2d.drawRect(0, 0, w-1, h-1);
            g2d.setColor(mouseEncima ? Color.WHITE : PanelRevisionComentarios.TEXTO_VALOR);
        } else {
            g2d.setColor(mouseEncima ? PanelRevisionComentarios.CYAN_NEON : PanelRevisionComentarios.TEXTO_MUTED);
        }

        g2d.setFont(new Font("Monospaced", Font.BOLD, tamanoFuente));
        FontMetrics fm = g2d.getFontMetrics();
        int textX = (w - fm.stringWidth(stringLabel)) / 2;
        int textY = (h + fm.getAscent() - fm.getDescent()) / 2;
        g2d.drawString(stringLabel, textX, textY);
        g2d.dispose();
    }
}