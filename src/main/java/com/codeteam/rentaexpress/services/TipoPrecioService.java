package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.Categoria;
import com.codeteam.rentaexpress.models.TipoPrecio;
import com.codeteam.rentaexpress.repositories.CategoriaRepository;
import com.codeteam.rentaexpress.repositories.TipoPrecioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TipoPrecioService {
    @Autowired
    private TipoPrecioRepository tipoRepo;

    public List<TipoPrecio> listarTipos() {
        return tipoRepo.findAll();
    }

    public Optional<TipoPrecio> listarTipoPorId(Long id) {
        return tipoRepo.findById(id);
    }

}
