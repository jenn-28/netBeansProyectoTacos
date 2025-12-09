package controlador;

import interfaces.GestionProveedores;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Insumo;
import modelo.Proveedor;

public class CtrlProveedor implements ActionListener{
    
    private GestionProveedores vista;
    private DefaultTableModel modeloTabla;
    
    public CtrlProveedor (GestionProveedores vista){
        this.vista = vista;
        
        //Listener de botones
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);//Modificar Minimo
        this.vista.btnEditar.addActionListener(this);
        
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
        try{
            //Validar que se seleccionó algo
            if(vista.cmbProducto.getSelectedIndex() == 0 || vista.cmbProducto.getSelectedIndex() == -1){
                JOptionPane.showMessageDialog(vista, "Por favor selecciona qué producto surte.");
                return;
            }
            
            String nombre = vista.txtNombreEmpresa.getText();
            String tel = vista.txtTelefono.getText();
            String producto = vista.cmbProducto.getSelectedItem().toString();
            
            //El modelo valida si el teléfono es corto
            Proveedor nuevo = new Proveedor(nombre, tel, producto);
            AlmacenDatos.listaProveedores.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Proveedor registrado con éxito.");
            llenarTabla();
            limpiarCampos();
            
        }catch(IllegalArgumentException ex){
            JOptionPane.showMessageDialog(vista, ex.getMessage(), "Error de Validación", JOptionPane.WARNING_MESSAGE);
        }catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }
    
    private void eliminarProveedor(){
        //Obtener lo que escribe el usuario
        String nombreBuscado = vista.txtNombreEmpresa.getText();
        String telefonoBuscado = vista.txtTelefono.getText();

        // Validación: Que al menos escriban algo para buscar
        if (nombreBuscado.isEmpty() && telefonoBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Escribe el Nombre o Teléfono del proveedor a eliminar.");
            return;
        }

        boolean encontrado = false;

        //Recorrer la lista buscando coincidencias
        for (int i = 0; i < AlmacenDatos.listaProveedores.getTamanio(); i++) {
            Proveedor p = AlmacenDatos.listaProveedores.obtener(i);

            // CONDICIÓN: Coincide el Nombre o el Teléfono
            if (p.getNombreProveedor().equalsIgnoreCase(nombreBuscado) || p.getTelefono().equals(telefonoBuscado)) {

                // Confirmación de seguridad
                int confirm = JOptionPane.showConfirmDialog(vista, 
                        "¿Seguro que deseas eliminar a: " + p.getNombreProveedor()+ "?",
                        "Confirmar Eliminación", 
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    AlmacenDatos.listaProveedores.eliminar(i); // ¡Adiós!
                    encontrado = true;
                    JOptionPane.showMessageDialog(vista, "Proveedor eliminado.");

                    // Actualizamos tabla y limpiamos
                    llenarTabla();
                    limpiarCampos();
                }
                break;
            }
        }
    }
    
    private void editarProveedor() {
        String nombreBuscado = vista.txtNombreEmpresa.getText();

        if (nombreBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Escribe el Nombre exacto del proveedor que quieres editar.");
            return;
        }

        boolean encontrado = false;

        for (int i = 0; i < AlmacenDatos.listaProveedores.getTamanio(); i++) {
            Proveedor p = AlmacenDatos.listaProveedores.obtener(i);

            // Buscamos por nombre
            if (p.getNombreProveedor().equalsIgnoreCase(nombreBuscado)) {

                try {
                    // ENCONTRADO: Actualizamos sus datos con lo que hay en las cajas

                    //Actualizar teléfono (si escribieron algo nuevo)
                    String nuevoTel = vista.txtTelefono.getText();
                    if (!nuevoTel.isEmpty()) {
                        p.setTelefono(nuevoTel);
                    }

                    //Actualizar producto
                    if (vista.cmbProducto.getSelectedIndex() > 0) {
                       p.setProdcuto(vista.cmbProducto.getSelectedItem().toString());
                    }

                    JOptionPane.showMessageDialog(vista, "Datos actualizados para: " + p.getNombreProveedor());
                    encontrado = true;

                    llenarTabla();
                    limpiarCampos();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(vista, "Error al editar: " + ex.getMessage());
                }
                break;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(vista, "No encontré al proveedor '" + nombreBuscado + "'.\nVerifica que el nombre esté escrito igual que en la tabla.");
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
    
    private void limpiarCampos() {
        vista.txtNombreEmpresa.setText("");
        vista.txtTelefono.setText("");
        vista.cmbProducto.setSelectedIndex(0);
    }
}
