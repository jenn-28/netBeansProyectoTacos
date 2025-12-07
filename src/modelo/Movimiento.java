package modelo;

public class Movimiento {
    private String nombreInsumo;
    private String tipo;      // Entrada, Salida, Merma
    private double cantidad;
    private String responsable; // El usuario de la sesión

    public Movimiento(String nombreInsumo, String tipo, double cantidad, String responsable) {
        this.nombreInsumo = nombreInsumo;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.responsable = responsable;
    }

    public String getNombreInsumo() { return nombreInsumo; }
    public String getTipo() { return tipo; }
    public double getCantidad() { return cantidad; }
    public String getResponsable() { return responsable; }
}