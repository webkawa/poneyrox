package com.akasoft.poneyrox.exceptions;

/**
 *  Erreur d'API.
 *  Erreur due à une anomalie levée lors d'un appel à une API distante.
 */
public class ApiException extends AbstractException {
    /**
     *  Constructeur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public ApiException(String message, Object... params) {
        super(message, params);
    }

    /**
     *  Constructeur à charge.
     *  @param cause Cause de l'erreur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public ApiException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
