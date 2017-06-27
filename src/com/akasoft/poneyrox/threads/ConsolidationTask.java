package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.api.whaleclub.dao.WhaleClubAccess;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubPositionDTO;
import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.leads.EntryLead;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.dao.PositionDAO;
import com.akasoft.poneyrox.dao.TransactionDAO;
import com.akasoft.poneyrox.dao.WalletDAO;
import com.akasoft.poneyrox.dto.PerformanceDTO;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.positions.*;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.exceptions.ApiException;
import com.akasoft.poneyrox.exceptions.InnerException;
import javafx.geometry.Pos;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *  Tache de consolidation.
 *  Tache en charge de la consolidation des données présentes en base.
 */
public class ConsolidationTask extends ConsolidationTaskWrapper {
    /**
     *  Point d'accès à l'API.
     */
    private final WhaleClubAccess access;

    /**
     *  DAO des positions.
     */
    private final PositionDAO positionDAO;

    /**
     *  DAO des transactions.
     */
    private final TransactionDAO transactionDAO;

    /**
     *  DAO des portefeuilles.
     */
    private final WalletDAO walletDAO;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     *  @param access Point d'accès à l'API.
     *  @param positionDAO DAO des positions.
     *  @param transactionDAO DAO des transactions.
     */
    public ConsolidationTask(
            ManagerComponent manager,
            WhaleClubAccess access,
            PositionDAO positionDAO,
            TransactionDAO transactionDAO,
            WalletDAO walletDAO) {
        super(manager);
        this.access = access;
        this.transactionDAO = transactionDAO;
        this.positionDAO = positionDAO;
        this.walletDAO = walletDAO;
    }

    /**
     *  Exécution.
     *  @throws AbstractException En cas d'erreur lors du traitement.
     */
    @Override
    protected void execute() throws AbstractException {
        /* Récupération de l'index des constructions */
        Map<AbstractCurve, List<AbstractCell>> builds = new HashMap<>();
        for (AbstractCurve curve : super.getManager().getAllCurves()) {
            builds.put(curve, curve.getBuilds());
        }

        /* Gestion des transactions */
        try {
            this.createTransactions(builds);
        } catch (AbstractException ex) {
            ex.printStackTrace();
        }

        /* Gestion des simulations */
        try {
            this.createTargetedPositions(builds);
        } catch (AbstractException ex) {
            ex.printStackTrace();
        }

        /* Nettoyage des simulations les plus anciennes */
        long expiration = new java.util.Date().getTime() - super.getWallet().getTimeout();
        this.positionDAO.deleteExpiredSimulations();
        this.positionDAO.deleteUntestedSimulations(expiration, super.getWallet().getRetentionProfit());
        this.positionDAO.deleteAllOldPositions(new java.util.Date().getTime() - super.getWallet().getRetentionDelay());

        /* Rafraichissement du portefeuille */
        WalletEntity wallet = this.walletDAO.refreshWallet(this.getManager().getWallet());
        this.getManager().setWallet(wallet);
    }

    /**
     *  Génère et publie une liste de transactions.
     *  @param builds Liste des cellules.
     *  @throws InnerException En cas d'erreur interne.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    private void createTransactions(Map<AbstractCurve, List<AbstractCell>> builds) throws InnerException, ApiException {
        /* Extraction des performances */
        List<PerformanceDTO> targets = this.positionDAO.getTopStrategiesForProduction(
                new java.util.Date().getTime() - super.getWallet().getProdPeriod(),
                super.getWallet().getProdPercent(),
                super.getWallet().getProdConfirmations(),
                super.getWallet().getFeeSpread(),
                super.getWallet().getProdGrain());

        /* Filtrage */
        targets = targets.stream()
                .filter(e -> {
                    if (super.hasCurve(e.getTimeline(), e.getSmooth())) {
                        return true;
                    } else {
                        return false;
                    }
                })
                .filter(e -> e.isSafeForProduction(super.getWallet()))
                .sorted((left, right) -> {
                    double lc = left.getConfidence(super.getWallet());
                    double rc = right.getConfidence(super.getWallet());

                    if (lc > rc) {
                        return -1;
                    } else if (lc < rc) {
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .collect(Collectors.toList());

        /* Parcours */
        int count = 0;
        for (int i = 0; i < targets.size() && super.getWallet().getProdPool() - super.getManager().getVirtualTransactionsCount() > 0; i++) {
            /* Récupération de la cible */
            PerformanceDTO target = targets.get(i);

            /* Paramètres */
            AbstractCurve curve = super.getCurve(target.getTimeline(), target.getSmooth());

            /* TODO : Calcul du montant de la transaction */
            double size = 0;
            if (target.getMode()) {
                size = (super.getWallet().getProdSize() / curve.getOwner().getCurrent().getAsk()) / 0.00000001;
            } else {
                size = (super.getWallet().getProdSize() / curve.getOwner().getCurrent().getBid()) / 0.00000001;
            }
            size = 100000000l;

            /* Récupération du score */
            double score = this.process(curve, builds.get(curve), target);
            if (score != -1) {
                /* Récupération du précédent */
                PositionEntity precedent = this.positionDAO.getLastPositionsByStrategy(
                        target.getTimeline(),
                        target.getSmooth(),
                        target.getMode(),
                        target.getEntryMix(),
                        target.getExitMix(),
                        1).get(0);

                /* Vérification.
                 * Pour etre placée, la position précédente doit nécessairement etre un test et avoir dégagé un profit. */
                if (precedent.getProfit() > 0 && !precedent.getType().equals(PositionType.SIMULATION)) {
                    /* Récupération du taux courant */
                    RateEntity rate = curve.getOwner().getCurrent();

                    /* Calcul du pont */
                    double gap = ((target.getRelativeProfit() / 100) * (target.getMode() ? rate.getBid() : rate.getAsk())) * (super.getWallet().getProdSecurity() / 100);
                    double stop = target.getMode() ? rate.getBid() - gap : rate.getAsk() + gap;

                    /* Prise de position dans l'API */
                    WhaleClubPositionDTO dto = this.access.takePosition(
                            true,
                            target.getMode(),
                            curve.getEntity().getMarket(),
                            1,
                            size);

                    /* Création de la position */
                    PositionEntity position = this.positionDAO.persistPosition(
                            target.getTimeline(),
                            rate,
                            target.getMode(),
                            PositionType.VIRTUAL,
                            score,
                            curve.getSmooth(),
                            target.getEntryMix(),
                            target.getExitMix());

                    /* Création de la transaction */
                    TransactionEntity transaction = this.transactionDAO.persistTransaction(
                            dto.getId(),
                            dto.getEntryPrice(),
                            dto.getSize(),
                            stop,
                            gap,
                            position,
                            super.getWallet());

                    /* Diffusion */
                    super.getManager().publishTransaction(true, curve, transaction);
                    count++;
                }
            }
        }
        if (count > 0) {
            System.out.println("CREATED " + count + " TRANSACTIONS FOR " + targets.size() + " POSSIBILITIES");
        }
    }

    /**
     *  Génère et publie une liste de positions ciblées.
     *  @param builds Liste des cellules.
     *  @throws InnerException En cas d'erreur interne.
     */
    private void createTargetedPositions(Map<AbstractCurve, List<AbstractCell>> builds) throws InnerException {
        /* Extraction des listes exactes */
        List<PerformanceDTO> targets = this.positionDAO.getTopStrategiesForTesting(
                new java.util.Date().getTime() - super.getWallet().getTestPeriod(),
                super.getWallet().getTestGrain(),
                super.getWallet().getTestLimit());

        /* Parcours */
        int count = 0;
        for (int i = 0; i < targets.size(); i++) {
            PerformanceDTO target = targets.get(i);
            if (super.getManager().getTargetedPositionsCount() < super.getWallet().getTestPool() && super.hasCurve(target.getTimeline(), target.getSmooth())) {
                /* Paramètres */
                AbstractCurve curve = super.getCurve(target.getTimeline(), target.getSmooth());

                /* Récupération du score */
                double score = this.process(curve, builds.get(curve), target);
                if (score != -1) {
                    /* Enregistrement */
                    PositionEntity position = this.positionDAO.persistPosition(
                            target.getTimeline(),
                            curve.getOwner().getCurrent(),
                            target.getMode(),
                            PositionType.TEST,
                            score,
                            curve.getSmooth(),
                            target.getEntryMix(),
                            target.getExitMix());

                    /* Publication */
                    super.getManager().publishPosition(false, curve, position);
                    count++;
                }
            }
        }
        if (count > 0) {
            System.out.println("CREATED " + count + " TESTS FOR " + targets.size() + " POSSIBILITIES");
        }
    }

    /**
     *  Indique si une performance peut donner lieu à une prise de position.
     *  @param curve Courbe d'application.
     *  @param builds Cellules évaluées (performance).
     *  @param target Cible.
     *  @return Score de la performance (-1 si non-applicable).
     *  @throws InnerException En cas d'erreur interne.
     */
    private double process(AbstractCurve curve, List<AbstractCell> builds, PerformanceDTO target) throws InnerException {
        /* Evaluation de la sortie */
        ExitLead exit = target.getExitMix().asExitLead(curve, builds, target.getMode());
        exit.score(target.getMode(), curve.getOwner().getCurrent());

        /* Evaluation de l'entrée */
        EntryLead entry = target.getEntryMix().asEntryLead(curve, builds, target.getMode());
        entry.score(target.getMode(), curve.getOwner().getCurrent());

        /* Calcul du score en sortie */
        double score = target.getMode() ? exit.getLongScore() : exit.getShortScore();
        boolean enter = super.getWallet().getBarrierExit() > score;

        /* Calcul du score en entrée */
        score = target.getMode() ? entry.getLongScore() : entry.getShortScore();
        enter &= super.getWallet().getBarrierEntry() < score;

        return enter ? score : -1;
    }
}
