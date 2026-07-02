package main.Admin.AdminCuenta;

public class TicketData {
    public int idSolicitud;
    public int idUsuario;
    public String username;
    public int idEstadoSolicitud;
    public String nombreEstado;
    public String tituloConsulta;
    public String mensaje;
    public String dictamenAdministrador;

    public TicketData(int idSolicitud, int idUsuario, String username, int idEstadoSolicitud, 
                      String nombreEstado, String tituloConsulta, String mensaje, String dictamenAdministrador) {
        this.idSolicitud = idSolicitud;
        this.idUsuario = idUsuario;
        this.username = username;
        this.idEstadoSolicitud = idEstadoSolicitud;
        this.nombreEstado = nombreEstado;
        this.tituloConsulta = tituloConsulta;
        this.mensaje = mensaje;
        this.dictamenAdministrador = dictamenAdministrador;
    }
}