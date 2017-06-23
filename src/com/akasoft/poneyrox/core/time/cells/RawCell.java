package com.akasoft.poneyrox.core.time.cells;

import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Cellule brute.
 *  Cellule basée sur des taux bruts.
 */
public class RawCell extends RawCellWrapper {
    /**
     *  Constructeur avec précédent.
     *  @param start Date de la cellule.
     *  @param source Entité source.
     *  @param owner Courbe propriétaire.
     *  @param previous Cellule précédente.
     */
    public RawCell(long start, RateEntity source, AbstractCurve owner, RawCell previous) {
        super(
                previous,
                owner,
                start,
                source,
                new Cluster(
                        previous.getAsk(),
                        source.getAsk(),
                        source.getAsk() == previous.getAsk().getLast() ?
                                previous.getAsk().getDirection() :
                                source.getAsk() > previous.getAsk().getLast()),
                new Cluster(
                        previous.getBid(),
                        source.getBid(),
                        source.getBid() == previous.getBid().getLast() ?
                                previous.getBid().getDirection() :
                                source.getBid() > previous.getBid().getLast()));
    }

    /**
     *  Constructeur sans précédent.
     *  @param start Date de la cellule.
     *  @param source Entité source.
     *  @param owner Courbe propriétaire.
     */
    public RawCell(long start, RateEntity source, AbstractCurve owner) {
        super(
                null,
                owner,
                start,
                source,
                new Cluster(null, source.getAsk(), false),
                new Cluster(null, source.getBid(), false)
        );
    }

    /**
     *  Intègre un taux dans la cellule.
     *  @param rate Taux intégré.
     */
    public void integrate(RateEntity rate) {
        /* Gestion de la demande */
        Cluster ask = super.getAsk();
        if (ask.getMinimum() > rate.getAsk()) {
            ask.setMinimum(rate.getAsk());
        }
        if (ask.getMaximum() < rate.getAsk()) {
            ask.setMaximum(rate.getAsk());
        }

        /* Gestion de l'offre */
        Cluster bid = super.getBid();
        if (bid.getMinimum() > rate.getBid()) {
            bid.setMinimum(rate.getBid());
        }
        if (bid.getMaximum() < rate.getBid()) {
            bid.setMaximum(rate.getBid());
        }

        /* Gestion de la dernière valeur */
        ask.setLast(rate.getAsk());
        bid.setLast(rate.getBid());

        /* Ajout à l'historique */
        super.addRate(rate);
    }

    /**
     *  Complète le calcul avec prise en compte du précédent.
     *  @param previous Cellule précédente.
     *  @throws InnerException En cas d'erreur de calcul.
     */
    public void complete(RawCell previous) throws InnerException {
        this.complete();

        if (this.getAsk().getAverage() == previous.getAsk().getAverage()) {
            this.getAsk().setDirection(previous.getAsk().getDirection());
        } else {
            this.getAsk().setDirection(this.getAsk().getAverage() > previous.getAsk().getAverage());
        }

        if (this.getBid().getAverage() == previous.getBid().getAverage()) {
            this.getBid().setDirection(previous.getBid().getDirection());
        } else {
            this.getBid().setDirection(this.getBid().getAverage() > previous.getBid().getAverage());
        }
    }

    /**
     *  Complète le calcul de la cellule.
     *  @throws InnerException En cas d'erreur de calcul.
     */
    public void complete() throws InnerException {
        /* Récupération de l'historique */
        List<RateEntity> history = super.getHistory();

        /* Calcul des moyennes */
        double askBuff = 0;
        double bidBuff = 0;
        for (RateEntity rate : history) {
            askBuff += rate.getAsk();
            bidBuff += rate.getBid();
        }

        /* Mise à jour des moyennes */
        super.getAsk().setAverage(askBuff / history.size());
        super.getBid().setAverage(bidBuff / history.size());

        /* Mise à jour des paramètres */
        super.achieve();
    }
}
