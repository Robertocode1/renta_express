package com.codeteam.rentaexpress.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tipo_publicaciones", schema = "public")
public class TipoPublicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tipo", nullable = false)
    private Integer id;

    @Column(name = "tipo_publicacion", nullable = false, length = 20)
    private String tipoPublicacion;

}