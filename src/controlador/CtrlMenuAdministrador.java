package controlador;

import estructuras.Pila;
import interfaces.GestionMenu;
import interfaces.GestionProveedores;
import interfaces.GestionUsuarios;
import interfaces.Login;
import interfaces.MenuAdministrador;
import interfaces.ReporteMovimientos;
import interfaces.ReporteVentas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

public class CtrlMenuAdministrador implements ActionListener{
    private MenuAdministrador vista;
    
    //Guardar las ventas abiertas en orden con una pila
    private Pila<JInternalFrame> historial = new Pila<>();

    public CtrlMenuAdministrador(MenuAdministrador vista) {
        this.vista = vista;
        
        //Administración
        this.vista.menuItemGestionUsuarios.addActionListener(this);
        this.vista.menuItemGestionProvedores.addActionListener(this);
        this.vista.menuItemGestionMenus.addActionListener(this);
        
        //Reportes
        this.vista.menuReporteInsumos.addActionListener(this);
        this.vista.menuReporteVentas.addActionListener(this);
        
        //Acerca de 
        this.vista.menuDesarolladores.addActionListener(this);
        
        //Acciones
        
        this.vista.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
    }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
        
        //Administración
        if(e.getSource() == vista.menuItemGestionUsuarios){
            navegarA(new GestionUsuarios());
        }
        if(e.getSource() == vista.menuItemGestionProvedores){
            navegarA(new GestionProveedores());
        }
        if(e.getSource() == vista.menuItemGestionMenus){
            navegarA(new GestionMenu());
        }
        
        //Reportes
        if(e.getSource() == vista.menuReporteVentas){
            navegarA(new ReporteVentas());
        }
        if(e.getSource() == vista.menuReporteInsumos){
            navegarA(new ReporteMovimientos());
        }
        
        //Acciones
        if(e.getSource() == vista.btnRegresar){
            regresar();
        }
        if(e.getSource() == vista.btnCancelar){
            cancelarSesion();
        }
        
        //Acerca de
        if(e.getSource() == vista.menuDesarolladores){
            mostrarCreditos();
        }
    }
    
    public void navegarA(JInternalFrame nuevaVentana){
        if (!historial.estaVacia()){
            JInternalFrame actual = historial.peek();
            actual.setVisible(true);
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
                + "<h2 style='color: #003366;'>✨ Equipo de Desarrollo ✨</h2>"
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
        // 'JOptionPane.PLAIN_MESSAGE' quita el icono de interrogación/info por defecto para que se vea más limpio
        JOptionPane.showMessageDialog(vista, 
                mensaje, 
                "Acerca de Nosotros", 
                JOptionPane.PLAIN_MESSAGE);
    }
}
