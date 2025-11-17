package com.codeteam.rentaexpress.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@ToString()
@Getter
@Setter
@Entity
@Table(name = "usuarios", schema = "public")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer id;

    @Column(name = "usuario", nullable = false, length = 50)
    private String usuario;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "documento", nullable = false, length = 15)
    private String documento;

    @Column(name = "contrasenha", nullable = false)
    private String contrasenha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_rol", nullable = false)
    //@JsonIgnore // ✅ Agregué @JsonIgnore
    private Rol rol;

    @ManyToOne(fetch = FetchType.LAZY, optional = false) // Cambié a LAZY
    @OnDelete(action = OnDeleteAction.RESTRICT)
    @JoinColumn(name = "id_estado", nullable = false)
    @JsonIgnore // ✅ Agregué @JsonIgnore
    private Estado estado;

    @Column(name = "foto")
    private String foto;

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
//import lombok.ToString;
//import org.hibernate.annotations.ColumnDefault;
//import org.hibernate.annotations.OnDelete;
//import org.hibernate.annotations.OnDeleteAction;
//
//import java.time.LocalDateTime;
//
//@NoArgsConstructor
//@ToString()
//@Getter
//@Setter
//@Entity
//@Table(name = "usuarios", schema = "public")
//public class Usuario {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "id_usuario", nullable = false)
//    private Integer id;
//
//    @Column(name = "usuario", nullable = false, length = 50)
//    private String usuario;
//
//    @Column(name = "nombre", nullable = false, length = 100)
//    private String nombre;
//
//    @Column(name = "email", nullable = false, length = 100)
//    private String email;
//
//    @Column(name = "documento", nullable = false, length = 15)
//    private String documento;
//
//    @Column(name = "contrasenha", nullable = false)
//    private String contrasenha;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @OnDelete(action = OnDeleteAction.RESTRICT)
//    @JoinColumn(name = "id_rol", nullable = false)
//    private Rol rol;
//
//    @ManyToOne(fetch = FetchType.EAGER, optional = false)
//    @OnDelete(action = OnDeleteAction.RESTRICT)
//    @JoinColumn(name = "id_estado", nullable = false)
//    private Estado estado;
//
//    @Column(name = "foto")
//    private String foto;
//
//    @ColumnDefault("CURRENT_TIMESTAMP")
//    @Column(name = "fecha_creacion")
//    private LocalDateTime fechaCreacion;
//
//}