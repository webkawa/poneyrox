package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;

import java.util.ArrayList;
import java.util.List;

/**
 *  Enveloppe des taches stratégiques.
 */
public abstract class StrategyTaskWrapper extends AbstractTask {
    /**
     *  Courbes placées dans le tampon.
     */
    private final List<AbstractCurve> buffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public StrategyTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.buffer = new ArrayList<>();
    }

    /**
     *  Retourne la taille du tampon.
     *  @return Taille du tampon.
     */
    protected synchronized int getBufferSize() {
        synchronized (this.buffer) {
            return this.buffer.size();
        }
    }

    /**
     *  Retourne le contenu du tampon.
     *  @return Contenu du tampon.
     */
    protected synchronized List<AbstractCurve> getBuffer() {
        synchronized (this.buffer) {
            return new ArrayList<>(this.buffer);
        }
    }

    /**
     *  Ajoute une courbe dans le tampon.
     *  @param curve Courbe ajoutée.
     */
    public synchronized void addBuffer(AbstractCurve curve) {
        synchronized (this.buffer) {
            this.buffer.add(curve);
        }
    }

    /**
     *  Nettoie le contenu du tampon.
     */
    protected synchronized void clearBuffer() {
        synchronized (this.buffer) {
            this.buffer.clear();
        }
    }
}
