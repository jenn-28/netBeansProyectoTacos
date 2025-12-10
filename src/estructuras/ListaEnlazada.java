package estructuras;

import java.util.function.Function;
import java.util.function.Predicate;

public class ListaEnlazada<T> {
    
    private Nodo<T> inicio;
    private int longitud;

    public ListaEnlazada() {
        this.inicio = null;
        this.longitud = 0;
    }

    public boolean estaVacia() {
        return inicio == null;
    }

    public int getTamanio() {
        return longitud;
    }

    // Método AGREGAR (Al final de la lista)
    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        
        if (estaVacia()) {
            inicio = nuevo;
        } else {
            Nodo<T> aux = inicio;
            // Recorremos hasta el último nodo
            while (aux.getAptSiguiente() != null) {
                aux = aux.getAptSiguiente();
            }
            // Conectamos el último con el nuevo
            aux.setAptSiguiente(nuevo);
        }
        longitud++;
    }

    // Método OBTENER (Vital para llenar JTables y Recorrer)
    // Permite hacer: lista.obtener(0), lista.obtener(1)...
    public T obtener(int indice) {
        if (indice < 0 || indice >= longitud) {
            return null; // O lanzar excepción
        }
        
        Nodo<T> aux = inicio;
        for (int i = 0; i < indice; i++) {
            aux = aux.getAptSiguiente();
        }
        return aux.getDato();
    }

    // Método ELIMINAR por índice (Para el botón "Borrar Usuario/Producto")
    public void eliminar(int indice) {
        if (indice < 0 || indice >= longitud) return;

        if (indice == 0) {
            // Si es el primero, movemos el inicio
            inicio = inicio.getAptSiguiente();
        } else {
            // Si es otro, buscamos el ANTERIOR al que queremos borrar
            Nodo<T> anterior = inicio;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.getAptSiguiente();
            }
            // Saltamos el nodo a eliminar
            Nodo<T> aEliminar = anterior.getAptSiguiente();
            anterior.setAptSiguiente(aEliminar.getAptSiguiente());
        }
        longitud--;
    }

    // Método ACTUALIZAR (Para editar un producto/usuario)
    public void actualizar(int indice, T nuevoDato) {
        if (indice < 0 || indice >= longitud) return;
        
        Nodo<T> aux = inicio;
        for (int i = 0; i < indice; i++) {
            aux = aux.getAptSiguiente();
        }
        aux.setDato(nuevoDato);
    }
    
    // Limpiar toda la lista
    public void vaciar() {
        inicio = null;
        longitud = 0;
    }
    
     public ListaEnlazada<T> filtrar(Predicate<T> predicate)
    {
        var res = new ListaEnlazada<T>();

        var aux = inicio;
        while (aux != null) {
            var dato = aux.getDato();

            if (dato != null && predicate.test(dato))
                res.agregar(dato);

            aux = aux.getAptSiguiente();
        }

        return res;
    }

    public <R> ListaEnlazada<R> mapear(Function<T, R> func)
    {
        var res = new ListaEnlazada<R>();

        var aux = inicio;
        while (aux != null) {
            var dato = aux.getDato();

            if (dato != null) {
                var mapeado = func.apply(dato);
                res.agregar(mapeado);
            }

            aux = aux.getAptSiguiente();
        }

        return res;
    }
}