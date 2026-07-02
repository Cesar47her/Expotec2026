package main.Admin.AdminCuenta;

public class CompraData {
    public String idTx;
    public String idUsuario;
    public String item;
    public double monto;
    public String timestamp;
    public String estado;

    public CompraData(String idTx, String idUsuario, String item, double monto, String timestamp, String estado) {
        this.idTx = idTx;
        this.idUsuario = idUsuario;
        this.item = item;
        this.monto = monto;
        this.timestamp = timestamp;
        this.estado = estado;
    }
}