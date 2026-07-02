package main.Admin.AgregarNovedades;

import java.sql.Timestamp;

public class NoticiaData {
    private int idNoticia;
    private int idAdministrador;
    private String titulo;
    private String contenido;
    private Timestamp fechaPublicacion;
    private String colorHex;

    // Constructor vacío
    public NoticiaData() {}

    // Constructor lleno
    public NoticiaData(String titulo, String contenido, String colorHex) {
        this.titulo = titulo;
        this.contenido = contenido;
        this.colorHex = colorHex;
    }

    // Getters y Setters
    public int getIdNoticia() { return idNoticia; }
    public void setIdNoticia(int idNoticia) { this.idNoticia = idNoticia; }

    public int getIdAdministrador() { return idAdministrador; }
    public void setIdAdministrador(int idAdministrador) { this.idAdministrador = idAdministrador; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public Timestamp getFechaPublicacion() { return fechaPublicacion; }
    public void setFechaPublicacion(Timestamp fechaPublicacion) { this.fechaPublicacion = fechaPublicacion; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }
}