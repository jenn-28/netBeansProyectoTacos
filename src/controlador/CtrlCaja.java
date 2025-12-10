package controlador;

import interfaces.CajaCobro;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.AlmacenDatos;
import modelo.DetallePedido;
import modelo.Pedido;
import modelo.Producto; // Necesario para la prueba
import modelo.Venta;

public class CtrlCaja implements ActionListener {

    private CajaCobro vista;
    private DefaultTableModel modeloTabla;
    
    public CtrlCaja(CajaCobro vista) {
        this.vista = vista;
        
        //INICIALIZAR DATOS DE PRUEBA
        crearPedidoPrueba();

        //AGRUPAR BOTONES DE PAGO
        ButtonGroup grupoPago = new ButtonGroup();
        grupoPago.add(vista.rbtnEfectivo);
        grupoPago.add(vista.rbtnTarjeta);
        vista.rbtnEfectivo.setSelected(true); // Efectivo por defecto

        // 3. LISTENERS DE BOTONES
        this.vista.btnCobrar.addActionListener(this);
        this.vista.btnRefrescar.addActionListener(this); 
        this.vista.cmbMesas.addActionListener(this);
        
        // Listeners para cambio de Efectivo/Tarjeta
        this.vista.rbtnEfectivo.addActionListener(this);
        this.vista.rbtnTarjeta.addActionListener(this);

        //LISTENER PARA CALCULAR CAMBIO AUTOMÁTICO
        this.vista.txtRecibido.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                calcularCambio();
            }
        });
        
        inicializarTabla();
        cargarMesasActivas();
        bloquearCamposPago(false); // Inicia desbloqueado porque Efectivo es default
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if (e.getSource() == vista.btnRefrescar) {
            cargarMesasActivas();
        }
        
        if (e.getSource() == vista.btnCobrar) {
            cobrar();
        }
        
        if (e.getSource() == vista.cmbMesas) {
            mostrarPedidoDeMesaSeleccionada();
        }

        // Lógica visual de Tarjeta vs Efectivo
        if (e.getSource() == vista.rbtnTarjeta) {
            bloquearCamposPago(true); // Bloquear cajas
        }
        if (e.getSource() == vista.rbtnEfectivo) {
            bloquearCamposPago(false); // Desbloquear cajas
        }
    }
    
    // --- LÓGICA VISUAL ---
    
    private void bloquearCamposPago(boolean bloquear) {
        // Si bloquear es true (Tarjeta), deshabilitamos las cajas
        vista.txtRecibido.setEnabled(!bloquear);
        vista.txtCambio.setEnabled(!bloquear);
        
        if (bloquear) {
            vista.txtRecibido.setText("");
            vista.txtCambio.setText("");
            vista.txtRecibido.setBackground(new Color(240, 240, 240)); // Gris
        } else {
            vista.txtRecibido.setBackground(Color.WHITE);
            vista.txtRecibido.requestFocus();
        }
    }

    private void calcularCambio() {
        try {
            // Si no hay pedido seleccionado, no hacer nada
            if (AlmacenDatos.pedidoTemporal == null) return;

            double total = AlmacenDatos.pedidoTemporal.getTotal();
            double recibido = Double.parseDouble(vista.txtRecibido.getText());
            
            double cambio = recibido - total;
            vista.txtCambio.setText(String.format("%.2f", cambio));
            
        } catch (NumberFormatException ex) {
            vista.txtCambio.setText(""); // Si escriben letras, borrar cambio
        }
    }

    //LÓGICA PRINCIPAL

    private void cargarMesasActivas() {
        vista.cmbMesas.removeAllItems();
        vista.cmbMesas.addItem("Seleccione Mesa");
        
        if (AlmacenDatos.pedidoTemporal != null && !AlmacenDatos.pedidoTemporal.isPagado()) {
            vista.cmbMesas.addItem("Mesa 1 (Ocupada)");
        }
    }

    private void mostrarPedidoDeMesaSeleccionada() {
        String seleccion = (String) vista.cmbMesas.getSelectedItem();
        
        if (seleccion == null || seleccion.equals("Seleccione Mesa")) {
            limpiarTablaYTotal();
            return;
        }

        if (seleccion.contains("Mesa 1") && AlmacenDatos.pedidoTemporal != null) {
            modeloTabla.setRowCount(0);
            for (int i = 0; i < AlmacenDatos.pedidoTemporal.getDetalles().getTamanio(); i++) {
                DetallePedido dp = AlmacenDatos.pedidoTemporal.getDetalles().obtener(i);
                modeloTabla.addRow(new Object[]{
                    dp.getProducto().getNombre(),
                    dp.getCantidad(),
                    "$" + dp.getSubtotal()
                });
            }
            vista.lblTotal.setText(String.valueOf(AlmacenDatos.pedidoTemporal.getTotal()));
        }
    }

    private void cobrar() {
        String seleccion = (String) vista.cmbMesas.getSelectedItem();
        
        //Validaciones Generales
        if (seleccion == null || seleccion.equals("Seleccione Mesa") || AlmacenDatos.pedidoTemporal == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona una mesa con cuenta pendiente.");
            return;
        }

        //Validación de Pago (Solo si es Efectivo)
        if (vista.rbtnEfectivo.isSelected()) {
            try {
                double total = AlmacenDatos.pedidoTemporal.getTotal();
                double recibido = Double.parseDouble(vista.txtRecibido.getText());

                if (recibido < total) {
                    JOptionPane.showMessageDialog(vista, "El dinero recibido es insuficiente.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingresa una cantidad válida en 'Recibido'.");
                return;
            }
        }

        //PROCESAR VENTA
        Venta nuevaVenta = new Venta(AlmacenDatos.pedidoTemporal);
        AlmacenDatos.pilaVentas.push(nuevaVenta);

        //CERRAR MESA
        AlmacenDatos.pedidoTemporal.setPagado(true);
        AlmacenDatos.pedidoTemporal = null;

        JOptionPane.showMessageDialog(vista, "¡Cobro realizado con éxito!\nFolio: " + nuevaVenta.getFolio());

        //LIMPIEZA
        cargarMesasActivas();
        limpiarTablaYTotal();
        vista.txtRecibido.setText("");
        vista.txtCambio.setText("");
        
        // Regenerar prueba
        crearPedidoPrueba();
    }
    
    //DATOS DE PRUEBA
    private void crearPedidoPrueba() {
        if (AlmacenDatos.pedidoTemporal == null) {
            Pedido p = new Pedido(1);
            if (AlmacenDatos.listaProductos.getTamanio() >= 2) {
                p.agregarDetalle(new DetallePedido(AlmacenDatos.listaProductos.obtener(0), 2, "Con todo"));
                p.agregarDetalle(new DetallePedido(AlmacenDatos.listaProductos.obtener(1), 1, "Fria"));
                AlmacenDatos.pedidoTemporal = p;
                System.out.println(">>> Pedido de prueba creado automáticamente en Mesa 1.");
            }
        }
    }

    private void inicializarTabla() {
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Producto");
        modeloTabla.addColumn("Cant.");
        modeloTabla.addColumn("Importe");
        vista.tblDetalles.setModel(modeloTabla);
    }
    
    private void limpiarTablaYTotal() {
        modeloTabla.setRowCount(0);
        vista.lblTotal.setText("0.00");
    }
}