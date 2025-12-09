package controlador;

import interfaces.Inventario;
import interfaces.ReporteMovimientos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Insumo;
import modelo.Movimiento;


public class CtrlInsumos implements ActionListener{
    //referenciar la vista
    private Inventario vista;
    private DefaultTableModel modeloTabla;
    
    public CtrlInsumos(Inventario vista){
        this.vista = vista;
        
        //Listener de botones 
        //INSUMOS
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);//Modificar Minimo
        this.vista.btnSalir.addActionListener(this);
        //ALMACEN
        this.vista.btnRegistrarMovimiento.addActionListener(this);
        this.vista.btnVerMoviminetos.addActionListener(this);
        
        inicializarTabla();
        llenarTabla();
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        //GESTIÓN DE INSUMOS
        if(e.getSource() == vista.btnGuardar){
            guardarInsumo();
        }
        if(e.getSource() == vista.btnEliminar){
            eliminarInsumo();
        }
        if (e.getSource() == vista.btnModificar) {
            modificarMinimo();
        }
        if (e.getSource() == vista.btnSalir) {
            vista.dispose();
        }
        //ALMACÉN
        if (e.getSource() == vista.btnRegistrarMovimiento) {
            registrarMovimiento();
        }
        if (e.getSource() == vista.btnVerMoviminetos) {
            reporteMovimientos();
        }
    }
    
    private void guardarInsumo(){
        try{
            String nombre = vista.txtInsumos.getText();
            String unidad = vista.cmbUnidad.getSelectedItem().toString();
            double stockIni = Double.parseDouble(vista.txtCantidadInicial.getText());
            double stockMin = Double.parseDouble(vista.txtCantidadMinima.getText());
            
            //Crear y guardar
            Insumo nuevo = new Insumo (nombre, unidad, stockIni, stockMin);
            AlmacenDatos.listaInsumos.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Insumo guardado correctamente");
            llenarTabla();
            limpiarCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Revisa que Stock y Mínimo sean números.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }//guardar-insumo
    
    private void eliminarInsumo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo para eliminar.");
            return;
        }
        
        // Usamos el método eliminar de tu ListaEnlazada
        AlmacenDatos.listaInsumos.eliminar(fila);
        JOptionPane.showMessageDialog(vista, "Eliminado.");
        llenarTabla();
    }//eliminar-insumo
    
    private void modificarMinimo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo para modificar su mínimo.");
            return;
        }

        try {
            // Opción A: Leer de la caja de texto (Si el usuario escribió ahí y le dio Modificar)
            double nuevoMinimo = Double.parseDouble(vista.txtCantidadMinima.getText());
            
            // Actualizar objeto
            Insumo insumo = AlmacenDatos.listaInsumos.obtener(fila);
            insumo.setStockMinimo(nuevoMinimo);
            
            JOptionPane.showMessageDialog(vista, "Stock mínimo actualizado a: " + nuevoMinimo);
            llenarTabla();
            limpiarCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Escribe el nuevo valor numérico en la caja de 'Cantidad Mínima'.");
        }
    }//modificar-min
    
    private void registrarMovimiento() {
        int fila = vista.tblInsumos.getSelectedRow(); 
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo de la tabla primero.");
            return;
        }

        try {
            double cant = ((Number) vista.spinnerCantidadMin.getValue()).doubleValue();
            
            // Validación extra: Que no muevan 0 o negativos
            if (cant <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
                return;
            }

            String tipo = vista.cmbTipoMovimiento.getSelectedItem().toString();
            Insumo item = AlmacenDatos.listaInsumos.obtener(fila);

            //Lógica Matemática
            if (tipo.equalsIgnoreCase("Entrada") || tipo.contains("Entrada")) {
                item.setStock(item.getStock() + cant);
            } else {
                // Es Salida o Merma
                if (item.getStock() >= cant) {
                    item.setStock(item.getStock() - cant);
                } else {
                    JOptionPane.showMessageDialog(vista, "Stock insuficiente para esa salida.");
                    return; 
                }
            }

            //Guardar Historial
            /*
            String user = "Usuario"; 
            if (AlmacenDatos.usuarioLogueado != null) {
                user = AlmacenDatos.usuarioLogueado.getNombre();
            }
            
            Movimiento mov = new Movimiento(item.getNombre(), tipo, cant, user);
            AlmacenDatos.historialMovimientos.agregar(mov);
*/
            JOptionPane.showMessageDialog(vista, "Movimiento registrado.");
            llenarTabla();
            
            // Reiniciar el spinner a 0 o 1
            vista.spinnerCantidadMin.setValue(0); 

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error al leer el spinner: " + e.getMessage());
        }
    }
    
    private void reporteMovimientos() {
        // Crear la ventana
        ReporteMovimientos repVista = new ReporteMovimientos();
        DefaultTableModel modRep = new DefaultTableModel();
        
        modRep.addColumn("Producto");
        modRep.addColumn("Tipo");
        modRep.addColumn("Cantidad");
        modRep.addColumn("Responsable");

        // Llenar tabla DIRECTAMENTE
        // Si la tabla en ReporteMovimientos se llama 'tblMovimientos':
        repVista.tblMovimientos.setModel(modRep);
        
        for (int i = 0; i < AlmacenDatos.historialMovimientos.getTamanio(); i++) {
            Movimiento m = AlmacenDatos.historialMovimientos.obtener(i);
            modRep.addRow(new Object[]{
                m.getNombreInsumo(), m.getTipo(), m.getCantidad(), m.getResponsable()
            });
        }
        
        // Mostrar
        repVista.setVisible(true);
    }
    
    private void inicializarTabla(){
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Insumo");
        modeloTabla.addColumn("Unidad");
        modeloTabla.addColumn("Stock Actual");
        modeloTabla.addColumn("Mínimo");
        modeloTabla.addColumn("Estado");
        vista.tblInsumos.setModel(modeloTabla);
    }
    
    private void llenarTabla(){
        modeloTabla.setRowCount(0);
        for (int i=0; i<AlmacenDatos.listaInsumos.getTamanio(); i++){
            Insumo temp = AlmacenDatos.listaInsumos.obtener(i);
            
            //Alerta
            String estado = "OK";
            if(temp.requiereReabastecer()){
                estado="REABASTECER";
            }
            
            modeloTabla.addRow(new Object[]{
                temp.getNomInsumo(),
                temp.getUnidadMedida(),
                temp.getStock(),
                temp.getStockMinimo(),
                estado
            });
        }
    }
    
    private void limpiarCampos() {
        vista.txtInsumos.setText("");
        vista.txtCantidadInicial.setText("");
        vista.txtCantidadMinima.setText("");
    }
    
    
}
