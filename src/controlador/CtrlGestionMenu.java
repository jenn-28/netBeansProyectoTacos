/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import interfaces.GestionMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Producto;

/**
 *
 * @author rc
 */
public class CtrlGestionMenu
{

    private GestionMenu vista;
    private Producto _productoSeleccionado = null;
    private Integer _productoSeleccionadoIdx = null;
    private DefaultTableModel modeloTabla;
    private static final System.Logger LOG = System.getLogger(CtrlGestionMenu.class.getName());

    public CtrlGestionMenu(GestionMenu vista)
    {
        this.vista = vista;

        inicializarTabla();
        llenarTabla();

        ComboBoxModel<String> aModel = new DefaultComboBoxModel<>(Producto.categorias);
        vista.cmbProductos.setModel(aModel);

        vista.txtPrecio.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                var input = e.getKeyCode();

                if (!Character.isDigit(input) && !(e.getKeyChar() != '.')) {
                    e.consume();
                }
            }

            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

        });

        vista.btnGuardar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if (_productoSeleccionado == null) {
                    agregarNuevoProducto();
                } else {
                    editarProducto();
                }

                llenarTabla();

            }
        });

        vista.btnEditar.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                var nombre = vista.txtNombreProducto.getText();
                if (nombre == null || nombre.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Ingrese el nombre del producto");
                    return;
                }

                var productoIdx = AlmacenDatos.listaProductos.buscar(p -> p.getNombre().equals(nombre));

                _productoSeleccionadoIdx = productoIdx;
                var producto = AlmacenDatos.listaProductos.obtener(productoIdx);
                seleccionarProducto(producto);

                if (productoIdx == null || producto == null) {
                    JOptionPane.showMessageDialog(vista, "Producto no encontrado");
                    return;
                }


                JOptionPane.showMessageDialog(vista, "Producto encontrado");
            }
        });

        LOG.log(System.Logger.Level.INFO, "Vista de GestionMenu inicializada correctamente");
    }

    private void inicializarTabla()
    {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Precio");
        modeloTabla.addColumn("Categoria");
        vista.tableMostrarMenu.setModel(modeloTabla);
    }

    private void llenarTabla()
    {
        modeloTabla.setRowCount(0);

        AlmacenDatos.listaProductos.forEach(p -> {
            modeloTabla.addRow(new Object[]{
                p.getNombre(),
                p.getPrecio(),
                p.getCategoria(),});
        });
    }

    private void seleccionarProducto(Producto p)
    {
        _productoSeleccionado = p;

        if (p == null) {
            limpiarForm();
        } else {
            vista.txtNombreProducto.setText(p.getNombre());
            vista.txtPrecio.setText(Float.toString(p.getPrecio()));
            vista.cmbProductos.setSelectedItem(p.getCategoria());
        }
    }

    private void limpiarForm()
    {
        vista.txtNombreProducto.setText("");
        vista.txtPrecio.setText("");
    }

    private void agregarNuevoProducto()
    {
        var nombre = vista.txtNombreProducto.getText();
        if (nombre == null || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el nombre del producto");
            return;
        }

        var precioString = vista.txtPrecio.getText();
        if (precioString == null || precioString.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el precio del producto");
            return;
        }

        float precio;
        try {
            precio = Float.parseFloat(precioString);
        } catch (Exception ex) {
            vista.txtPrecio.setText("");
            JOptionPane.showMessageDialog(vista, "Ingrese el precio del producto");
            return;
        }

        var categoria = (String) vista.cmbProductos.getSelectedItem();

        var producto = new Producto(nombre, categoria, precio);

        AlmacenDatos.listaProductos.agregar(producto);
        JOptionPane.showMessageDialog(vista, "Producto agregado exitosamente!");
    }

    private void editarProducto()
    {
        var nombre = vista.txtNombreProducto.getText();
        if (nombre == null || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el nombre del producto");
            return;
        }

        var precioString = vista.txtPrecio.getText();
        if (precioString == null || precioString.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese el precio del producto");
            return;
        }

        float precio;
        try {
            precio = Float.parseFloat(precioString);
        } catch (Exception ex) {
            vista.txtPrecio.setText("");
            JOptionPane.showMessageDialog(vista, "Ingrese el precio del producto");
            return;
        }

        var categoria = (String) vista.cmbProductos.getSelectedItem();

        var producto = new Producto(nombre, categoria, precio);
        _productoSeleccionado = producto;
        AlmacenDatos.listaProductos.actualizar(_productoSeleccionadoIdx, producto);
        JOptionPane.showMessageDialog(vista, "Producto editado exitosamente!");
    }

}
