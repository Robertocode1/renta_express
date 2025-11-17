package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.*;
import com.codeteam.rentaexpress.services.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;

@ControllerAdvice
public class GlobalController {
    
    @Autowired
    private CategoriaService categoriaService; // Inyectamos el nuevo servicio

    @Autowired
    private CaracteristicaService caracteristicaService; // Inyectamos el nuevo servicio

    @Autowired
    private TipoPrecioService tipoPrecioService; // Inyectamos el nuevo servicio

    @Autowired
    private DepartamentoService departamentoService;

    @Autowired
    private MunicipioService municipioService;

    @Autowired
    private RolService rolService;

    @Autowired
    private ContactenosService contactenosService;

    @ModelAttribute("usuario")
    public Usuario usuario() {
        return new Usuario();
    }

    @ModelAttribute("publicacion")
    public Publicacion publicacion() {
        return new Publicacion();
    }

    @ModelAttribute("contacto")
    public Contactenos contactenos() {
        return new Contactenos();
    }


    // Este método se ejecutará para CADA petición.
    // La lista de categorías estará disponible en el modelo bajo el nombre "categorias".
    @ModelAttribute("categorias")
    public List<Categoria> addCategoriasToModel() {
        return categoriaService.listarCategorias();
    }

    @ModelAttribute("caracteristicas")
    public List<Caracteristica> addCaracteristicasToModel() {
        return caracteristicaService.listarCaracteristicas();
    }

    @ModelAttribute("tipos")
    public List<TipoPrecio> addTiposToModel() {
        return tipoPrecioService.listarTipos();
    }

    @ModelAttribute("departamentos")
    public List<Departamento> addDepartamentosToModel() {
        return departamentoService.listarDepartamentos();
    }

    @ModelAttribute("municipios")
    public List<Municipio> addMunicipiosToModel() {
        return municipioService.listarMunicipios();
    }

    @ModelAttribute("roles")
    public List<Rol> addRolesToModel() { return rolService.listarRoles(); }

}
