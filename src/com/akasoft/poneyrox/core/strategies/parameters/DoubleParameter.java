package com.akasoft.poneyrox.core.strategies.parameters;

import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Paramètre de type flottant.
 */
public class DoubleParameter extends AbstractParameter<Double> {
    /**
     *  Valeur minimale.
     */
    private double minimum;

    /**
     *  Valeur maximale.
     */
    private double maximum;

    /**
     *  Nombre de divisions réalisées.
     */
    private int divisions;

    /**
     *  Liste des instances.
     */
    private Double[] instances;

    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     *  @param minimum Valeur minimale.
     *  @param maximum Valeur maximale.
     *  @param divisions Nombre de divisions.
     *  @throws InnerException En cas d'erreur interne.
     */
    public DoubleParameter(String key, double minimum, double maximum, int divisions) throws InnerException {
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
        this.instances = new Double[this.divisions + 1];
        double interval = (this.maximum - this.minimum) / this.divisions;
        double buffer = this.minimum;
        for (int i = 0; i < this.divisions; i++) {
            this.instances[i] = buffer;
            buffer += interval;
        }
        this.instances[this.instances.length - 1] = this.maximum;
    }

    /**
     *  Retourne la liste des instances disponibles.
     *  @return Liste des instances.
     */
    @Override
    public Double[] getInstances() {
        return this.instances;
    }
}
