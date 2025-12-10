package servicios;

import modelo.Administrador;
import modelo.Cajero;
import modelo.Mesero;
import modelo.Usuario;

/**
 *
 * @author rc
 */
public class SrvUsuario
{
    private static Usuario _usuario = null;

    public static Usuario getUsuario()
    {
        return _usuario;
    }

    public static void setUsuario(Usuario _usuario)
    {
        SrvUsuario._usuario = _usuario;
    }



    public static boolean esMesero()
    {
        return _usuario != null && _usuario instanceof Mesero;
    }

    public static boolean esCajero()
    {
        return _usuario != null && _usuario instanceof Cajero;
    }

    public static boolean esAdministrador()
    {
        return _usuario != null && _usuario instanceof Administrador;
    }

}
