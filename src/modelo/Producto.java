/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author rc
 */
public class Producto
{

    private String nombre;
    private String categoria;
    private float precio;

    public static final String CATEGORIA_TACOS = "Tacos";
    public static final String CATEGORIA_LONCHES = "Lonches";
    public static final String CATEGORIA_BEBIDA = "Bebida";
    public static final String CATEGORIA_DULCE = "Dulce";

    public static final String[] categorias = new String[]{
        CATEGORIA_TACOS, CATEGORIA_LONCHES,
        CATEGORIA_BEBIDA, CATEGORIA_DULCE,};

    public Producto(String nombre, String categoria, float precio)
    {
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
    }

    /**
     * Get the value of precio
     *
     * @return the value of precio
     */
    public float getPrecio()
    {
        return precio;
    }

    /**
     * Set the value of precio
     *
     * @param precio new value of precio
     */
    public void setPrecio(float precio)
    {
        this.precio = precio;
    }

    /**
     * Get the value of categoria
     *
     * @return the value of categoria
     */
    public String getCategoria()
    {
        return categoria;
    }

    /**
     * Set the value of categoria
     *
     * @param categoria new value of categoria
     */
    public void setCategoria(String categoria)
    {
        this.categoria = categoria;
    }

    /**
     * Get the value of nombre
     *
     * @return the value of nombre
     */
    public String getNombre()
    {
        return nombre;
    }

    /**
     * Set the value of nombre
     *
     * @param nombre new value of nombre
     */
    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    @Override
    public String toString()
    {
        return "Producto{" + "nombre=" + nombre + ", categoria=" + categoria + ", precio=" + precio + '}';
    }

}
