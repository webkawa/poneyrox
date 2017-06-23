package com.akasoft.poneyrox.core.strategies.parameters;

import com.akasoft.poneyrox.core.time.clusters.Cluster;

import java.util.Arrays;

/**
 *  Paramètre de variation (maximum/moyenne/minimum).
 */
public class VariationParameter extends AbstractParameter<VariationType> {
    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     */
    public VariationParameter(String key) {
        super(key);
    }

    /**
     *  Génère la liste des instances.
     *  @return Liste des instances.
     */
    @Override
    public VariationType[] getInstances() {
        return VariationType.values();
    }

    /**
     *  Retourne une valeur rattachée à une cellule par le biais d'un mode de variation.
     *  @param cluster Cellule évaluée.
     *  @param type Mode de variation.
     *  @return Valeur correspondante.
     */
    public static double getRateByVariation(Cluster cluster, VariationType type) {
        switch (type) {
            case MINIMUM:
                return cluster.getMinimum();
            case MAXIMUM:
                return cluster.getMaximum();
            case AVERAGE:
            default:
                return cluster.getAverage();
        }
    }

    /**
     *  Retourne un sommet ou un repli rattaché à une cellule par le biais d'un mode de variation.
     *  @param cluster Cellule évaluée.
     *  @param variation Mode de variation.
     *  @param type Type d'opposition recherchée (true : sommet, false : repli).
     *  @return Valeur courante.
     */
    public static boolean getOppositeByVariation(Cluster cluster, VariationType variation, boolean type) {
        if (type) {
            return VariationParameter.getTopByVariation(cluster, variation);
        } else {
            return VariationParameter.getBottomByVariation(cluster, variation);
        }
    }

    /**
     *  Retourne le sommet rattaché à une cellule par le biais d'un mode de variation.
     *  @param cluster Cellule évaluée.
     *  @param type Mode de variation.
     *  @return Valeur correspondante.
     */
    public static boolean getTopByVariation(Cluster cluster, VariationType type) {
        switch (type) {
            case MINIMUM:
                return cluster.isTopMinimum();
            case MAXIMUM:
                return cluster.isTopMaximum();
            case AVERAGE:
            default:
                return cluster.isTopAverage();
        }
    }

    /**
     *  Retourne le repli rattaché à une cellule par le biais d'un mode de variation.
     *  @param cluster Cellule évaluée.
     *  @param type Mode de variation.
     *  @return Valeur correspondante.
     */
    public static boolean getBottomByVariation(Cluster cluster, VariationType type) {
        switch (type) {
            case MINIMUM:
                return cluster.isBottomMinimum();
            case MAXIMUM:
                return cluster.isBottomMaximum();
            case AVERAGE:
            default:
                return cluster.isBottomAverage();
        }
    }

    /**
     *  Affecte une valeur d'opposition par type.
     *  @param cluster Noeud modifié.
     *  @param variation Variation.
     *  @param type Type de valeur (true : sommet, false : repli).
     *  @param value Valeur affectée.
     */
    public static void setOppositeByVariation(Cluster cluster, VariationType variation, boolean type, boolean value) {
        if (type) {
            switch (variation) {
                case MINIMUM:
                    cluster.setTopMinimum(value);
                    break;
                case AVERAGE:
                    cluster.setTopAverage(value);
                    break;
                case MAXIMUM:
                    cluster.setTopMaximum(value);
                    break;

            }
        } else {
            switch (variation) {
                case MINIMUM:
                    cluster.setBottomMinimum(value);
                    break;
                case AVERAGE:
                    cluster.setBottomAverage(value);
                    break;
                case MAXIMUM:
                    cluster.setBottomMaximum(value);
                    break;
            }
        }
    }
}
