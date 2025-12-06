package estructuras;

public class ListaEnlazada <T>{
    private Nodo<T> inicio;
    private int longitud;
    
    public ListaEnlazada(){
        this.inicio = null;
        this.longitud = 0;
    }
    
    public boolean estaVacia(){
        return inicio == null;
    }
    
    public int getTamanio(){
        return longitud;
    }
    
    //Método AGREGAR (al final de la lista)
    public void agregar(T dato){
        Nodo <T> nuevo = new Nodo<>(dato);
        
        if (estaVacia()){
            inicio = nuevo;
        } else{
            Nodo<T> aux = inicio;
            //Recorrer hasta el último nodo
            while (aux.getAptSiguiente() != null){
                aux = aux.getAptSiguiente();
            }
            //Conectamos el último con el nuevo
            aux.setAptSiguiente(nuevo);
        }
        longitud++;
    }
    
    //Método OBTENER
    //Permite hacer: lista.obtener(0)
    public T obtener (int indice){
        if (indice < 0 || indice >= longitud){
            return null;//excepción
        }
        
        Nodo<T> aux = inicio;
        for (int i =0; i < indice; i++){
            aux = aux.getAptSiguiente();
        }
        
        return aux.getDato();
    }
    
    //Método ELIMINAR por ÍNDICE
    public void eliminar (int indice){
        if(indice < 0 || indice >= longitud) return;
        
        if(indice == 0){
            //En caso de ser el primero, mover el incio
            inicio = inicio.getAptSiguiente();
        } else {
            //De ser otro buscamos el ANTERIOR al que deseamos eliminar
            Nodo<T> anterior = inicio;
            for (int i = 0; i < indice - 1; i++){
                anterior = anterior.getAptSiguiente();
            }//for
            //Saltamos al nodo a eliminar
            Nodo<T> toDelete = anterior.getAptSiguiente();
            anterior.setAptSiguiente(toDelete.getAptSiguiente());
        }//else
        longitud --; 
    }
    
    //Método ACTUALIZAR 
    public void actualizar(int indice, T nuevoDato){
        if (indice < 0 || indice >= longitud ) return;
        
        Nodo<T> aux = inicio;
        for(int i = 0; i < indice; i++){
            aux = aux.getAptSiguiente();
        }
        aux.setDato(nuevoDato);
    }
    
    //Limpiar toda la lista
    public void vaciar(){
        inicio = null;
        longitud = 0;
    }
}
