package com.akasoft.poneyrox.core.strategies.parameters;

/**
 *  Paramètre.
 *  Paramètre d'une stratégie pouvant décliner une liste de valeurs entrant en jeu dans les
 *  prises de position.
 *  @param <TResult> Type de valeur générée par le paramètre.
 */
public abstract class AbstractParameter<TResult> {
    /**
     *  Clef d'accès au paramètre.
     */
    private String key;

    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     */
    protected AbstractParameter(String key) {
        this.key = key;
    }

    /**
     *  Retourne la clef d'accès au paramètre.
     *  @return Clef d'accès.
     */
    public String getKey() {
        return this.key;
    }

    /**
     *  Génère une liste d'instances correspondant à la configuration du paramètre.
     *  @return Liste d'instances.
     */
    public abstract TResult[] getInstances();
}
