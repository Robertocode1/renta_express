package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Contactenos;
import com.codeteam.rentaexpress.services.ContactenosService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@RequiredArgsConstructor
@Controller
@RequestMapping("/contactos")
public class ContactenosController {

    private final ContactenosService contactenosService;

    @GetMapping("/contactoView")
    public String contactoView(Model model){
        model.addAttribute("view", "home/contacto_view");
        return "layout/layout";
    }

    @GetMapping("/reporte")
    public String reporte(Model model){
        model.addAttribute("contactos", contactenosService.listarContactos());
        model.addAttribute("view", "home/reportes_view");
        return "layout/layout";
    }

    @PostMapping("/agregarContacto")
    public String agregarContacto(@ModelAttribute("contacto") Contactenos contacto,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {

        // Hay sesión, proceder con la lógica normal
        switch(contactenosService.agregarMensaje(contacto)){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Su mensaje se envio exitosamente");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "El correo es obligatorio.");
                break;
            case 3:
                redirectAttributes.addFlashAttribute("tipo", "warning");
                redirectAttributes.addFlashAttribute("mensaje", "Falto el asunto o emensaje.");
                break;
            case 0:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "Su mensaje no se envio.");
                break;
        }
        return "redirect:/contactos/contactoView";
    }

    @PostMapping("/enviarRespuesta")
    public String enviarRespuesta(
            @RequestParam("destinatario") String destinatario,
            @RequestParam("cuerpoMensaje") String cuerpoMensaje,
            @RequestParam("mensajeId") Integer mensajeId,
            RedirectAttributes redirectAttributes) {
        try {
            switch (contactenosService.ResponderMensaje(destinatario, cuerpoMensaje, mensajeId)){
                case 1:
                    redirectAttributes.addFlashAttribute("tipo", "success");
                    redirectAttributes.addFlashAttribute("mensaje", "Su mensaje se envio exitosamente");
                    break;
                case 0:
                    redirectAttributes.addFlashAttribute("tipo", "danger");
                    redirectAttributes.addFlashAttribute("mensaje", "Su mensaje no se envio");
                break;
            }
        } catch (Exception e) {
            // En caso de error, añade un mensaje de error.
            // Es buena práctica registrar el error también: e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Error al enviar la respuesta. Por favor, inténtelo de nuevo.");
        }
        return "redirect:/contactos/reporte";
    }


}
