package com.codeteam.rentaexpress.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@Data
@Entity
@Table(name = "publicaciones", schema = "public")
public class Publicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_publicacion", nullable = false)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_usuario", nullable = false)
    @JsonIgnore // ✅ Agregué @JsonIgnore
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_categoria", nullable = false)
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private Categoria idCategoria;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "precio", precision = 10, scale = 2)
    private BigDecimal precio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @JoinColumn(name = "id_tipo_precio", nullable = false)
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private TipoPrecio idTipoPrecio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @JoinColumn(name = "id_departamento", nullable = false)
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private Departamento idDepartamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @JoinColumn(name = "id_municipio", nullable = false)
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private Municipio idMunicipio;

    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @JoinColumn(name = "id_estado", nullable = false)
    @JsonIgnore // ✅ Agregué @JsonIgnore
    private Estado idEstado;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "clicks")
    private Long clicks = 0L;

    //RELACIÓN CORRECTA CON CARACTERÍSTICAS
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "publicacion_caracteristica",
            joinColumns = @JoinColumn(name = "id_publicacion"),
            inverseJoinColumns = @JoinColumn(name = "id_caracteristica")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private Set<Caracteristica> caracteristicas = new HashSet<>();

    @OneToMany(
            mappedBy = "publicacion",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private List<PublicacionImagen> imagenes = new ArrayList<>();
}


//package com.codeteam.rentaexpress.models;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.ColumnDefault;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//@NoArgsConstructor
//@Data
//@Entity
//@Table(name = "publicaciones", schema = "public")
//public class Publicacion {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_publicacion", nullable = false)
//    private Integer id;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "id_usuario", nullable = false)
//    private Usuario usuario;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @OnDelete(action = OnDeleteAction.RESTRICT)
//    @JoinColumn(name = "id_categoria", nullable = false)
//    private Categoria idCategoria;
//
//    @Column(name = "titulo", nullable = false, length = 100)
//    private String titulo;
//
//    @Column(name = "descripcion", length = 500)
//    private String descripcion;
//
//    @Column(name = "precio", precision = 10, scale = 2)
//    private BigDecimal precio;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "id_tipo_precio", nullable = false)
//    private TipoPrecio idTipoPrecio;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "id_departamento", nullable = false)
//    private Departamento idDepartamento;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "id_municipio", nullable = false)
//    private Municipio idMunicipio;
//
//    @Column(name = "telefono", nullable = false, length = 20)
//    private String telefono;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @JoinColumn(name = "id_estado", nullable = false)
//    private Estado idEstado;
//
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "fecha_creacion")
//    private LocalDateTime fechaCreacion;
//
//    @Column(name = "clicks")
//    private Long clicks = 0L;
//
//    //RELACIÓN CORRECTA CON CARACTERÍSTICAS
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "publicacion_caracteristica",
//            joinColumns = @JoinColumn(name = "id_publicacion"),
//            inverseJoinColumns = @JoinColumn(name = "id_caracteristica")
//    )
//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private Set<Caracteristica> caracteristicas = new HashSet<>();
//
//    @OneToMany(
//            mappedBy = "publicacion", // ¡MUY IMPORTANTE!
//            cascade = CascadeType.ALL,
//            orphanRemoval = true,
//            fetch = FetchType.LAZY
//    )
//    private List<PublicacionImagen> imagenes = new ArrayList<>();
//
//}