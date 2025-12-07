package modelo;

public class Proveedor {
    private String nombreProveedor;
    private String telefono;
    private String prodcuto;

    public Proveedor(String nombreProveedor, String telefono, String prodcuto) {
        this.nombreProveedor = nombreProveedor;
        this.telefono = telefono;
        this.prodcuto = prodcuto;
    }

    public String getNombreProveedor() {
        return nombreProveedor;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getProdcuto() {
        return prodcuto;
    }

    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    public void setTelefono(String telefono) {
        if(telefono == null || telefono.length()<10){
            throw new IllegalArgumentException ("El telefono debe tener 10 dígitos");
        }
        this.telefono = telefono;
    }

    public void setProdcuto(String prodcuto) {
        this.prodcuto = prodcuto;
    }
    
    
}
