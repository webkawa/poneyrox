package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.views.RateViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  Retourne la liste des taux les plus récents.
 */
@RestController
public class GetRatesController {
    /**
     *  Gestionnaire de taches.
     */
    private ManagerComponent manager;

    /**
     *  Constructeur.
     *  @param manager Gestionnaires de taches.
     */
    public GetRatesController(@Autowired ManagerComponent manager) {
        this.manager = manager;
    }

    /**
     *  Exécution.
     *  @return Liste des taux les plus récents.
     */
    @RequestMapping("rates/get")
    @JsonView(RateViews.Public.class)
    public List<RateEntity> execute() {
        return this.manager.getWatcher().getRates();
    }
}
