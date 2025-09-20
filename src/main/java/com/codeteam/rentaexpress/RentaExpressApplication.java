package com.codeteam.rentaexpress;

import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.repositories.UsuarioRepository;
import com.codeteam.rentaexpress.services.UsuarioService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RentaExpressApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentaExpressApplication.class, args);
    }

    /*@Bean
    public CommandLineRunner demo(UsuarioService usuarioService, UsuarioRepository usuarioRepo) {
        return (args) -> {
            try {
                System.out.println("=== USANDO REPOSITORIO Y SERVICIO ===");

                // 1. Usando el repositorio directamente
                System.out.println("\n--- Usando Repositorio ---");
                Usuario usuario = usuarioRepo.findById(3)
                        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
                System.out.println("Usuario encontrado: " + usuario.getNombre());

                // 2. Usando el servicio
                System.out.println("\n--- Usando Servicio ---");

                usuario.setUsuario("user_new"); // Usuario único
                usuario.setEmail("nuevo233@test.com");
                usuario.setNombre("Josh N.");

                // Actualizar usando el servicio

                Usuario actualizado = usuarioService.actualizarUsuario(usuario, 5);
                System.out.println("Usuario actualizado: " + actualizado.getNombre());

                // Listar todos los usuarios
                System.out.println("\n--- Lista de Usuarios ---");
                usuarioService.listarUsuarios().forEach(u ->
                        System.out.println(u.getId() + ": " + u.getNombre() + " - " + u.getEmail())
                );

            } catch (Exception e) {
                System.err.println("\nERROR: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }*/

}
