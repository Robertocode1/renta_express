package com.codeteam.rentaexpress.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "publicacion_imagen", schema = "public")
public class PublicacionImagen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_imagen", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "id_publicacion", nullable = false)
    @JsonIgnore // ✅ Agregué @JsonIgnore
    private Publicacion publicacion;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @ColumnDefault("CURRENT_TIMESTAMP")
    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;
}


//package com.codeteam.rentaexpress.models;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import org.hibernate.annotations.ColumnDefault;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import java.time.LocalDateTime;
//import java.time.OffsetDateTime;
//
//@NoArgsConstructor
//@Getter
//@Setter
//@Entity
//@Table(name = "publicacion_imagen", schema = "public")
//public class PublicacionImagen {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_imagen", nullable = false)
//    private Long id;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @OnDelete(action = OnDeleteAction.CASCADE)
//    @JoinColumn(name = "id_publicacion", nullable = false)
//    private Publicacion publicacion;
//
//    @Column(name = "url_imagen", nullable = false)
//    private String urlImagen;
//
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "fecha_creacion")
//    private LocalDateTime fechaCreacion;
//
//}