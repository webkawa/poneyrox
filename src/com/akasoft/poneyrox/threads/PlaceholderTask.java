package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.leads.AbstractLead;
import com.akasoft.poneyrox.core.mixins.leads.EntryLead;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;
import com.akasoft.poneyrox.dao.MixinDAO;
import com.akasoft.poneyrox.dao.PositionDAO;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Tache de positionnement.
 *  Tache chargé de réaliser les prises ou sorties de position dépendamment des apports remontés
 *  par le mixer.
 */
public class PlaceholderTask extends PlaceholderTaskWrapper {
    /**
     *  DAO des positions.
     */
    private PositionDAO positionDAO;

    /**
     *  DAO des stratégies.
     */
    private MixinDAO mixinDAO;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public PlaceholderTask(
            ManagerComponent manager,
            PositionDAO positionDAO,
            MixinDAO mixinDAO) {
        super(manager);
        this.positionDAO = positionDAO;
        this.mixinDAO = mixinDAO;
    }

    @Override
    protected void execute() throws AbstractException {
        /* Parcours des stratégies d'entrée disponibles */
        for (AbstractCurve curve : this.getEntryCurves()) {
            if (super.isExitBuffer(curve)) {
                /* Extraction des pistes.
                 * Le programme doit disposer de deux listes de pistes, l'une en entrée, l'autre en sortie. */
                List<EntryLead> entryLeads = super.getEntryBuffer(curve);
                List<ExitLead> exitLeadsLong = super.getExitBuffer(curve).stream().filter(e -> e.getMode()).collect(Collectors.toList());
                List<ExitLead> exitLeadsShort = super.getExitBuffer(curve).stream().filter(e -> !e.getMode()).collect(Collectors.toList());

                /* Création des entrées simulées */
                int count = 0;
                int current = super.getManager().getRandomPositionsCount();
                for (int i = 0;
                     i < super.getWallet().getSimulationGrain()
                             && current < super.getWallet().getSimulationPool()
                             && i < entryLeads.size()
                             && i < exitLeadsLong.size()
                             && i < exitLeadsShort.size();
                     i++, current++) {
                    /* Sélection d'une stratégie d'entrée */
                    EntryLead entryLead = entryLeads.get(i);
                    MixinEntity entryMix = this.mixinDAO.retrieveOrPersist(
                            curve.getEntity(),
                            curve.getSmooth(),
                            entryLead.getPonderation(),
                            entryLead.getStrategies());

                    /* Sélection d'une stratégie de sortie */
                    ExitLead exitLead = exitLeadsLong.get(i);
                    if (!entryLead.getMode()) {
                        exitLead = exitLeadsShort.get(i);
                    }
                    MixinEntity exitMix = this.mixinDAO.retrieveOrPersist(
                            curve.getEntity(),
                            curve.getSmooth(),
                            exitLead.getPonderation(),
                            exitLead.getStrategies());

                    /* Création de la position */
                    PositionEntity position = this.positionDAO.persistPosition(
                            curve.getEntity(),
                            curve.getOwner().getCurrent(),
                            entryLead.getMode(),
                            PositionType.SIMULATION,
                            entryLead.getMode() ? entryLead.getLongScore() : entryLead.getShortScore(),
                            curve.getSmooth(),
                            entryMix,
                            exitMix);

                    /* Diffusion */
                    super.getManager().publishPosition(true, curve, position);
                    count++;
                }

                /* Nettoyage */
                super.clearEntryBuffer(curve);
                super.clearExitBuffer(curve);

                /* Affichage */
                if (count > 0) {
                    System.out.println("PUBLISHED " + count + " SIMULATIONS ON " + curve);
                }
            }
        }
    }

    /**
     *  Fonction d'ordonnancement des pondérations sur la basse de l'élément présentant la valeur la plus
     *  basse.
     *  @param left Pondérations à gauche.
     *  @param right Pondérations à droite.
     *  @return Valeur employée pour l'ordonnancement.
     */
    private static int orderPonderations(double[] left, double right[]) {
        /* Définition du coté présentant la pondération la plus basse */
        double minLeft = Arrays.stream(left).min().getAsDouble();
        double minRight = Arrays.stream(right).min().getAsDouble();
        double maxLeft = Arrays.stream(left).max().getAsDouble();
        double maxRight = Arrays.stream(right).max().getAsDouble();

        /* Renvoi */
        if (maxLeft < maxRight) {
            return -1;
        } else if (maxLeft > maxRight) {
            return 1;
        }
        return 0;
    }
}
