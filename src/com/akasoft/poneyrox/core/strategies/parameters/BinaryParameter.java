package com.akasoft.poneyrox.core.strategies.parameters;

/**
 *  Paramètre binaire.
 */
public class BinaryParameter extends AbstractParameter<Boolean> {
    /**
     *  Liste des instances.
     */
    private Boolean[] instances;

    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     */
    public BinaryParameter(String key) {
        super(key);
        this.instances = new Boolean[] { true, false };
    }

    /**
     *  Retourne la liste des instances.
     *  @return Liste des instances.
     */
    @Override
    public Boolean[] getInstances() {
        return this.instances;
    }
}
