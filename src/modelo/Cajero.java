package modelo;

public class Cajero extends Usuario{
    public Cajero (String nombre, String user, String pass){
        super(nombre, user, pass);
    }
    
    @Override
    public String getRol(){
        return "CAJERO";
    }
}
