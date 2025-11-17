package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Caracteristica;
import com.codeteam.rentaexpress.repositories.CaracteristicaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CaracteristicaService {
    private final CaracteristicaRepository caracteristicaRepo;

    @Transactional(readOnly = true)
    public List<Caracteristica> listarCaracteristicas() {
        return caracteristicaRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Caracteristica> listarCaracteristicaPorId(Integer id) {
        return caracteristicaRepo.findById(id);
    }

}
