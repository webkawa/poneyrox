package com.akasoft.poneyrox.exceptions;

/**
 *  Exception.
 *  Exception produite par l'application.
 */
public abstract class AbstractException extends Exception {
    /**
     *  Constructeur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    protected AbstractException(String message, Object... params) {
        super(String.format(message, params));
    }

    /**
     *  Constructeur à charge.
     *  @param cause Cause de l'erreur.
     *  @param message Message d'erreur.
     *  @param params Paramètres de formatage.
     */
    protected AbstractException(Throwable cause, String message, Object... params) {
        super(String.format(message, params), cause);
    }
}
