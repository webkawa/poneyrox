package com.akasoft.poneyrox.exceptions;

/**
 *  Erreur interne.
 *  Erreur levée par une violation interne à l'application.
 */
public class InnerException extends AbstractException {
    /**
     *  Constructeur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public InnerException(String message, Object... params) {
        super(message, params);
    }

    /**
     *  Constructeur à charge.
     *  @param cause Cause de l'erreur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public InnerException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
