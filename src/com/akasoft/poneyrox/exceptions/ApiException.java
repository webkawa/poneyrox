package com.akasoft.poneyrox.exceptions;

/**
 *  Erreur d'API.
 *  Erreur due à une anomalie levée lors d'un appel à une API distante.
 */
public class ApiException extends AbstractException {
    /**
     *  Code de réponse HTTP.
     */
    private int code;

    /**
     *  Constructeur.
     *  @param code Code de réponse HTTP.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public ApiException(int code, String message, Object... params) {
        super(message, params);
    }

    /**
     *  Constructeur à charge.
     *  @param code Code de réponse HTTP.
     *  @param cause Cause de l'erreur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public ApiException(int code, Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }

    /**
     *  Retourne le code de réponse HTTP.
     *  @return Code de réponse HTTP.
     */
    public int getCode() {
        return this.code;
    }
}
