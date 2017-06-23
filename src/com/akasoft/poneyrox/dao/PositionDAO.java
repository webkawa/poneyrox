package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.dto.PerformanceDTO;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import org.hibernate.SessionFactory;
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
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public PositionDAO(@Autowired SessionFactory factory) {
        super(factory);
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
        List<Object[]> source = super.getSession()
                .getNamedQuery("Position.getTopStrategiesForTesting")
                .setParameter("type", PositionType.TEST)
                .setParameter("start", start)
                .setParameter("confirmations", confirmations)
                .setMaxResults(limit)
                .list();
        return this.toPerformance(source);
    }

    /**
     *  Retourne la liste des stratégies les plus performances pour placement en production.
     *  @param start Date de départ.
     *  @param percent Pourcentage minimum recherché.
     *  @param confirmations Nombre de confirmations attendues.
     *  @param limit Nombre d'entrées sélectionnées.
     *  @return Liste des stratégies les plus performantes.
     */
    public List<PerformanceDTO> getTopStrategiesForProduction(long start, double percent, long confirmations, int limit) {
        List<Object[]> source =  super.getSession()
                .getNamedQuery("Position.getTopStrategiesForProduction")
                .setParameter("start", start)
                .setParameter("percent", percent)
                .setParameter("confirmations", confirmations)
                .setMaxResults(limit)
                .list();
        return this.toPerformance(source);
    }

    /**
     *  Supprime l'ensemble des simulations plus anciennes qu'une date donnée et présentant un seuil
     *  de rentabilité journalier inférieur à un score donné.
     *  @param start Date de départ.
     *  @param minimum Score minimum pour suppression (négatif !).
     */
    public void deleteFailedSimulations(long start, double minimum) {
        super.getSession()
                .getNamedQuery("Position.deleteFailedSimulations")
                .setParameter("type", PositionType.SIMULATION)
                .setParameter("start", start)
                .setParameter("daily", minimum)
                .executeUpdate();
    }

    /**
     *  Nettoie les positions d'un type donné dont la date de départ précède une date passée en paramètre.
     *  @param type Type de position.
     *  @param start Date de départ.
     */
    public void deleteUselessPositions(PositionType type, long start) {
        super.getSession()
                .getNamedQuery("Position.deleteUselessPositions")
                .setParameter("type", type)
                .setParameter("start", start)
                .executeUpdate();
    }

    /**
     *  Retourne une liste de performances générées à partir d'un résultat brut.
     *  @param source Résultat brut.
     *  @return Liste de performances.
     */
    private List<PerformanceDTO> toPerformance(List<Object[]> source) {
        return source.stream()
                .map(raw -> {
                    PerformanceDTO result = new PerformanceDTO();
                    result.setProfit((double) raw[0]);
                    result.setTimeline((TimelineEntity) raw[1]);
                    result.setSmooth((int) raw[2]);
                    result.setMode((boolean) raw[3]);
                    result.setEntryMix((MixinEntity) raw[4]);
                    result.setExitMix((MixinEntity) raw[5]);
                    return result;
                })
                .collect(Collectors.toList());
    }
}
