package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.PublicacionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private  final PublicacionService publicacionService;

    @ModelAttribute("publicacion")
    public Publicacion publicacion() { return new Publicacion(); }

    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }

    @GetMapping({"/", "/home"})
    public String home(Model model){
        model.addAttribute("publicaciones", publicacionService.listarPublicaciones());
        model.addAttribute("view", "home/home_view");
        return "layout/layout";
//        // Verificar si el filtro solicitó mostrar el modal
//        if (request.getAttribute("showtoast") != null) {
//            model.addAttribute("tipo", "warning");
//            model.addAttribute("mensaje", "Debe iniciar sesión para continuar");
//        }
        //model.addAttribute("tipo", "success");
        //model.addAttribute("mensaje", "Agregado con exito");
    }

    @GetMapping("/publicacion")
    public String publicacion(Model model){
        model.addAttribute("view", "home/publicacion_view");
        return "layout/layout";
    }

    @GetMapping("/acceso_denegado")
    public String accesoDenegado(Model model){
        return "home/acceso_denegado_view";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "home/login_view";
    }


}
