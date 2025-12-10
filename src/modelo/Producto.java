package modelo;

public class Producto {
    private String nombre;
    private double precio;
    private String categoria; // Ej: "Alimentos", "Bebidas", "Postres"

    public Producto(String nombre, double precio, String categoria) {
        this.nombre = nombre;
        this.precio = precio;
        this.categoria = categoria;
    }

    // Getters
    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    
    // Setters (por si acaso se editan precios luego)
    public void setPrecio(double precio) { this.precio = precio; }

    //IMPORTANTE
    // Este método hace que en el ComboBox se vea "Taco Pastor"
    // en lugar de "modelo.Producto@5f1842"
    @Override
    public String toString() {
        return nombre; 
    }
}