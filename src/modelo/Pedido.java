package modelo;
import estructuras.ListaEnlazada;

public class Pedido {
    private int numeroMesa;
    private ListaEnlazada<DetallePedido> detalles;
    private double total;
    private boolean pagado;

    public Pedido(int numeroMesa) {
        this.numeroMesa = numeroMesa;
        this.detalles = new ListaEnlazada<>();
        this.total = 0;
        this.pagado = false;
    }

    public void agregarDetalle(DetallePedido detalle) {
        detalles.agregar(detalle);
        this.total += detalle.getSubtotal();
    }
    
    // Getters
    public double getTotal() { return total; }
    public ListaEnlazada<DetallePedido> getDetalles() { return detalles; }
    public int getNumeroMesa() { return numeroMesa; }
    public boolean isPagado() { return pagado; }
    public void setPagado(boolean pagado) { this.pagado = pagado; }
}