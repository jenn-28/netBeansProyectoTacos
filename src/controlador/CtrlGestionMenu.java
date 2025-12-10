package controlador;

import interfaces.GestionMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Producto;

public class CtrlGestionMenu implements ActionListener {

    private GestionMenu vista;
    private DefaultTableModel modeloTabla;
    
    // Variables para controlar la edición
    private Producto _productoSeleccionado = null;
    private Integer _productoSeleccionadoIdx = null;

    public CtrlGestionMenu(GestionMenu vista) {
        this.vista = vista;

        // --- LISTENERS DE BOTONES ---
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEditar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        
        this.vista.tblProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarProductoDeTabla();
            }
        });

        inicializarTabla();
        llenarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            agregarProducto();
        }
        if (e.getSource() == vista.btnEditar) {
            editarProducto();
        }
        if (e.getSource() == vista.btnEliminar) {
            eliminarProducto();
        }
        if (e.getSource() == vista.btnLimpiar) {
            limpiarCampos();
        }
    }
    
    // MÉTODOS CRUD (Lógica Principal)

    private void agregarProducto() {
        // Validar que no estemos en modo edición (opcional, pero buena práctica)
        if (_productoSeleccionado != null) {
            JOptionPane.showMessageDialog(vista, "Estás editando un producto. Usa el botón 'Editar' o 'Limpiar' primero.");
            return;
        }

        String nombre = vista.txtNombreProducto.getText();
        String precioStr = vista.txtPrecio.getText();
        String categoria = (String) vista.cmbProductos.getSelectedItem();

        if (nombre.isEmpty() || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Llena todos los campos.");
            return;
        }

        try {
            double precio = Double.parseDouble(precioStr);
            Producto nuevo = new Producto(nombre, precio, categoria);
            
            // Guardar en la BD
            AlmacenDatos.listaProductos.agregar(nuevo);
            
            JOptionPane.showMessageDialog(vista, "Producto agregado exitosamente.");
            
            // Refrescar y Limpiar
            llenarTabla();
            limpiarCampos();
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El precio debe ser un número válido.");
        }
    }

    private void editarProducto() {
        if (_productoSeleccionado == null || _productoSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto de la tabla primero.");
            return;
        }

        try {
            // Leer nuevos datos
            String nombre = vista.txtNombreProducto.getText();
            double precio = Double.parseDouble(vista.txtPrecio.getText());
            String categoria = (String) vista.cmbProductos.getSelectedItem();

            // Crear objeto actualizado
            Producto productoEditado = new Producto(nombre, precio, categoria);
            
            AlmacenDatos.listaProductos.actualizar(_productoSeleccionadoIdx, productoEditado);
            
            JOptionPane.showMessageDialog(vista, "Producto editado correctamente.");
            
            llenarTabla();   // Refresca la vista
            limpiarCampos(); // Suelta la selección
            
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(vista, "El precio debe ser un número.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al editar: " + ex.getMessage());
        }
    }

    private void eliminarProducto() {
        if (_productoSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un producto para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Seguro de eliminar " + _productoSeleccionado.getNombre() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            AlmacenDatos.listaProductos.eliminar(_productoSeleccionadoIdx);
            llenarTabla();
            limpiarCampos();
            JOptionPane.showMessageDialog(vista, "Producto eliminado.");
        }
    }
    // MÉTODOS AUXILIARES

    private void seleccionarProductoDeTabla() {
        int fila = vista.tblProductos.getSelectedRow();
        if (fila != -1) {
            // Guardamos cual seleccionó (Objeto e Índice)
            _productoSeleccionado = AlmacenDatos.listaProductos.obtener(fila);
            _productoSeleccionadoIdx = fila;
            
            // Llenamos los campos para que pueda editar
            vista.txtNombreProducto.setText(_productoSeleccionado.getNombre());
            vista.txtPrecio.setText(String.valueOf(_productoSeleccionado.getPrecio()));
            vista.cmbProductos.setSelectedItem(_productoSeleccionado.getCategoria());
        }
    }

    private void limpiarCampos() {
        vista.txtNombreProducto.setText("");
        vista.txtPrecio.setText("");
        vista.cmbProductos.setSelectedIndex(0);
        
        // --- CORRECCIÓN VITAL ---
        // Reseteamos las variables de selección para que el sistema sepa que ya no estamos editando
        _productoSeleccionado = null;
        _productoSeleccionadoIdx = null;
        vista.tblProductos.clearSelection();
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Categoría");
        vista.tblProductos.setModel(modeloTabla);
    }

    private void llenarTabla() {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < AlmacenDatos.listaProductos.getTamanio(); i++) {
            Producto p = AlmacenDatos.listaProductos.obtener(i);
            modeloTabla.addRow(new Object[]{
                p.getNombre(),
                p.getPrecio(),
                p.getCategoria()
            });
        }
    }
}
