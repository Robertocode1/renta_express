package com.codeteam.rentaexpress.services;

import com.codeteam.rentaexpress.models.*;
import com.codeteam.rentaexpress.repositories.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Service
@Transactional
public class PublicacionService {

    private final PublicacionRepository publicacionRepo;
    private final EstadoRepository estadoRepo;
    private final UsuarioRepository usuarioRepo;
    private final CaracteristicaRepository caracteristicaRepo;
    private final FileUpLoadService fileUpLoadService;
    private final PublicacionImagenRepository imagenRepo;

    //Listar las publicaciones
    @Transactional(readOnly = true)
    public List<Publicacion> listarPublicaciones(){ return publicacionRepo.findAll(); }

    @Transactional(readOnly = true)
    public Publicacion listarPublicacionPorId(Integer id){
        return publicacionRepo.findById(id).orElse(null);
    }

    //Listar por id para ajax
    @Transactional(readOnly = true)
    public ResponseEntity<Publicacion> listarPublicacionAjax(Integer id){

        Optional<Publicacion> publicacion = publicacionRepo.findById(id);

        if (publicacion.isPresent()) {
            Publicacion pubFind = publicacion.get();

            // Respuesta HTTP 200 OK, poniendo la entidad encontrada en el cuerpo.
            ResponseEntity<Publicacion> respuestaDeExito = ResponseEntity.ok(pubFind);
            return respuestaDeExito;

        } else {
            // Crear una respuesta HTTP 404 Not Found (sin cuerpo).
            ResponseEntity<Publicacion> respuestaDeError = ResponseEntity.notFound().build();
            return respuestaDeError;
        }
    }

    //Listar publicaciones por usuario
    @Transactional(readOnly = true)
    public List<Publicacion> usuarioPublicacionPorId(Integer id){
        return publicacionRepo.findByUsuarioIdOrderByFechaCreacionDesc(id);
    }

    //Funcion para crear una publicacion
    @Transactional
    public int agregarPublicacion(Publicacion publicacion, List<Integer> caracteristicasIds, MultipartFile[] listaImagenes, HttpSession session) {
        try {
            Assert.notNull(publicacion, "publicacion no valido");

            // Usuario autenticado
            Integer userId = (Integer) session.getAttribute("id");
            Usuario usuario = usuarioRepo.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
            publicacion.setUsuario(usuario);

            //VALORES POR DEFECTO
            publicacion.setFechaCreacion(LocalDateTime.now());
            publicacion.setIdEstado(estadoRepo.findById(1).orElseThrow()); // 1 = Activo);

            // Buscar todas las características de una vez usando los IDs recibidos
            List<Caracteristica> caracteristicasCompletas = caracteristicaRepo.findAllById(caracteristicasIds);

            // Asignar el conjunto de objetos Caracteristica a la publicación
            publicacion.setCaracteristicas(new HashSet<>(caracteristicasCompletas));

            //PROCESAR Y GUARDAR IMÁGENES
            for (MultipartFile file : listaImagenes) {
                if (file.isEmpty()) {
                    continue;
                }
                // Usar el servicio para guardar la imagen
                String rutaImagen = fileUpLoadService.saveImage(file, publicacion.getTitulo(), "publicaciones");

                // Crear la entidad PublicacionImagen y asociarla
                PublicacionImagen publicacionImagen = new PublicacionImagen();
                publicacionImagen.setUrlImagen(rutaImagen);
                publicacionImagen.setFechaCreacion(LocalDateTime.now());

                // Establecer la relación bidireccional
                publicacion.getImagenes().add(publicacionImagen);
                publicacionImagen.setPublicacion(publicacion);
            }
            publicacionRepo.save(publicacion);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //Funcion para actualizar una publicacion
    @Transactional
    public int actualizarPublicacion(
            Publicacion publicacionForm,
            List<Integer> caracteristicasIds,
            MultipartFile[] listaImagenes,
            @RequestParam(name = "imagenesAEliminar", required = false) List<Long> imagenesAEliminarIds,
            HttpSession session
    ) {
        try {
            Assert.notNull(publicacionForm, "publicacion no valida");
            Assert.notNull(publicacionForm.getId(), "ID de publicación no puede ser nulo para actualizar");

            // 1. Obtener usuario autenticado
            Integer userId = (Integer) session.getAttribute("id");
            if (userId == null) {
                throw new RuntimeException("Usuario no autenticado");
            }

            Publicacion publicacionExistente = publicacionRepo.findById(publicacionForm.getId())
                    .orElseThrow(() -> new RuntimeException("Publicación no encontrada con ID: " + publicacionForm.getId()));

            if (!publicacionExistente.getUsuario().getId().equals(userId)) {
                throw new SecurityException("Acción no permitida: No eres el propietario de esta publicación.");
            }

            // Actualizar los campos
            publicacionExistente.setTitulo(publicacionForm.getTitulo());
            publicacionExistente.setDescripcion(publicacionForm.getDescripcion());
            publicacionExistente.setPrecio(publicacionForm.getPrecio());
            publicacionExistente.setTelefono(publicacionForm.getTelefono());

            // Actualizar relaciones
            publicacionExistente.setIdCategoria(publicacionForm.getIdCategoria());
            publicacionExistente.setIdTipoPrecio(publicacionForm.getIdTipoPrecio());
            publicacionExistente.setIdDepartamento(publicacionForm.getIdDepartamento());
            publicacionExistente.setIdMunicipio(publicacionForm.getIdMunicipio());

            // Actualizar Características
            List<Caracteristica> caracteristicasCompletas = caracteristicaRepo.findAllById(caracteristicasIds);
            publicacionExistente.setCaracteristicas(new HashSet<>(caracteristicasCompletas));

            //---PROCESAR IMÁGENES---

            // Eliminar imágenes marcadas
            if (imagenesAEliminarIds != null && !imagenesAEliminarIds.isEmpty()) {
                for (Long imagenId : imagenesAEliminarIds) {
                    PublicacionImagen img = imagenRepo.findById(imagenId)
                            .orElse(null); // Omitir si no se encuentra

                    // Doble chequeo: que exista Y que pertenezca a esta publicación
                    if (img != null && img.getPublicacion().getId().equals(publicacionExistente.getId())) {
                        fileUpLoadService.deleteImage(img.getUrlImagen());
                        // Quitar de la colección. Si tienes `orphanRemoval=true`,
                        publicacionExistente.getImagenes().remove(img);
                    }
                }
            }

            for (MultipartFile file : listaImagenes) {
                if (file.isEmpty()) {
                    continue;
                }
                String rutaImagen = fileUpLoadService.saveImage(file, publicacionExistente.getTitulo(), "publicaciones");
                PublicacionImagen publicacionImagen = new PublicacionImagen();
                publicacionImagen.setUrlImagen(rutaImagen);

                // Establecer la relación bidireccional
                publicacionExistente.getImagenes().add(publicacionImagen);
                publicacionImagen.setPublicacion(publicacionExistente);
            }
            publicacionRepo.save(publicacionExistente);
            return 1;

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    //Funcion para actualizar clicks una publicacion
    @Transactional
    public void actualizarClickPublicacion(Publicacion publicacion, Integer id) {
        //Validar que los parametros no vengas nulos
        Assert.notNull(publicacion, "La publicacion no puede ser nulo.");
        Assert.notNull(id, "El id no puede ser nulo.");

        Publicacion publicacionEx = publicacionRepo.findById(id).orElseThrow(() -> new IllegalArgumentException("El usuario no existe"));
        if (publicacionEx != null) {
            publicacion.setClicks(publicacion.getClicks() + 1);
            publicacionRepo.save(publicacion); // Guardar cambios
        }

    }
}
