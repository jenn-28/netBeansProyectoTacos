package controlador;

import interfaces.ReporteMovimientos; // Tu vista
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Movimiento;

public class CtrlReporteMovimientos {
    
    private ReporteMovimientos vista;
    
    public CtrlReporteMovimientos(ReporteMovimientos vista) {
        this.vista = vista;
        llenarTabla();
    }
    
    public void llenarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Producto");
        modelo.addColumn("Tipo");
        modelo.addColumn("Cantidad");
        modelo.addColumn("Responsable");
        
        // Asignamos el modelo a la tabla de la vista
        vista.tblMovimientos.setModel(modelo);
        
        // Recorremos el historial y llenamos los datos
        for (int i = 0; i < AlmacenDatos.historialMovimientos.getTamanio(); i++) {
            Movimiento m = AlmacenDatos.historialMovimientos.obtener(i);
            
            modelo.addRow(new Object[]{
                m.getNombreInsumo(),
                m.getTipo(),
                m.getCantidad(),
                m.getResponsable()
            });
        }
    }
}