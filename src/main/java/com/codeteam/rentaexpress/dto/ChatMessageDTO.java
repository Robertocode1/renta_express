package com.codeteam.rentaexpress.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// @Data de Lombok nos da Getters, Setters, ToString, etc.
@Data
@NoArgsConstructor
public class ChatMessageDTO {
    private Long id;
    private String contenido;
    private Integer remitenteId;
    private Integer destinatarioId;
    private LocalDateTime timestamp;

    // El JavaScript llenará esto cuando envíe un mensaje.
}