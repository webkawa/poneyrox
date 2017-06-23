package com.akasoft.poneyrox.core.time.cells;

import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.views.CellViews;
import com.fasterxml.jackson.annotation.JsonView;

/**
 *  Cellule.
 *  Cellule unitaire entrant dans le calcul d'une courbe.
 */
public abstract class AbstractCell {
    /**
     *  Départ.
     *  Point de départ de la cellule, exprimée en temps UNIX.
     */
    @JsonView(
            CellViews.Public.class
    )
    private long start;

    /**
     *  Niveau de la demande.
     */
    @JsonView(
            CellViews.Public.class
    )
    private Cluster ask;

    /**
     *  Niveau de l'offre.
     */
    @JsonView(
            CellViews.Public.class
    )
    private Cluster bid;

    /**
     *  Référence à la cellule précédente.
     */
    private AbstractCell previous;

    /**
     *  Courbe propriétaire.
     */
    private AbstractCurve owner;

    /**
     *  Indicateur de finalisation.
     */
    private Boolean finalized;

    /**
     *  Constructeur.
     *  @param previous Référence à la cellule précédente (si disponible).
     *  @param owner Ligne temporelle propriétaire.
     *  @param start Date de la cellule.
     *  @param ask Demande initiale.
     *  @param bid Offre initiale.
     */
    public AbstractCell(AbstractCell previous, AbstractCurve owner, long start, Cluster ask, Cluster bid) {
        this.previous = previous;
        this.owner = owner;
        this.start = start;
        this.ask = ask;
        this.bid = bid;
        this.finalized = false;
    }

    /**
     *  Retourne la date de départ.
     *  @return Date de départ.
     */
    public long getStart() {
        return this.start;
    }

    /**
     *  Retourne le niveau de la demande.
     *  @return Niveau de la demande.
     */
    public Cluster getAsk() {
        return this.ask;
    }

    /**
     *  Retourne le niveau de l'offre.
     *  @return Niveau de l'offre.
     */
    public Cluster getBid() {
        return this.bid;
    }


    /**
     *  Retourne la cellule précédente (si applicable).
     *  @return Cellule précédente.
     */
    public AbstractCell getPrevious() {
        return this.previous;
    }

    /**
     *  Indique si la cellule est finalisée.
     *  @return true si la cellule est finalisée.
     */
    public synchronized boolean isFinalized() {
        synchronized (this.finalized) {
            return this.finalized;
        }
    }

    /**
     *  Finalise la cellule.
     */
    protected void achieve() {
        /* Traitement de l'offre */
        this.bid.finalize(this.previous == null ? null : this.previous.getBid());
        this.ask.finalize(this.previous == null ? null : this.previous.getAsk());

        /* Marquage */
        this.finalized = true;
        this.owner.setLastBuild(this);
    }
}
