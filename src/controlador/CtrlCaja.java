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
    private Pedido pedidoSeleccionado = null;
    
    public CtrlCaja(CajaCobro vista) {
        this.vista = vista;

        //AGRUPAR BOTONES DE PAGO
        ButtonGroup grupoPago = new ButtonGroup();
        grupoPago.add(vista.rbtnEfectivo);
        grupoPago.add(vista.rbtnTarjeta);
        vista.rbtnEfectivo.setSelected(true);

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
        
        for (int i = 0; i < AlmacenDatos.pedidosActivos.getTamanio(); i++) {
            Pedido p = AlmacenDatos.pedidosActivos.obtener(i);
            
            if (!p.isPagado()) {
                vista.cmbMesas.addItem("Mesa " + p.getNumeroMesa());
            }
        }
    }

    private void mostrarPedidoDeMesaSeleccionada() {
        String seleccion = (String) vista.cmbMesas.getSelectedItem();
        
        if (seleccion == null || seleccion.equals("Seleccione Mesa")) {
            limpiarTablaYTotal();
            return;
        }

        // Extraer el número de la mesa del texto "Mesa 5" -> 5
        try {
            int numeroMesa = Integer.parseInt(seleccion.replaceAll("\\D+", "")); // Quitar letras
            
            // Buscar ese pedido en la lista
            pedidoSeleccionado = null;
            for (int i = 0; i < AlmacenDatos.pedidosActivos.getTamanio(); i++) {
                Pedido p = AlmacenDatos.pedidosActivos.obtener(i);
                if (p.getNumeroMesa() == numeroMesa && !p.isPagado()) {
                    pedidoSeleccionado = p;
                    break;
                }
            }
            
            // Si lo encontramos, llenamos la tabla
            if (pedidoSeleccionado != null) {
                llenarTablaConPedido(pedidoSeleccionado);
            }
            
        } catch (Exception e) {
            System.out.println("Error al leer mesa seleccionada: " + e.getMessage());
        }
    }
    
    private void llenarTablaConPedido(Pedido p) {
        modeloTabla.setRowCount(0);
        for (int i = 0; i < p.getDetalles().getTamanio(); i++) {
            DetallePedido dp = p.getDetalles().obtener(i);
            modeloTabla.addRow(new Object[]{
                dp.getProducto().getNombre(),
                dp.getCantidad(),
                "$" + dp.getSubtotal()
            });
        }
        vista.lblTotal.setText(String.valueOf(p.getTotal()));
    }

    private void cobrar() {
        if (pedidoSeleccionado == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona una mesa válida.");
            return;
        }

        // Validación de dinero
        if (vista.rbtnEfectivo.isSelected()) {
            try {
                double total = pedidoSeleccionado.getTotal();
                double recibido = Double.parseDouble(vista.txtRecibido.getText());
                if (recibido < total) {
                    JOptionPane.showMessageDialog(vista, "Dinero insuficiente.");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(vista, "Ingresa cantidad válida.");
                return;
            }
        }

        //CREAR VENTA Y GUARDAR
        String metodo = vista.rbtnTarjeta.isSelected() ? "Tarjeta" : "Efectivo";
        Venta nuevaVenta = new Venta(pedidoSeleccionado, metodo);
        AlmacenDatos.pilaVentas.push(nuevaVenta);

        //LIBERAR PEDIDO
        pedidoSeleccionado.setPagado(true);

        JOptionPane.showMessageDialog(vista, "¡Cobro Exitoso!\nFolio: " + nuevaVenta.getFolio());

        //LIMPIEZA
        cargarMesasActivas(); // Recargar el combo (la mesa cobrada desaparecerá)
        limpiarTablaYTotal();
        vista.txtRecibido.setText("");
        vista.txtCambio.setText("");
        pedidoSeleccionado = null;
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