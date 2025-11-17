package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Departamento;
import com.codeteam.rentaexpress.repositories.DepartamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class DepartamentoService {
    private final DepartamentoRepository departamentoRepo;

    @Transactional(readOnly = true)
    public List<Departamento> listarDepartamentos(){
        return departamentoRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Departamento> buscarDepartamentos(Long id){
        return departamentoRepo.findById(id);
    }




}
