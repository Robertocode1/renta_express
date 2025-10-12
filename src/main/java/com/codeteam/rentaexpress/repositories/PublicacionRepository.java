package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Publicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicacionRepository extends JpaRepository<Publicacion, Integer> {
}
