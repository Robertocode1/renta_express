package com.codeteam.rentaexpress.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionController {
//    @ExceptionHandler(DuplicateResourceException.class)
//    public ResponseEntity<String> handleDuplicateResourceException(DuplicateResourceException ex) {
//        // 'ex' es el objeto que creaste en el servicio.
//        // ex.getMessage() recupera el string: "El email '...' ya está en uso."
//        String mensajeDeError = ex.getMessage();
//
//        // Aquí construyes la respuesta final para el usuario, usando ese mensaje.
//        return new ResponseEntity<>(mensajeDeError, HttpStatus.CONFLICT); // 409
//    }
}
