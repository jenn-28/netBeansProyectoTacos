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
        this.pedidoActual = new Pedido(numMesa); // Creamos un pedido nuevo vacío
        
        // Listeners
        this.vista.btnAcocina.addActionListener(this); // Botón Enviar
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnTacos.addActionListener(this);
        this.vista.btnBebidas.addActionListener(this);
        this.vista.btnLonches.addActionListener(this);
        
        // Doble click en la lista para agregar producto
        this.vista.jlistProductos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) agregarProductoAlPedido();
            }
        });

        inicializarTablaPedido();
        cargarListaProductos("Todos"); // Cargar todo al inicio
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnAcocina) enviarACocina();
        if (e.getSource() == vista.btnEliminar) eliminarDelPedido();
        if (e.getSource() == vista.btnLimpiar) limpiarSeleccion();
        
        // Filtros
        if (e.getSource() == vista.btnTacos) cargarListaProductos("Alimentos"); // O "Taco" según tu BD
        if (e.getSource() == vista.btnBebidas) cargarListaProductos("Bebidas");
        if (e.getSource() == vista.btnLonches) cargarListaProductos("Todos");
    }

    // 1. Cargar productos en la lista izquierda
    private void cargarListaProductos(String filtro) {
        DefaultListModel<Object> modeloLista = new DefaultListModel<>();
        vista.jlistProductos.setModel(modeloLista);
        
        for (int i = 0; i < AlmacenDatos.listaProductos.getTamanio(); i++) {
            Producto p = AlmacenDatos.listaProductos.obtener(i);
            // Filtro simple (ajusta según tus categorías reales en Producto.java)
            if (filtro.equals("Todos") || p.getCategoria().equalsIgnoreCase(filtro)) {
                modeloLista.addElement(p); // Agregamos el objeto directo 
            }
        }
    }

    // 2. Agregar a la tabla de la derecha
    private void agregarProductoAlPedido() {
        // Obtener producto seleccionado
        Producto p = (Producto) vista.jlistProductos.getSelectedValue();
        if (p == null) return;

        // Leer cantidad del spinner (Asumo que se llama spCantidad)
        // Si no tienes nombre de variable, ponle spCantidad en el diseño
        int cantidad = 1; 
        try {
             cantidad = (int) vista.spinerCantidad.getValue(); // Ajusta nombre variable
        } catch (Exception e) { cantidad = 1; }
        
        if (cantidad <= 0) cantidad = 1;

        // Crear detalle y agregar al pedido local
        DetallePedido detalle = new DetallePedido(p, cantidad, ""); // Notas vacías por ahora
        pedidoActual.agregarDetalle(detalle);
        
        llenarTablaPedido();
    }

    // 3. Enviar a Base de Datos
    private void enviarACocina() {
        if (pedidoActual.getDetalles().getTamanio() == 0) {
            JOptionPane.showMessageDialog(vista, "El pedido está vacío.");
            return;
        }

        // Guardar en la lista global de AlmacenDatos
        AlmacenDatos.pedidosActivos.agregar(pedidoActual);
        
        // MOCK: También guardamos en el temporal para que el Cajero lo vea (Compatibilidad)
        if(numMesa == 1) AlmacenDatos.pedidoTemporal = pedidoActual;

        JOptionPane.showMessageDialog(vista, "¡Pedido enviado a cocina!");
        vista.dispose(); // Cerrar ventana
    }
    
    private void eliminarDelPedido() {
        pedidoActual = new Pedido(numMesa);
        llenarTablaPedido();
    }
    
    private void limpiarSeleccion() {
        vista.lblProductoSeleccionado.setText("---");
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