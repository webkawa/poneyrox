package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.dao.MarketDAO;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.views.MarketViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.List;

/**
 *  Récupération des marchés.
 *  Sélectionne et retourne l'intégralité des marchés disponibles en base.
 */
@RestController
public class GetMarketsController {
    /**
     *  DAO d'accès aux marchés.
     */
    private MarketDAO dao;

    /**
     *  Constructeur.
     *  @param dao DAO d'accès aux marchés.
     */
    public GetMarketsController(@Autowired MarketDAO dao) {
        this.dao = dao;
    }

    /**
     *  Exécution.
     *  @return Liste complète des marchés.
     */
    @RequestMapping("markets/get")
    @JsonView(MarketViews.Public.class)
    public List<MarketEntity> execute() {
        return this.dao.getAllMarkets();
    }
}
