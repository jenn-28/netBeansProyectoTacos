package controlador;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDesktopPane;
import javax.swing.JToggleButton;

import interfaces.MapMesas;
import interfaces.TomaComand;

public class CtrlMapMesas implements ActionListener {
    private MapMesas vista;
    private JDesktopPane escritorio;

    public CtrlMapMesas(MapMesas mapaMesas, JDesktopPane escritorio){
        vista = mapaMesas; 
        this.escritorio = escritorio;

        for(JToggleButton btn: vista.getBotonesMesas()){
            btn.addActionListener(this);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JToggleButton btn = (JToggleButton) e.getSource();

        String mesa = btn.getText();
        int numeroMesa = Integer.parseInt(mesa.replaceAll("\\D+", ""));

        abrirTomaComanda(numeroMesa);
    }

    public void abrirTomaComanda(int numeroMesa){
        TomaComand comanda = new TomaComand(numeroMesa);

        escritorio.add(comanda);
        comanda.setVisible(true);
        comanda.toFront();
    }

    
    

}
