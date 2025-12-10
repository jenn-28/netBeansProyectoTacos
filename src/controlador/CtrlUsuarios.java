package controlador;

import interfaces.GestionUsuarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Administrador;
import modelo.AlmacenDatos;
import modelo.Cajero;
import modelo.Mesero;
import modelo.Usuario;


public class CtrlUsuarios implements ActionListener{
    private GestionUsuarios vista;
    private DefaultTableModel modeloTabla;
    
    //variables para controlar la edición
    private Usuario _usuarioSeleccionado = null;
    private Integer _usuarioSeleccionadoIdx = null;

    public CtrlUsuarios(GestionUsuarios vista) {
        this.vista = vista;
        
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnEditar.addActionListener(this);
        
        // Listener de la Tabla (Para seleccionar)
        this.vista.tableMostrarUsuarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarUsuarioDeTabla();
            }
        });
        
        inicializarTabla();
        llenarTabla();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.btnGuardar) {
            guardarUsuario();
        }
        if (e.getSource() == vista.btnEliminar) {
            eliminarUsuario();
        }
        if (e.getSource() == vista.btnLimpiar) {
            limpiarCampos();
        }
        if (e.getSource() == vista.btnEditar) {
            editarUsuario();
        }
    }
    
    private void guardarUsuario(){
        if (_usuarioSeleccionado != null) {
            JOptionPane.showMessageDialog(vista, "Estás en modo edición. Usa 'Limpiar' para crear uno nuevo.");
            return;
        }

        Usuario nuevoUsuario = crearObjetoDesdeVista();
        if (nuevoUsuario != null) {
            AlmacenDatos.listaUsuarios.agregar(nuevoUsuario);
            JOptionPane.showMessageDialog(vista, "Usuario guardado exitosamente.");
            llenarTabla();
            limpiarCampos();
        }
    }
    
    private void eliminarUsuario(){
        if (_usuarioSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un usuario para eliminar.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Seguro de eliminar a " + _usuarioSeleccionado.getNombre() + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            AlmacenDatos.listaUsuarios.eliminar(_usuarioSeleccionadoIdx);
            
            JOptionPane.showMessageDialog(vista, "Usuario eliminado.");
            llenarTabla();
            limpiarCampos();
        }
    }
    
    private void editarUsuario(){
        if (_usuarioSeleccionado == null || _usuarioSeleccionadoIdx == null) {
            JOptionPane.showMessageDialog(vista, "Selecciona un usuario de la tabla.");
            return;
        }

        Usuario usuarioEditado = crearObjetoDesdeVista();
        if (usuarioEditado != null) {
            // Actualizar directo por índice
            AlmacenDatos.listaUsuarios.actualizar(_usuarioSeleccionadoIdx, usuarioEditado);
            
            JOptionPane.showMessageDialog(vista, "Usuario actualizado correctamente.");
            llenarTabla();
            limpiarCampos();
        }
    }
    
    private boolean existeUsuario(String user){
        for (int i = 0; i < AlmacenDatos.listaUsuarios.getTamanio(); i++) {
            if (AlmacenDatos.listaUsuarios.obtener(i).getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;
    }
    private Usuario crearObjetoDesdeVista() {
        String nombre = vista.txtNombreTrabajador.getText();
        String user = vista.txtUsuario.getText();
        String pass = vista.txtPassword.getText();
        String rol = (String) vista.cmbRoles.getSelectedItem(); // Administrador, Mesero, Cajero

        if (nombre.isEmpty() || user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Llena todos los campos.");
            return null;
        }

        // Crear la instancia correcta según el rol
        Usuario u = null;
        if (rol.equalsIgnoreCase("Administrador")) {
            u = new Administrador(nombre, user, pass);
        } else if (rol.equalsIgnoreCase("Cajero")) {
            u = new Cajero(nombre, user, pass);
        } else {
            u = new Mesero(nombre, user, pass);
        }
        return u;
    }

    private void seleccionarUsuarioDeTabla() {
        int fila = vista.tableMostrarUsuarios.getSelectedRow();
        if (fila != -1) {
            _usuarioSeleccionado = AlmacenDatos.listaUsuarios.obtener(fila);
            _usuarioSeleccionadoIdx = fila;
            
            // Cargar datos a la vista
            vista.txtNombreTrabajador.setText(_usuarioSeleccionado.getNombre());
            vista.txtUsuario.setText(_usuarioSeleccionado.getUsername());
            vista.txtPassword.setText(_usuarioSeleccionado.getPassword());
            
            // Seleccionar el rol en el combo (Comparando texto)
            String rol = _usuarioSeleccionado.getRol(); // Devuelve "ADMINISTRADOR", etc.
            // Ajustamos mayúsculas/minúsculas para que coincida con el combo
            if(rol.equalsIgnoreCase("ADMINISTRADOR")) vista.cmbRoles.setSelectedItem("Administrador");
            else if(rol.equalsIgnoreCase("CAJERO")) vista.cmbRoles.setSelectedItem("Cajero");
            else vista.cmbRoles.setSelectedItem("Mesero");
        }
    }

    private void limpiarCampos() {
        vista.txtNombreTrabajador.setText("");
        vista.txtUsuario.setText("");
        vista.txtPassword.setText("");
        vista.cmbRoles.setSelectedIndex(0);
        
        // Resetear selección
        _usuarioSeleccionado = null;
        _usuarioSeleccionadoIdx = null;
        vista.tableMostrarUsuarios.clearSelection();
    }
    
    private void inicializarTabla(){
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Username");
        modeloTabla.addColumn("Rol");
        vista.tableMostrarUsuarios.setModel(modeloTabla);
    }
    
    private void llenarTabla(){
        modeloTabla.setRowCount(0);
        for (int i=0; i<AlmacenDatos.listaUsuarios.getTamanio(); i++){
            Usuario u = AlmacenDatos.listaUsuarios.obtener(i);
            modeloTabla.addRow(new Object[]{
                u.getNombre(),
                u.getUsername(),
                u.getRol()
            });
        }
    }
}

