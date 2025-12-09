package modelo;

public class Mesero extends Usuario{
    public Mesero (String nombre, String user, String pass){
        super(nombre, user, pass);
    }
    
    @Override
    public String getRol(){
        return "MESERO";
    }
}
