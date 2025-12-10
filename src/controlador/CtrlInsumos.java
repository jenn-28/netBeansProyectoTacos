package controlador;

import interfaces.Inventario;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Insumo;
import modelo.Movimiento;

public class CtrlInsumos implements ActionListener, ListSelectionListener {
    
    private Inventario vista;
    private DefaultTableModel modeloTabla;
    private DefaultListModel<String> modeloLista;
    
    // Constructor
    public CtrlInsumos(Inventario vista){
        this.vista = vista;
        
        // BLOQUE ANTI-DOBLE EJECUCIÓN
        // Elimina los listeners que NetBeans agrega por defecto para evitar errores
        removerListeners(vista.btnGuardar);
        removerListeners(vista.btnEliminar);
        removerListeners(vista.btnModificar);
        removerListeners(vista.btnSalir);
        removerListeners(vista.btnRegistrarMovimiento);
        removerListeners(vista.btnVerMoviminetos);
        
        // Listeners Pestaña 1 (Gestión)
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);
        this.vista.btnSalir.addActionListener(this);
        
        // Listeners Pestaña 2 (Almacén)
        this.vista.btnRegistrarMovimiento.addActionListener(this);
        this.vista.btnVerMoviminetos.addActionListener(this);
        
        // Listener Lista
        this.vista.listInsumos.addListSelectionListener(this);
        
        inicializarTabla();
        llenarTabla();        
        llenarListaAlmacen(); 
    }
    
    // Método auxiliar para limpiar listeners previos
    private void removerListeners(javax.swing.JButton btn) {
        for(ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }
    
    // Manejo de Botones
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == vista.btnGuardar) guardarInsumo();
        if(e.getSource() == vista.btnEliminar) eliminarInsumo();
        if(e.getSource() == vista.btnModificar) modificarMinimo();
        if(e.getSource() == vista.btnSalir) vista.dispose();
        
        if(e.getSource() == vista.btnRegistrarMovimiento) registrarMovimiento();
        if(e.getSource() == vista.btnVerMoviminetos) verHistorialSimple();
    }

    // Manejo de Selección en Lista
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) { 
            int index = vista.listInsumos.getSelectedIndex();
            if (index != -1) {
                Insumo seleccionado = AlmacenDatos.listaInsumos.obtener(index);
                vista.lblStockActual.setText(String.valueOf(seleccionado.getStock()));
            }
        }
    }

    // Métodos de Lógica - Almacén
    private void registrarMovimiento() {
        int index = vista.listInsumos.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo de la lista.");
            return;
        }

        try {
            Insumo item = AlmacenDatos.listaInsumos.obtener(index);
            
            // Usamos el spinner que indicaste
            double cantidadMover = ((Number) vista.spinnerCantidadMin.getValue()).doubleValue();
            String tipo = vista.cmbTipoMovimiento.getSelectedItem().toString().trim();
            String notas = vista.txtNota.getText(); 

            if (cantidadMover <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad a mover debe ser mayor a 0.");
                return;
            }

            // Lógica de suma/resta explícita
            double stockActual = item.getStock();
            double nuevoStock = 0;

            if (tipo.equalsIgnoreCase("Entrada") || tipo.startsWith("Entrada")) {
                nuevoStock = stockActual + cantidadMover;
                item.setStock(nuevoStock);
            } else {
                // Salida o Merma
                if (stockActual >= cantidadMover) {
                    nuevoStock = stockActual - cantidadMover;
                    item.setStock(nuevoStock);
                } else {
                    JOptionPane.showMessageDialog(vista, "Stock insuficiente. Tienes: " + stockActual);
                    return;
                }
            }
            
            String usuario = (AlmacenDatos.usuarioLogueado != null) ? AlmacenDatos.usuarioLogueado.getNombre() : "Admin";
            
            Movimiento mov = new Movimiento(item.getNomInsumo(), tipo + " (" + notas + ")", cantidadMover, usuario);
            AlmacenDatos.historialMovimientos.agregar(mov);

            JOptionPane.showMessageDialog(vista, "Movimiento registrado.\nStock anterior: " + stockActual + "\nNuevo Stock: " + nuevoStock);
            
            // Actualizar visuales
            vista.lblStockActual.setText(String.valueOf(nuevoStock)); 
            vista.spinnerCantidadMin.setValue(0);
            vista.txtNota.setText("");
            
            llenarTabla(); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al registrar: " + ex.getMessage());
        }
    }

    private void llenarListaAlmacen() {
        modeloLista = new DefaultListModel<>();
        for (int i = 0; i < AlmacenDatos.listaInsumos.getTamanio(); i++) {
            Insumo item = AlmacenDatos.listaInsumos.obtener(i);
            modeloLista.addElement(item.getNomInsumo());
        }
        vista.listInsumos.setModel(modeloLista);
    }
    
    private void verHistorialSimple() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- HISTORIAL ---\n");
        if (AlmacenDatos.historialMovimientos.getTamanio() == 0) {
            reporte.append("Sin movimientos.");
        } else {
            for (int i = 0; i < AlmacenDatos.historialMovimientos.getTamanio(); i++) {
                Movimiento m = AlmacenDatos.historialMovimientos.obtener(i);
                reporte.append(m.getNombreInsumo())
                       .append(" | ").append(m.getTipo())
                       .append(" | ").append(m.getCantidad())
                       .append("\n");
            }
        }
        JOptionPane.showMessageDialog(vista, reporte.toString());
    }

    // Métodos de Lógica - Gestión
    private void guardarInsumo(){
        // Validación para evitar error empty string
        if (vista.txtInsumos.getText().isEmpty() || vista.txtCantidadInicial.getText().isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Llena todos los campos.");
            return;
        }

        try{
            String nombre = vista.txtInsumos.getText();
            String unidad = vista.cmbUnidad.getSelectedItem().toString();
            double stockIni = Double.parseDouble(vista.txtCantidadInicial.getText());
            double stockMin = Double.parseDouble(vista.txtCantidadMinima.getText());
            
            Insumo nuevo = new Insumo (nombre, unidad, stockIni, stockMin);
            AlmacenDatos.listaInsumos.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Guardado.");
            llenarTabla();
            llenarListaAlmacen(); 
            limpiarCampos();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }
    
    private void eliminarInsumo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila == -1) return;
        AlmacenDatos.listaInsumos.eliminar(fila);
        JOptionPane.showMessageDialog(vista, "Eliminado.");
        llenarTabla();
        llenarListaAlmacen(); 
    }
    
    private void modificarMinimo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila != -1) {
             try {
                double nuevoMin = Double.parseDouble(vista.txtCantidadMinima.getText());
                AlmacenDatos.listaInsumos.obtener(fila).setStockMinimo(nuevoMin);
                JOptionPane.showMessageDialog(vista, "Actualizado.");
                llenarTabla();
                limpiarCampos();
             } catch(Exception e) {
                 JOptionPane.showMessageDialog(vista, "Error al modificar.");
             }
        }
    }

    private void inicializarTabla(){
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Insumo");
        modeloTabla.addColumn("Unidad");
        modeloTabla.addColumn("Stock");
        modeloTabla.addColumn("Mínimo");
        modeloTabla.addColumn("Estado");
        vista.tblInsumos.setModel(modeloTabla);
    }
    
    private void llenarTabla(){
        modeloTabla.setRowCount(0);
        for (int i=0; i<AlmacenDatos.listaInsumos.getTamanio(); i++){
            Insumo temp = AlmacenDatos.listaInsumos.obtener(i);
            String estado = temp.requiereReabastecer() ? "REABASTECER" : "OK";
            modeloTabla.addRow(new Object[]{
                temp.getNomInsumo(), temp.getUnidadMedida(), temp.getStock(), temp.getStockMinimo(), estado
            });
        }
    }
    
    private void limpiarCampos() {
        vista.txtInsumos.setText("");
        vista.txtCantidadInicial.setText("");
        vista.txtCantidadMinima.setText("");
    }
}