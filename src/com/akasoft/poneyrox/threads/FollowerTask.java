package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.api.whaleclub.dao.WhaleClubAccess;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubPositionDTO;
import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.dao.PositionDAO;
import com.akasoft.poneyrox.dao.TransactionDAO;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.TransactionEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.exceptions.InnerException;
import javafx.geometry.Pos;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Tache de suivi.
 *  Tache chargée du suivi des positionnements en cours.
 */
public class FollowerTask extends FollowerTaskWrapper {
    /**
     *  Accès à l'API.
     */
    private WhaleClubAccess access;

    /**
     *  DAO des positions.
     */
    private PositionDAO positionDAO;

    /**
     *  DAO des transactions.
     */
    private TransactionDAO transactionDAO;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     *  @param access Point d'accès à l'API.
     *  @param positionDAO DAO des positions.
     *  @param transactionDAO DAO des transactions.
     */
    public FollowerTask(
            ManagerComponent manager,
            WhaleClubAccess access,
            PositionDAO positionDAO,
            TransactionDAO transactionDAO) {
        super(manager);
        this.access = access;
        this.positionDAO = positionDAO;
        this.transactionDAO = transactionDAO;
    }

    /**
     *  Exécution.
     *  @throws AbstractException En cas d'erreur lors du traitement.
     */
    @Override
    @Transactional
    protected void execute() throws AbstractException {
        /* Parcours des positions virtuelles */
        for (AbstractCurve curve : super.getVirtualKeys()) {
            /* Récupération de la liste de transactions */
            List<TransactionEntity> transactions = super.getVirtualBuffer(curve);

            /* Extractions des positions */
            List<PositionEntity> positions = transactions
                    .stream()
                    .map(e -> e.getPosition())
                    .collect(Collectors.toList());

            /* Traitement */
            List<PositionEntity> removes = this.treat(curve, positions, true);
            List<TransactionEntity> cleanup = new ArrayList<>();

            /* Mise à jour des transactions */
            for (TransactionEntity transaction : transactions) {
                this.transactionDAO.updateStopLoss(transaction, transaction.getPosition().getStopLoss());
            }

            /* Cloture des transactions */
            for (PositionEntity remove : removes) {
                try {
                    /* Récupération de la transaction */
                    TransactionEntity transaction = transactions.stream()
                            .filter(e -> e.getPosition().getId().equals(remove.getId()))
                            .findFirst()
                            .get();

                    /* Cloture */
                    WhaleClubPositionDTO dto = this.access.closePosition(transaction);
                    cleanup.add(transaction);
                    this.transactionDAO.closeTransaction(transaction, dto.getClosePrice(), dto.getProfit(), dto.getFinancing());

                    /* Affichage */
                    System.out.println("DEAL CLOSED ! PROFIT IS " + dto.getProfit());
                } catch (Exception cause) {
                    System.out.println("FAILED TO CLOSE TRANSACTION - That kinda sucks...");
                    cause.printStackTrace();
                }
            }

            /* Nettoyage */
            super.removeVirtualBuffer(curve, cleanup);
        }

        /* Parcours des positions aléatoires */
        for (AbstractCurve curve : super.getRandomKeys()) {
            List<PositionEntity> removes = this.treat(curve, super.getRandomBuffer(curve), false);
            super.removeRandomBuffer(curve, removes);
            if (removes.size() > 0) {
                System.out.println("REMOVED " + removes.size() + " SIMULATIONS FROM " + curve);
            }
        }

        /* Parcours des positions en test */
        for (AbstractCurve curve : super.getTargetedKeys()) {
            List<PositionEntity> removes = this.treat(curve, super.getTargetedBuffer(curve), true);
            super.removeTargetedBuffer(curve, removes);
            if (removes.size() > 0) {
                System.out.println("REMOVED " + removes.size() + " TESTS FROM " + curve);
            }
        }
    }

    /**
     *  Réalise le traitement d'une liste de positions passées en paramètre.
     *  @param curve Courbe évaluée.
     *  @param positions Liste des positions.
     *  @param exitAtConfidence Indique si la position doit etre cloturée une fois le seuil de confiance atteint.
     *  @return Liste des positions retirées.
     *  @throws InnerException En cas d'erreur interne.
     */
    private List<PositionEntity> treat(AbstractCurve curve, List<PositionEntity> positions, boolean exitAtConfidence) throws InnerException {
        /* Création des variables utiles */
        long now = new java.util.Date().getTime();
        List<PositionEntity> result = new ArrayList<>();

        /* Extraction des cellules utiles */
        List<AbstractCell> builds = curve.getBuilds();

        /* Récupération du taux */
        RateEntity rate = curve.getOwner().getCurrent();

        /* Création de la liste des expirations */
        List<PositionEntity> timeouts = new ArrayList<>();

        /* Parcours */
        for (PositionEntity position : positions) {
            if (position.getStart() < now - super.getWallet().getTimeout()) {
                /* Fermeture de la position */
                this.positionDAO.closePosition(
                        curve.getOwner().getCurrent(),
                        position,
                        -1,
                        true);
                result.add(position);

                /* Suppression des tests liés */
                timeouts.add(position);
            } else {
                /* Vérication du seuil de sécurité */
                boolean panick = false;
                double loss = position.getStopLoss();
                if (position.getMode()) {
                    /* Calcul du décalage en mode long */
                    double decal = rate.getBid() - position.getStopGap();
                    loss = decal > loss ? decal : loss;

                    /* Fermeture si nécessaire */
                    if (loss > rate.getBid()) {
                        panick = true;
                    }
                } else {
                    /* Calcul du décalage en mode court */
                    double decal = rate.getAsk() + position.getStopGap();
                    loss = decal < loss ? decal : loss;

                    /* Fermeture si nécessaire */
                    if (loss < rate.getAsk()) {
                        panick = true;
                    }
                }
                position.setStopLoss(loss);

                /* Vérification de l'objectif */
                boolean done = false;
                if (exitAtConfidence) {
                    if (position.getMode()) {
                        done = rate.getAsk() > position.getStopSuccess();
                    } else {
                        done = rate.getBid() < position.getStopSuccess();
                    }
                }

                /* Extraction de la piste de sortie */
                ExitLead exit = position.getExitMix().asExitLead(curve, builds, position.getMode());

                /* Consolidation */
                boolean pertinent = true;
                for (AbstractStrategy strategy : Arrays.stream(exit.getStrategies()).map(e -> e.getStrategy()).collect(Collectors.toList())) {
                    pertinent &= strategy.isPertinent();
                }

                /* Calcul */
                if (panick || done || pertinent) {
                    exit.score(position.getMode(), curve.getOwner().getCurrent());

                    /* Sortie */
                    double score = position.getMode() ? exit.getLongScore() : exit.getShortScore();
                    if (panick || done || score > super.getWallet().getBarrierExit()) {
                        this.positionDAO.closePosition(
                                rate,
                                position,
                                done ? -2 : (panick ? -1 : score),
                                false);
                        result.add(position);
                    }
                }
            }
        }

        /* Suppression des tests expirés */
        this.positionDAO.deleteExpiredTestsByHashCodes(timeouts);

        /* Renvoi */
        return result;
    }
}
