package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.dto.ChatMessageDTO;
import com.codeteam.rentaexpress.models.Mensaje;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.repositories.UsuarioRepository; // ¡Lo necesitamos!
import com.codeteam.rentaexpress.services.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioRepository usuarioRepository; // Para buscar a los usuarios

    @Autowired
    private SimpMessagingTemplate messagingTemplate; // El "cartero" de Spring

    /**
     * MUESTRA LA PÁGINA DE "BANDEJA DE ENTRADA" (EL LOBBY)
     */
    @GetMapping("/mensajes")
    public String getInboxPage(Model model, HttpSession session) {

        // 1. Seguridad: Obtener mi usuario de la sesión
        Usuario miUsuario = (Usuario) session.getAttribute("usuario");
        if (miUsuario == null) {
            return "redirect:/login"; // O a donde sea
        }

        // 2. Llamar al servicio (Paso B)
        List<Mensaje> conversaciones = chatService.getInbox(miUsuario.getId());

        // 3. Pasar los datos a la vista
        model.addAttribute("conversaciones", conversaciones);
        model.addAttribute("miUsuario", miUsuario); // La vista necesita saber quién soy yo
        model.addAttribute("view", "home/inbox_view"); // ¡Nueva vista!

        return "layout/layout";
    }


    /**
     * Este método MUESTRA la página del chat.
     * Recibe el ID del usuario con el que queremos chatear.
     */
    @GetMapping("/chat/con/{otroUsuarioId}")
    public String getChatPage(@PathVariable("otroUsuarioId") Integer otroUsuarioId,
                              Model model,
                              HttpSession session) {

        // 1. Verificamos que el usuario esté logueado
        if (session.getAttribute("usuario") == null) {
            return "redirect:/home"; // O donde sea
        }

        // 2. Buscamos al otro usuario
        Usuario otroUsuario = usuarioRepository.findById(otroUsuarioId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. ¡AQUÍ ESTÁ LA MAGIA!
        // Añadimos al "otroUsuario" al modelo.
        model.addAttribute("otroUsuario", otroUsuario);
        model.addAttribute("view", "home/chat_view");

        // 4. Devolvemos el nombre de la plantilla HTML del chat
        return "layout/layout"; // (O "fragmentos/chat :: chat_content", como lo tengas)
    }

    /**
     * Este método "escucha" en el destino "/app/chat.privado".
     * (El prefijo /app se define en WebSocketConfig).
     *
     * Cuando un JS envía un mensaje a "/app/chat.privado", este método lo recibe.
     */
    @MessageMapping("/chat.privado")
    public void procesarMensajePrivado(@Payload ChatMessageDTO chatMessageDTO) {

        System.out.println("Mensaje recibido: " + chatMessageDTO.getContenido());

        // 1. Convertir el DTO (simple) en una Entidad (completa)
        Mensaje mensajeAGuardar = new Mensaje();
        mensajeAGuardar.setContenido(chatMessageDTO.getContenido());

        // Buscamos los objetos Usuario
        Usuario remitente = usuarioRepository.findById(chatMessageDTO.getRemitenteId())
                .orElseThrow(() -> new RuntimeException("Remitente no encontrado"));
        Usuario destinatario = usuarioRepository.findById(chatMessageDTO.getDestinatarioId())
                .orElseThrow(() -> new RuntimeException("Destinatario no encontrado"));

        mensajeAGuardar.setRemitente(remitente);
        mensajeAGuardar.setDestinatario(destinatario);
        mensajeAGuardar.setTimestamp(LocalDateTime.now());

        Mensaje mensajeGuardado = chatService.guardarMensaje(mensajeAGuardar);
        // 2. Guardar el mensaje en la Base de Datos (usando el Paso 3)
        // El servicio le pondrá el timestamp
        ChatMessageDTO dtoRespuesta = new ChatMessageDTO();
        dtoRespuesta.setId(mensajeGuardado.getId());
        dtoRespuesta.setContenido(mensajeGuardado.getContenido());
        dtoRespuesta.setTimestamp(mensajeGuardado.getTimestamp());
        dtoRespuesta.setRemitenteId(mensajeGuardado.getRemitente().getId());
        dtoRespuesta.setDestinatarioId(mensajeGuardado.getDestinatario().getId());

        // 3. Enviar el mensaje al destinatario
        // El destino es la "cola" personal de ese usuario.
        String destino = "/queue/private." + destinatario.getId();

        // 4. El "cartero" (messagingTemplate) envía el mensaje guardado (con ID y fecha)
        // a la cola del destinatario.
        // El JS del destinatario, que está suscrito a esa cola, lo recibirá.
        messagingTemplate.convertAndSend(destino, dtoRespuesta);

        // (Opcional: también podrías enviarle una confirmación de "entregado" al remitente
        // a su propia cola: /queue/private.{remitenteId})
    }
}