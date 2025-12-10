package controlador;

import interfaces.PedidosActivos;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Pedido;

public class CtrlPedidosActivos {
    
    public CtrlPedidosActivos(PedidosActivos vista) {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("Mesa");
        modelo.addColumn("Total");
        modelo.addColumn("Estado");
        
        vista.tablePedidos.setModel(modelo);
        
        // Recorrer lista de pedidos activos
        for (int i = 0; i < AlmacenDatos.pedidosActivos.getTamanio(); i++) {
            Pedido p = AlmacenDatos.pedidosActivos.obtener(i);
            if (!p.isPagado()) { // Solo mostrar si no han pagado
                modelo.addRow(new Object[]{
                    "Mesa " + p.getNumeroMesa(),
                    "$" + p.getTotal(),
                    "En Curso"
                });
            }
        }
    }
}