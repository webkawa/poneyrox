package com.akasoft.poneyrox.core.time.cells;

import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.markets.RateEntity;

import java.util.ArrayList;
import java.util.List;

/**
 *  Enveloppe des cellules brutes.
 */
public abstract class RawCellWrapper extends AbstractCell {
    /**
     *  Historique utilisé pour le calcul des moyennes.
     */
    private List<RateEntity> history;

    /**
     *  Constructeur.
     *  @param previous Référence à la cellule précédente (si disponible).
     *  @param owner Ligne temporelle propriétaire.
     *  @param start Date de la cellule.
     *  @param source Taux source.
     *  @param ask Demande initiale.
     *  @param bid Offre initiale.
     */
    public RawCellWrapper(AbstractCell previous, AbstractCurve owner, long start, RateEntity source, Cluster ask, Cluster bid) {
        super(previous, owner, start, ask, bid);
        this.history = new ArrayList<>();
        this.history.add(source);
    }

    /**
     *  Retourne l'historique complet.
     *  @return Historique complet.
     */
    protected synchronized List<RateEntity> getHistory() {
        synchronized (this.history) {
            return new ArrayList<>(this.history);
        }
    }

    /**
     *  Ajoute un taux dans la cellule.
     *  @param rate Taux ajouté.
     */
    protected synchronized void addRate(RateEntity rate) {
        synchronized (this.history) {
            this.history.add(rate);
        }
    }
}
