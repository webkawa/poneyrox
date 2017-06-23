package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.TransactionEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Enveloppe de la tache de suivi.
 */
public abstract class FollowerTaskWrapper extends AbstractTask {
    /**
     *  Tampon des simulations aléatoires.
     */
    private final Map<AbstractCurve, List<PositionEntity>> randomBuffer;

    /**
     *  Tampon des simulations ciblées.
     */
    private final Map<AbstractCurve, List<PositionEntity>> targetedBuffer;

    /**
     *  Tampon des transactions virtuelles.
     */
    private final Map<AbstractCurve, List<TransactionEntity>> virtualBuffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public FollowerTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.randomBuffer = new HashMap<>();
        this.targetedBuffer = new HashMap<>();
        this.virtualBuffer = new HashMap<>();
    }

    /**
     *  Retourne la liste des courbes d'accès au tampon aléatoire.
     *  @return Liste des courbes d'accès.
     */
    protected synchronized List<AbstractCurve> getRandomKeys() {
        synchronized (this.randomBuffer) {
            return new ArrayList<>(this.randomBuffer.keySet());
        }
    }

    /**
     *  Retourne la liste des courbes d'accès au tampon ciblé.
     *  @return Liste des courbes d'accès.
     */
    protected synchronized List<AbstractCurve> getTargetedKeys() {
        synchronized (this.targetedBuffer) {
            return new ArrayList<>(this.targetedBuffer.keySet());
        }
    }

    /**
     *  Retourne la liste des courbes d'accès au tampon virtual.
     *  @return Liste des courbes d'accès.
     */
    protected synchronized List<AbstractCurve> getVirtualKeys() {
        synchronized (this.virtualBuffer) {
            return new ArrayList<>(this.virtualBuffer.keySet());
        }
    }

    /**
     *  Retourne un composant du tampon aléatoire.
     *  @param key Courbe d'accès.
     *  @return Contenu du tampon.
     */
    protected synchronized List<PositionEntity> getRandomBuffer(AbstractCurve key) {
        synchronized (this.randomBuffer) {
            return new ArrayList<>(this.randomBuffer.get(key));
        }
    }

    /**
     *  Retourne un composant du tampon ciblé.
     *  @param key Courbe d'accès.
     *  @return Contenu du tampon.
     */
    protected synchronized List<PositionEntity> getTargetedBuffer(AbstractCurve key) {
        synchronized (this.targetedBuffer) {
            return new ArrayList<>(this.targetedBuffer.get(key));
        }
    }

    /**
     *  Retourne un composant du tampon virtual.
     *  @param key Courbe d'accès.
     *  @return Contenu du tampon.
     */
    protected synchronized List<TransactionEntity> getVirtualBuffer(AbstractCurve key) {
        synchronized (this.virtualBuffer) {
            return new ArrayList<>(this.virtualBuffer.get(key));
        }
    }

    /**
     *  Compte le nombre de positions dans le tampon aléatoire.
     *  @return Nombre de positions.
     */
    public synchronized int getRandomBufferSize() {
        synchronized (this.randomBuffer) {
            int result = 0;
            for (AbstractCurve curve : this.randomBuffer.keySet()) {
                result += this.randomBuffer.get(curve).size();
            }
            return result;
        }
    }

    /**
     *  Retourne le nombre de positions dans le tampon ciblé.
     *  @return Nombre de positions.
     */
    public synchronized int getTargetedBufferSize() {
        synchronized (this.targetedBuffer) {
            int result = 0;
            for (AbstractCurve curve : this.targetedBuffer.keySet()) {
                result += this.targetedBuffer.get(curve).size();
            }
            return result;
        }
    }

    /**
     *  Retourne le nombre de transactions virtuelles dans le tampon.
     *  @return Nombre de transactions virtuelles.
     */
    public synchronized int getVirtualBufferSize() {
        synchronized (this.virtualBuffer) {
            int result = 0;
            for (AbstractCurve curve : this.virtualBuffer.keySet()) {
                result += this.virtualBuffer.get(curve).size();
            }
            return result;
        }
    }

    /**
     *  Ajoute une entrée dans le tampon des simulations aléatoires.
     *  @param curve Courbe concernée.
     *  @param entity Entrée ajoutée.
     */
    public synchronized void addRandomBuffer(AbstractCurve curve, PositionEntity entity) {
        synchronized (this.randomBuffer) {
            if (!this.randomBuffer.containsKey(curve)) {
                this.randomBuffer.put(curve, new ArrayList<>());
            }
            this.randomBuffer.get(curve).add(entity);
        }
    }

    /**
     *  Ajoute une entrée dans le tampon des simulations
     *  @param curve Courbe concernée.
     *  @param entity Entité ajoutée.
     */
    public synchronized void addTargetedBuffer(AbstractCurve curve, PositionEntity entity) {
        synchronized (this.targetedBuffer) {
            if (!this.targetedBuffer.containsKey(curve)) {
                this.targetedBuffer.put(curve, new ArrayList<>());
            }
            this.targetedBuffer.get(curve).add(entity);
        }
    }

    /**
     *  Ajoute une entrée dans le tampon virtual.
     *  @param curve Courbe concernée.
     *  @param entity Entité ajoutée.
     */
    public synchronized void addVirtualBuffer(AbstractCurve curve, TransactionEntity entity) {
        synchronized (this.virtualBuffer) {
            if (!this.virtualBuffer.containsKey(curve)) {
                this.virtualBuffer.put(curve, new ArrayList<>());
            }
            this.virtualBuffer.get(curve).add(entity);
        }
    }

    /**
     *  Retire une liste d'entrées du tampon des simulations.
     *  @param curve Courbe d'accès.
     *  @param entity Entrées retirées.
     */
    protected synchronized void removeRandomBuffer(AbstractCurve curve, List<PositionEntity> entity) {
        synchronized (this.randomBuffer) {
            this.randomBuffer.get(curve).removeAll(entity);
        }
    }

    /**
     *  Retire une liste d'entrées du tampon ciblés.
     *  @param curve Courbe d'accès.
     *  @param entity Entités retirées.
     */
    protected synchronized void removeTargetedBuffer(AbstractCurve curve, List<PositionEntity> entity) {
        synchronized (this.targetedBuffer) {
            this.targetedBuffer.get(curve).removeAll(entity);
        }
    }

    /**
     *  Retourne une liste d'entrées du tampon virtuel.
     *  @param curve Courbe d'accès.
     *  @param entities Entrée retirées.
     */
    protected synchronized void removeVirtualBuffer(AbstractCurve curve, List<TransactionEntity> entities) {
        synchronized (this.virtualBuffer) {
            this.virtualBuffer.get(curve).remove(entities);
        }
    }
}
