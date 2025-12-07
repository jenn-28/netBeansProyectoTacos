package modelo;

public class Insumo {
    private String nomInsumo;
    private String unidadMedida;
    private double stock;
    private double stockMinimo;

    public Insumo(String nomInsumo, String unidadMedida, double stock, double stockMinimo) {
        this.nomInsumo = nomInsumo;
        this.unidadMedida = unidadMedida;
        setStock(stock);
        setStockMinimo(stockMinimo);
    }

    public String getNomInsumo() {
        return nomInsumo;
    }

    public String getUnidadMedida() {
        return unidadMedida;
    }

    public double getStock() {
        return stock;
    }

    public double getStockMinimo() {
        return stockMinimo;
    }

    public void setNomInsumo(String nomInsumo) {
        this.nomInsumo = nomInsumo;
    }

    public void setUnidadMedida(String unidadMedida) {
        this.unidadMedida = unidadMedida;
    }

    public void setStock(double stock) {
        if (stock < 0){
            throw new IllegalArgumentException("ERROR: El stock no puede ser negativo");
        } else {
            this.stock = stock;
        }
    }

    public void setStockMinimo(double stockMinimo) {
        if (stock < 0){
            throw new IllegalArgumentException("ERROR: El stock minimo no puede ser negativo");
        } else {
            this.stockMinimo = stockMinimo;
        }
    }
    
    //MÉTODO EXTRA: marcar si se llega al mínimo
    public boolean requiereReabastecer(){
        return this.stock <= this.stockMinimo;
    }
}
