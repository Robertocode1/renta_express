package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Estado;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.models.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.codeteam.rentaexpress.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
public class UsuarioController {

    private  final UsuarioService usuarioService;
    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }

    @GetMapping("/usuarios")
    public  String usuarios(Model model){
        model.addAttribute("usuarios", usuarioService.listarUsuarios());
        //model.addAttribute("usuario", new Usuario()); // importante para los forms
        model.addAttribute("view", "usuarios/usuarios_view");

        return "admin/admin_view";
    }

    // Agregar usuario
    @PostMapping("/usuario/agregarUsuario")
    public String agregarUsuario(@ModelAttribute("usuario") Usuario usuario, RedirectAttributes redirectAttributes) {

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

        return "redirect:/usuarios"; // vuelve al listado
    }

    // Editar usuario
    @PostMapping("/usuario/editar")
    public String editarUsuario(@ModelAttribute Usuario usuario) {
        //usuarioService.actualizar(usuario);
        return "redirect:/usuarios";
    }

    //Eliminar usuario
    @PostMapping("/usuario/eliminar")
    public String eliminarUsuario(@RequestParam("id") Integer id) {
        //usuarioService.eliminar(id);
        return "redirect:/usuarios";
    }

}
