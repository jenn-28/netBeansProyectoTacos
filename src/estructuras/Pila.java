package estructuras;

public class Pila<T> {
    private Nodo<T> cima;
    
    public Pila(){
        this.cima = null;
    }
    
    //Saber si está vacía
    public boolean estaVacia(){
        return cima == null;
    }
    
    //Push: agrega un nuevo elemento a la pila
    public void push(T dato){
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setAptSiguiente(cima);
        cima = nuevo;
    }
    
    //Pop: Elimina el elemento más reciente
    public T pop(){
        if(estaVacia()) {
            System.out.println("La pila está vacía.");
            return null;
        }else{
            T dato = cima.getDato();
            cima = cima.getAptSiguiente();
            return dato;
        }
    }
    
    //Recorrer pila
    //Devuelve el contenido como String para mostrar en GUI
    public String recorrer(){
        if(estaVacia()){
            return "La pila está vacía.";
        } 
        StringBuilder sb = new StringBuilder(); 
        //Stringbuilder => cambios en una cadena
        //aquí nos ayuda a ir concatenando 
        Nodo<T> aux = cima;
        while (aux != null){
            sb.append(aux.getDato().toString()).append("\n----------------------\n");
            aux = aux.getAptSiguiente();
        }
        return sb.toString();
    }
    
    //Vacíar la pila
    public void clear(){
        this.cima = null;
    }
    
    //Ver el primer elemento sin eliminarlo
    public T peek(){
        if(estaVacia()){
            return null;
        }
        return cima.getDato();
    }
    
    //Size: cantidad de elementos agregados
    public int tamanio(){
        int cont = 0;
        Nodo<T> aux = cima;
        while(aux != null){
            cont++;
            aux = aux.getAptSiguiente();
        }
        return cont;
    }
}
