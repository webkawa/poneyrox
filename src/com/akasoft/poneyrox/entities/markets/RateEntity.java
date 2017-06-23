package com.akasoft.poneyrox.entities.markets;

import com.akasoft.poneyrox.views.MarketViews;
import com.akasoft.poneyrox.views.RateViews;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.UUID;

/**
 *  Taux.
 *  Taux brut enregistré pour un marché à une date précise.
 */
@Entity
public class RateEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            RateViews.Public.class
    })
    private UUID id;

    /**
     *  Date du taux.
     *  Exprimé en temps UNIX.
     */
    @Column(nullable = false)
    @JsonView({
            RateViews.Public.class
    })
    private long time;

    /**
     *  Cours de la demande.
     */
    @Column(nullable = false)
    @JsonView({
            RateViews.Public.class
    })
    private double ask;

    /**
     *  Cours de l'offre.
     */
    @Column(nullable = false)
    @JsonView({
            RateViews.Public.class
    })
    private double bid;

    /**
     *  Marché.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonView({
            RateViews.Public.class
    })
    private MarketEntity market;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant du taux.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne la date du taux.
     *  Exprimé en temps UNIX.
     *  @return Date du taux.
     */
    public long getTime() {
        return this.time;
    }

    /**
     *  Retourne la demande.
     *  @return Cours de la demande.
     */
    public double getAsk() {
        return this.ask;
    }

    /**
     *  Retourne l'offre.
     *  @return Cours de l'offre.
     */
    public double getBid() {
        return this.bid;
    }

    /**
     *  Retourne le marché.
     *  @return Marché.
     */
    public MarketEntity getMarket() {
        return this.market;
    }

    /**
     *  Définit la date.
     *  @param time Date définie.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     *  Définit le cours de la demande.
     *  @param ask Cours de la demande.
     */
    public void setAsk(double ask) {
        this.ask = ask;
    }

    /**
     *  Définit le cours de l'offre.
     *  @param bid Cours de l'offre.
     */
    public void setBid(double bid) {
        this.bid = bid;
    }

    /**
     *  Définit le marché.
     *  @param market Marché défini.
     */
    public void setMarket(MarketEntity market) {
        this.market = market;
    }
}
