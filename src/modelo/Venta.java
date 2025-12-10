package modelo;
import estructuras.ListaEnlazada;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Venta {
    private String folio;
    private double total;
    private String fecha;
    private ListaEnlazada<DetallePedido> listaDeLoQueComieron;
    
    public Venta(Pedido pedidoTerminado) {
        // Generar folio simple basado en el tiempo
        this.folio = "V-" + System.currentTimeMillis();
        
        // Generar fecha actual automática
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        this.fecha = LocalDateTime.now().format(dtf);
        
        // Copiar datos del pedido
        this.total = pedidoTerminado.getTotal();
        this.listaDeLoQueComieron = pedidoTerminado.getDetalles();
    }

    public String getFolio() { return folio; }
    public double getTotal() { return total; }
    public String getFecha() { return fecha; }
    public ListaEnlazada<DetallePedido> getDetalles() { return listaDeLoQueComieron; }
    
    @Override
    public String toString() {
        return "Folio: " + folio + " | Total: $" + total;
    }
}