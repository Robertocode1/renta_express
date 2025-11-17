package com.codeteam.rentaexpress.controllers;

import com.codeteam.rentaexpress.models.Mensaje;
import com.codeteam.rentaexpress.models.Usuario;
import com.codeteam.rentaexpress.services.ChatService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map; // Para un error bonito

@RestController
@RequestMapping("/chat")
public class ChatRestController {

    @Autowired
    private ChatService chatService;

    /**
     * Endpoint para obtener el historial de chat con otro usuario.
     * El JavaScript llamará a este endpoint (vía AJAX)
     * cuando un usuario abra una ventana de chat.
     */
    @GetMapping("/historial/{otroUsuarioId}")
    public ResponseEntity<?> getHistorialDeChat(
            @PathVariable Integer otroUsuarioId,
            HttpSession session)
    {
        Usuario miUsuario = (Usuario) session.getAttribute("usuario");

        if (miUsuario == null) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "No autenticado. Por favor, inicie sesión."));
        }

        Integer miId = miUsuario.getId();

        if (miId.equals(otroUsuarioId)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "No se puede chatear con uno mismo."));
        }

        // 3. ¡Ir al servicio!
        try {
            // El servicio (Paso 3) hace el trabajo pesado
            List<Mensaje> historial = chatService.getHistorial(miId, otroUsuarioId);

            // 4. Devolver la lista como JSON (Error 200 OK)
            return ResponseEntity.ok(historial);

        } catch (Exception e) {
            // Por si el ID no existe o algo más truena
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR) // Error 500
                    .body(Map.of("error", "Error al recuperar el historial: " + e.getMessage()));
        }
    }
}