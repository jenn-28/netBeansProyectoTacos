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
    private MapMesas mapaMesas;
    private CtrlMapMesas ctrlMapa;
    private String usuario;

    //Guardar las ventas abiertas en orden con una pila
    private Pila<JInternalFrame> historial = new Pila<>();

    public CtrlMenuMesero(MenuMesero vista) {
        this.vista = vista;
        usuario = SrvUsuario.getUsuario().toString();
        vista.lblUsuarioActual.setText(usuario);
        
        // Servicio: ACTUALIZADOS
        this.vista.getJMenuItemMapaMesas().addActionListener(this);
        this.vista.getJMenuItemPedidosActivos().addActionListener(this); 

        // acerca de: ACTUALIZADO
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
        Object source = e.getSource(); // Usamos e.getSource() directamente
        
        // Caja: ACTUALIZADOS
        if(source == vista.getJMenuItemMapaMesas()){
            if(mapaMesas == null){
                mapaMesas = new MapMesas(); //Crea el JInternal Frame
                ctrlMapa = new CtrlMapMesas(mapaMesas, vista.escritorio); //conecta lo botones
            }
            navegarA(mapaMesas);
        }
        if(source == vista.getJMenuItemPedidosActivos()){
            PedidosActivos v = new PedidosActivos();
            navegarA(v);
        }

        // Acerca de: ACTUALIZADO
        if(source == vista.getJMenuItemDesarrolladores()){
            mostrarCreditos();
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