package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.PublicacionService;
import com.codeteam.rentaexpress.services.UsuarioService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/publicacion")
public class PublicacionController {
    private  final PublicacionService publicacionService;

    @ModelAttribute("publicacion")
    public Publicacion publicacion() {
        return new Publicacion();
    }

    @GetMapping("/")
    public String publicaciones(Model model){
        model.addAttribute("publicaciones", publicacionService.listarPublicaciones());
        //model.addAttribute("usuario", new Usuario()); // importante para los forms
        model.addAttribute("view", "home/publicacion_view");

        return "layout/layout";
    }
}
