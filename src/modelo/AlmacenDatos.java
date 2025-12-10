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
    
    //public static Usuario usuarioLogeado = null;
    
    static {
        Administrador superAdmin = new Administrador("SYSTEM ROOT", "admin","777");
        listaUsuarios.agregar(superAdmin);
        
        System.out.println(">>> Sistema Iniciado. Super Admin activo");
        //Productos falsos para probar
        listaProductos.agregar(new Producto("Gringa", 45.0, "Alimentos"));
        listaProductos.agregar(new Producto("Coca Cola", 25.0, "Bebidas"));
    }
}
