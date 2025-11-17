package com.codeteam.rentaexpress.services;

import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codeteam.rentaexpress.repositories.UsuarioRepository;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.repositories.RolRepository;
import com.codeteam.rentaexpress.repositories.EstadoRepository;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.security.SecureRandom;


@Service
@Transactional
@RequiredArgsConstructor
public class UsuarioService {
    private final EstadoRepository estadoRepo;
    private final RolRepository rolRepo;
    private final UsuarioRepository usuarioRepo;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final EmailService emailService;
    private final FileUpLoadService fileUpLoadService;

    //Funcion para agregar un usuario nuevo
    public int agregarUsuario(Usuario usuario, MultipartFile fotoUsuario) {
        Assert.notNull(usuario, "El usuario no puede ser nulo.");
        validarUsuario(usuario.getUsuario());
        validarEmail(usuario.getEmail());

        //Para la foto
        String rutaFoto = fileUpLoadService.saveImage(fotoUsuario, usuario.getUsuario(), "usuarios");
        if (rutaFoto != null) {
            usuario.setFoto(rutaFoto);
        }

        //VALORES POR DEFECTO
        if(usuario.getRol() == null){
            usuario.setRol(rolRepo.findById(2).orElseThrow()); // 2 = Usuario normal
        }
        usuario.setEstado(estadoRepo.findById(1).orElseThrow()); // 1 = Activo
        usuario.setFechaCreacion(LocalDateTime.now());

        String password = generarPassword();
        emailService.sendEmail(usuario.getEmail(), "Cuenta creada", "su contrasenha es: " + password);

        //encriptar contrasenha

        usuario.setContrasenha(BCrypt.hashpw(password, BCrypt.gensalt()));

        try {
            usuarioRepo.save(usuario);
            return 1;
        }
        catch (Exception e){
            return 0;
        }
    }

    //Funcion para autenticar usuario
    public  Usuario autenticarUsuario(String usuario, String contrasenha){
        Optional<Usuario> usuarioOptional = usuarioRepo.findByUsuario(usuario);
        if (usuarioOptional.isPresent() && BCrypt.checkpw(contrasenha, usuarioOptional.get().getContrasenha()) && usuarioOptional.get().getEstado().getId() == 1)
        {
            return usuarioOptional.get();
        }
        return null;
    }

    //Funcion para actualizar usuario
    public int actualizarUsuario(Usuario usuario, Integer id , MultipartFile fotoUsuario) {
        Assert.notNull(usuario, "El usuario no puede ser nulo.");
        Assert.notNull(id, "El id no puede ser nulo.");

        Usuario usuarioEx = usuarioRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));

        //Ya qeu si existe usuario hacer las demas validaciones de nulos
        Assert.hasText(usuario.getUsuario(), "El usuario no puede ser nulo.");
        Assert.hasText(usuario.getEmail(), "El email no puede ser nulo.");
        Assert.hasText(usuario.getDocumento(), "El documento no puede ser nulo.");
        Assert.hasText(usuario.getNombre(),  "El nombre no puede ser nulo.");

        //Validar si ya existe un usuario (solo si cambió el nombre de usuario)
        if (!usuarioEx.getUsuario().equals(usuario.getUsuario())) {
            if (usuarioRepo.existsByUsuario(usuario.getUsuario())) {
                //throw new IllegalArgumentException("El nombre de usuario '" + usuario.getUsuario() + "' ya está en uso");
                return 0; // El usuario ya existe
            }
        }
        // Validar email (solo si cambió el email)
        if (!usuarioEx.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepo.existsByEmail(usuario.getEmail())) {
                //throw new IllegalArgumentException("El email: " + usuario.getEmail() + " ya está asignado a otra cuenta.");
                return 2; // El email ya está en uso
            }
        }
        // LÓGICA DE FOTOS
        String fotoAntigua = usuarioEx.getFoto();
        String rutaFotoNueva = null;

        if (fotoUsuario != null && !fotoUsuario.isEmpty()) {
            rutaFotoNueva = fileUpLoadService.saveImage(fotoUsuario, usuario.getUsuario(), "usuarios");
            if (rutaFotoNueva != null) {
                usuarioEx.setFoto(rutaFotoNueva);
            }
        }
        System.out.println("Rol es" + usuarioEx.getRol().getNombre());
        usuarioEx.setUsuario(usuario.getUsuario());
        usuarioEx.setEmail(usuario.getEmail());
        usuarioEx.setNombre(usuario.getNombre());
        if(usuario.getRol() != null){
            usuarioEx.setRol(rolRepo.findById(usuario.getRol().getId()).orElseThrow()); // 2 = Usuario normal
        }

        try {
            usuarioRepo.save(usuarioEx);
            if (rutaFotoNueva != null && fotoAntigua != null && !fotoAntigua.contains("default_user_img.jpg")) {
                try {
                    fileUpLoadService.deleteImage(fotoAntigua);
                } catch (Exception e) {
                    System.err.println("No se pudo borrar el archivo antiguo: " + fotoAntigua);
                }
            }
            return 1; // El usuario se actualizó correctamente
        }
        catch (Exception e){
            return 0; // El usuario no se actualizó.
        }
    }

    //Listar todos los usuarios
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    //Listar usuarios activos
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepo.findByEstadoNombre("activo");
    }

    //Buscar usuarios por id
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorId(Integer id) {
        Assert.notNull(id, "El id no puede ser nulo");
        try {
            return usuarioRepo.findById(id);
        }
        catch (Exception e){
            return Optional.empty();
        }
    }

    //Buscar usuarios por email
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        Assert.hasText(email, "El email es obligatorio");
        return usuarioRepo.findByEmail(email);
    }

    //Eliminar usuario por id
    public int eliminarUsuario(Integer id) {
        Assert.notNull(id, "El id no puede ser nulo");
        if(!usuarioRepo.existsById(id)){
            throw new IllegalArgumentException("El usuario no existe");
        }
        try{
            Usuario usuarioEx = usuarioRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
            usuarioEx.setEstado(estadoRepo.findById(2).orElseThrow()); // 1 = Inactivo
            usuarioRepo.save(usuarioEx);
            return 1; // Cuenta desactivada
        }catch (Exception e){
            return 0; // Cuenta no desactivada
        }
    }

    //Verificar contraseña actual
    public boolean verificarPasswordActual(Integer id, String passwordPlana) {

        Usuario usuario = usuarioRepo.findById(id).orElse(null);

        if (usuario == null) {
            return false;
        }
        return BCrypt.checkpw(passwordPlana, usuario.getContrasenha());
    }

    // Cambiar contraseña de usuario (con validación segura)
    public int cambiarPassword(Integer id, String contrasenaActual,
                               String nuevaContrasena, String confirmarContrasena) {
        Assert.notNull(id, "El id no puede ser nulo");
        Assert.hasText(contrasenaActual, "La contraseña actual no puede estar vacía");
        Assert.hasText(nuevaContrasena, "La nueva contraseña no puede estar vacía");

        if (!usuarioRepo.existsById(id)) {
            throw new IllegalArgumentException("El usuario no existe");
        }

        Usuario usuario = usuarioRepo.findById(id).orElseThrow();

        // 1. Validar que la contraseña actual es correcta
        if (!BCrypt.checkpw(contrasenaActual, usuario.getContrasenha())) {
            return 3; // La contraseña actual es incorrecta
        }

        // validar que las contrasenas nuevas coincidan
        if (!nuevaContrasena.equals(confirmarContrasena)) {
            return 2; // Las contraseñas no coinciden
        }

        // 2. Validar que la nueva contraseña no sea igual a la actual (en texto claro)
        if (BCrypt.checkpw(nuevaContrasena, usuario.getContrasenha())) {
            return 0; // La nueva contraseña es la misma que la actual
        }

        // 3. Validar longitud mínima
//        if (newPassword.length() < 8) {
//            throw new IllegalArgumentException("La nueva contraseña debe tener al menos 8 caracteres");
//        }

        // 4. Encriptar la nueva contraseña
        String nuevaContrasenhaEncriptada = BCrypt.hashpw(confirmarContrasena, BCrypt.gensalt());
        usuario.setContrasenha(nuevaContrasenhaEncriptada);
        try {
            usuarioRepo.save(usuario);
            return 1; // La contraseña se actualizó correctamente
        } catch (Exception e){
            return 4; // La contraseña no se actualizó.
        }
    }

    // reset contraseña de usuario
    public int resetPassword(Integer id) {
        Assert.notNull(id, "El id no puede ser nulo");

        if (!usuarioRepo.existsById(id)) {
            return 0; //El usuario no existe
        }

        Usuario usuario = usuarioRepo.findById(id).orElseThrow();
        if(usuario.getEstado().getId() != 1){
            return 2; //Usuario desactivado
        }

        String password = generarPassword();
        emailService.sendEmail(usuario.getEmail(), "Restablecer contraseña", "Hola, " + usuario.getUsuario() + " su contraseña es: " + password);

        usuario.setContrasenha(BCrypt.hashpw(password, BCrypt.gensalt()));
        try {
            usuarioRepo.save(usuario);
            return 1; // La contraseña se actualizó correctamente
        } catch (Exception e){
            return 3; // La contraseña no se actualizó.
        }
    }

    //solicitar password por email
    @Transactional
    public int procesarSolicitudPorEmail(String email) {
        Optional<Usuario> optUsuario = usuarioRepo.findByEmail(email);
        if (!optUsuario.isPresent()) {
            return 0; // 0 = Usuario no existe
        }
        Usuario usuario = optUsuario.get();
        return this.resetPassword(usuario.getId());
    }

    //METODOS INTERNOS PARA VALIDACIONES

    //Funcion para validar usuario
    private void validarUsuario(String usuario) {
        Assert.hasText(usuario, "El nombre de usuario no puede estar vacio");
        if(usuarioRepo.findByUsuario(usuario).isPresent()){
            throw new IllegalArgumentException("El usuario: " + usuario + " ya existe");
        }
    }

    //Funcion para validar si existe el rol por ID
    private void validarRol(Integer id) {
        Assert.notNull(id, "El id del rol no puede ser nulo");
        if(!rolRepo.existsById(id)){
            throw new IllegalArgumentException("El rol no existe");
        }
    }

    //Funcion para validar estado por ID
    private void validarEstado(Integer id) {
        Assert.notNull(id, "El id del estado no puede ser nulo");
        if(!estadoRepo.existsById(id)){
            throw new IllegalArgumentException("El estado no existe");
        }
    }

    //Funcion para validar email
    private void validarEmail(String email) {
        Assert.hasText(email, "El email es obligatorio");
        if(usuarioRepo.findByEmail(email).isPresent()){
            throw new IllegalArgumentException("El email: " + email + " ya existe");
        }
    }

    //Generar contrasenas
    private static String generarPassword(){
        StringBuilder password = new StringBuilder(8);
        for(int i = 0; i < 8; i++){
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

}
