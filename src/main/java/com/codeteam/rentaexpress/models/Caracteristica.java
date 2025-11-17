package com.codeteam.rentaexpress.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "caracteristicas", schema = "public")
public class Caracteristica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_caracteristica", nullable = false)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @ManyToMany(mappedBy = "caracteristicas", fetch = FetchType.LAZY)
    @JsonIgnore // Evita la serialización de vuelta a Publicacion y un bucle infinito.
    private Set<Publicacion> publicaciones = new HashSet<>();

}