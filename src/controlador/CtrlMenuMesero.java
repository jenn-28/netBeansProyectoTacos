package controlador;

import interfaces.MenuMesero;
import interfaces.MapMesas;
import estructuras.Pila;
import interfaces.Login;
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
    
    //Guardar las ventas abiertas en orden con una pila
    private Pila<JInternalFrame> historial = new Pila<>();

    public CtrlMenuMesero(MenuMesero vista) {
        this.vista = vista;
        //Servicio
        this.vista.menuMapaMesas.addActionListener(this);
        this.vista.menuPedidos.addActionListener(this);
        
        //acerca de
        this.vista.menuDesarrolladores.addActionListener(this);
        
        //Acciones
        // 1. Botón REGRESAR
        this.vista.btnRegresar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                regresar();
            }
        });
// Poner manita al pasar el mouse
        this.vista.btnRegresar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        // 2. Botón CANCELAR
        this.vista.btnCancelar.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cancelarSesion(); // Lógica de MASTER
            }
        });
        this.vista.btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Configuración de ventana (De MASTER)
        this.vista.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);

        var usuario = SrvUsuario.getUsuario(); // De MASTER
        vista.lblUsuarioActual.setText(usuario.getNombre()); // De MASTER
    }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
        // Caja
        if(e.getSource() == vista.menuMapaMesas){ // Lógica de MASTER (navegarA)
            MapMesas v = new MapMesas();
            navegarA(v);
        }
        if(e.getSource() == vista.menuPedidos){ // Lógica de MASTER (navegarA)
            PedidosActivos v = new PedidosActivos();
            navegarA(v);
        }
        
        // Acerca de
        if(e.getSource() == vista.menuDesarrolladores){ // Lógica de MASTER
            mostrarCreditos();
        }
    }
    
    public void navegarA(JInternalFrame nuevaVentana){
        // Todo este método es de MASTER (basado en la pila historial)
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
        // Todo este método es de MASTER (basado en la pila historial)
        if (historial.estaVacia()) return; //No hay nada a donde regresar

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
        // Todo este método es de MASTER (Cierre de sesión)
        int confirm = JOptionPane.showConfirmDialog(vista, "¿Cerrar sesión?");
        if(confirm == JOptionPane.YES_OPTION){
            vista.dispose(); //cerrar el menú admin
            new Login().setVisible(true);
        }
    }
    
    private void centrarVentana(JInternalFrame frame) {
        // Todo este método es de MASTER (Ayuda visual)
        int x = (vista.escritorio.getWidth() - frame.getWidth()) / 2;
        int y = (vista.escritorio.getHeight() - frame.getHeight()) / 2;
        frame.setLocation(x, y);
    }
    
    private void mostrarCreditos() {
        // Todo este método es de MASTER (Información)
        String mensaje = "<html><body style='width: 250px; text-align: center;'>"
                + "<h2 style='color: #003366;'> Equipo de Desarrollo </h2>"
                // ... contenido HTML de créditos ...
                + "</body></html>";

        // Mostramos el mensaje
        JOptionPane.showMessageDialog(vista, 
                mensaje, 
                "Acerca de Nosotros", 
                JOptionPane.PLAIN_MESSAGE);
    }
}