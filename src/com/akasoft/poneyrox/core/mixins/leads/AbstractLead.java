package com.akasoft.poneyrox.core.mixins.leads;

import com.akasoft.poneyrox.core.mixins.artifacts.AbstractArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.batch.AbstractBatch;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 *  Piste.
 *  Sélection d'une liste de stratégies préalablement pondérées.
 */
public abstract class AbstractLead<TArtifact extends AbstractArtifact> {
    /**
     *  Pondération.
     */
    private double[] ponderation;

    /**
     *  Stratégies.
     */
    private TArtifact[] strategies;

    /**
     *  Mode.
     *  true pour long, false pour court.
     */
    private boolean mode;

    /**
     *  Score en sortie longue.
     */
    private double longScore;

    /**
     *  Score en sortie courte.
     */
    private double shortScore;

    /**
     *  Constructeur.
     *  Pondérations et stratégies doivent etre placées dans le meme ordre.
     *  @param ponderation Pondérations.
     *  @param strategies Stratégies.
     *  @param mode Mode.
     *  @throws InnerException En cas d'erreur d'instanciation.
     */
    public AbstractLead(double[] ponderation, TArtifact[] strategies, boolean mode) throws InnerException {
        if (ponderation.length != strategies.length) {
            throw new InnerException("Failed to generate abstract lead");
        }

        this.ponderation = ponderation;
        this.strategies = strategies;
        this.mode = mode;
    }

    /**
     *  Retourne les pondérations.
     *  @return Liste des pondérations.
     */
    public double[] getPonderation() {
        return this.ponderation;
    }

    /**
     *  Retourne les stratégies.
     *  @return Liste des stratégies.
     */
    public TArtifact[] getStrategies() {
        return this.strategies;
    }

    /**
     *  Retourne le mode.
     *  true pour long, false pour court.
     *  @return Mode.
     */
    public boolean getMode() {
        return this.mode;
    }

    /**
     *  Retourne le score long.
     *  @return Score long.
     */
    public double getLongScore() {
        return this.longScore;
    }

    /**
     *  Retourne le score court.
     *  @return Score court.
     */
    public double getShortScore() {
        return this.shortScore;
    }

    /**
     *  Définit le score long.
     *  @param score Score affecté.
     */
    public void setLongScore(double score) {
        this.longScore = score;
    }

    /**
     *  Définit le score court.
     *  @param score Score court.
     */
    public void setShortScore(double score) {
        this.shortScore = score;
    }

    /**
     *  Retourne le niveau de rareté sur la base d'une liste de moyenne issues de la commande
     *  "Mixin.selectAverageEntryPonderations"
     *  @param reference Référentiel.
     *  @return Niveau de rareté.
     */
    public double rarity(Map<Class<? extends AbstractStrategy>, Double> reference) {
        /* Création du résultat */
        double result = 0;

        /* Parcours des entrées */
        for (Class<? extends AbstractStrategy> clazz : reference.keySet()) {
            /* Comparaison aux stratégies */
            for (int i = 0; i < this.strategies.length; i++) {
                /* Récupération */
                TArtifact artifact = this.strategies[i];
                double ponderation = this.ponderation[i];

                /* Vérification */
                if (artifact.getStrategy().getClass().equals(clazz) && ponderation > reference.get(clazz)) {
                    result += 1;
                }
            }
        }

        /* Renvoi */
        return result;
    }

    /**
     *  Calcule le score de la piste.
     *  @param mode Mode de la transaction.
     *  @param rate Taux à l'entrée.
     */
    public abstract void score(boolean mode, RateEntity rate);
}
