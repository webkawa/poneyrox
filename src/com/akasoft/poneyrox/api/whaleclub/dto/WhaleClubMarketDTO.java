package com.akasoft.poneyrox.api.whaleclub.dto;

/**
 *  Marché.
 *  DTO d'un marché récupéré par le biais de l'API WhaleClub.
 */
public class WhaleClubMarketDTO {
    /**
     *  Clef d'accès.
     */
    private final String key;

    /**
     *  Nom du marché.
     */
    private final String name;

    /**
     *  Constructeur.
     *  @param key Clef d'accès.
     *  @param name Nom du marché.
     */
    public WhaleClubMarketDTO(String key, String name) {
        this.key = key;
        this.name = name;
    }

    /**
     *  Retourne la clef d'accès.
     *  @return Clef d'accès au marché.
     */
    public String getKey() {
        return this.key;
    }

    /**
     *  Retourne le libellé du marché.
     *  @return Nom du marché.
     */
    public String getName() {
        return this.name;
    }
}
