package com.akasoft.poneyrox.core.time.curves;

import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.threads.TimelineTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Enveloppe des courbes.
 */
public class AbstractCurveWrapper<TCell extends AbstractCell> {
    /**
     *  Liste des cellules constituantes.
     */
    private final List<TCell> cells;

    /**
     *  Liste des cellules consolidées.
     */
    private List<TCell> builds;

    /**
     *  Dernière cellule finalisée.
     */
    private TCell last;

    /**
     *  Constructeur.
     */
    public AbstractCurveWrapper() {
        this.cells = new ArrayList<>();
        this.builds = new ArrayList<>();
        this.last = null;
    }

    /**
     *  Retourne la liste des cellules.
     *  @return Liste des cellules.
     */
    protected synchronized List<TCell> getCells() {
        synchronized (this.cells) {
            return new ArrayList<>(this.cells);
        }
    }

    /**
     *  Retourne la liste des cellules finalisées.
     *  @return Liste des cellules finalisées.
     */
    public synchronized List<TCell> getBuilds() {
        synchronized (this.builds) {
            return new ArrayList<>(this.builds);
        }
    }

    /**
     *  Indique si la courbe contient une entrée finalisée.
     *  @return true si la courbe contient une entrée finalisée.
     */
    public synchronized boolean hasLastBuild() {
        return this.last != null;
    }

    /**
     *  Retourne la dernière cellule finalisée.
     *  @return Dernière cellule finalisée (ou nul).
     */
    public synchronized TCell getLastBuild() throws InnerException {
        synchronized (this.last) {
            if (this.last != null) {
                return this.last;
            }
            throw new InnerException("No build disponible for curve");
        }
    }

    /**
     *  Ajout d'une cellule.
     *  @param cell Cellule ajoutée.
     */
    protected synchronized void addCell(TCell cell) {
        synchronized (this.cells) {
            this.cells.add(cell);
        }
    }

    /**
     *  Définit la dernière cellule finalisée.
     *  @param last Dernière cellule finalisée.
     */
    public synchronized void setLastBuild(TCell last) {
        this.last = last;
        synchronized (this.builds) {
            this.builds.add(last);
        }
    }

    /**
     *  Nettoyage des cellules.
     */
    protected synchronized void clearCells() {
        synchronized (this.cells) {
            if (this.cells.size() > AbstractCurve.WIDTH) {
                this.cells.remove(0);
            }
        }
        synchronized (this.builds) {
            if (this.builds.size() > AbstractCurve.WIDTH) {
                this.builds.remove(0);
            }
        }
    }
}
