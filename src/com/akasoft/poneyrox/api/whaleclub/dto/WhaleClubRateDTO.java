package com.akasoft.poneyrox.api.whaleclub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Taux.
 *  DTO représentatif d'un taux récupéré auprès de l'API WhaleClub.
 */
public class WhaleClubRateDTO {
    /**
     *  Clef d'accès au marché.
     */
    private final String market;

    /**
     *  Date de réception.
     */
    private final long date;

    /**
     *  Cout de la demande.
     */
    private final double ask;

    /**
     *  Cout de l'offre.
     */
    private final double bid;

    /**
     *  Constructeur.
     *  @param market Marché attaqué.
     *  @param date Date d'application.
     *  @param ask Demande.
     *  @param bid Offre.
     */
    public WhaleClubRateDTO(String market, long date, double ask, double bid) {
        this.market = market;
        this.date = date;
        this.ask = ask;
        this.bid = bid;
    }

    /**
     *  Retourne la clef d'accès au marché.
     *  @return Clef d'accès.
     */
    public String getMarket() {
        return this.market;
    }

    /**
     *  Retourne la date.
     *  @return Date d'application.
     */
    public long getDate() {
        return this.date;
    }

    /**
     *  Retourne la demande.
     *  @return Demande.
     */
    public double getAsk() {
        return this.ask;
    }

    /**
     *  Retourne l'offre.
     *  @return Offre.
     */
    public double getBid() {
        return this.bid;
    }
}
