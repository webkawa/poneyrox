package com.akasoft.poneyrox.exceptions;

/**
 *  Erreur de requete.
 *  Erreur due à une anomalie dans une requete entrante ou dans le traitement sous-jacent.
 */
public class RequestException extends AbstractException {
    /**
     *  Constructeur.
     * @param message Message d'erreur.
     * @param params Paramètres de formatage.
     */
    public RequestException(String message, Object... params) {
        super(message, params);
    }

    /**
     *  Constructeur à charge.
     *  @param cause Cause de l'erreur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    public RequestException(Throwable cause, String message, Object... params) {
        super(cause, message, params);
    }
}
