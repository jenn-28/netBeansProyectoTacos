package modelo;
import estructuras.ListaEnlazada;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Venta {
    private String folio;
    private double total;
    private String fecha;
    private String metodoPago;
    private ListaEnlazada<DetallePedido> listaDeLoQueComieron;
    private int numMesa;
    
    public Venta(Pedido pedidoTerminado, String metodoPago) {
        // Generar folio simple basado en el tiempo
        this.folio = "V-" + System.currentTimeMillis();
        
        // Generar fecha actual automática
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fecha = LocalDateTime.now().format(dtf);
        
        // Copiar datos del pedido
        this.total = pedidoTerminado.getTotal();
        this.listaDeLoQueComieron = pedidoTerminado.getDetalles();
        this.numMesa = pedidoTerminado.getNumeroMesa();
        this.metodoPago = metodoPago;
    }

    public String getFolio() { return folio; }
    public double getTotal() { return total; }
    public String getFecha() { return fecha; }
    public String getMetodoPago(){ return metodoPago; }
    public ListaEnlazada<DetallePedido> getDetalles() { return listaDeLoQueComieron; }
    public int getNumeroMesa() { return numMesa; }
    
    @Override
    public String toString() {
        return "Folio: " + folio + " | "+metodoPago+ " | $" + total;
    }
}