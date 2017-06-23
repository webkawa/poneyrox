package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.WalletEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 *  Enveloppe de la tache de consolidation.
 */
public abstract class ConsolidationTaskWrapper extends AbstractTask {

    /**
     *  Liste des courbes.
     */
    private final List<AbstractCurve> curves;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire.
     */
    public ConsolidationTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.curves = new ArrayList<>();
    }

    /**
     *  Indique si une courbe est disponible.
     *  @param timeline Ligne temporelle ciblée.
     *  @param smooth Niveau de lissage.
     *  @return true si la courbe est disponible.
     */
    public synchronized boolean hasCurve(TimelineEntity timeline, int smooth) {
        synchronized (this.curves) {
            return this.getOptionalCurve(timeline, smooth).isPresent();
        }
    }

    /**
     *  Retourne une des courbes disponibles.
     *  @param timeline Ligne temporelle signée.
     *  @param smooth Niveau de lissage.
     *  @return Courbe correspondate.
     */
    public synchronized AbstractCurve getCurve(TimelineEntity timeline, int smooth) {
        synchronized (this.curves) {
            return this.getOptionalCurve(timeline, smooth).get();
        }
    }

    /**
     *  Ajoute une courbe à la liste.
     *  @param curve Courbe ajoutée.
     */
    public synchronized void addCurve(AbstractCurve curve) {
        synchronized (this.curves) {
            this.curves.add(curve);
        }
    }

    /**
     *  Recherche et retourne une courbe par lissage.
     *  @param timeline Ligne temporelle ciblée.
     *  @param smooth Niveau de lissage.
     *  @return Courbe correspondante.
     */
    private synchronized Optional<AbstractCurve> getOptionalCurve(TimelineEntity timeline, int smooth) {
        synchronized (this.curves) {
            return this.curves
                    .stream()
                    .filter(e -> e.getEntity().getId().equals(timeline.getId()) && e.getSmooth() == smooth)
                    .findFirst();
        }
    }
}
