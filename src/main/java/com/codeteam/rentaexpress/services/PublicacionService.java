package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Publicacion;
import com.codeteam.rentaexpress.repositories.PublicacionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional
public class PublicacionService {

    private final PublicacionRepository publicacionRepo;
    //Listar las publicaciones
    @Transactional(readOnly = true)
    public List<Publicacion> listarPublicaciones(){ return publicacionRepo.findAll(); }

    //Funcion para crear una publicacion
    public int crearPublicacion(Publicacion publicacion){
        Assert.notNull(publicacion, "publicacion no valido");

        publicacion.setFechaCreacion(LocalDateTime.now());

        try {
            publicacionRepo.save(publicacion);
            return 1;
        }
        catch (Exception e) {
            return 0;
        }

    }

}
