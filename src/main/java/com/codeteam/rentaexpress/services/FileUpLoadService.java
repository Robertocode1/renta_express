package com.codeteam.rentaexpress.services;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUpLoadService {

    private Path storageBaseFolder;

    //Usar @PostConstruct para la lógica de inicialización de carpetas
    @PostConstruct
    public void init() {
        // Carpeta base donde se guardarán todas las imágenes
        String userHome = System.getProperty("user.home");
        this.storageBaseFolder = Paths.get(userHome, "rentaexpress-uploads", "images");

        try {
            Files.createDirectories(this.storageBaseFolder);
        } catch (IOException e) {
            throw new RuntimeException("No se pudo inicializar la carpeta base de almacenamiento de imágenes.", e);
        }
    }

    /**
     * Guarda una imagen en una carpeta específica y devuelve la ruta relativa para usar en HTML.
     * @param file El archivo subido
     * @param prefix Prefijo para el nombre del archivo
     * @param folder Carpeta donde se guardará (por ejemplo, "usuarios", "publicaciones", etc.)
     * @return Ruta relativa del archivo guardado, o null si no se subió archivo
     */
    public String saveImage(MultipartFile file, String prefix, String folder) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = StringUtils.getFilenameExtension(originalFilename);

        if (extension == null) {
            throw new IllegalArgumentException("La imagen debe tener una extensión válida.");
        }

        // Sanitizar el prefijo
        String sanitizedPrefix = prefix.toLowerCase().replaceAll("\\s+", "-").replaceAll("[^a-z0-9-]", "");
        // Crear un nombre único
        String uniqueFilename = sanitizedPrefix + "_" + UUID.randomUUID().toString().substring(0, 8) + "." + extension;

        // Carpeta de destino específica
        Path destinationFolder = this.storageBaseFolder.resolve(folder);
        try {
            Files.createDirectories(destinationFolder); // Crea la subcarpeta si no existe
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear la carpeta de almacenamiento: " + folder, e);
        }

        // Guardar archivo
        Path destinationFile = destinationFolder.resolve(uniqueFilename).normalize().toAbsolutePath();
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la imagen.", e);
        }

        // Devolver ruta relativa
        return "/images/" + folder + "/" + uniqueFilename;
    }

    /**
     * Sobrecarga para usar carpeta por defecto "usuarios"
     */
    public String saveImage(MultipartFile file, String prefix) {
        return saveImage(file, prefix, "usuarios");
    }

    /**
     * Elimina un archivo de imagen del sistema de archivos.
     * @param relativeImagePath La ruta relativa devuelta por saveImage (ej: /images/publicaciones/archivo.jpg)
     */
    public void deleteImage(String relativeImagePath) {
        if (relativeImagePath == null || relativeImagePath.isBlank()) {
            // No hay nada que borrar
            return;
        }

        // 1. Definimos el prefijo de URL que usa saveImage()
        final String urlPrefix = "/images/";

        // 2. Verificamos que la ruta sea válida y tenga nuestro prefijo
        if (!relativeImagePath.startsWith(urlPrefix)) {
            System.err.println("Se intentó borrar una imagen con una ruta no válida: " + relativeImagePath);
            // Podrías lanzar una excepción si prefieres ser más estricto
            return;
        }

        // Extraer la ruta real del archivo (ej: "publicaciones/archivo.jpg")
        String fileSystemPath = relativeImagePath.substring(urlPrefix.length());

        //  Construimos la ruta absoluta en el disco
        //   (ej: /home/usuario/rentaexpress-uploads/images/publicaciones/archivo.jpg)
        Path absolutePath = this.storageBaseFolder.resolve(fileSystemPath).normalize();

        // [Seguridad] Verificamos que la ruta resuelta siga DENTRO de nuestra carpeta base
        //  Esto previene ataques de Path Traversal (ej: /images/../../etc/passwd)
        if (!absolutePath.startsWith(this.storageBaseFolder)) {
            System.err.println("¡Peligro de seguridad! Se bloqueó intento de borrado fuera de la carpeta de uploads: " + relativeImagePath);
            return;
        }

        // Intentamos borrar el archivo
        try {
            Files.delete(absolutePath);
        } catch (NoSuchFileException e) {
            // El archivo ya no existía. No es un error, solo lo registramos.
            System.err.println("Intento de borrar archivo fallido (ya no existe): " + absolutePath);
        } catch (IOException e) {
            // Un error real (permisos, etc.)
            System.err.println("Error al borrar el archivo: " + absolutePath);
            e.printStackTrace();
            // Dependiendo de tu lógica, podrías querer relanzar esto:
            // throw new RuntimeException("No se pudo borrar el archivo", e);
        }
    }
}
