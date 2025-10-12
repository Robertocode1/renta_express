package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Estado;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.models.Usuario;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.codeteam.rentaexpress.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/usuario")
public class UsuarioController {

    private  final UsuarioService usuarioService;
    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }

    @GetMapping()
    public String usuarios(Model model){
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        //model.addAttribute("usuario", new Usuario()); // importante para los forms
        model.addAttribute("view", "usuarios/usuarios_view");

        return "layout/layout";
    }

    // Agregar usuario
    @PostMapping("/agregarUsuario")
    public String agregarUsuario(@ModelAttribute("usuario") Usuario usuario,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {


        // Hay sesión, proceder con la lógica normal
        switch(usuarioService.agregarUsuario(usuario)){
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
        return "redirect:/usuario"; // vuelve al listado
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, RedirectAttributes redirectAttributes, HttpSession session) {
        Usuario user = usuarioService.autenticarUsuario(username, password);
        if(user != null){
            session.setAttribute("usuario", user);
            return "redirect:/home";
        }
        else{
            redirectAttributes.addFlashAttribute("tipo", "warning");
            redirectAttributes.addFlashAttribute("mensaje", "Usuario o contrasenha incorrectos");
            return "redirect:/login";
        }
    }

    //Cerrar sesion
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
    // Editar usuario
    @PostMapping("/usuario/editar")
    public String editarUsuario(@ModelAttribute Usuario usuario) {
        //usuarioService.actualizar(usuario);
        return "redirect:/usuarios";
    }

    //Eliminar usuario
    @PostMapping("/eliminar")
    public String eliminarUsuario(@RequestParam("id") Integer id) {
        //usuarioService.eliminar(id);
        return "redirect:/usuario";
    }

}
