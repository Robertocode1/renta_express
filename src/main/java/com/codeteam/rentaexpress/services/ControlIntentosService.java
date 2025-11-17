package com.codeteam.rentaexpress.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Servicio para controlar el número de intentos de operaciones sensibles,
 * como el restablecimiento de contraseña, por correo electrónico.
 *
 * Evita abusos limitando a 3 intentos por correo cada 5 minutos.
 */
@Service
public class ControlIntentosService {

    // Almacena los intentos por correo: clave = email, valor = cantidad de intentos
    private final Cache<String, Integer> intentosPorCorreo = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES) // Los registros caducan a los 5 minutos
            .maximumSize(1000) // Soporta hasta 1000 correos distintos en memoria
            .build();

    private static final int INTENTOS_MAXIMOS = 3; // Máximo permitido en ventana de 5 minutos

    /**
     * Verifica si un correo tiene permitido realizar una nueva solicitud.
     *
     * @param correo el correo electrónico a verificar
     * @return true si está permitido, false si superó el límite
     */
    public boolean estaPermitido(String correo) {
        Integer intentos = intentosPorCorreo.getIfPresent(correo);
        int intentosActuales = (intentos == null) ? 0 : intentos;

        if (intentosActuales >= INTENTOS_MAXIMOS) {
            return false; // Bloqueado por exceso de intentos
        }

        // Registrar el nuevo intento
        intentosPorCorreo.put(correo, intentosActuales + 1);
        return true;
    }

    /**
     * Obtiene la cantidad de intentos restantes para un correo.
     *
     * @param correo el correo a consultar
     * @return número de intentos disponibles (0 si está bloqueado)
     */
    public int getIntentosRestantes(String correo) {
        Integer intentos = intentosPorCorreo.getIfPresent(correo);
        int usados = (intentos == null) ? 0 : intentos;
        return Math.max(0, INTENTOS_MAXIMOS - usados);
    }
}
