package modelo;

import estructuras.ListaEnlazada;
import estructuras.Pila;

public class AlmacenDatos {
    //LISTAS GLOBALES (Simulan una base de datos)
    public static ListaEnlazada<Insumo> listaInsumos = new ListaEnlazada<>();
    public static ListaEnlazada<Movimiento> historialMovimientos = new ListaEnlazada<>();
    public static ListaEnlazada<Proveedor> listaProveedores = new ListaEnlazada<>();
    public static ListaEnlazada<Usuario> listaUsuarios = new ListaEnlazada();
    public static ListaEnlazada<Producto> listaProductos = new ListaEnlazada<>();
    public static ListaEnlazada<Pedido> pedidosActivos = new ListaEnlazada<>();
    
    public static Pila<Venta> pilaVentas = new Pila<>();
    
    public static Pedido pedidoTemporal = null;
    
    static {
        listaUsuarios.agregar(new Administrador("SYSTEM ROOT", "admin", "777"));
        listaUsuarios.agregar(new Cajero("Carlos Cajero", "caja", "123"));
        listaUsuarios.agregar(new Mesero("Pedro Mesero", "mesa", "123"));
        
        System.out.println(">>> Sistema Iniciado. Super Admin activo");
        //Productos falsos para probar
        listaProductos.agregar(new Producto("Gringa", 45.0, "Alimentos"));
        listaProductos.agregar(new Producto("Coca Cola", 25.0, "Bebidas"));


        pedidoTemporal = new Pedido(1);
        pedidoTemporal.agregarDetalle(new DetallePedido(new Producto("Asada", 10, "Taco"), 2, "nomas"));
        pedidoTemporal.agregarDetalle(new DetallePedido(new Producto("Asada", 10, "Taco"), 1, "nomas"));
        pedidoTemporal.agregarDetalle(new DetallePedido(new Producto("Horchata", 25, "Bebida"), 1, "nomas"));
        pedidoTemporal.agregarDetalle(new DetallePedido(new Producto("Horchata", 25, "BebidaTaco"), 1, "nomas"));

        var venta = new Venta(pedidoTemporal);
        pilaVentas.push(venta);
        
        // 3. INSUMOS (Inventario Inicial) - ¡ESTO PEDISTE!
        if (listaInsumos.estaVacia()) {
            // Nombre, Unidad, Stock Inicial, Stock Mínimo
            listaInsumos.agregar(new Insumo("Carne de Cerdo", "Kg", 10.0, 5.0));
            listaInsumos.agregar(new Insumo("Tortillas", "Paq", 50.0, 10.0));
            listaInsumos.agregar(new Insumo("Salsa Roja", "Lt", 5.0, 2.0));
        }

        pedidoTemporal = null;
    }
}
