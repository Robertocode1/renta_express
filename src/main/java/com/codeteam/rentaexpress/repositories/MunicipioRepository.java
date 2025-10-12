package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;

interface MunicipioRepository extends JpaRepository<Municipio, Long> {
}
