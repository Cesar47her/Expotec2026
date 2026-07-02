package main.Admin.AdminCuenta;

public class ItemData {
    public int idItem;
    public int idTipoItem;
    public String nombreTipo; // Para mostrar texto en la JTable helyx
    public String nombreItem;
    public int precio;
    public String descripcion;

    public ItemData(int idItem, int idTipoItem, String nombreTipo, String nombreItem, int precio, String descripcion) {
        this.idItem = idItem;
        this.idTipoItem = idTipoItem;
        this.nombreTipo = nombreTipo;
        this.nombreItem = nombreItem;
        this.precio = precio;
        this.descripcion = descripcion;
    }
}