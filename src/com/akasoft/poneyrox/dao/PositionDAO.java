package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.dto.PerformanceDTO;
import com.akasoft.poneyrox.dto.PerformanceTransformer;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.exceptions.InnerException;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  DAO des positions.
 */
@Repository
public class PositionDAO extends AbstractDAO {
    /**
     *  Retourne le transformateur de performances.
     */
    private PerformanceTransformer transformer;

    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public PositionDAO(@Autowired SessionFactory factory) {
        super(factory);
        this.transformer = new PerformanceTransformer();
    }

    /**
     *  Insère une position dans la base.
     *  @param rate Taux à l'entrée.
     *  @param mode Mode (true pour long, false pour court).
     *  @param type Type de position.
     *  @param score Score à l'entrée.
     *  @param smooth Niveau de lissage.
     *  @param entryMixin Stratégie à l'entrée.
     *  @param exitMixin Stratégie de sortie.
     *  @return Position créée.
     */
    public PositionEntity persistPosition(TimelineEntity timeline, RateEntity rate, boolean mode, PositionType type, double score, int smooth, MixinEntity entryMixin, MixinEntity exitMixin) {
        PositionEntity position = new PositionEntity();
        position.setOpen(true);
        position.setMode(mode);
        position.setType(type);
        position.setEntryScore(score);
        position.setTimeline(timeline);
        position.setSmooth(smooth);
        position.setEntry(mode ? rate.getAsk() : rate.getBid());
        position.setEntryMix(entryMixin);
        position.setExitMix(exitMixin);

        super.getSession().persist(position);
        return position;
    }

    /**
     *  Supprime une position.
     *  @param position Position supprimée.
     */
    public void removePosition(PositionEntity position) {
        super.getSession().remove(position);
    }

    /**
     *  Réalise la fermeture de la position.
     *  @param position Position fermée.
     *  @param re Taux à la fermeture.
     *  @param score Score à la fermeture.
     *  @param timeout Indique si la position a expiré.
     *  @return Position fermée.
     */
    public PositionEntity closePosition(RateEntity re, PositionEntity position, double score, boolean timeout) {
        /* Calcul du cours */
        if (position.getMode())  {
            position.setExit(re.getBid());
            position.setProfit(position.getExit() - position.getEntry());
        } else {
            position.setExit(re.getAsk());
            position.setProfit(position.getEntry() - position.getExit());
        }

        /* Fermeture */
        position.setExitScore(score);
        position.setOpen(false);
        position.setTimeout(timeout);
        position.setEnd(new java.util.Date().getTime());
        super.getSession().update(position);

        /* Renvoi */
        return position;
    }

    /**
     *  Retourne la liste des positions les plus rentables.
     *  @param type Type de position recherché.
     *  @param mode Mode.
     *  @param limit Limite.
     *  @return Liste des positions les plus rentables.
     */
    public List<PositionEntity> getTopPositions(PositionType type, boolean mode, int limit) {
        return super.getSession()
                .getNamedQuery("Position.getTopPositions")
                .setParameter("type", type)
                .setParameter("mode", mode)
                .setMaxResults(limit)
                .list();
    }

    /**
     *  Retourne la liste des stratégies les plus performantes pour placement en test.
     *  @param start Date de départ minimum.
     *  @param limit Nombre d'entrées sélectionnées.
     *  @param confirmations Nombre maximum de confirmations toléré.
     *  @return Liste des stratégies les plus performantes.
     */
    public List<PerformanceDTO> getTopStrategiesForTesting(long start, int limit, long confirmations) {
        return super.getSession()
                .getNamedQuery("Position.getTopStrategiesForTesting")
                .setParameter("ttype", PositionType.TEST)
                .setParameter("vtype", PositionType.VIRTUAL)
                .setParameter("start", start)
                .setParameter("confirmations", confirmations)
                .setMaxResults(limit)
                .setResultTransformer(this.transformer)
                .list();
    }

    /**
     *  Retourne la liste des stratégies les plus performances pour placement en production.
     *  @param start Date de départ.
     *  @param percent Pourcentage minimum recherché.
     *  @param confirmations Nombre de confirmations attendues.
     *  @param feeSpread Frais sur le spread.
     *  @param limit Nombre d'entrées sélectionnées.
     *  @return Liste des stratégies les plus performantes.
     */
    public List<PerformanceDTO> getTopStrategiesForProduction(long start, double percent, long confirmations, double feeSpread, int limit) {
        return super.getSession()
                .getNamedQuery("Position.getTopStrategiesForProduction")
                .setParameter("ttype", PositionType.TEST)
                .setParameter("vtype", PositionType.VIRTUAL)
                .setParameter("start", start)
                .setParameter("percent", percent)
                .setParameter("confirmations", confirmations)
                .setParameter("feeSpread", feeSpread)
                .setMaxResults(limit)
                .setResultTransformer(this.transformer)
                .list();
    }

    /**
     *  Retourne la liste des dernières positions référencées pour une stratégie donnée.
     *  @param timeline Ligne temporelle ciblée.
     *  @param smooth Niveau de lissage.
     *  @param mode Mode de la position.
     *  @param entry Stratégie d'entrée.
     *  @param exit Stratégie de sortie.
     *  @param limit Nombre maximum de résultats conservés.
     *  @return Liste des dernières positions.
     */
    public List<PositionEntity> getLastPositionsByStrategy(TimelineEntity timeline, int smooth, boolean mode, MixinEntity entry, MixinEntity exit, int limit) {
        return super.getSession()
                .getNamedQuery("Position.getLastPositionsByStrategy")
                .setParameter("timeline", timeline)
                .setParameter("smooth", smooth)
                .setParameter("mode", mode)
                .setParameter("entry", entry)
                .setParameter("exit", exit)
                .setMaxResults(limit)
                .list();
    }

    /**
     *  Supprime l'ensemble des simulations expirées.
     */
    public void deleteExpiredSimulations() {
        super.getSession()
                .getNamedQuery("Position.deleteExpiredSimulations")
                .setParameter("type", PositionType.SIMULATION)
                .executeUpdate();
    }

    /**
     *  Supprime les simulations non-retenues pour des tests complémentaires.
     *  @param start Date de départ.
     *  @param profit Profit journalier minimum (négatif !).
     *  @throws InnerException En cas d'erreur interne.
     */
    public void deleteUntestedSimulations(long start, double profit) throws InnerException {
        if (profit > 0) {
            throw new InnerException("Try to delete positive simulations...");
        }

        super.getSession()
                .getNamedQuery("Position.deleteUntestedSimulations")
                .setParameter("type", PositionType.SIMULATION)
                .setParameter("start", start)
                .setParameter("profit", profit)
                .executeUpdate();
    }

    /**
     *  Supprime toutes les positions finalisées depuis un certain délai.
     *  @param start Date de départ.
     */
    public void deleteAllOldPositions(long start) {
        super.getSession()
                .getNamedQuery("Position.deleteAllOldPositions")
                .setParameter("vtype", PositionType.VIRTUAL)
                .setParameter("start", start)
                .executeUpdate();
    }

    /**
     *  Supprime toutes les positions ouvertes.
     */
    public void deleteAllOpenPositions() {
        super.getSession()
                .getNamedQuery("Position.deleteAllOpenPositions")
                .executeUpdate();
    }
}
