package controlador;

import interfaces.CorteCaja;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Venta;
import estructuras.Pila; // Tu pila personalizada
import servicios.SrvUsuario;

public class CtrlCorteCaja implements ActionListener {
    
    private CorteCaja vista;
    private DefaultTableModel modeloTabla;

    public CtrlCorteCaja(CorteCaja vista) {
        this.vista = vista;
        
        // Listener del botón para salir
        this.vista.btnFinalizar.addActionListener(this);
        
        inicializarTabla();
        cargarCorteDelDia();
        String usuario = SrvUsuario.getUsuario().toString();
        vista.jlbUsuario.setText(usuario);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == vista.btnFinalizar) {
            vista.dispose(); // Cerrar ventana
        }
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Folio");
        modeloTabla.addColumn("Fecha");
        modeloTabla.addColumn("Total");
        vista.jTable2.setModel(modeloTabla); 
    }

    private void cargarCorteDelDia() {
        modeloTabla.setRowCount(0);
        double totalDia = 0.0;
        
        Pila<Venta> pilaAux = new Pila<>();
        
        while (!AlmacenDatos.pilaVentas.estaVacia()) {
            Venta v = AlmacenDatos.pilaVentas.pop();
            
            modeloTabla.addRow(new Object[]{
                v.getFolio(),
                v.getFecha(),
                "$" + v.getTotal()
            });
            
            totalDia += v.getTotal();
            pilaAux.push(v);
        }
        
        while (!pilaAux.estaVacia()) {
            AlmacenDatos.pilaVentas.push(pilaAux.pop());
        }

        vista.jLabel3.setText("Hisotiral de ventas. Total:  $" + totalDia);
    }
}