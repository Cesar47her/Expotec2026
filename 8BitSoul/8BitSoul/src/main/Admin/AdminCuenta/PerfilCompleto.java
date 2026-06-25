package main.Admin.AdminCuenta;

public class PerfilCompleto {
    public int idUsuario;
    public String username;
    public String correo;
    public String contrasena;
    public String fechaRegistro;
    public String nombreRol;
    public int nivelCuenta;
    public int cantidadMonedas;

    public PerfilCompleto(int idUsuario, String username, String correo, String contrasena, 
                          String fechaRegistro, String nombreRol, int nivelCuenta, int cantidadMonedas) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.correo = correo;
        this.contrasena = contrasena;
        this.fechaRegistro = fechaRegistro;
        this.nombreRol = nombreRol;
        this.nivelCuenta = nivelCuenta;
        this.cantidadMonedas = cantidadMonedas;
    }
}