package com.akasoft.poneyrox.core.time.cells;

import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;

import java.util.List;

/**
 *  Cellule lissée.
 */
public class SmoothCell extends AbstractCell {
    /**
     *  Constructeur avec précédent.
     *  @param start Date de la cellule.
     *  @param source Liste de cellules source.
     *  @param owner Courbe propriétaire.
     *  @param previous Entrée précédente.
     */
    public SmoothCell(long start, List<RawCell> source, AbstractCurve owner, SmoothCell previous) {
        super(
                previous,
                owner,
                start,
                SmoothCell.smooth(source, true, previous),
                SmoothCell.smooth(source, false, previous));
        super.achieve();
    }

    /**
     *  Constructeur sans précédent.
     *  @param start Date de la cellule.
     *  @param source Liste de cellules source.
     *  @param owner Courbe propriétaire.
     */
    public SmoothCell(long start, List<RawCell> source, AbstractCurve owner) {
        super(null, owner, start, SmoothCell.smooth(source, true), SmoothCell.smooth(source, false));
        super.achieve();
    }

    /**
     *  Calcule les valeurs lissées à partir d'une liste de cellules pré-alimentées et d'une entrée
     *  précédente.
     *  @param source Liste source.
     *  @param ask Type de valeur lissée.
     *  @param previous Cellule précédente.
     *  @return Valeur lissée.
     */
    private static Cluster smooth(List<RawCell> source, boolean ask, SmoothCell previous) {
        /* Création du résultat */
        Cluster result = SmoothCell.smooth(source, ask);
        if (ask) {
            result.setPrevious(previous.getAsk());
        } else {
            result.setPrevious(previous.getBid());
        }

        /* Gestion de la direction */
        boolean direction = false;
        if (ask) {
            if (result.getAverage() == previous.getAsk().getAverage()) {
                direction = previous.getAsk().getDirection();
            } else {
                direction = previous.getAsk().getAverage() < result.getAverage();
            }
        } else {
            if (result.getAverage() == previous.getBid().getAverage()) {
                direction = previous.getBid().getDirection();
            } else {
                direction = previous.getBid().getAverage() < result.getAverage();
            }
        }
        result.setDirection(direction);

        /* Renvoi */
        return result;
    }

    /**
     *  Calcul les valeurs lissées à partir d'une liste de cellules pré-alimentées.
     *  @param source Liste source.
     *  @param ask Type de valeur lissée.
     *  @return Valeur lissée.
     */
    private static Cluster smooth(List<RawCell> source, boolean ask) {
        /* Création des variables */
        double min = 0;
        double avg = 0;
        double max = 0;

        /* Parcours */
        for (RawCell raw : source) {
            if (ask) {
                min += raw.getAsk().getMinimum();
                avg += raw.getAsk().getAverage();
                max += raw.getAsk().getMaximum();
            } else {
                min += raw.getBid().getMinimum();
                avg += raw.getBid().getAverage();
                max += raw.getBid().getMaximum();
            }
        }
        min /= source.size();
        avg /= source.size();
        max /= source.size();

        /* Renvoi */
        return new Cluster(null, min, avg, max, false);
    }
}
