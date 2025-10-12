package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

interface CaracteristicaRepository extends JpaRepository<Caracteristica, Integer> {
}
