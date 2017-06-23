package com.akasoft.poneyrox.core.strategies.parameters;

import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Paramètre entier.
 */
public class IntegerParameter extends AbstractParameter<Long> {
    /**
     *  Valeur minimum.
     */
    private long minimum;

    /**
     *  Valeur maximum.
     */
    private long maximum;

    /**
     *  Nombre de divisions réalisées.
     */
    private int divisions;

    /**
     *  Liste des instances.
     */
    private Long[] instances;

    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     *  @param minimum Taille minimum.
     *  @param maximum Taille maximum.
     *  @param divisions Nombre de divisions réalisées.
     *  @throws InnerException En cas d'erreur interne.
     */
    public IntegerParameter(String key, long minimum, long maximum, int divisions) throws InnerException {
        super(key);
        if (maximum < minimum) {
            throw new InnerException("Maximum is under minimum");
        }
        if (divisions < 1) {
            throw new InnerException("Invalid double parameter divisions");
        }

        /* Paramètres */
        this.minimum = minimum;
        this.maximum = maximum;
        this.divisions = divisions;

        /* Calcul des instances */
        this.instances = new Long[this.divisions + 1];
        double interval = (this.maximum - this.minimum) / this.divisions;
        double buffer = this.minimum;
        for (int i = 0; i < this.divisions; i++) {
            this.instances[i] = Math.round(buffer);
            buffer += interval;
        }
        this.instances[this.instances.length - 1] = this.maximum;
    }

    /**
     *  Retourne la liste des instances.
     *  @return Liste des instances.
     */
    @Override
    public Long[] getInstances() {
        return this.instances;
    }
}
