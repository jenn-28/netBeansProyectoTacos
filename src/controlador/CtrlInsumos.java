package controlador;

import interfaces.Inventario;
import interfaces.ReporteMovimientos; // Solo si usas la ventana aparte
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
import servicios.SrvUsuario;
// import servicios.SrvUsuario; // Descomenta si usas este servicio

public class CtrlInsumos implements ActionListener, ListSelectionListener {
    
    private Inventario vista;
    private DefaultTableModel modeloTabla;
    private DefaultListModel<String> modeloLista;
    
    public CtrlInsumos(Inventario vista){
        this.vista = vista;
        
        // --- LIMPIEZA DE LISTENERS (Anti Doble-Ejecución) ---
        // Esto es vital para que no te salga el error de "Empty String" por doble clic
        removerListeners(vista.btnGuardar);
        removerListeners(vista.btnEliminar);
        removerListeners(vista.btnModificar);
        removerListeners(vista.btnSalir);
        removerListeners(vista.btnRegistrarMovimiento);
        removerListeners(vista.btnVerMov);
        
        // Listeners Pestaña 1 (Gestión)
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnModificar.addActionListener(this);
        this.vista.btnSalir.addActionListener(this);
        
        // Listeners Pestaña 2 (Almacén)
        this.vista.btnRegistrarMovimiento.addActionListener(this);
        this.vista.btnVerMov.addActionListener(this);
        
        // Listener Lista
        this.vista.listInsumos.addListSelectionListener(this);
        
        inicializarTabla();
        llenarTabla();
        llenarListaAlmacen();
    }
    
    // Método auxiliar para limpiar
    private void removerListeners(javax.swing.JButton btn) {
        for(ActionListener al : btn.getActionListeners()) {
            btn.removeActionListener(al);
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // GESTIÓN DE INSUMOS
        if(e.getSource() == vista.btnGuardar) guardarInsumo();
        if(e.getSource() == vista.btnEliminar) eliminarInsumo();
        if(e.getSource() == vista.btnModificar) modificarMinimo();
        if(e.getSource() == vista.btnSalir) vista.dispose();
        
        // ALMACÉN
        if(e.getSource() == vista.btnRegistrarMovimiento) registrarMovimiento();
        if(e.getSource() == vista.btnVerMov) reporteMovimientos(); // O verHistorialSimple()
    }
    
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
    
    // --- MÉTODOS DE LÓGICA ---

    private void guardarInsumo(){
        // Validación extra
        if(vista.txtInsumos.getText().isEmpty()){
            JOptionPane.showMessageDialog(vista, "El nombre no puede estar vacío.");
            return;
        }

        try{
            String nombre = vista.txtInsumos.getText();
            String unidad = vista.cmbUnidad.getSelectedItem().toString();
            double stockIni = Double.parseDouble(vista.txtCantidadInicial.getText());
            double stockMin = Double.parseDouble(vista.txtCantidadMinima.getText());
            
            Insumo nuevo = new Insumo (nombre, unidad, stockIni, stockMin);
            AlmacenDatos.listaInsumos.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Insumo guardado correctamente");
            llenarTabla();
            llenarListaAlmacen(); // Actualizar pestaña 2
            limpiarCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "Revisa que Stock y Mínimo sean números válidos.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error: " + ex.getMessage());
        }
    }
    
    private void eliminarInsumo() {
        int fila = vista.tblInsumos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo para eliminar.");
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
                AlmacenDatos.listaInsumos.obtener(fila).setStockMinimo(nuevoMin);
                JOptionPane.showMessageDialog(vista, "Actualizado.");
                llenarTabla();
                limpiarCampos();
             } catch(Exception e) {
                 JOptionPane.showMessageDialog(vista, "Ingresa un número válido.");
             }
        } else {
            JOptionPane.showMessageDialog(vista, "Selecciona una fila primero.");
        }
    }
    
    private void registrarMovimiento() {
        int index = vista.listInsumos.getSelectedIndex(); 
        if (index == -1) {
            JOptionPane.showMessageDialog(vista, "Selecciona un insumo de la lista izquierda.");
            return;
        }

        try {
            Insumo item = AlmacenDatos.listaInsumos.obtener(index);
            
            double cantidad = ((Number) vista.spCantidadMover.getValue()).doubleValue();
            
            // TRIM es importante para evitar errores de espacios
            String tipo = vista.cmbTipoMovimiento.getSelectedItem().toString().trim();
            String notas = vista.txtArea.getText();
            
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad debe ser mayor a 0.");
                return;
            }

            // Lógica Matemática
            if (tipo.equalsIgnoreCase("Entrada") || tipo.startsWith("Entrada")) {
                item.setStock(item.getStock() + cantidad);
            } else {
                // Salida o Merma
                if (item.getStock() >= cantidad) {
                    item.setStock(item.getStock() - cantidad);
                } else {
                    JOptionPane.showMessageDialog(vista, "Stock insuficiente.");
                    return; 
                }
            }
            
            // Obtener usuario (Usa SrvUsuario o AlmacenDatos según tengas)
            String user = "Admin";
            if (SrvUsuario.getUsuario() != null) {
                user = SrvUsuario.getUsuario().getNombre();
            }
            
            Movimiento mov = new Movimiento(item.getNomInsumo(), tipo, cantidad, user);
            AlmacenDatos.historialMovimientos.agregar(mov);

            JOptionPane.showMessageDialog(vista, "Movimiento registrado. Nuevo Stock: " + item.getStock());
            
            // Actualizar visuales
            vista.lblStockActual.setText(String.valueOf(item.getStock())); 
            vista.spCantidadMover.setValue(0);
            vista.txtArea.setText("");
            
            llenarTabla(); // Sincronizar pestaña 1
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error: " + e.getMessage());
        }
    }
    
    // Opción A: Abrir ventana nueva
    private void reporteMovimientos() {
        ReporteMovimientos repVista = new ReporteMovimientos();
        new CtrlReporteMovimientos(repVista); 
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
    
    private void llenarListaAlmacen() {
        modeloLista = new DefaultListModel<>();
        for (int i = 0; i < AlmacenDatos.listaInsumos.getTamanio(); i++) {
            Insumo item = AlmacenDatos.listaInsumos.obtener(i);
            modeloLista.addElement(item.getNomInsumo());
        }
        vista.listInsumos.setModel(modeloLista);
    }
    
    private void limpiarCampos() {
        vista.txtInsumos.setText("");
        vista.txtCantidadInicial.setText("");
        vista.txtCantidadMinima.setText("");
    }
}