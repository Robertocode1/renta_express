package com.codeteam.rentaexpress.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // La ruta de la URL que usarás en tu HTML
        String resourceHandler = "/images/**";

        // La ubicación física en el disco duro donde están los archivos
        String userHome = System.getProperty("user.home");
        Path storagePath = Paths.get(userHome, "rentaexpress-uploads", "images");
        String resourceLocations = "file:" + storagePath.toAbsolutePath() + "/";

        // Mapeamos la URL a la ubicación física
        registry.addResourceHandler(resourceHandler)
                .addResourceLocations(resourceLocations);
    }
}