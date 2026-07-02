package main.Admin.Configuraciones;

public class ConfiguracionUsuario {
    private int idUsuario;
    private double volumenAudio;
    private String tamanoBotones;
    private String mapeoControles;

    // Constructor Completo (Requerido por el DAO y el Controller)
    public ConfiguracionUsuario(int idUsuario, double volumenAudio, String tamanoBotones, String mapeoControles) {
        this.idUsuario = idUsuario;
        this.volumenAudio = volumenAudio;
        this.tamanoBotones = tamanoBotones;
        this.mapeoControles = mapeoControles;
    }

    // Getters y Setters
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getVolumenAudio() {
        return volumenAudio;
    }

    public void setVolumenAudio(double volumenAudio) {
        this.volumenAudio = volumenAudio;
    }

    public String getTamanoBotones() {
        return tamanoBotones;
    }

    public void setTamanoBotones(String tamanoBotones) {
        this.tamanoBotones = tamanoBotones;
    }

    public String getMapeoControles() {
        return mapeoControles;
    }

    public void setMapeoControles(String mapeoControles) {
        this.mapeoControles = mapeoControles;
    }
}