package com.akasoft.poneyrox.core.mixins.leads;

import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitShortITF;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Piste de sortie.
 */
public class ExitLead extends AbstractLead<ExitArtifact> {
    /**
     *  Constructeur.
     *  Pondérations et stratégies doivent etre placées dans le meme ordre.
     *  @param ponderation Pondérations.
     *  @param strategies  Stratégies.
     *  @param mode Mode.
     *  @throws InnerException En cas d'erreur d'instanciation.
     */
    public ExitLead(double[] ponderation, ExitArtifact[] strategies, boolean mode) throws InnerException {
        super(ponderation, strategies, mode);
    }

    /**
     *  Calcule le score.
     *  @param mode Mode de la transaction.
     *  @param rate Taux à l'entrée.
     */
    public void score(boolean mode, RateEntity rate) {
        /* Paramètres */
        double longScore = 0;
        double shortScore = 0;

        for (int i = 0; i < super.getPonderation().length; i++) {
            /* Récupération des paramètres */
            double ponderation = super.getPonderation()[i];
            ExitArtifact artifact = super.getStrategies()[i];
            AbstractStrategy strategy = artifact.getStrategy();

            if (mode) {
                /* Traitement des longs */
                if (strategy instanceof ExitLongITF) {
                    longScore += ponderation * (((ExitLongITF) strategy).mustExitLong(rate.getBid()) ? 1 : 0);
                } else {
                    longScore = 0;
                }
            } else {
                /* Traitement des cours */
                if (strategy instanceof ExitShortITF) {
                    shortScore += ponderation * (((ExitShortITF) strategy).mustExitShort(rate.getAsk()) ? 1 : 0);
                } else {
                    shortScore = 0;
                }
            }
        }

        /* Mise à jour */
        super.setLongScore(longScore);
        super.setShortScore(shortScore);
    }
}
