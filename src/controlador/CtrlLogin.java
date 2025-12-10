package controlador;

import interfaces.Login;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;
import modelo.AlmacenDatos;
import servicios.SrvUsuario;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author rc
 */
public class CtrlLogin
{
    private Login vista;
    private static final System.Logger LOG = System.getLogger(CtrlLogin.class.getName());

    public CtrlLogin(Login vista)
    {

        vista.jbnIngresar.addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e) {

                var userName = vista.txtUsuario.getText();
                if (userName == null || userName.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Ingresa el usuario");
                    return;
                }


                var passwordChars = vista.txtPassword.getPassword();
                if (passwordChars == null) {
                    JOptionPane.showMessageDialog(vista, "Ingresa la contraseña");
                    return;
                }

                var password = new String(passwordChars);
                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(vista, "Ingresa la contraseña");
                    return;
                }
                    

                var usuariosEncontrados = modelo.AlmacenDatos.listaUsuarios.filtrar(u -> {
                    if (!u.getUsername().equals(userName)) return false;

                    if (!u.getPassword().equals(password)) return false;

                    return true;
                });

                var usuario = usuariosEncontrados.obtener(0);
                if (usuario == null) {
                    JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos");
                    return;
                }

                SrvUsuario.setUsuario(usuario);

                if (SrvUsuario.esAdministrador()) {
                    LOG.log(System.Logger.Level.INFO, "Ingresando como administrador");
                    new interfaces.MenuAdministrador().setVisible(true);
                    vista.dispose();
                } else if (SrvUsuario.esCajero()) {
                    LOG.log(System.Logger.Level.INFO, "Ingresando como cajero");
                    new interfaces.MenuCajero().setVisible(true);
                    vista.dispose();
                } else if (SrvUsuario.esMesero()) {
                    LOG.log(System.Logger.Level.INFO, "Ingresando como mesero");
                    new interfaces.MenuMesero().setVisible(true);
                    vista.dispose();
                }
            }
        });
    }

}
