package com.codeteam.rentaexpress.services; // (Tu paquete)

import com.codeteam.rentaexpress.models.Mensaje;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.repositories.MensajeRepository;
import com.codeteam.rentaexpress.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // ¡Lo necesitamos!

    /**
     * Método 1: Obtener el historial de chat entre dos usuarios.
     * Recibe los IDs, los convierte en objetos Usuario,
     * y llama a nuestro query personalizado.
     */
    @Transactional(readOnly = true) // readOnly = true es una optimización para consultas
    public List<Mensaje> getHistorial(Integer idUsuarioA, Integer idUsuarioB) {

        // 1. Buscamos los objetos Usuario completos.
        Usuario usuarioA = usuarioRepository.findById(idUsuarioA)
                .orElseThrow(() -> new RuntimeException("Usuario A no encontrado"));

        Usuario usuarioB = usuarioRepository.findById(idUsuarioB)
                .orElseThrow(() -> new RuntimeException("Usuario B no encontrado"));

        // 2. Llamamos al método mágico que creamos en el Paso 2.5
        return mensajeRepository.findChatHistorial(usuarioA, usuarioB);
    }

    /**
     * Método 2: Guardar un nuevo mensaje en la base de datos.
     * Este método será llamado por el WebSocketController (Paso 7).
     */
    @Transactional
    public Mensaje guardarMensaje(Mensaje mensaje) {
        // Podrías añadir validaciones aquí (ej: no enviar mensajes vacíos)
        if (mensaje.getContenido() == null || mensaje.getContenido().isEmpty()) {
            throw new IllegalArgumentException("El contenido del mensaje no puede estar vacío");
        }

        // Simplemente lo guarda.
        return mensajeRepository.save(mensaje);
    }

    @Transactional(readOnly = true)
    public List<Mensaje> getInbox(Integer miUsuarioId) {
        // 1. Busca el objeto Usuario (buena práctica para validar)
        Usuario usuario = usuarioRepository.findById(miUsuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado para la bandeja de entrada"));

        // 2. Llama a la nueva super-consulta
        return mensajeRepository.findConversacionesRecientes(usuario.getId());
    }
}