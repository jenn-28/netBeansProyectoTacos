/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import interfaces.ReporteVentas;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;

/**
 *
 * @author rc
 */
public class CtrlReporteVentas
{

    private final ReporteVentas vista;
    private DefaultTableModel tblVentasModel;
    private DefaultTableModel tblProductosModel;

    public CtrlReporteVentas(ReporteVentas vista)
    {
        this.vista = vista;

        inicializarTblVentas();
        llenarTblVentas();

        inicializarTblProductos();
        llenarTblProductos();
    }

    private void inicializarTblVentas()
    {
        tblVentasModel  = new DefaultTableModel();

        tblVentasModel.addColumn("Folio");
        tblVentasModel.addColumn("Mesa");
        tblVentasModel.addColumn("Total");

        vista.tableVentasDia.setModel(tblVentasModel);
    }

    private void llenarTblVentas()
    {
        tblVentasModel.setRowCount(0);

        AlmacenDatos.pilaVentas.forEach(venta -> {
            tblVentasModel.addRow(new Object[] {
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
        var mapaProductos = new HashMap<String, Integer>();

        AlmacenDatos.pilaVentas.forEach(venta -> {
            venta.getDetalles().forEach(detalle -> {
                var nombre = detalle.getProducto().getNombre();
                var productoCount = mapaProductos.get(nombre);
                if (productoCount == null) {
                    mapaProductos.put(nombre, 1);
                } else {
                    mapaProductos.put(nombre, productoCount + 1);
                }
            });
        });

        mapaProductos.forEach((producto, ventas) -> {
            tblProductosModel.addRow(new Object[] {
                producto,
                ventas
            });
        });
    }
}
