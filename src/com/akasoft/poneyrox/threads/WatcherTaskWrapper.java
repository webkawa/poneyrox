package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.RateEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Enveloppe de la tache d'observation.
 */
public abstract class WatcherTaskWrapper extends AbstractTask {
    /**
     *  Tampon.
     *  Date des dernières mises à jour reçues pour chaque marché.
     */
    private Map<MarketEntity, RateEntity> buffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public WatcherTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.buffer = new HashMap<>();
    }

    /**
     *  Retourne la liste des taux.
     *  @return Liste des taux.
     */
    public synchronized List<RateEntity> getRates() {
        synchronized (this.buffer) {
            return new ArrayList<>(this.buffer.values());
        }
    }

    /**
     *  Indique si le tampon contient un marché.
     *  @param market Marché ciblé.
     *  @return true si le tampon contient le marché.
     */
    protected synchronized boolean hasRate(MarketEntity market) {
        synchronized (this.buffer) {
            return this.buffer.containsKey(market);
        }
    }

    /**
     *  Retourne le taux lié à un marché.
     *  @param market Marché recherché.
     *  @return Taux correspondant.
     */
    protected synchronized RateEntity getRate(MarketEntity market) {
        synchronized (this.buffer) {
            return this.buffer.get(market);
        }
    }

    /**
     *  Ajoute un élément au tampon.
     *  @param market Marché.
     *  @param rate Taux.
     */
    protected synchronized void addRate(MarketEntity market, RateEntity rate) {
        synchronized (this.buffer) {
            this.buffer.put(market, rate);
        }
    }
}
