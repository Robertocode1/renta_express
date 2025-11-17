package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.CategoriaService;
import com.codeteam.rentaexpress.services.PublicacionService;
import com.codeteam.rentaexpress.services.UsuarioService;
import jakarta.servlet.http.HttpSession;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestHeader;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/publicaciones")
public class PublicacionController {
    private  final PublicacionService publicacionService;

    // Agregar publicacion
    @PostMapping("/agregar")
    public String agregarPublicacion(@ModelAttribute("publicacion") Publicacion publicacion,
                                     @RequestParam(value = "caracteristicas", required = false) List<Integer> caracteristicasIds,
                                     @RequestParam(value = "listaImagenes") MultipartFile[] listaImagenes,
                                     HttpSession session,
                                     RedirectAttributes redirectAttributes,
                                     @RequestHeader(value = "Referer", required = false) String referer)
    {
        // Si no se selecciona ninguna característica, la lista puede ser nula.
        // La inicializamos para evitar errores.
        if (caracteristicasIds == null) {
            caracteristicasIds = new ArrayList<>();
        }
        // Se le envia el array de ids al servicio
        switch(publicacionService.agregarPublicacion(publicacion, caracteristicasIds, listaImagenes, session)){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Publicacion creada exitosamente");
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

        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";
        return "redirect:" + redirectUrl;

    }

    // actualizar publicacion
    @PutMapping("/actualizar/{id}")
    public String actualizarPublicacion(
            @PathVariable("id") Integer publicacionId, // 2. Obtenemos el ID de la URL
            @ModelAttribute("publicacion") Publicacion publicacionForm, // Objeto con datos del form
            @RequestParam(value = "caracteristicas", required = false) List<Integer> caracteristicasIds,
            @RequestParam(value = "listaImagenes") MultipartFile[] listaImagenes,
            @RequestParam(name = "imagenesAEliminar", required = false) List<Long> imagenesAEliminarIds, // 3. ¡Parámetro añadido!
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer)
    {
        publicacionForm.setId(publicacionId);

        // Si no se selecciona ninguna característica, la lista puede ser nula.
        // La inicializamos para evitar errores.
        if (caracteristicasIds == null) {
            caracteristicasIds = new ArrayList<>();
        }

        int resultado = publicacionService.actualizarPublicacion(
                publicacionForm,
                caracteristicasIds,
                listaImagenes,
                imagenesAEliminarIds,
                session
        );

        // Se le envia el array de ids al servicio
        switch(resultado){
            case 1:
                redirectAttributes.addFlashAttribute("tipo", "success");
                redirectAttributes.addFlashAttribute("mensaje", "Publicacion actualizada");
                break;
            case 2:
                redirectAttributes.addFlashAttribute("tipo", "danger");
                redirectAttributes.addFlashAttribute("mensaje", "No se pudo completar la accion.");
                break;
        }
        // Si no existe (es nulo o vacío), usa tu "/home" como plan B.
        String redirectUrl = (referer != null && !referer.isEmpty()) ? referer : "/home";
        return "redirect:" + redirectUrl;

    }

    //listar por Id para vistas
    @GetMapping("/publicacionView")
    public String publicacionView(@RequestParam("id") Integer id, Model model){
        Publicacion publicacion = publicacionService.listarPublicacionPorId(id);
        if (publicacion != null) {
            publicacionService.actualizarClickPublicacion(publicacion, id);
        }
        model.addAttribute("publicacionPorId", publicacion);
        model.addAttribute("view", "home/publicacion_view");
        return "layout/layout";
    }

    // Endpoint para devolver datos de una publicación en formato JSON (para editar)
    @GetMapping("/publicacionPorId/{publicacionId}")
    public ResponseEntity<Publicacion> obtenerPublicacionPorId(@PathVariable Integer publicacionId) {
        ResponseEntity<Publicacion> respuestaDeExito = publicacionService.listarPublicacionAjax(publicacionId);

        return respuestaDeExito;
    }
}
