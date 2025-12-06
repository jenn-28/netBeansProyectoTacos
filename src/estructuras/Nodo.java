package estructuras;

public class Nodo<T> {
    private T dato;
    private Nodo<T> aptSiguiente;

    public Nodo(T dato) {
        this(dato, null);
    }

    public Nodo(T dato, Nodo<T> siguiente) {
        this.dato = dato;
        this.aptSiguiente = siguiente;
    }

    public T getDato() {
        return dato;
    }

    public Nodo<T> getAptSiguiente() {
        return aptSiguiente;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    public void setAptSiguiente(Nodo<T> aptSiguiente) {
        this.aptSiguiente = aptSiguiente;
    }
}