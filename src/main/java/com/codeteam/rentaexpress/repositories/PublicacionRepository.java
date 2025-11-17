package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {
    // Ordenar por fecha (más recientes primero)
    List<Publicacion> findByUsuarioIdOrderByFechaCreacionDesc(Integer usuarioId);

}
