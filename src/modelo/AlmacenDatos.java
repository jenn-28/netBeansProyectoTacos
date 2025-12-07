package modelo;

import estructuras.ListaEnlazada;
import estructuras.Pila;

public class AlmacenDatos {
    //LISTAS GLOBALES (Simulan una base de datos)
    //lista de insumos
    public static ListaEnlazada<Insumo> listaInsumos = new ListaEnlazada<>();
    public static ListaEnlazada<Movimiento> historialMovimientos = new ListaEnlazada<>();
}
