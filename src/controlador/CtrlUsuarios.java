package controlador;

import interfaces.GestionUsuarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public CtrlUsuarios(GestionUsuarios vista) {
        this.vista = vista;
        
        this.vista.btnGuardar.addActionListener(this);
        this.vista.btnEliminar.addActionListener(this);
        this.vista.btnLimpiar.addActionListener(this);
        this.vista.btnEditar.addActionListener(this);
        
        //configuracion inicial
        
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
        try{
            String nombre = vista.txtNombreTrabajador.getText();
            String user = vista.txtUsuario.getText().trim(); // trim() quita espacios accidentales
            String pass = vista.txtPassword.getText(); 
            String rolSeleccionado = vista.cmbRoles.getSelectedItem().toString();
            
            //Validaciones
            if(nombre.isEmpty() || user.isEmpty() || pass.isEmpty()){
                JOptionPane.showMessageDialog(vista,"Todos los campos son obligatorios");
                return;
            }if (existeUsuario(user)) {
                JOptionPane.showMessageDialog(vista, "El usuario '" + user + "' ya existe. Usa otro.");
                return;
            }
            
            //POLIMORFISMO: Crear usuario (objeto) según el rol 
            Usuario nuevoUsuario;
            
            if(rolSeleccionado.equalsIgnoreCase("ADMINISTRADOR")){
                nuevoUsuario = new Administrador(nombre, user, pass);
            } 
            else if (rolSeleccionado.equalsIgnoreCase("CAJERO")){
                nuevoUsuario = new Cajero(nombre, user, pass);
            }
            else {
                nuevoUsuario = new Mesero(nombre, user, pass);
            }
            
            AlmacenDatos.listaUsuarios.agregar(nuevoUsuario);
            
            JOptionPane.showMessageDialog(vista, "Usuario registrado exitosamente");
            llenarTabla();
            limpiarCampos();
        }catch (Exception ex) {
            JOptionPane.showMessageDialog(vista, "Error al guardar: " + ex.getMessage());
        }
    }
    
    private void eliminarUsuario(){
        String userBusqueda = vista.txtUsuario.getText().trim();
        
        if (userBusqueda.isEmpty()){
            JOptionPane.showMessageDialog(vista, "Escribe el USERNAME (Usuario) que quieres eliminar.");
            return;
        }
        
        //Proteger al super admin de ser borrado
        if (userBusqueda.equalsIgnoreCase(userBusqueda)){
            JOptionPane.showMessageDialog(vista, "ACCESO DENEGADO \nNo puedes eliminar al Super Administrador.", "Seguridad", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        //Quitar comentario cuando se pueda leer un usuario actual
        /*
        //Proteger al usuario actual
        if (AlmacenDatos.usuarioLogueado != null){
            String miUsuarioActual = AlmacenDatos.usuarioLogueado.getUsername();
            
            if (userBusqueda.equalsIgnoreCase(miUsuarioActual)) {
                JOptionPane.showMessageDialog(vista, " No puedes eliminar tu propia cuenta mientras la estás usando.\nCierra sesión y entra con otro Admin para hacerlo.", "Acción Inválida", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        */
        
        boolean encontrado = false;
        
        for (int i = 0; i<AlmacenDatos.listaUsuarios.getTamanio(); i++){
            Usuario u = AlmacenDatos.listaUsuarios.obtener(i);
            
            if(u.getUsername().equalsIgnoreCase(userBusqueda)){
                int confirm = JOptionPane.showConfirmDialog(vista, 
                        "¿Estás seguro de eliminar al usuario " + u.getRol() + ": " + u.getNombre() + "?",
                        "Confirmar Eliminación",
                        JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    AlmacenDatos.listaUsuarios.eliminar(i);
                    JOptionPane.showMessageDialog(vista, "Usuario eliminado correctamente.");
                    encontrado = true;
                    llenarTabla();
                    limpiarCampos();
                }
                break;
            }//if
        }//for
        if (!encontrado) {
            JOptionPane.showMessageDialog(vista, "No se encontró el usuario: " + userBusqueda);
        }
    }
    
    private void editarUsuario(){
        String userBusqueda = vista.txtUsuario.getText().trim();
        
        if(userBusqueda.isEmpty()){
            JOptionPane.showMessageDialog(vista, "Escribe el USERNAME para buscar y editar su contraseña o nombre.");
            return;
        }
        
        if(userBusqueda.equalsIgnoreCase("admin")){
            JOptionPane.showMessageDialog(vista, "El Super Admin no se puede editar desde aquí.");
             return;
        }
        
        boolean encontrado = false;
        for(int i = 0; i < AlmacenDatos.listaUsuarios.getTamanio(); i++){
            Usuario u = AlmacenDatos.listaUsuarios.obtener(i);
            
            if(u.getUsername().equalsIgnoreCase(userBusqueda)){
                u.setNombre(vista.txtNombreTrabajador.getText());
                u.setPassword(vista.txtPassword.getText());
                //el rol no se actualiza, se tiene que borrar y crear otro usuario
                
                JOptionPane.showMessageDialog(vista, "Datos actulaizados");
                encontrado = true;
                llenarTabla();
                limpiarCampos();
                break;
            }
        }
        if(!encontrado) JOptionPane.showMessageDialog(vista, "Usuario no encontrado");
    }
    
    private boolean existeUsuario(String user){
        for (int i = 0; i < AlmacenDatos.listaUsuarios.getTamanio(); i++) {
            if (AlmacenDatos.listaUsuarios.obtener(i).getUsername().equalsIgnoreCase(user)) {
                return true;
            }
        }
        return false;
    }
    
    private void llenarComboRoles() {
        vista.cmbRoles.removeAllItems();
        vista.cmbRoles.addItem("ADMINISTRADOR");
        vista.cmbRoles.addItem("CAJERO");
        vista.cmbRoles.addItem("MESERO");
    }
    
    private void inicializarTabla(){
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Username");
        modeloTabla.addColumn("Rol(Tipo)");
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
    
    private void limpiarCampos() {
        vista.txtNombreTrabajador.setText("");
        vista.txtUsuario.setText("");
        vista.txtPassword.setText("");
        vista.cmbRoles.setSelectedIndex(0);
    }
    
}
