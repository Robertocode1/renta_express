package com.codeteam.rentaexpress.services;


import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.codeteam.rentaexpress.repositories.UsuarioRepository;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.repositories.RolRepository;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.repositories.EstadoRepository;
import com.codeteam.rentaexpress.models.Estado;
import org.springframework.util.Assert;

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
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();
    private final EmailService emailService;

    //Funcion para agregar un usuario nuevo
    public int agregarUsuario(Usuario usuario) {
        Assert.notNull(usuario, "El usuario no puede ser nulo.");
        validarUsuario(usuario.getUsuario());
        validarEmail(usuario.getEmail());


        //VALORES POR DEFECTO
        usuario.setRol(new Rol(2,"Usuario")); // 2 = Usuario normal
        usuario.setEstado(new Estado(1, "Activo")); // 1 = Activo
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

    //Funcion para actualizar usuario
    public Usuario actualizarUsuario(Usuario usuario, Integer id) {
        //Validar que los parametros no vengas nulos
        Assert.notNull(usuario, "El usuario no puede ser nulo.");
        Assert.notNull(id, "El id no puede ser nulo.");

        //Validar que existe un usuario con ese id
        Usuario usuarioEx = usuarioRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));

        //Ya qeu si existe usuario hacer las demas validaciones de nulos
        Assert.hasText(usuario.getUsuario(), "El usuario no puede ser nulo.");
        Assert.hasText(usuario.getEmail(), "El email no puede ser nulo.");
        Assert.hasText(usuario.getDocumento(), "El documento no puede ser nulo.");
        Assert.hasText(usuario.getNombre(),  "El nombre no puede ser nulo.");
        Assert.notNull(usuario.getRol(), "El rol no puede ser nulo.");
        Assert.notNull(usuario.getEstado(), "El estado no puede ser nulo.");

        //Validar si ya existe un usuario (solo si cambió el nombre de usuario)
        if (!usuarioEx.getUsuario().equals(usuario.getUsuario())) {
            if (usuarioRepo.existsByUsuario(usuario.getUsuario())) {
                throw new IllegalArgumentException("El nombre de usuario '" + usuario.getUsuario() + "' ya está en uso");
            }
        }

        // Validar email (solo si cambió el email)
        if (!usuarioEx.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            if (usuarioRepo.existsByEmail(usuario.getEmail())) {
                throw new IllegalArgumentException("El email: " + usuario.getEmail() + " ya está asignado a otra cuenta.");
            }
        }

        usuarioEx.setUsuario(usuario.getUsuario());
        usuarioEx.setEmail(usuario.getEmail());
        usuarioEx.setNombre(usuario.getNombre());
        usuarioEx.setRol(usuario.getRol());
        usuarioEx.setEstado(usuario.getEstado());

        return usuarioRepo.save(usuarioEx);

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

    //Buscar usuarios por email
    @Transactional(readOnly = true)
    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        Assert.hasText(email, "El email es obligatorio");
        return usuarioRepo.findByEmail(email);
    }

    //Eliminar usuario por id
    public void eliminarUsuario(Integer id) {
        Assert.notNull(id, "El id no puede ser nulo");
        if(!usuarioRepo.existsById(id)){
            throw new IllegalArgumentException("El usuario no existe");
        }
        usuarioRepo.deleteById(id);
    }

    //Cambiar contrasenha de usuario
    public Usuario cambiarPassword(Integer id, String password) {
        Assert.notNull(id, "El id es obligatorio");
        Assert.hasText(password, "La contrasena es obligatoria");

        if(!usuarioRepo.existsById(id)){
            throw new IllegalArgumentException("El usuario no existe");
        }
        Usuario usuario = usuarioRepo.findById(id).get();
        String contraActual = usuario.getContrasenha();

        /*if(password.length() < 8){
            throw new IllegalArgumentException("La contrasena no puede tener 8 caracteres");
        }*/
        usuario.setContrasenha(password);
        return usuarioRepo.save(usuario);
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

    private static String generarPassword(){
        StringBuilder password = new StringBuilder(8);
        for(int i = 0; i < 8; i++){
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }
        return password.toString();
    }

}
