package modelo;

public class Administrador extends Usuario{
    public Administrador (String nombre, String user, String pass){
        super(nombre, user, pass);
    }
    
    @Override
    public String getRol(){
        return "ADMINISTRADOR";
    }
}
