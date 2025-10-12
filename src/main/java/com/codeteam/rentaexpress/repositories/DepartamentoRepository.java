package com.codeteam.rentaexpress.repositories;

import com.codeteam.rentaexpress.models.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;

interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
}
