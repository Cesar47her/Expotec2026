package main.Util;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReproducirSonido {

    private static Clip clipActual;
    private static FloatControl volumeControl; // Controlador maestro nativo
    private static double volumenActual = 0.80; // Control de persistencia global (80% por defecto)

    private static List<String> listaCanciones = new ArrayList<>();
    private static List<String> colaReproduccion = new ArrayList<>();
    private static String cancionActual;

    // 1. Configurar la lista de canciones (Se llama una sola vez al inicio)
    public static void configurarLista(List<String> rutasCanciones) {
        listaCanciones = new ArrayList<>(rutasCanciones);
        generarNuevaColaAleatoria();
    }

    // 2. Iniciar la reproducción de la lista
    public static void reproducirSiguiente() {
        detener();

        if (listaCanciones.isEmpty()) {
            System.out.println("No hay canciones en la lista.");
            return;
        }

        // Si la cola se vació, volvemos a mezclar para que no se repitan de inmediato
        if (colaReproduccion.isEmpty()) {
            generarNuevaColaAleatoria();
        }

        // Tomamos la primera canción de la cola mezclada
        cancionActual = colaReproduccion.remove(0);

        try {
            File archivoSonido = new File(cancionActual);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(archivoSonido);

            clipActual = AudioSystem.getClip();
            clipActual.open(audioStream);

            // --- INYECCIÓN DEL RECEPTOR DE AUDIO CENTRAL (CORREGIDO) ---
            if (clipActual.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                // El casteo correcto debe ser a (FloatControl), no a su Type
                volumeControl = (FloatControl) clipActual.getControl(FloatControl.Type.MASTER_GAIN);
                // Forzamos a la nueva pista a respetar el volumen actual del slider/configuración
                asignarVolumen(volumenActual);
            } else {
                System.out.println("SISTEMA AUDIO: MASTER_GAIN no es soportado por este formato de audio.");
                volumeControl = null;
            }

            // ¡La magia está aquí! Escuchamos cuando la canción termine de reproducirse
            clipActual.addLineListener(new LineListener() {
                @Override
                public void update(LineEvent event) {
                    if (event.getType() == LineEvent.Type.STOP) {
                        // Verificamos si terminó por su cuenta y no porque la detuvimos manualmente
                        if (clipActual != null && event.getFramePosition() >= clipActual.getFrameLength()) {
                            // Importante: Correr en un hilo separado para evitar bloquear la interfaz o el flujo principal
                            new Thread(() -> reproducirSiguiente()).start();
                        }
                    }
                }
            });

            clipActual.start();
            System.out.println("Reproduciendo ahora: " + cancionActual);

        } catch (Exception e) {
            System.out.println("Error al reproducir: " + cancionActual + " -> " + e.getMessage());
            // Si una canción falla, pasa automáticamente a la siguiente
            reproducirSiguiente();
        }
    }

    /**
     * Modifica el volumen en tiempo real dinámicamente (Rango de entrada: 0.0 a
     * 1.0)
     */
    public static void asignarVolumen(double volumen) {
        volumenActual = volumen; // Persiste el cambio para las próximas canciones en cola

        if (volumeControl == null) {
            return;
        }

        // Limites de desbordamiento por seguridad
        if (volumen < 0.0) {
            volumen = 0.0;
        }
        if (volumen > 1.0) {
            volumen = 1.0;
        }

        if (volumen == 0.0) {
            volumeControl.setValue(volumeControl.getMinimum()); // Silencio total absoluto
        } else {
            // Conversión lineal del Slider a escala logarítmica de decibelios requerida por Java Sound
            float decibelios = (float) (Math.log(volumen) / Math.log(10.0) * 20.0);

            if (decibelios < volumeControl.getMinimum()) {
                decibelios = volumeControl.getMinimum();
            }
            if (decibelios > volumeControl.getMaximum()) {
                decibelios = volumeControl.getMaximum();
            }

            volumeControl.setValue(decibelios);
        }
    }

    public static double getVolumenActual() {
        return volumenActual;
    }

    // Método auxiliar para mezclar las canciones sin repetir seguidas
    private static void generarNuevaColaAleatoria() {
        colaReproduccion = new ArrayList<>(listaCanciones);
        Collections.shuffle(colaReproduccion); // Mezcla aleatoria de Java
    }

    public static void agregarCancionALista(String ruta) {
        if (!listaCanciones.contains(ruta)) {
            listaCanciones.add(ruta);
            generarNuevaColaAleatoria(); // Mezclamos de nuevo para incluir la nueva
            System.out.println("SISTEMA: Audio inyectado en Music Core -> " + ruta);
        }
    }

    public static List<String> getListaCanciones() {
        return listaCanciones;
    }

    // Método para detener la música por completo
    public static void detener() {
        if (clipActual != null) {
            if (clipActual.isRunning()) {
                clipActual.stop();
            }
            clipActual.close();
            clipActual = null;
            volumeControl = null; // Limpiamos la línea de audio de la pista vieja
        }
    }
}
