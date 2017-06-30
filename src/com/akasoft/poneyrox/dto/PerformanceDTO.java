package com.akasoft.poneyrox.dto;

import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import org.hibernate.transform.ResultTransformer;

import java.util.List;

/**
 *  DTO descriptif d'une combinaison stratégique précise.
 */
public class PerformanceDTO {
    /**
     *  Profit moyen brut.
     *  Exprimé en valeur brute.
     */
    private double rawProfit;

    /**
     *  Profit moyen relatif.
     *  Exprimé en pourcentage du cours à l'entrée.
     */
    private double relativeProfit;

    /**
     *  Profit moyen journalier.
     *  Exprimé en pourcentage du cours à l'entrée, porté sur une journée.
     */
    private double dailyProfit;

    /**
     *  Nombre total de confirmations.
     */
    private int confirmations;

    /**
     *  Nombre de transactions bénéficiaires.
     */
    private int wins;

    /**
     *  Nombre de transactions déficitaires.
     */
    private int loss;

    /**
     *  Ligne temporelle.
     */
    private TimelineEntity timeline;

    /**
     *  Niveau de lissage.
     */
    private int smooth;

    /**
     *  Niveau de confiance.
     */
    private double confidence;

    /**
     *  Mode.
     */
    private boolean mode;

    /**
     *  Stratégie d'entrée.
     */
    private MixinEntity entryMix;

    /**
     *  Stratégie de sortie.
     */
    private MixinEntity exitMix;

    /**
     *  Retourne le profit brut moyen généré.
     *  @return Profit brut généré.
     */
    public double getRawProfit() {
        return this.rawProfit;
    }

    /**
     *  Retourne le pourcentage de profit moyen généré.
     *  @return Pourcentage de profit relatif moyen.
     */
    public double getRelativeProfit() {
        return this.relativeProfit;
    }

    /**
     *  Retourne le profit journalier moyen généré.
     *  @return Profit journalier généré.
     */
    public double getDailyProfit() {
        return this.dailyProfit;
    }

    /**
     *  Retourne le nombre total de confirmations.
     *  @return Nombre total de confirmations.
     */
    public int getConfirmations() {
        return this.confirmations;
    }

    /**
     *  Retourne le nombre de succès.
     *  @return Nombre de succès.
     */
    public int getWins() {
        return this.wins;
    }

    /**
     *  Retourne le nombre d'échecs.
     *  @return Nombre d'échecs.
     */
    public int getLoss() {
        return this.loss;
    }

    /**
     *  Retourne la ligne temporelle liée.
     *  @return Ligne temporelle.
     */
    public TimelineEntity getTimeline() {
        return this.timeline;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    public int getSmooth() {
        return this.smooth;
    }

    /**
     *  Retourne le niveau de confiance.
     *  @return Niveau de confiance.
     */
    public double getConfidence() {
        return this.confidence;
    }

    /**
     *  Retourne le mode.
     *  @return Mode.
     */
    public boolean getMode() {
        return this.mode;
    }

    /**
     *  Retourne la stratégie d'entrée.
     *  @return Stratégie d'entrée.
     */
    public MixinEntity getEntryMix() {
        return this.entryMix;
    }

    /**
     *  Retourne la stratégie de sortie.
     *  @return Stratégie de sortie.
     */
    public MixinEntity getExitMix() {
        return this.exitMix;
    }

    /**
     *  Définit le profit brut.
     *  @param rawProfit Valeur affectée.
     */
    public void setRawProfit(double rawProfit) {
        this.rawProfit = rawProfit;
    }

    /**
     *  Définit le profit relatif.
     *  @param relativeProfit Profit relatif.
     */
    public void setRelativeProfit(double relativeProfit) {
        this.relativeProfit = relativeProfit;
    }

    /**
     *  Définit le profit journalier.
     *  @param dailyProfit Profit journalier.
     */
    public void setDailyProfit(double dailyProfit) {
        this.dailyProfit = dailyProfit;
    }

    /**
     *  Affecte le niveau de confiance.
     *  @param confidence Niveau de confiance.
     */
    public void setConfidence(double confidence) {
        this.confidence = confidence;
    }

    /**
     *  Définit le nombre de confirmations.
     *  @param confirmations Nombre de confirmations.
     */
    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    /**
     *  Définit le nombre de succès.
     *  @param wins Nombre de succès.
     */
    public void setWins(int wins) {
        this.wins = wins;
    }

    /**
     *  Définit le nombre de pertes.
     *  @param loss Nombre de pertes.
     */
    public void setLoss(int loss) {
        this.loss = loss;
    }

    /**
     *  Définit la ligne temporelle liée.
     *  @param timeline Ligne temporelle liée.
     */
    public void setTimeline(TimelineEntity timeline) {
        this.timeline = timeline;
    }

    /**
     *  Définit le niveau de lissage.
     *  @param smooth Niveau de lissage.
     */
    public void setSmooth(int smooth) {
        this.smooth = smooth;
    }

    /**
     *  Définit le mode.
     *  @param mode Mode.
     */
    public void setMode(boolean mode) {
        this.mode = mode;
    }

    /**
     *  Définit la stratégie d'entrée.
     *  @param entryMix Stratégie d'entrée.
     */
    public void setEntryMix(MixinEntity entryMix) {
        this.entryMix = entryMix;
    }

    /**
     *  Définit la stratégie de sortie.
     *  @param exitMix Stratégie de sortie.
     */
    public void setExitMix(MixinEntity exitMix) {
        this.exitMix = exitMix;
    }

    /**
     *  Indique si la performance est considérée comme sure pour un placement en production.
     *  @param wallet Portefeuille.
     *  @return true si la performance est sure.
     */
    public boolean isSafeForProduction(WalletEntity wallet) {
        if (this.loss == 0) {
            return true;
        }
        return this.wins / this.loss >= wallet.getProdRisk();
    }

    /**
     *  Retourne le niveau de confiance accordable à la stratégie.
     *  @param wallet Portefeuille porteur.
     *  @return Niveau de confiance.
     */
    public double getConfidence(WalletEntity wallet) {
        double ratio = this.loss == 0 ? this.wins : this.wins / this.loss;
        double relativeScore = (this.relativeProfit * wallet.getProdBalancing()) * ratio;
        double dailyScore = this.dailyProfit * ratio;
        return relativeScore + dailyScore;
    }
}
