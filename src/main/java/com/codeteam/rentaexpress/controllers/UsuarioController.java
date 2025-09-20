package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Estado;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.models.Usuario;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.codeteam.rentaexpress.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;

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
    public String agregarUsuario(@ModelAttribute("usuario") Usuario usuario) {
        // Valores por defecto
        usuario.setContrasenha("123456"); //  encriptarla con BCrypt
        usuario.setRol(new Rol(2,"Usuario")); // 2 = Usuario normal
        usuario.setEstado(new Estado(1, "Activo")); // 1 = Activo
        usuario.setFechaCreacion(LocalDateTime.now());


        usuarioService.agregarUsuario(usuario);
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
