package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Municipio;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.repositories.MunicipioRepository;
import com.codeteam.rentaexpress.repositories.RolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RolService {
    private final RolRepository rolRepo;

    @Transactional(readOnly = true)
    public List<Rol> listarRoles(){
        return rolRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Rol> buscarRolPorId(Integer id) {
        return rolRepo.findById(id);
    }

}
