package com.akasoft.poneyrox.dto;

import com.akasoft.poneyrox.exceptions.AbstractException;

/**
 *  Erreur.
 *  DTO représentatif d'une erreur levée dans l'application.
 */
public class ErrorDTO {
    /**
     *  Notification.
     */
    private String notification;

    /**
     *  Constructeur.
     *  @param exception Exception représentée.
     */
    public ErrorDTO(AbstractException exception) {
        this.notification = exception.getMessage();
    }
}
