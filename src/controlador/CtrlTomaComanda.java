package controlador;

import interfaces.TomaComand;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Producto;

public class CtrlTomaComanda implements ActionListener {
    
    private TomaComand vista;
    private int numMesa;
    private Pedido pedidoActual; // El pedido que estamos armando
    private DefaultTableModel modeloTabla;

    public CtrlTomaComanda(TomaComand vista, int numMesa) {
        this.vista = vista;
        this.numMesa = numMesa;
        this.pedidoActual = new Pedido(numMesa);
        
        this.vista.lblMesa.setText("Comanda mesa #"+String.valueOf(numMesa));
        
        // Listeners Botones
        this.vista.btnAcocina.addActionListener(this); 
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnTacos.addActionListener(this);
        this.vista.btnBebidas.addActionListener(this);
        this.vista.btnLonches.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        
        
        this.vista.jlistProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Si es doble clic, agregamos
                if (e.getClickCount() == 2) {
                    agregarProductoAlPedido();
                }
            }
        });

        inicializarTablaPedido();
        cargarListaProductos("Todos");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnAcocina) enviarACocina();
        if (e.getSource() == vista.btnEliminar) eliminarDelPedido();
        if (e.getSource() == vista.btnLimpiar) limpiarSeleccion();
        
        if (e.getSource() == vista.btnTacos) cargarListaProductos("Alimentos");
        if (e.getSource() == vista.btnBebidas) cargarListaProductos("Bebidas");
        if (e.getSource() == vista.btnLonches) cargarListaProductos("Todos");
    }

    private void cargarListaProductos(String filtro) {
        DefaultListModel<Object> modeloLista = new DefaultListModel<>();
        vista.jlistProductos.setModel(modeloLista);
        
        for (int i = 0; i < AlmacenDatos.listaProductos.getTamanio(); i++) {
            Producto p = AlmacenDatos.listaProductos.obtener(i);
            boolean mostrar = false;
            if (filtro.equals("Todos")) mostrar = true;
            else if (filtro.equals("Alimentos") && p.getCategoria().equals("Alimentos")) mostrar = true;
            else if (filtro.equals("Bebidas") && p.getCategoria().equals("Bebidas")) mostrar = true;
            
            if (mostrar) {
                modeloLista.addElement(p);
            }
        }
    }

    private void agregarProductoAlPedido() {
        Producto p = (Producto) vista.jlistProductos.getSelectedValue();
        if (p == null) return;

        int cantidad = 1; 
        try {
             cantidad = (int) vista.spinerCantidad.getValue();
        } catch (Exception e) { cantidad = 1; }
        
        if (cantidad <= 0) cantidad = 1;

        DetallePedido detalle = new DetallePedido(p, cantidad, "");
        pedidoActual.agregarDetalle(detalle);
        
        llenarTablaPedido();
    }

    private void enviarACocina() {
        if (pedidoActual.getDetalles().getTamanio() == 0) {
            JOptionPane.showMessageDialog(vista, "El pedido está vacío.");
            return;
        }

        AlmacenDatos.pedidosActivos.agregar(pedidoActual);
        
        // Mock para cajero (Mesa 1 siempre va a temporal)
        if(numMesa == 1) AlmacenDatos.pedidoTemporal = pedidoActual;

        JOptionPane.showMessageDialog(vista, "¡Pedido enviado a cocina!");
        vista.dispose();
    }
    
    private void eliminarDelPedido() {
        pedidoActual = new Pedido(numMesa);
        llenarTablaPedido();
    }
    
    private void limpiarSeleccion() {
        vista.spinerCantidad.setValue(0);
        vista.jlistProductos.clearSelection();
    }

    private void inicializarTablaPedido() {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Cant");
        modeloTabla.addColumn("Total");
        vista.tableComanda.setModel(modeloTabla); 
    }

    private void llenarTablaPedido() {
        modeloTabla.setRowCount(0);
        double total = 0;
        for (int i = 0; i < pedidoActual.getDetalles().getTamanio(); i++) {
            DetallePedido det = pedidoActual.getDetalles().obtener(i);
            modeloTabla.addRow(new Object[]{
                det.getProducto().getNombre(),
                det.getCantidad(),
                "$" + det.getSubtotal()
            });
            total += det.getSubtotal();
        }
        vista.lblTotal.setText("$ " + total);
    }
}