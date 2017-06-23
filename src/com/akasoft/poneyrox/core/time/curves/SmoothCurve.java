package com.akasoft.poneyrox.core.time.curves;

import com.akasoft.poneyrox.core.time.cells.RawCell;
import com.akasoft.poneyrox.core.time.cells.SmoothCell;
import com.akasoft.poneyrox.threads.TimelineTask;

import java.util.List;

/**
 *  Courbe lissée.
 *  Courbe constituée à partir des résultat d'une courbe brute et représentative d'un lissage apporté aux taux.
 */
public class SmoothCurve extends AbstractCurve<SmoothCell> {
    /**
     *  Niveau de lissage.
     */
    private final int level;

    /**
     *  Constructeur.
     *  @param owner Tache propriétaire.
     *  @param level Niveau de lissage.
     */
    public SmoothCurve(TimelineTask owner, int level) {
        super(owner);
        this.level = level;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Lissage de la courbe.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    @Override
    public int getSmooth() {
        return this.level;
    }

    /**
     *  Intègre une mise à jour dans la courbe lissée.
     *  @param source Liste des valeurs lissées.
     *  @return Cellule lissée.
     */
    public SmoothCell integrate(List<RawCell> source) {
        /* Récupération de l'historique */
        List<SmoothCell> cells = super.getCells();

        /* Ajout */
        SmoothCell add = null;
        if (cells.size() == 0) {
            /* Gestion de la première valeur */
            add = new SmoothCell(source.get(source.size() - 1).getStart(), source, this);
        } else {
            /* Gestion des valeurs suivantes */
            SmoothCell last = cells.get(cells.size() - 1);
            add = new SmoothCell(
                    last.getStart() + (super.getOwner().getTimeline().getSize() * 1000),
                    source,
                    this,
                    last);
        }
        super.addCell(add);

        /* Diffusion */
        this.getOwner().getManager().diffuseCurve(this);

        /* Nettoyage */
        super.clearCells();

        /* Renvoi */
        return add;
    }
}
