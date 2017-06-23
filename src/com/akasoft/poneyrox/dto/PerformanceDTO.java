package com.akasoft.poneyrox.dto;

import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;

/**
 *  DTO descriptif d'une combinaison stratégique précise.
 */
public class PerformanceDTO {
    /**
     *  Profit généré.
     */
    private double profit;

    /**
     *  Ligne temporelle.
     */
    private TimelineEntity timeline;

    /**
     *  Niveau de lissage.
     */
    private int smooth;

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
     *  Retourne le profit généré.
     *  @return Profit généré.
     */
    public double getProfit() {
        return this.profit;
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
     *  Affecte le profit.
     *  @param profit Profit généré.
     */
    public void setProfit(double profit) {
        this.profit = profit;
    }

    /**
     *  Affecte la ligne temporelle.
     *  @param timeline Ligne temporelle.
     */
    public void setTimeline(TimelineEntity timeline) {
        this.timeline = timeline;
    }

    /**
     *  Affecte le niveau de lissage.
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
     *  Définit la stratégie.
     *  @param entryMix Stratégie.
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
}
