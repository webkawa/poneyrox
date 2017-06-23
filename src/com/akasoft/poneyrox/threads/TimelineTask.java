package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.time.cells.RawCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.RawCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;
import com.akasoft.poneyrox.dto.CellDTO;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

/**
 *  Tache de gestion d'une ligne temporelle.
 *  Tache planifiée chargée de la constitution et du rafraichissement d'une ligne temporelle.
 */
public class TimelineTask extends TimelineTaskWrapper {
    /**
     *  Niveau maximal de lissage.
     */
    public final static int SMOOTH = 6;

    /**
     *  Ligne de temps observée.
     */
    private final TimelineEntity timeline;

    /**
     *  Taux le plus récent.
     */
    private RateEntity current;

    /**
     *  Promesse rattachée à la tache.
     */
    private ScheduledFuture future;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public TimelineTask(ManagerComponent manager, TimelineEntity timeline) {
        super(manager);
        /* Paramètres */
        this.timeline = timeline;
        this.current = null;
        this.future = null;
        super.setRaw(new RawCurve(this, this.timeline.getSize()));

        /* Courbes */
        for (int i = 1, j = 2; i < TimelineTask.SMOOTH; i++, j *= 2) {
            SmoothCurve add = new SmoothCurve(this, j);
            super.addSmooth(add);
        }
    }

    /**
     *  Retourne l'entité rattachée.
     *  @return Entité rattachée.
     */
    public TimelineEntity getTimeline() {
        return this.timeline;
    }

    /**
     *  Retourne la promesse rattachée à la tache.
     *  @return Promesse rattachée.
     */
    public ScheduledFuture getFuture() {
        return this.future;
    }

    /**
     *  Retourne le taux courant.
     *  @return Taux courant.
     */
    public RateEntity getCurrent() {
        return this.current;
    }

    /**
     *  Retourne les derniers taux disponibles pour la ligne temporelle.
     *  @return Derniers taux disponibles.
     *  @throws InnerException En cas d'erreur interne.
     */
    public List<CellDTO> getLast() throws InnerException {
        /* Initialisation */
        List<CellDTO> result = new ArrayList<>();

        /* Courbe brute */
        RawCurve raw = super.getRaw();
        if (raw.hasLastBuild()) {
            CellDTO add = new CellDTO(1, raw.getLastBuild());
            result.add(add);
        }

        /* Courbes lissées */
        for (SmoothCurve curve : super.getSmooth()) {
            if (curve.hasLastBuild()) {
                CellDTO add = new CellDTO(curve.getLevel(), curve.getLastBuild());
                result.add(add);
            }
        }

        /* Renvoi */
        return result;
    }

    /**
     *  Définit la promesse rattachée à la tache.
     *  @param future Promesse rattachée.
     */
    public void setFuture(ScheduledFuture future) {
        this.future = future;
    }

    /**
     *  Enregistre un taux dans le tampon.
     *  @param rate Taux ajouté.
     */
    public void addBuffer(RateEntity rate) {
        super.addBuffer(rate);
        this.current = rate;
    }

    /**
     *  Exécution.
     *  @throws AbstractException En cas d'erreur lors du traitement.
     */
    @Override
    protected void execute() throws AbstractException {
        if (super.getBufferSize() > 0) {
            /* Mise en cache */
            List<RateEntity> backup = this.getBuffer();
            super.clearBuffer();

            /* Intégration à la courbe brute */
            super.integrateRaw(backup);

            /* Calcul des courbes lissées */
            for (SmoothCurve curve : super.getSmooth()) {
                List<RawCell> history = this.getRaw().getLast(curve.getLevel());
                if (curve.getLevel() <= history.size()) {
                    curve.integrate(history);
                }
            }
        }
    }
}
