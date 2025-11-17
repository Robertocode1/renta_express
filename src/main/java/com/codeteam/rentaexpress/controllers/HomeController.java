package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Categoria;
import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.CaracteristicaService;
import com.codeteam.rentaexpress.services.CategoriaService;
import com.codeteam.rentaexpress.services.PublicacionService;
import com.codeteam.rentaexpress.services.TipoPrecioService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@Controller
@RequestMapping("/")
public class HomeController {

    private final PublicacionService publicacionService;
    private final CategoriaService categoriaService;
    private final TipoPrecioService tipoPrecioService;
    private final CaracteristicaService caracteristicaService;

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

    @GetMapping("/acceso_denegado")
    public String accesoDenegado(Model model){
        model.addAttribute("view", "home/acceso_denegado_view");
        return "layout/layout";
    }

    @GetMapping("/chat")
    public String chat(Model model){
        model.addAttribute("view", "home/chat_view");
        return "layout/layout";
    }

    @GetMapping("/login")
    public String login(Model model){
        return "home/login_view";
    }


}
