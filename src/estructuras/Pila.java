package estructuras;

import java.util.function.Function;
import java.util.function.Predicate;

public class Pila<T> {
    private Nodo<T> cima;

    public Pila() {
        this.cima = null;
    }

    public boolean estaVacia() {
        return cima == null;
    }

    // Push: agrega un nuevo elemento a la pila
    public void push(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setAptSiguiente(cima);
        cima = nuevo;
    }

    // Pop: elimina el elemento más reciente
    public T pop() {
        if (estaVacia()) {
            System.out.println("La pila está vacía, no se puede eliminar.");
            return null;
        } else {
            T dato = cima.getDato();
            cima = cima.getAptSiguiente();
            return dato;
        }
    }

    // Recorrer: devuelve el contenido como String (útil para mostrar en GUI)
    public String recorrer() {
        if (estaVacia()) {
            return "La pila está vacía.";
        }
        StringBuilder sb = new StringBuilder();
        Nodo<T> aux = cima;
        while (aux != null) {
            sb.append(aux.getDato().toString()).append("\n--------------------\n");
            aux = aux.getAptSiguiente();
        }
        return sb.toString();
    }
    
    public void clear() {
        this.cima = null; // La forma más eficiente de vaciar la pila
    }
    
    // Peek: Mira el elemento de arriba SIN eliminarlo
    public T peek() {
        if (estaVacia()) {
            return null;
        } else {
            return cima.getDato();
        }
    }
    
    // Size: Para saber cuántas ventas llevas en el corte
    public int tamanio() {
        int cont = 0;
        Nodo<T> aux = cima;
        while(aux != null){
            cont++;
            aux = aux.getAptSiguiente();
        }
        return cont;
    }

}