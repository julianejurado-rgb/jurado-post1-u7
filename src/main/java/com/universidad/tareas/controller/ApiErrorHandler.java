package com.universidad.tareas.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiErrorHandler {

    // Este manejador aplica solo a los controladores REST (@RequestBody).
    // Los errores de validación del formulario Thymeleaf de TareaController
    // se resuelven aparte, con BindingResult dentro del propio método
    // guardar(), porque ese flujo debe volver a renderizar una vista HTML
    // con los mensajes de error, no devolver un cuerpo JSON.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
            .forEach(error -> errores.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }
}
