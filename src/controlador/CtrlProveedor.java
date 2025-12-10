package controlador;

import interfaces.GestionProveedores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Insumo;
import modelo.Producto;
import modelo.Proveedor;

public class CtrlProveedor implements ActionListener{
    
    private GestionProveedores vista;
    private DefaultTableModel modeloTabla;
    
    // Variables de edición
    private Proveedor _proveedorSeleccionado = null;
    private Integer _proveedorSeleccionadoIdx = null;
    
    public CtrlProveedor (GestionProveedores vista){
        this.vista = vista;
        
        //Listener de botones
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);//Modificar Minimo
        this.vista.btnEditar.addActionListener(this);
        
        // Listener Tabla
        this.vista.tableProveedores.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarProveedorDeTabla();
            }
        });
        
        inicializarTabla();
        llenarTabla();
        
        cargarProductos();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == vista.btnGuardar){
            guardarProveedor();
        }
        if(e.getSource() == vista.btnEditar){
            editarProveedor();
        }
        if(e.getSource() == vista.btnEliminar){
            eliminarProveedor();
        }
        if(e.getSource() == vista.btnLimpiar){
            limpiarCampos();
        }
    }
    
    private void cargarProductos(){
        vista.cmbProducto.removeAllItems();
        vista.cmbProducto.addItem("Seleccione un producto");
        //Cargamos los productos añadidos en insumos por el administrador
        for (int i=0; i<AlmacenDatos.listaInsumos.getTamanio(); i++){
            Insumo insumo = AlmacenDatos.listaInsumos.obtener(i);
            vista.cmbProducto.addItem(insumo.getNomInsumo());
        }
    }
    
    private void guardarProveedor(){
        if (_proveedorSeleccionado != null) {
            JOptionPane.showMessageDialog(vista, "Estás editando. Usa 'Limpiar' primero.");
            return;
        }

        String nombre = vista.txtNombreEmpresa.getText();
        String telefono = vista.txtTelefono.getText();
        String producto = (String) vista.cmbProducto.getSelectedItem();

        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Llena todos los campos.");
            return;
        }

        Proveedor nuevo = new Proveedor(nombre, telefono, producto);
        AlmacenDatos.listaProveedores.agregar(nuevo);
        
        JOptionPane.showMessageDialog(vista, "Proveedor guardado.");
        llenarTabla();
        limpiarCampos();
    }
    
    private void eliminarProveedor(){
        if (_proveedorSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un proveedor para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Eliminar a " + _proveedorSeleccionado.getNombreProveedor() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            AlmacenDatos.listaProveedores.eliminar(_proveedorSeleccionadoIdx);
            
            JOptionPane.showMessageDialog(vista, "Eliminado correctamente.");
            llenarTabla();
            limpiarCampos();
        }
    }
    
    private void editarProveedor() {
        if (_proveedorSeleccionado == null || _proveedorSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un proveedor de la tabla.");
            return;
        }

        String nombre = vista.txtNombreEmpresa.getText();
        String telefono = vista.txtTelefono.getText();
        String producto = (String) vista.cmbProducto.getSelectedItem();

        Proveedor actualizado = new Proveedor(nombre, telefono, producto);
        
        // Actualizar en lista
        AlmacenDatos.listaProveedores.actualizar(_proveedorSeleccionadoIdx, actualizado);
        
        JOptionPane.showMessageDialog(vista, "Proveedor actualizado.");
        llenarTabla();
        limpiarCampos();
    }
    
    private void seleccionarProveedorDeTabla() {
        int fila = vista.tableProveedores.getSelectedRow();
        if (fila != -1) {
            _proveedorSeleccionado = AlmacenDatos.listaProveedores.obtener(fila);
            _proveedorSeleccionadoIdx = fila;
            
            vista.txtNombreEmpresa.setText(_proveedorSeleccionado.getNombreProveedor());
            vista.txtTelefono.setText(_proveedorSeleccionado.getTelefono());
            vista.cmbProducto.setSelectedItem(_proveedorSeleccionado.getProdcuto());
        }
    }

    private void limpiarCampos() {
        vista.txtNombreEmpresa.setText("");
        vista.txtTelefono.setText("");
        if(vista.cmbProducto.getItemCount() > 0) vista.cmbProducto.setSelectedIndex(0);
        
        _proveedorSeleccionado = null;
        _proveedorSeleccionadoIdx = null;
        vista.tableProveedores.clearSelection();
    }
    
    private void cargarComboProductos() {
        vista.cmbProducto.removeAllItems();
        // Llenamos el combo con los nombres de los productos que existen en el menú
        for(int i=0; i<AlmacenDatos.listaProductos.getTamanio(); i++) {
            Producto p = AlmacenDatos.listaProductos.obtener(i);
            vista.cmbProducto.addItem(p.getNombre());
        }
        // Opción manual si no hay productos
        if (vista.cmbProducto.getItemCount() == 0) {
            vista.cmbProducto.addItem("Varios");
        }
    }
    
    private void inicializarTabla(){
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Telefono");
        modeloTabla.addColumn("Producto");
        vista.tableProveedores.setModel(modeloTabla);
    }
    
    private void llenarTabla(){
        modeloTabla.setRowCount(0);
        for (int i=0; i<AlmacenDatos.listaProveedores.getTamanio(); i++){
            Proveedor temp = AlmacenDatos.listaProveedores.obtener(i);
            modeloTabla.addRow(new Object[]{
                temp.getNombreProveedor(),
                temp.getTelefono(),
                temp.getProdcuto()
            });
        }
    }
}

