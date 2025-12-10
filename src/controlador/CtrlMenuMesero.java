package controlador;

import interfaces.MenuMesero;
import interfaces.MapMesas;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CtrlMenuMesero implements ActionListener {

    private MenuMesero vista;

    public CtrlMenuMesero(MenuMesero vista) {
        this.vista = vista;

        //Listeners 
        this.vista.getJMenuItemMapaMesas().addActionListener(this);
        this.vista.getJMenuItemPedidosActivos().addActionListener(this);
        this.vista.getJMenuItemDesarrolladores().addActionListener(this);

        this.vista.getBtnRegresar().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                regresar();
            }
        });

        this.vista.getBtnCancelar().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cerrar();
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        Object source = e.getSource();

        if (source == vista.getJMenuItemMapaMesas()) {
            System.out.println("Entró a Mapa Mesas");
            abrirMapaMesas();
        }

        if (source == vista.getJMenuItemPedidosActivos()) {
            System.out.println("Entró a Pedidos Activos");
        }

        if (source == vista.getJMenuItemDesarrolladores()) {
            System.out.println("Entró a Desarrolladores");
        }
    }

    private void abrirMapaMesas() {
        vista.getDesktopPane().removeAll();
        vista.getDesktopPane().repaint();

        MapMesas mapa = new MapMesas();
        new CtrlMapMesas(mapa, vista.getDesktopPane());

        vista.getDesktopPane().add(mapa);
        mapa.setVisible(true);
        mapa.toFront();
    }

    private void regresar() {
        System.out.println("Regresar presionado");
        // TODO Agregar a donde regresaria 
    }

    private void cerrar() {
        System.exit(0);
    }
}
