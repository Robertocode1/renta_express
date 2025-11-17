package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
}
