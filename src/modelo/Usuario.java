package modelo;

public abstract class Usuario {
    protected String nombreReal;
    protected String username;
    protected String password;

    public Usuario(String nombreReal, String username, String password) {
        this.nombreReal = nombreReal;
        this.username = username;
        this.password = password;
    }
    
    //Método abstracto, cada hijo debe decir qué es
    public abstract String getRol();
    
    public String getNombre() { return nombreReal; }
    public void setNombre(String n) { this.nombreReal = n; }
    
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
    
    @Override
    public String toString() {return nombreReal;}
}
