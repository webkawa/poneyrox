package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.RawCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Enveloppe des taches temporelles.
 */
public abstract class TimelineTaskWrapper extends AbstractTask {
    /**
     *  Courbe brute.
     */
    private RawCurve raw;

    /**
     *  Courbes lissées.
     */
    private final List<SmoothCurve> smooth;

    /**
     *  Tampon.
     */
    private final List<RateEntity> buffer;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public TimelineTaskWrapper(ManagerComponent manager) {
        super(manager);
        this.buffer = new ArrayList<>();
        this.raw = raw;
        this.smooth = new ArrayList<>();
    }

    /**
     *  Retourne la courbe brute.
     *  @return Courbe brute.
     */
    public synchronized RawCurve getRaw() {
        synchronized (this.raw) {
            return this.raw;
        }
    }

    /**
     *  Retourne la liste des courbes lissées.
     *  @return Liste des courbes.
     */
    public synchronized List<SmoothCurve> getSmooth() {
        synchronized (this.smooth) {
            return new ArrayList<>(this.smooth);
        }
    }

    /**
     *  Retourne la liste des courbes.
     *  @return Liste des courbes.
     */
    public synchronized List<AbstractCurve> getCurves() {
        synchronized (this.raw) {
            synchronized (this.smooth) {
                List<AbstractCurve> result = new ArrayList<>();
                result.add(this.raw);
                result.addAll(this.smooth);
                return result;
            }
        }
    }

    /**
     *  Retourne une courbe inscrite dans la ligne temporelle.
     *  @param smooth Niveau de lissage.
     *  @return Courbe inscrite.
     *  @throws InnerException En cas de niveau de lissage invalide.
     */
    public synchronized AbstractCurve getCurve(int smooth) throws InnerException {
        synchronized (this.raw) {
            synchronized (this.smooth) {
                if (smooth == 1) {
                    return this.raw;
                } else {
                    for (SmoothCurve curve : this.smooth) {
                        if (curve.getLevel() == smooth) {
                            return curve;
                        }
                    }
                }
                throw new InnerException("Failed to retrieve curve for smooth level %d", smooth);
            }
        }
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
     *  Retourne le tampon.
     *  @return Contenu du tampon.
     */
    protected synchronized List<RateEntity> getBuffer() {
        synchronized (this.buffer) {
            return new ArrayList<>(this.buffer);
        }
    }

    /**
     *  Définit la courbe brute.
     *  @param raw Valeur affectée.
     */
    protected synchronized void setRaw(RawCurve raw) {
        this.raw = raw;
    }

    /**
     *  Ajoute une courbe lissée.
     *  @param smooth Courbe ajoutée.
     */
    protected synchronized void addSmooth(SmoothCurve smooth) {
        synchronized (this.smooth) {
            this.smooth.add(smooth);
        }
    }

    /**
     *  Ajoute un élément dans le tampon.
     *  @param rate Elément ajouté.
     */
    protected synchronized void addBuffer(RateEntity rate) {
        synchronized (this.buffer) {
            this.buffer.add(rate);
        }
    }

    /**
     *  Nettoie le tampon.
     */
    protected synchronized void clearBuffer() {
        synchronized (this.buffer) {
            this.buffer.clear();
        }
    }

    /**
     *  Intègre une liste d'élément à la courbe brute.
     *  @param rate Liste des taux intégrés.
     *  @throws InnerException En cas d'erreur lors de l'intégration.
     */
    protected synchronized void integrateRaw(List<RateEntity> rate) throws InnerException {
        synchronized (this.raw) {
            this.raw.integrate(rate);
        }
    }
}
