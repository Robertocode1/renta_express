package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Municipio;
import com.codeteam.rentaexpress.repositories.MunicipioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class MunicipioService {
    private final MunicipioRepository municipioRepo;

    @Transactional(readOnly = true)
    public List<Municipio> listarMunicipios(){
        return municipioRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Municipio> buscarMunicipioPorId(Long id) {
        return municipioRepo.findById(id);
    }



}
