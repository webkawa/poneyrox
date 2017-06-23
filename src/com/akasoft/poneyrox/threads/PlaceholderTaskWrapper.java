package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.leads.EntryLead;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;

import java.util.*;

/**
 *  Enveloppe de la tache de placement.
 */
public abstract class PlaceholderTaskWrapper extends AbstractTask {

    /**
     *  Liste des pistes d'entrée en attente.
     *  Classées par courbe d'appartenance.
     */
    private final Map<AbstractCurve, List<EntryLead>> entryBuffer;

    /**
     *  Liste des pistes de sortie en attente.
     *  Classées par position de rattachement.
     */
    private final Map<AbstractCurve, List<ExitLead>> exitBuffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public PlaceholderTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.entryBuffer = new HashMap<>();
        this.exitBuffer = new HashMap<>();
    }

    /**
     *  Retourne la liste des courbes d'entrée.
     *  @return Liste des courbes d'entrée.
     */
    protected synchronized Set<AbstractCurve> getEntryCurves() {
        synchronized (this.entryBuffer) {
            return new HashSet<>(this.entryBuffer.keySet());
        }
    }

    /**
     *  Retourne la liste des courbes de sortie.
     *  @return Liste des courbes de sortie.
     */
    protected synchronized Set<AbstractCurve> getExitCurves() {
        synchronized (this.exitBuffer) {
            return new HashSet<>(this.exitBuffer.keySet());
        }
    }

    /**
     *  Retourne un élément du tampon d'entrée.
     *  @param curve Courbe d'accès.
     *  @return Liste des pistes.
     */
    protected synchronized List<EntryLead> getEntryBuffer(AbstractCurve curve) {
        synchronized (this.entryBuffer) {
            return new ArrayList<>(this.entryBuffer.get(curve));
        }
    }

    /**
     *  Indique si un tampon de sortie existe pour une courbe.
     *  @param curve Courbe.
     *  @return true si le tampon de sortie existe.
     */
    protected synchronized boolean isExitBuffer(AbstractCurve curve) {
        synchronized (this.exitBuffer) {
            return this.exitBuffer.containsKey(curve);
        }
    }

    /**
     *  Retourne un élément du tampon de sortie.
     *  @param curve Courbe d'accès.
     *  @return Liste des pistes.
     */
    protected synchronized List<ExitLead> getExitBuffer(AbstractCurve curve) {
        synchronized (this.exitBuffer) {
            return new ArrayList<>(this.exitBuffer.get(curve));
        }
    }

    /**
     *  Ajoute un élément au tampon d'entrée.
     *  @param curve Courbe d'accès.
     *  @param lead Elément ajouté.
     */
    public synchronized void addEntryBuffer(AbstractCurve curve, EntryLead lead) {
        synchronized (this.entryBuffer) {
            if (!this.entryBuffer.containsKey(curve)) {
                this.entryBuffer.put(curve, new ArrayList<>());
            }
            this.entryBuffer.get(curve).add(lead);
        }
    }

    /**
     *  Ajoute un élément au tampon de sortie.
     *  @param curve Courbe d'accès.
     *  @param lead Piste ajoutée.
     */
    public synchronized void addExitBuffer(AbstractCurve curve, ExitLead lead) {
        synchronized (this.exitBuffer) {
            if (!this.exitBuffer.containsKey(curve)) {
                this.exitBuffer.put(curve, new ArrayList<>());
            }
            this.exitBuffer.get(curve).add(lead);
        }
    }

    /**
     *  Nettoie un tampon d'entrée.
     *  @param curve Courbe d'accès.
     */
    protected synchronized void clearEntryBuffer(AbstractCurve curve) {
        synchronized (this.entryBuffer) {
            this.entryBuffer.remove(curve);
        }
    }

    /**
     *  Nettoie un tampon de sortie.
     *  @param curve Courbe d'accès.
     */
    protected synchronized void clearExitBuffer(AbstractCurve curve) {
        synchronized (this.exitBuffer) {
            this.exitBuffer.remove(curve);
        }
    }
}
