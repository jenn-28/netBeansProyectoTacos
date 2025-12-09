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
        
        // Listeners Pestaña 1 (Gestión)
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);
        this.vista.btnSalir.addActionListener(this);
        
        // Listeners Pestaña 2 (Almacén)
        this.vista.btnRegistrarMov.addActionListener(this);
        this.vista.btnVerMov.addActionListener(this);
        
        // Listener para la Lista de la izquierda
        this.vista.lstInsumos.addListSelectionListener(this);
        
        // Inicialización
        inicializarTabla();
        llenarTabla();       
        llenarListaAlmacen(); 
    }
    
    // Manejo de Botones
    @Override
    public void actionPerformed(ActionEvent e) {
        // Gestión
        if(e.getSource() == vista.btnGuardar) guardarInsumo();
        if(e.getSource() == vista.btnEliminar) eliminarInsumo();
        if(e.getSource() == vista.btnModificar) modificarMinimo();
        if(e.getSource() == vista.btnSalir) vista.dispose();
        
        // Almacén
        if(e.getSource() == vista.btnRegistrarMov) registrarMovimiento();
        if(e.getSource() == vista.btnVerMov) verHistorialSimple();
    }

    // Manejo de Selección en Lista (Pestaña 2)
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) { 
            int index = vista.lstInsumos.getSelectedIndex();
            if (index != -1) {
                Insumo seleccionado = AlmacenDatos.listaInsumos.obtener(index);
                // Actualizar el label de stock disponible
                vista.lblStockActual.setText(String.valueOf(seleccionado.getStock()));
            }
        }
    }

    // Métodos de Lógica - Almacén (Pestaña 2)

    private void llenarListaAlmacen() {
        modeloLista = new DefaultListModel<>();
        
        for (int i = 0; i < AlmacenDatos.listaInsumos.getTamanio(); i++) {
            Insumo item = AlmacenDatos.listaInsumos.obtener(i);
            modeloLista.addElement(item.getNomInsumo());
        }
        
        vista.lstInsumos.setModel(modeloLista);
    }
    
    private void registrarMovimiento() {
        int index = vista.lstInsumos.getSelectedIndex();
        if (index == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo de la lista de la izquierda.");
            return;
        }

        try {
            Insumo item = AlmacenDatos.listaInsumos.obtener(index);
            double cantidad = ((Number) vista.spCantidadMover.getValue()).doubleValue();
            String tipo = vista.cmbTipoMovimiento.getSelectedItem().toString();
            String notas = vista.txtNotas.getText(); 

            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad a mover debe ser mayor a 0.");
                return;
            }

            if (tipo.equalsIgnoreCase("Entrada")) {
                item.setStock(item.getStock() + cantidad);
            } else {
                if (item.getStock() >= cantidad) {
                    item.setStock(item.getStock() - cantidad);
                } else {
                    JOptionPane.showMessageDialog(vista, "No hay suficiente stock para esa salida.");
                    return;
                }
            }
            
            String usuario = (AlmacenDatos.usuarioLogeado != null) ? AlmacenDatos.usuarioLogeado.getNombre() : "Admin";
            
            Movimiento mov = new Movimiento(item.getNomInsumo(), tipo + " (" + notas + ")", cantidad, usuario);
            AlmacenDatos.historialMovimientos.agregar(mov);

            JOptionPane.showMessageDialog(vista, "Movimiento registrado. Nuevo Stock: " + item.getStock());
            
            vista.lblStockActual.setText(String.valueOf(item.getStock())); 
            vista.spCantidadMover.setValue(0);
            vista.txtNotas.setText("");
            
            llenarTabla(); 

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }

    private void verHistorialSimple() {
        StringBuilder reporte = new StringBuilder();
        reporte.append("--- HISTORIAL DE MOVIMIENTOS ---\n\n");
        
        if (AlmacenDatos.historialMovimientos.getTamanio() == 0) {
            reporte.append("No hay movimientos registrados aún.");
        } else {
            for (int i = 0; i < AlmacenDatos.historialMovimientos.getTamanio(); i++) {
                Movimiento m = AlmacenDatos.historialMovimientos.obtener(i);
                reporte.append("• ").append(m.getNombreInsumo())
                       .append(" | ").append(m.getTipo())
                       .append(" | Cant: ").append(m.getCantidad())
                       .append(" | Por: ").append(m.getResponsable())
                       .append("\n");
            }
        }
        
        JOptionPane.showMessageDialog(vista, reporte.toString(), "Historial Rápido", JOptionPane.INFORMATION_MESSAGE);
    }

    // Métodos de Lógica - Gestión (Pestaña 1)
    
    private void guardarInsumo(){
        try{
            String nombre = vista.txtInsumos.getText();
            String unidad = vista.cmbUnidad.getSelectedItem().toString();
            double stockIni = Double.parseDouble(vista.txtCantidadInicial.getText());
            double stockMin = Double.parseDouble(vista.txtCantidadMinima.getText());
            
            Insumo nuevo = new Insumo (nombre, unidad, stockIni, stockMin);
            AlmacenDatos.listaInsumos.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Insumo guardado.");
            llenarTabla();
            llenarListaAlmacen(); 
            limpiarCampos();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }
    
    private void eliminarInsumo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona en la tabla para eliminar.");
            return;
        }
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
                Insumo i = AlmacenDatos.listaInsumos.obtener(fila);
                i.setStockMinimo(nuevoMin);
                JOptionPane.showMessageDialog(vista, "Mínimo actualizado.");
                llenarTabla();
             } catch(Exception e) {
                 JOptionPane.showMessageDialog(vista, "Ingresa un número válido en Cantidad Mínima.");
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