package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.batch.EntryBatch;
import com.akasoft.poneyrox.core.mixins.batch.ExitBatch;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *  Enveloppe de la tache de mixage.
 */
public abstract class MixerTaskWrapper extends AbstractTask {
    /**
     *  Tampon des positionnements.
     */
    private final Set<EntryBatch> entryBuffer;

    /**
     *  Tampon des sorties.
     */
    private final Set<ExitBatch> exitBuffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    protected MixerTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.entryBuffer = new HashSet<>();
        this.exitBuffer = new HashSet<>();
    }

    /**
     *  Retourne le tampon des positionnements.
     *  @return Tampons des positionnements.
     */
    protected synchronized Set<EntryBatch> getEntryBuffer() {
        synchronized (this.entryBuffer) {
            return new HashSet<>(this.entryBuffer);
        }
    }

    /**
     *  Retourne la taille du tampon des positionnements.
     *  @return Taille du tampon.
     */
    protected synchronized int getEntryBufferSize() {
        synchronized (this.entryBuffer) {
            return this.entryBuffer.size();
        }
    }

    /**
     *  Retourne le tampon des sorties.
     *  @return Tampon des sorties.
     */
    protected synchronized Set<ExitBatch> getExitBuffer() {
        synchronized (this.exitBuffer) {
            return new HashSet<>(this.exitBuffer);
        }
    }

    /**
     *  Retourne la taille du tampon de sortie.
     *  @return Taille du tampon.
     */
    protected synchronized int getExitBufferSize() {
        synchronized (this.exitBuffer) {
            return this.exitBuffer.size();
        }
    }

    /**
     *  Ajoute un élément dans le tampon d'entrée.
     *  @param batch Lot ajouté.
     */
    public synchronized void addEntryBuffer(EntryBatch batch) {
        synchronized (this.entryBuffer) {
            /* Sélection des entrées en doublon */
            List<EntryBatch> removes = this.entryBuffer.stream().filter(e -> {
                return e.getCurve().equals(batch.getCurve()) && e.getStrategy().equals(batch.getStrategy());
            }).collect(Collectors.toList());

            /* Nettoyage */
            this.entryBuffer.removeAll(removes);

            /* Ajout */
            this.entryBuffer.add(batch);
        }
    }

    /**
     *  Ajoute un élément dans le tampon de sortie.
     *  @param batch Lot ajouté.
     */
    public synchronized void addExitBuffer(ExitBatch batch) {
        synchronized (this.exitBuffer) {
            /* Sélection des entrées en doublon */
            List<ExitBatch> removes = this.exitBuffer.stream().filter(e -> {
                return e.getCurve().equals(batch.getCurve()) && e.getStrategy().equals(batch.getStrategy());
            }).collect(Collectors.toList());

            /* Nettoyage */
            this.exitBuffer.removeAll(removes);

            /* Ajout */
            this.exitBuffer.add(batch);
        }
    }

    /**
     *  Supprime un élément du tampon d'entrée.
     *  @param entry Elément supprimé.
     */
    protected synchronized void removeEntryBuffer(EntryBatch entry) {
        synchronized (this.entryBuffer) {
            this.entryBuffer.remove(entry);
        }
    }

    /**
     *  Supprime un élément du tampon de sortie.
     *  @param exit Elément supprimé.
     */
    protected synchronized void removeExitBuffer(ExitBatch exit) {
        synchronized (this.exitBuffer) {
            this.exitBuffer.remove(exit);
        }
    }
}
