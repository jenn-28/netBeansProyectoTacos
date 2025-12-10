package controlador;

import estructuras.Pila;
import interfaces.Login;
import interfaces.MapMesas;
import interfaces.MenuMesero;
import interfaces.PedidosActivos;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import servicios.SrvUsuario;

public class CtrlMenuMesero implements ActionListener{
    private MenuMesero vista;
    private Pila<JInternalFrame> historial = new Pila<>();

    public CtrlMenuMesero(MenuMesero vista) {
        this.vista = vista;
        if(SrvUsuario.getUsuario() != null)
            vista.lblUsuarioActual.setText(SrvUsuario.getUsuario().getNombre());
        
        // Servicio
        this.vista.getJMenuItemMapaMesas().addActionListener(this);
        this.vista.getJMenuItemPedidosActivos().addActionListener(this); 
        // acerca de
        this.vista.getJMenuItemDesarrolladores().addActionListener(this);

        // Acciones
        // 1. Botón REGRESAR: ACTUALIZADO
        this.vista.getBtnRegresar().addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                regresar();
            }
        });
        // Poner manita al pasar el mouse
        this.vista.getBtnRegresar().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR)); 

        // 2. Botón CANCELAR: ACTUALIZADO
        this.vista.getBtnCancelar().addMouseListener(new MouseAdapter() { 
            @Override
            public void mouseClicked(MouseEvent e) {
                cancelarSesion();
            }
        });
        this.vista.getBtnCancelar().setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // Configuración de ventana
        this.vista.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //ABRIR MAPA DE MESAS
        if (e.getSource() == vista.getJMenuItemMapaMesas()) {
            MapMesas v = new MapMesas();
            // Pasamos el escritorio para que el mapa pueda abrir la comanda ahí mismo
            new CtrlMapMesas(v, vista.escritorio); 
            navegarA(v);
        }

        //VER PEDIDOS ACTIVOS
        if (e.getSource() == vista.getJMenuItemPedidosActivos()) {
            PedidosActivos v = new PedidosActivos();
            new CtrlPedidosActivos(v); // <--- CREAREMOS ESTE CONTROLADOR ABAJO
            navegarA(v);
        }

        //ACERCA DE
        if (e.getSource() == vista.getJMenuItemDesarrolladores()) {
            JOptionPane.showMessageDialog(vista, "Proyecto Gestaco 2025");
        }
    }

    public void navegarA(JInternalFrame nuevaVentana){
        if (!historial.estaVacia()){
            JInternalFrame actual = historial.peek();
            actual.setVisible(false);
        }

        //poner en el desktopPane
        vista.escritorio.add(nuevaVentana);
        //guardar en la pila
        historial.push(nuevaVentana);
        //mostrar
        centrarVentana(nuevaVentana);
        nuevaVentana.setVisible(true);
    }

    private void regresar(){
        if (historial.estaVacia()) return; 

        //Sacamos la ventana actual y la matamos
        JInternalFrame actual = historial.pop();
        actual.dispose(); 

        //Si queda una ventana anterior, la mostramos
        if (!historial.estaVacia()) {
            JInternalFrame anterior = historial.peek();
            anterior.setVisible(true);
            anterior.toFront();
        }
    }

    private void cancelarSesion(){
        int confirm = JOptionPane.showConfirmDialog(vista, "¿Cerrar sesión?");
        if(confirm == JOptionPane.YES_OPTION){
            vista.dispose(); //cerrar el menú admin
            new Login().setVisible(true);
        }
    }

    private void centrarVentana(JInternalFrame frame) {
        int x = (vista.escritorio.getWidth() - frame.getWidth()) / 2;
        int y = (vista.escritorio.getHeight() - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }

    private void mostrarCreditos() {
        String mensaje = "<html><body style='width: 250px; text-align: center;'>"
                + "<h2 style='color: #003366;'> Equipo de Desarrollo </h2>"
                + "<hr>"
                + "<br>"
                + "<b>1. Castro Acosta Jennifer </b><br>"
                + "<span style='color: gray;'>Reg: 24310145</span><br><br>"
                + "<b>2. Contreras Monsivais Cynthia Jimena</b><br>"
                + "<span style='color: gray;'>Reg: 24310144</span><br><br>"
                + "<b>3. Lima Moreno Alanna Mishel</b><br>"
                + "<span style='color: gray;'>Reg: 24310192</span><br><br>"
                + "<b>4. Padilla Martínez Ruy Felipe</b><br>"
                + "<span style='color: gray;'>Reg: 24310136</span><br><br>"
                + "<hr>"
                + "<i>Proyecto Gestaco © 2025</i>"
                + "</body></html>";

        // Mostramos el mensaje
        JOptionPane.showMessageDialog(vista, 
                mensaje, 
                "Acerca de Nosotros", 
                JOptionPane.PLAIN_MESSAGE);
    }
}