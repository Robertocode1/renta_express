package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Estado;
import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.ControlIntentosService;
import com.codeteam.rentaexpress.services.PublicacionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.codeteam.rentaexpress.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestHeader;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private  final UsuarioService usuarioService;
    private  final PublicacionService publicacionService;
    private  final ControlIntentosService controlIntentosService;

    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }

    //Listar usuarios
    @GetMapping("listar")
    public String usuarios(Model model, HttpSession session){
        Usuario usuarioActual = (Usuario) session.getAttribute("usuario");
        if (usuarioActual == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        model.addAttribute("idUsuarioLogueado", usuarioActual.getId());
        model.addAttribute("view", "usuarios/usuarios_view");

        return "layout/layout";
    }

    //Vista perfil publico
    @GetMapping("/perfil_publico_view")
    public String perfil_publico_view(@RequestParam("id") Integer id, Model model){
        Optional<Usuario> user = usuarioService.buscarUsuarioPorId(id);
        if (user == null) {
            return "error/404"; // o redirigir
        }
        model.addAttribute("user", user.get());
        model.addAttribute("listaPublicaciones", publicacionService.usuarioPublicacionPorId(id));
        model.addAttribute("view", "usuarios/perfil_publico_view");
        return "layout/layout";
    }

    //Vista perfil privado
    @GetMapping("/perfil_privado_view")
    public String perfil_privado_view(@RequestParam("id") Integer id, Model model){
        Optional<Usuario> user = usuarioService.buscarUsuarioPorId(id);
        if (user == null) {
            return "error/404"; // o redirigir
        }
        model.addAttribute("user", user.get());
        model.addAttribute("listaPublicaciones", publicacionService.usuarioPublicacionPorId(id));
        model.addAttribute("view", "usuarios/perfil_privado_view");
        return "layout/layout";
    }

    // Agregar usuario
    @PostMapping("/agregarUsuario")
    public String agregarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                 HttpSession session,
                                 @RequestParam(value = "fotoUsuario") MultipartFile fotoUsuario,
                                 RedirectAttributes redirectAttributes,
                                 @RequestHeader(value = "Referer", required = false) String referer) {

        // Hay sesión, proceder con la lógica normal
        switch(usuarioService.agregarUsuario(usuario, fotoUsuario)){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente");
                break;
            case 3:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente");
                break;
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario creado exitosamente");
                break;
        }

        // No hay sesión, redirigir al home
        if (session.getAttribute("usuario") == null) {
            return "redirect:/";
        }
        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";
        return "redirect:" + redirectUrl;
    }

    //Login
    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        RedirectAttributes redirectAttributes,
                        @RequestHeader(value = "Referer", required = false) String referer,
                        HttpSession session) {
        Usuario user = usuarioService.autenticarUsuario(username, password);
        if(user != null){
            session.setAttribute("usuario", user);
            session.setAttribute("id", user.getId());
            session.setAttribute("rol", user.getRol());
            redirectAttributes.addFlashAttribute("tipo", "success");
            redirectAttributes.addFlashAttribute("mensaje", "Bienvenido, " + user.getNombre());

            String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";
            return "redirect:" + redirectUrl;
        }
        else{
            redirectAttributes.addFlashAttribute("tipo", "warning");
            redirectAttributes.addFlashAttribute("mensaje", "Usuario o contrasenha incorrectos");
            return "redirect:/home";
        }
    }

    //Cerrar sesion
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }

    //Obtener usuario por id para editar
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> obtenerUsuarioParaEditar(@PathVariable Integer id) {

        Optional<Usuario> usuarioOptional = usuarioService.buscarUsuarioPorId(id);

        if (usuarioOptional.isPresent()) {
            Usuario usuarioEncontrado = usuarioOptional.get();

            // Respuesta HTTP 200 OK, poniendo el usuario encontrado en el cuerpo.
            ResponseEntity<Usuario> respuestaDeExito = ResponseEntity.ok(usuarioEncontrado);

            // Devolver la respuesta de éxito.
            return respuestaDeExito;

        } else {
            // Crear una respuesta HTTP 404 Not Found (sin cuerpo).
            ResponseEntity<Usuario> respuestaDeError = ResponseEntity.notFound().build();

            // Devolver la respuesta de error.
            return respuestaDeError;
        }
    }

    //verificar contraseña
    @PostMapping("/verificarPassword")
    public ResponseEntity<?> verificarPassword(@RequestParam("id") Integer id,
                                               @RequestParam("password") String password) {
        boolean esValida = usuarioService.verificarPasswordActual(id, password);

        if (esValida) {
            return ResponseEntity.ok("Contraseña válida");
        } else {
            return ResponseEntity.badRequest().body("La contraseña actual es incorrecta.");
        }
    }

    //Solicitud password por correo
    @ResponseBody
    @PostMapping("/reset_password_correo")
    public ResponseEntity<Map<String, String>> handleResetPassword(@RequestParam("resetEmail") String resetEmail) {

        Map<String, String> responseBody;
        if (resetEmail == null || !resetEmail.contains("@")) {
            responseBody = Collections.singletonMap("message", "Correo electrónico inválido.");
            return ResponseEntity.badRequest()
                    .body(responseBody);
        }
        if (!controlIntentosService.estaPermitido(resetEmail)) {
            responseBody = Collections.singletonMap("message", "Demasiados intentos. Por favor, espera 5 minutos.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(responseBody);
        }

        int resultado = usuarioService.procesarSolicitudPorEmail(resetEmail);


        switch (resultado) {

            case 1: // ÉXITO
                responseBody = Collections.singletonMap("message", "Contraseña restablecida. Revisa tu correo.");
                return ResponseEntity.ok(responseBody); // 200 OK

            case 0: // NO ENCONTRADO
                responseBody = Collections.singletonMap("message", "No se encontró una cuenta con ese correo.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody); // 404

            case 2: // DESACTIVADO
                responseBody = Collections.singletonMap("message", "Esa cuenta de usuario está desactivada.");
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(responseBody); // 403

            case 3: // ERROR AL GUARDAR
            default: // CUALQUIER OTRO ERROR
                responseBody = Collections.singletonMap("message", "Ocurrió un error inesperado al actualizar.");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody); // 500
        }
    }

    // ¡@PutMapping, no @PostMapping!
    @PutMapping("/actualizar/{id}")
    public String actualizarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                    @PathVariable Integer id,
                                    @RequestParam(value = "fotoUsuario") MultipartFile fotoUsuario,
                                    @RequestHeader(value = "Referer", required = false) String referer,
                                    RedirectAttributes redirectAttributes) {

        // Lógica de mensajes CLARA
        int resultado = usuarioService.actualizarUsuario(usuario, id, fotoUsuario);

        switch(resultado){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Usuario actualizado exitosamente");
                break;
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "Error: El nombre de usuario ya existe.");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "Error: El email ya está en uso.");
                break;
            default:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "Ocurrió un error inesperado al actualizar.");
                break;
        }
        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";
        return "redirect:" + redirectUrl;
    }

    //Eliminar usuario
    @PostMapping("/eliminar")
        public String eliminarUsuario(@RequestParam Integer eliminarUserId, RedirectAttributes redirectAttributes) {

        switch(usuarioService.eliminarUsuario(eliminarUserId)){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "warning");
                redirectAttributes.addFlashAttribute("mensaje", "Esta cuenta se ha desactivado");
                break;
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "La operacion no se realizo.");
                break;
        }
        return "redirect:/usuario";
    }

    //Cambiar contrasenia
    @PostMapping("/cambiarPassword")
    public String cambiarPassword(@RequestParam("cambiarPassId") Integer id,
                                    @RequestParam("contrasenaActual") String contrasenaActual,
                                    @RequestParam("nuevaContrasena") String nuevaContrasena,
                                    @RequestParam("confirmarContrasena") String confirmarContrasena,
                                    RedirectAttributes redirectAttributes,
                                    @RequestHeader(value = "Referer", required = false) String referer)
    {

        switch(usuarioService.cambiarPassword(id, contrasenaActual, nuevaContrasena, confirmarContrasena)){
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "warning");
                redirectAttributes.addFlashAttribute("mensaje", "La nueva contraseña es la misma que la actual");
                break;
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada exitosamente");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "Las contraseñas no coinciden!");
                break;
            case 3:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "La contraseña actual es incorrecta.");
                break;
            case 4:
                redirectAttributes.addFlashAttribute("tipo", "secondary");
                redirectAttributes.addFlashAttribute("mensaje", "La contraseña no se actualizo.");
                break;

        }
        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";

        return "redirect:" + redirectUrl;
    }

    //Resetear password
    @PostMapping("/reset_password")
    public String reset_password(@RequestParam("resetUserId") Integer id,
                                  RedirectAttributes redirectAttributes,
                                  @RequestHeader(value = "Referer", required = false) String referer)
    {

        switch(usuarioService.resetPassword(id)){
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "warning");
                redirectAttributes.addFlashAttribute("mensaje", "Error al encontrar el usuario");
                break;
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Contraseña actualizada exitosamente");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "warning");
                redirectAttributes.addFlashAttribute("mensaje", "El usuario no esta activo");
                break;
            case 3:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "No se completo la operacion.");
                break;

        }
        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";

        return "redirect:" + redirectUrl;
    }

}
