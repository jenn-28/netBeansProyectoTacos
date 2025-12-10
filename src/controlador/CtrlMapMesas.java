package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDesktopPane;
import javax.swing.JToggleButton;
import interfaces.MapMesas;
import interfaces.TomaComand;
import java.awt.Color;
import javax.swing.plaf.basic.BasicToggleButtonUI;

public class CtrlMapMesas implements ActionListener {
    private MapMesas vista;
    private JDesktopPane escritorio;
    private final Color COLOR_LIBRE = new Color(50, 205, 50); 
    private final Color COLOR_OCUPADA = new Color(220, 20, 60);

    public CtrlMapMesas(MapMesas mapaMesas, JDesktopPane escritorio){
        vista = mapaMesas; 
        this.escritorio = escritorio;

        for(JToggleButton btn: vista.getBotonesMesas()){
            btn.addActionListener(this);
            
            // Forzar que el color se pinte
            btn.setUI(new BasicToggleButtonUI());
            btn.setFocusPainted(false);           
            btn.setOpaque(true);
            
            // Pintar estado inicial
            actualizarColorBoton(btn);
        }
        
        actualizarContador();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JToggleButton btn = (JToggleButton) e.getSource();

        //CAMBIO DE COLOR
        actualizarColorBoton(btn);
        
        //ACTUALIZAR CONTADOR
        actualizarContador();

        //ABRIR COMANDA
        if (btn.isSelected()) {
            String mesa = btn.getText();
            try {
                int numeroMesa = Integer.parseInt(mesa.replaceAll("\\D+", ""));
                abrirTomaComanda(numeroMesa);
            } catch (Exception ex) {
                System.out.println("Error al leer mesa: " + ex.getMessage());
            }
        }
    }
    private void actualizarColorBoton(JToggleButton btn) {
        if (btn.isSelected()) {
            btn.setBackground(COLOR_OCUPADA);
            btn.setForeground(Color.WHITE); // Letra blanca para que contraste
            btn.setText(btn.getText().replace("Libre", "Ocupada")); // Opcional: Cambiar texto
        } else {
            btn.setBackground(COLOR_LIBRE);
            btn.setForeground(Color.BLACK);
            btn.setText(btn.getText().replace("Ocupada", "Libre")); // Opcional
        }
    }

    private void actualizarContador() {
        int ocupadas = 0;
        int total = 0;
        
        for (JToggleButton btn : vista.getBotonesMesas()) {
            total++;
            if (btn.isSelected()) {
                ocupadas++;
            }
        }
        
        try {
            vista.lblContador.setText(ocupadas + " / " + total);
        } catch (Exception e) {
            System.out.println("Error: No encontré el lblContador en la vista.");
        }
    }

    public void abrirTomaComanda(int numeroMesa){
        TomaComand v = new TomaComand(numeroMesa);
        new CtrlTomaComanda(v, numeroMesa); 
        
        escritorio.add(v);
        v.setVisible(true);
        v.toFront();
    }

}
