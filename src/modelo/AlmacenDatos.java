package modelo;

import estructuras.ListaEnlazada;
import estructuras.Pila;

public class AlmacenDatos {
    //LISTAS GLOBALES (Simulan una base de datos)
    public static ListaEnlazada<Insumo> listaInsumos = new ListaEnlazada<>();
    public static ListaEnlazada<Movimiento> historialMovimientos = new ListaEnlazada<>();
    public static ListaEnlazada<Proveedor> listaProveedores = new ListaEnlazada<>();
    public static ListaEnlazada<Usuario> listaUsuarios = new ListaEnlazada();
    public static ListaEnlazada<Producto> listaProductos = new ListaEnlazada();
    
    static {
        listaUsuarios.agregar(new Administrador("SYSTEM ROOT", "admin","777"));
        listaUsuarios.agregar(new Cajero("Carlos", "carlos","777"));
        listaUsuarios.agregar(new Mesero("Mesero", "mesi","777"));

        listaProductos.agregar(
            new Producto("Asada", Producto.CATEGORIA_TACOS, 20.0f)
        );
        listaProductos.agregar(
            new Producto("Horchata", Producto.CATEGORIA_BEBIDA, 25.0f)
        );
        
        System.out.println(">>> Sistema Iniciado. Super Admin activo");
    }
}
