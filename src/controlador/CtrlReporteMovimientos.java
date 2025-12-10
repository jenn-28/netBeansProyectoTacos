package controlador;

import interfaces.ReporteMovimientos;
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
        DefaultTableModel modRep = new DefaultTableModel();
        
        // 1. Columnas
        modRep.addColumn("Producto");
        modRep.addColumn("Tipo");
        modRep.addColumn("Cantidad");
        modRep.addColumn("Responsable");

        vista.tblMovimientos.setModel(modRep);
        
        for (int i = 0; i < AlmacenDatos.historialMovimientos.getTamanio(); i++) {
            Movimiento m = AlmacenDatos.historialMovimientos.obtener(i);
            modRep.addRow(new Object[]{
                m.getNombreInsumo(), 
                m.getTipo(), 
                m.getCantidad(), 
                m.getResponsable()
            });
        }
    }
}