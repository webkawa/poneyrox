package com.akasoft.poneyrox.api.whaleclub.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *  Position.
 *  DTO d'une position déposée par le biais de l'API Whaleclub.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WhaleClubPositionDTO {
    /**
     *  Identifiant.
     */
    @JsonProperty("id")
    private String id;

    /**
     *  Etat de la position.
     */
    @JsonProperty("state")
    private String state;

    /**
     *  Direction.
     */
    @JsonProperty("direction")
    private String direction;

    /**
     *  Marché.
     */
    @JsonProperty("market")
    private String market;

    /**
     *  Levier.
     */
    @JsonProperty("leverage")
    private int leverage;

    /**
     *  Taille de la position.
     *  En satoshis.
     */
    @JsonProperty("size")
    private double size;

    /**
     *  Profils réalisés.
     */
    @JsonProperty("profit")
    private double profit;

    /**
     *  Prix à l'entrée.
     */
    @JsonProperty("entry_price")
    private double entryPrice;

    /**
     *  Prix à la fermeture.
     */
    @JsonProperty("close_price")
    private double closePrice;

    /**
     *  Sécurité en perte.
     */
    @JsonProperty("stop_loss")
    private double stopLoss;

    /**
     *  Cout.
     */
    @JsonProperty("financing")
    private double financing;

    /**
     *  Date de création.
     *  Exprimée en temps UNIX.
     */
    @JsonProperty("created_at")
    private long created;

    /**
     *  Constructeur.
     */
    public WhaleClubPositionDTO() {
    }

    /**
     *  Retourne l'identifiant de la position.
     *  @return Identifiant de la position.
     */
    public String getId() {
        return this.id;
    }

    /**
     *  Retourne l'état de la position.
     *  @return Etat de la position.
     */
    public String getState() {
        return this.state;
    }

    /**
     *  Retourne la direction.
     *  @return Direction.
     */
    public String getDirection() {
        return this.direction;
    }

    /**
     *  Marché de positionnement.
     *  @return Marché.
     */
    public String getMarket() {
        return this.market;
    }

    /**
     *  Retourne le levier.
     *  @return Levier.
     */
    public int getLeverage() {
        return this.leverage;
    }

    /**
     *  Retourne la taille de la position.
     *  @return Taille de la position.
     */
    public double getSize() {
        return this.size;
    }

    /**
     *  Retourne les profits réalisés.
     *  @return Profits réalisés.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     *  Retourne le prix à l'entrée.
     *  @return Prix à l'entrée.
     */
    public double getEntryPrice() {
        return this.entryPrice;
    }

    /**
     *  Retourne le prix à la fermeture.
     *  @return Prix à la fermeture.
     */
    public double getClosePrice() {
        return this.closePrice;
    }

    /**
     *  Retourne la sécurité en perte.
     *  @return Sécurité en perte.
     */
    public double getStopLoss() {
        return this.stopLoss;
    }

    /**
     *  Retourne le cout de la position.
     *  @return Cout de la position.
     */
    public double getFinancing() {
        return this.financing;
    }

    /**
     *  Retourne la date de création.
     *  @return Date de création.
     */
    public long getCreated() {
        return this.created * 1000;
    }
}
