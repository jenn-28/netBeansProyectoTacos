package modelo;

public class DetallePedido {
    private Producto producto;
    private int cantidad;
    private String notas;
    private double subtotal;

    public DetallePedido(Producto producto, int cantidad, String notas) {
        this.producto = producto;
        this.cantidad = cantidad;
        this.notas = notas;
        this.subtotal = producto.getPrecio() * cantidad;
    }

    public Producto getProducto() { return producto; }
    public int getCantidad() { return cantidad; }
    public double getSubtotal() { return subtotal; }
    
    @Override
    public String toString() {
        return producto.getNombre() + " x" + cantidad;
    }
}