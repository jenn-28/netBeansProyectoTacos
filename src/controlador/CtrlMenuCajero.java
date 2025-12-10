package controlador;

import estructuras.Pila;
import interfaces.CajaCobro;
import interfaces.CorteCaja;
import interfaces.Inventario;
import interfaces.Login;
import interfaces.MenuCajero;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import modelo.AlmacenDatos;
import servicios.SrvUsuario;

public class CtrlMenuCajero implements ActionListener{
    private MenuCajero vista;
    private String usuario;
    
    //Guardar las ventas abiertas en orden con una pila
    private Pila<JInternalFrame> historial = new Pila<>();

    public CtrlMenuCajero(MenuCajero vista) {
        this.vista = vista;
        usuario = SrvUsuario.getUsuario().toString();
        vista.lblUsuarioActual.setText(usuario);
        
        //CAJA
        this.vista.menuCorteCaja.addActionListener(this);
        this.vista.menuCobrarMesa.addActionListener(this);
        
        //INSUMOS
        this.vista.menuGestionInsumos.addActionListener(this);
        
        //acerca de
        this.vista.menuAcercade.addActionListener(this);
        
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
                cancelarSesion();
            }
        });
        this.vista.btnCancelar.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Configuración de ventana
        this.vista.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        
        var usuario = SrvUsuario.getUsuario();
        vista.lblUsuarioActual.setText(usuario.getNombre());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        // 1. COBRAR MESA (Caja)
        if(e.getSource() == vista.menuCobrarMesa){
            CajaCobro v = new CajaCobro();    
            new controlador.CtrlCaja(v); 
            navegarA(v);
        }
        
        // 2. CORTE DE CAJA (Reporte)
        if(e.getSource() == vista.menuCorteCaja){
            CorteCaja v = new CorteCaja();    
            new controlador.CtrlCorteCaja(v); 
            navegarA(v);
        }
        
        // 3. INSUMOS (Inventario)
        if(e.getSource() == vista.menuGestionInsumos){
            Inventario v = new Inventario();    
            new controlador.CtrlInsumos(v); 
            navegarA(v);
        }
        
        // 4. ACERCA DE
        if(e.getSource() == vista.menuAcercade){
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
        // 'JOptionPane.PLAIN_MESSAGE' quita el icono de interrogación/info por defecto para que se vea más limpio
        JOptionPane.showMessageDialog(vista, 
                mensaje, 
                "Acerca de Nosotros", 
                JOptionPane.PLAIN_MESSAGE);
    }
}