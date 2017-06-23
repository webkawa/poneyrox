package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.api.whaleclub.dao.WhaleClubAccess;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubMarketDTO;
import com.akasoft.poneyrox.dao.MarketDAO;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.exceptions.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

/**
 *  Synchronisation des marchés.
 *  Réalise la synchronisation des marchés enregistrés en base avec la liste disponible par le
 *  biais de l'API.
 */
@Controller
public class LoadMarketsController {
    /**
     *  Point d'accès à l'API.
     */
    private WhaleClubAccess api;

    /**
     *  DAO d'accès aux marchés.
     */
    private MarketDAO dao;

    /**
     *  Constructeur.
     *  @param api Point d'accès à l'API.
     *  @param dao DAO d'accès aux marchés.
     */
    public LoadMarketsController(
            @Autowired WhaleClubAccess api,
            @Autowired MarketDAO dao) {
        this.api = api;
        this.dao = dao;
    }

    /**
     *  Exécution.
     *  @return Liste des marchés disponibles.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    @PostConstruct
    public List<MarketEntity> execute() throws ApiException {
        /* Lecture de l'API */
        List<WhaleClubMarketDTO> source = this.api.getMarkets();

        /* Lecture de la base */
        List<MarketEntity> current = this.dao.getAllMarkets();

        /* Comparaison */
        for (WhaleClubMarketDTO market : source) {
            if (!current.stream().anyMatch(c -> c.getKey().equals(market.getKey()))) {
                MarketEntity add = this.dao.persistMarket(market.getKey(), market.getName());
                current.add(add);
            }
        }

        /* Renvoi */
        return current;
    }
}
