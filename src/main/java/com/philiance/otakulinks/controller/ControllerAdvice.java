package com.philiance.otakulinks.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {

    // Cette méthode gère les exceptions de validation des arguments de méthode.
    // Elle s'active lorsque les validations sur les objets @RequestBody échouent.
    @ExceptionHandler
    public ResponseEntity handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // Crée une map pour stocker les noms de champs et les messages d'erreur.
        Map<String, String> errors = new HashMap<>();

        // Parcours toutes les erreurs et extrait les noms des champs et les messages d'erreur correspondants.
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField(); // Obtient le nom du champ qui a une erreur de validation.
            String errorMessage = error.getDefaultMessage();    // Obtient le message d'erreur pour ce champ.
            errors.put(fieldName, errorMessage);               // Ajoute le couple champ-message d'erreur dans la map.
        });

        // Renvoie une réponse avec le statut HTTP BAD_REQUEST (400) et la map des erreurs.
        return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
    }
}
