package com.codeteam.rentaexpress.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/")
public class HomeController {
    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("view", "home/home_view");
        model.addAttribute("tipo", "success");
        model.addAttribute("mensaje", "Agregado con exito");

        return "layout/layout";
    }


}
