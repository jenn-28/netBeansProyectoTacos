/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import interfaces.ReporteVentas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.Producto;

/**
 *
 * @author rc
 */
public class CtrlReporteVentas
{

    private final ReporteVentas vista;
    private DefaultTableModel tblVentasModel;
    private DefaultTableModel tblProductosModel;
    private double totalVentas;
    private HashMap<String, Integer> productoVentas;

    public CtrlReporteVentas(ReporteVentas vista)
    {
        this.vista = vista;

        inicializarTblVentas();
        llenarTblVentas();

        inicializarTblProductos();
        llenarTblProductos();

        calcularTotal();

        vista.btnProductoMasVendido.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                mostrarProductoMasVendido();
            }
        });
    }

    private void inicializarTblVentas()
    {
        tblVentasModel = new DefaultTableModel();

        tblVentasModel.addColumn("Folio");
        tblVentasModel.addColumn("Mesa");
        tblVentasModel.addColumn("Total");

        vista.tableVentasDia.setModel(tblVentasModel);
    }

    private void llenarTblVentas()
    {
        tblVentasModel.setRowCount(0);

        AlmacenDatos.pilaVentas.forEach(venta -> {
            tblVentasModel.addRow(new Object[]{
                venta.getFolio(),
                venta.getNumeroMesa(),
                venta.getTotal()
            });
        });
    }

    private void inicializarTblProductos()
    {
        tblProductosModel = new DefaultTableModel();

        tblProductosModel.addColumn("Producto");
        tblProductosModel.addColumn("Existencias");

        vista.tableExistencias.setModel(tblProductosModel);

    }

    private void llenarTblProductos()
    {
        productoVentas = new HashMap<>();

        AlmacenDatos.pilaVentas.forEach(venta -> {
            venta.getDetalles().forEach(detalle -> {
                var nombre = detalle.getProducto().getNombre();
                var productoCount = productoVentas.get(nombre);
                if (productoCount == null) {
                    productoVentas.put(nombre, detalle.getCantidad());
                } else {
                    productoVentas.put(nombre, productoCount + detalle.getCantidad());
                }
            });
        });

        productoVentas.forEach((producto, ventas) -> {
            tblProductosModel.addRow(new Object[]{
                producto,
                ventas
            });
        });
    }

    private void calcularTotal()
    {
        totalVentas = 0;

        AlmacenDatos.pilaVentas.forEach(venta -> {
            venta.getDetalles().forEach(detalle -> {
                totalVentas += detalle.getSubtotal();
            });
        });

        vista.lblTotalVentas.setText(String.format("Total: $%.2f", totalVentas));
    }

    String masVendido;
    Integer masVendidoCount;
    private void mostrarProductoMasVendido()
    {
        masVendido = null;
        masVendidoCount = null;

        productoVentas.forEach((producto, ventas) -> {
            if (masVendido == null || masVendidoCount == null || ventas > masVendidoCount) {
                masVendido = producto;
                masVendidoCount = ventas;
            }
        });

        if (masVendido == null || masVendidoCount == null) {
            JOptionPane.showMessageDialog(vista, "No se han registrado ventas hasta ahora");
            return;
        }

        var msg = String.format("El producto más vendido es '%s' (%d ventas)", masVendido, masVendidoCount);
        JOptionPane.showMessageDialog(vista, msg);
    }
}
