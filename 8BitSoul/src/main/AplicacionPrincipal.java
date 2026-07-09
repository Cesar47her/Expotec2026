package main;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import main.Admin.PanelAdmin;
import main.Login.CyberpunkLogin;
import main.Login.CyberpunkRegistro;
import main.Login.MenuPrincipal;
import main.PantalladeCarga.GlitchLoadingScreen;
import main.Usuario.MenuUsuario;

public class AplicacionPrincipal extends JFrame {

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel contenedorVistas = new JPanel(cardLayout);
    private JFrame ventanaActiva;

    private final GlitchLoadingScreen vistaCarga;
    private final MenuPrincipal vistaMenu;
    private final CyberpunkLogin vistaLogin;
    private final CyberpunkRegistro vistaRegistro;

    public AplicacionPrincipal() {
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        vistaCarga = new GlitchLoadingScreen(this::mostrarMenu);
        vistaMenu = new MenuPrincipal(this);
        vistaLogin = new CyberpunkLogin(this);
        vistaRegistro = new CyberpunkRegistro(this);

        contenedorVistas.add(vistaCarga, "CARGA");
        contenedorVistas.add(vistaMenu, "MENU");
        contenedorVistas.add(vistaLogin, "LOGIN");
        contenedorVistas.add(vistaRegistro, "REGISTRO");

        add(contenedorVistas, BorderLayout.CENTER);
        cardLayout.show(contenedorVistas, "CARGA");
        setSize(new Dimension(960, 540));
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public void mostrarMenu() {
        cerrarVentanaSecundaria();
        cambiarVista("MENU", new Dimension(960, 540));
    }

    public void mostrarLogin() {
        cerrarVentanaSecundaria();
        cambiarVista("LOGIN", new Dimension(900, 500));
    }

    public void mostrarRegistro() {
        cerrarVentanaSecundaria();
        cambiarVista("REGISTRO", new Dimension(900, 500));
    }

    public void mostrarAdmin() {
        cerrarVentanaSecundaria();
        setVisible(false);
        PanelAdmin admin = new PanelAdmin(this);
        admin.setLocationRelativeTo(null);
        admin.setVisible(true);
        ventanaActiva = admin;
        toFront();
    }

    public void mostrarUsuario(int idUsuario) {
        cerrarVentanaSecundaria();
        setVisible(false);
        MenuUsuario usuario = new MenuUsuario(idUsuario, this);
        usuario.setLocationRelativeTo(null);
        usuario.setVisible(true);
        ventanaActiva = usuario;
        toFront();
    }

    private void cambiarVista(String nombreVista, Dimension tamaño) {
        cardLayout.show(contenedorVistas, nombreVista);
        setSize(tamaño);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void cerrarVentanaSecundaria() {
        if (ventanaActiva != null && ventanaActiva != this) {
            ventanaActiva.dispose();
            ventanaActiva = null;
        }
    }
}
