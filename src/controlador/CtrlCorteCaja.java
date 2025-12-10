package controlador;

import interfaces.CorteCaja;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Venta;
import estructuras.Pila;
import servicios.SrvUsuario;

public class CtrlCorteCaja implements ActionListener {
    
    private CorteCaja vista;
    private DefaultTableModel modelHistorial; 
    private DefaultTableModel modelResumen; 

    public CtrlCorteCaja(CorteCaja vista) {
        this.vista = vista;
        this.vista.btnFinalizar.addActionListener(this);
        
        if(SrvUsuario.getUsuario() != null) {
            vista.jlbUsuario.setText(SrvUsuario.getUsuario().getNombre());
        }
        
        inicializarTablas();
        realizarCorte();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == vista.btnFinalizar) {
            vista.dispose();
        }
    }

    private void inicializarTablas() {
        //HISTORIAL DETALLADO
        modelHistorial = new DefaultTableModel();
        modelHistorial.addColumn("Folio");
        modelHistorial.addColumn("Fecha");
        modelHistorial.addColumn("Método");
        modelHistorial.addColumn("Monto");
        vista.jTable1.setModel(modelHistorial);

        //RESUMEN DE DINERO
        modelResumen = new DefaultTableModel();
        modelResumen.addColumn("TOTAL GENERAL");
        modelResumen.addColumn("EN EFECTIVO");
        modelResumen.addColumn("EN TARJETA");
        vista.jTable2.setModel(modelResumen);
    }

    private void realizarCorte() {
        modelHistorial.setRowCount(0);
        modelResumen.setRowCount(0);
        
        double sumaTotal = 0.0;
        double sumaEfectivo = 0.0;
        double sumaTarjeta = 0.0;
        
        // Pila auxiliar para no borrar los datos
        Pila<Venta> pilaAux = new Pila<>();
        
        //LEER VENTAS
        while (!AlmacenDatos.pilaVentas.estaVacia()) {
            Venta v = AlmacenDatos.pilaVentas.pop();
            
            // Llenar historial (Tabla de abajo)
            modelHistorial.addRow(new Object[]{
                v.getFolio(),
                v.getFecha(),
                v.getMetodoPago(),
                "$" + v.getTotal()
            });
            
            //Sumar según el método de pago
            sumaTotal += v.getTotal();
            
            if (v.getMetodoPago().equalsIgnoreCase("Tarjeta")) {
                sumaTarjeta += v.getTotal();
            } else {
                sumaEfectivo += v.getTotal();
            }
            
            pilaAux.push(v);
        }
        
        // Regresar datos a la pila original
        while (!pilaAux.estaVacia()) {
            AlmacenDatos.pilaVentas.push(pilaAux.pop());
        }

        //LLENAR TABLA DE RESUMEN
        modelResumen.addRow(new Object[]{
            "$" + sumaTotal,
            "$" + sumaEfectivo,
            "$" + sumaTarjeta
        });
    }
}