package main.Util;

import java.awt.Color;

public final class EstiloDiseno {

    // Constructor privado para evitar instanciación
    private EstiloDiseno() {}

    // Paleta de Fondos
    public static final Color CARD_BG = new Color(3, 5, 16, 200);   // Azul profundo semi-transparente
    public static final Color INPUT_BG = new Color(7, 10, 26);       // Fondo oscuro para inputs

    // Colores de Acento (Neones)
    public static final Color CYAN_NEON = new Color(0, 243, 255);   // Celeste Neón principal
    public static final Color PINK_NEON = new Color(255, 0, 127);   // Fucsia/Rosa Neón crítico
    public static final Color GREEN_NEON = new Color(0, 255, 130);  // Verde Neón para éxitos/activos

    // Tipografía y Textos
    public static final Color TEXT_WHITE = new Color(240, 244, 255); // Blanco brillante
    public static final Color TEXT_MUTED = new Color(100, 115, 148); // Gris azulado secundario
}