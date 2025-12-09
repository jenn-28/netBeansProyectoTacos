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
import modelo.AlmacenDatos;
import modelo.Producto;

/**
 *
 * @author rc
 */
public class CtrlGestionMenu
{
    private GestionMenu vista;
    private static final System.Logger LOG = System.getLogger(CtrlGestionMenu.class.getName());


    public CtrlGestionMenu(GestionMenu vista)
    {
        this.vista = vista;

        ComboBoxModel<String> aModel = new DefaultComboBoxModel<>(Producto.categorias);
        vista.cmbProductos.setModel(aModel);

        vista.txtPrecio.addKeyListener(new KeyListener()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                var input = e.getKeyCode();

                if (Character.isDigit(input) || e.getKeyChar() == '.')
                    return; // valido

                // invalido.
                e.consume();
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

        vista.btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
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

                LOG.log(System.Logger.Level.INFO, "Producto agregado exitosamente: {0}", producto);
            }
        });

        LOG.log(System.Logger.Level.INFO, "Vista de GestionMenu inicializada correctamente");
    }

}
