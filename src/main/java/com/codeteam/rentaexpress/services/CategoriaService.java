package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Categoria;
import com.codeteam.rentaexpress.repositories.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepo;

    @Transactional(readOnly = true)
    public List<Categoria> listarCategorias() {
        return categoriaRepo.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Categoria> obtenerCategoriaPorId(Integer id) {
        return categoriaRepo.findById(id);
    }

}
