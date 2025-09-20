package com.codeteam.rentaexpress.repositories;


import com.codeteam.rentaexpress.models.Estado;
import com.codeteam.rentaexpress.models.Rol;
import com.codeteam.rentaexpress.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    //Buscar usuario por nombre
    Optional<Usuario> findByUsuario(String usuario);

    //Buscar usuario por email
    Optional<Usuario> findByEmail(String email);

    //Buscar por nombres compuestos
    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    //Buscar usuario por rol
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByEstado(Estado estado);
    List<Usuario> findByRolAndEstado(Rol rol, Estado estado);

    List<Usuario> findEstadoById(Integer id);

    //Buscar usaurios registrados entre dos fechas
    List<Usuario> findByFechaCreacionBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    //Buscar usuarios cuyo nombre empieza con un prefijo
    List<Usuario> findByNombreStartingWith(String prefijo);

    //Buscar usuarios cuyo nombre empieza con un sufijo
    List<Usuario> findByNombreEndingWith(String sufijo);

    //Buscar usuario por nombre de rol
    List<Usuario> findByRolNombre(String rol);

    //Busqueda con JPQL
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    List<Usuario> findByNombreRol(@Param("rolNombre") String rolNombre);

    //Busqueda con SQL nativo
    @Query(nativeQuery = true, value = "SELECT * FROM usuarios u join roles r on u.id_rol = r.id_rol WHERE r.nombre = :rolNombre")
    List<Usuario> findByRolNombreSql(@Param("rolNombre") String rolNombre);

    //Lamar una funcion
    @Query(nativeQuery = true, value = "Select * from funcionSql")
    List<Usuario> findByFuncionSql();

    //Lamar procedimiento almacenado
    @Procedure(name = "eliminar_usuarios")
    void eliminarUsuarios();

    //List<Usuario> findAllByEstado(Integer estado);
    List<Usuario> findByEstadoNombre(String nombre);

    //buscar email bool
    boolean existsByEmail(String email);
    //buscar usuario bool
    boolean existsByUsuario(String usuario);
}
