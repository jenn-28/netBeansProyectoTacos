package proyectotaqueria;

import java.awt.Image;
import java.awt.Toolkit;

public class ProyectoTaqueria {

   public static Image getAppIcon() {
        return Toolkit.getDefaultToolkit().getImage(
                ProyectoTaqueria.class.getResource("/imagenes/icon.png")
        );
    }

    public static void main(String[] args) {
        // Aquí solo lanzas tu ventana principal
        new interfaces.Login().setVisible(true);
    }
    
}
