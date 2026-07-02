package main.Admin.AdminCuenta;

public class NoticiaData {
    // 1. Atributos públicos obligatorios para el mapeo del ResultSet
    public String titulo;
    public String contenido;
    public String fecha;
    public String colorHex;

    // 2. El constructor exacto que exige tu NovedadesDAO
    public NoticiaData(String titulo, String contenido, String fecha, String colorHex) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.fecha = fecha;
        this.colorHex = colorHex;
    }
}