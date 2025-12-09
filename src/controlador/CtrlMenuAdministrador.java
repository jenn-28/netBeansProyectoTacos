package controlador;

import estructuras.Pila;
import interfaces.GestionMenu;
import interfaces.GestionProveedores;
import interfaces.GestionUsuarios;
import interfaces.MenuAdministrador;
import interfaces.ReporteMovimientos;
import interfaces.ReporteVentas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JInternalFrame;

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
        /*
        //Acciones
        if(e.getSource() == vista.btnRegresar){
            regresar();
        }
        if(e.getSource() == vista.btnCancelar){
            cancelar();
        }
        
        //Acerca de
        if(e.getSource() == vista.menuDesarolladores){
            desarolladoresd();
        }
*/
    }
    
    public void navegarA(JInternalFrame nuevaVentana){
        if (!historial.estaVacia()){
            JInternalFrame actual = historial.peek();
            actual.setVisible(true);
        }
        
        vista.escritorio.add(nuevaVentana);
        historial.push(nuevaVentana);
    }
}
