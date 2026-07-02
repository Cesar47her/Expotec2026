package main.Admin.Configuraciones;

import javax.swing.JSlider;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import java.util.HashMap;
import java.util.Map;

public class ConfiguracionController {

    private final ConfiguracionDAO dao;

    public ConfiguracionController() {
        this.dao = new ConfiguracionDAO();
    }

    /**
     * Recupera los datos de la base de datos a través del DAO y los inyecta en
     * los componentes gráficos correspondientes de la interfaz visual.
     */
    public void cargarConfiguracionEnPantalla(
            int idUsuario, JSlider slVolumen,
            JRadioButton rbPequeno, JRadioButton rbMediano, JRadioButton rbGrande,
            JTextField txtArriba, JTextField txtAbajo, JTextField txtIzquierda, JTextField txtDerecha,
            JTextField txtSaltar, JTextField txtAtacar, JTextField txtInventario, JTextField txtPausa) {

        // 1. Obtener entidad de la base de datos
        ConfiguracionUsuario config = dao.obtenerPorUsuario(idUsuario);

        // CONTROL DE SEGURIDAD: Si el usuario es totalmente nuevo, evita que el programa falle (NullPointerException)
        if (config == null) {
            config = new ConfiguracionUsuario(idUsuario, 0.5, "MEDIANO", "");
        }

        // 2. Mapear Audio a la UI (Conversión de decimal 0.0-1.0 a entero de 0-100)
        int volPorcentaje = (int) (config.getVolumenAudio() * 100);
        slVolumen.setValue(volPorcentaje);

        // 3. Mapear Escalado del HUD a los RadioButtons
        String tamano = (config.getTamanoBotones() != null) ? config.getTamanoBotones().toUpperCase() : "MEDIANO";
        switch (tamano) {
            case "PEQUENO":
            case "PEQUEÑO":
                rbPequeno.setSelected(true);
                break;
            case "GRANDE":
                rbGrande.setSelected(true);
                break;
            case "MEDIANO":
            default:
                rbMediano.setSelected(true);
                break;
        }

        // 4. Parsear la cadena estructurada de controles ("Accion:Tecla;")
        Map<String, String> mapaControles = deserealizarControles(config.getMapeoControles());

        // 5. Rellenar los inputs de teclado mapeados usando valores del mapa o fallbacks seguros
        txtArriba.setText(mapaControles.getOrDefault("Arriba", "W"));
        txtAbajo.setText(mapaControles.getOrDefault("Abajo", "S"));
        txtIzquierda.setText(mapaControles.getOrDefault("Izquierda", "A"));
        txtDerecha.setText(mapaControles.getOrDefault("Derecha", "D"));
        txtSaltar.setText(mapaControles.getOrDefault("Saltar", "ESPACIO"));
        txtAtacar.setText(mapaControles.getOrDefault("Atacar", "J"));
        txtInventario.setText(mapaControles.getOrDefault("Inventario", "I"));
        txtPausa.setText(mapaControles.getOrDefault("Pausa", "P"));
    }

    /**
     * Recolecta el estado actual de los componentes gráficos en pantalla,
     * construye un objeto de configuración válido y solicita al DAO su
     * inserción/actualización.
     */
    public boolean recolectarYGuardar(
            int idUsuario, JSlider slVolumen, JRadioButton rbPequeno, JRadioButton rbGrande,
            JTextField txtArriba, JTextField txtAbajo, JTextField txtIzquierda, JTextField txtDerecha,
            JTextField txtSaltar, JTextField txtAtacar, JTextField txtInventario, JTextField txtPausa) {

        // 1. Obtener volumen en formato decimal para SQL (ej: 0.85)
        double volumenAudio = slVolumen.getValue() / 100.0;

        // 2. Identificar el enumerado de tamaño de botones seleccionado
        String tamanoBotones = "MEDIANO";
        if (rbPequeno.isSelected()) {
            tamanoBotones = "PEQUENO"; 
        } else if (rbGrande.isSelected()) {
            tamanoBotones = "GRANDE";
        }

        // 3. Concatenar y serializar las entradas de texto en formato clave-valor plano para la BD
        String mapeoControles = "Arriba:" + txtArriba.getText().trim().toUpperCase() + ";"
                + "Abajo:" + txtAbajo.getText().trim().toUpperCase() + ";"
                + "Izquierda:" + txtIzquierda.getText().trim().toUpperCase() + ";"
                + "Derecha:" + txtDerecha.getText().trim().toUpperCase() + ";"
                + "Saltar:" + txtSaltar.getText().trim().toUpperCase() + ";"
                + "Atacar:" + txtAtacar.getText().trim().toUpperCase() + ";"
                + "Inventario:" + txtInventario.getText().trim().toUpperCase() + ";"
                + "Pausa:" + txtPausa.getText().trim().toUpperCase() + ";";

        // 4. Instanciar el objeto de transferencia y guardarlo mediante el DAO
        ConfiguracionUsuario nuevaConfig = new ConfiguracionUsuario(idUsuario, volumenAudio, tamanoBotones, mapeoControles);
        
        // CORRECCIÓN: Llamamos al método exacto que definimos en tu clase ConfiguracionDAO
        return dao.guardarConfiguracion(nuevaConfig);
    }

    /**
     * Método utilitario para convertir la cadena de texto plana guardada en la
     * base de datos a un mapa asociativo estructurado de tipo Clave-Valor.
     */
    private Map<String, String> deserealizarControles(String rawData) {
        Map<String, String> mapa = new HashMap<>();
        if (rawData != null && !rawData.trim().isEmpty()) {
            String[] tokens = rawData.split(";");
            for (String token : tokens) {
                String[] componentes = token.split(":");
                if (componentes.length == 2) {
                    mapa.put(componentes[0].trim(), componentes[1].trim());
                }
            }
        }
        return mapa;
    }
}