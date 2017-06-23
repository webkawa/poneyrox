package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.dto.CellDTO;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.views.CellViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  Recherche et retourne les derniers taux disponibles pour une ligne
 *  temporelle.
 */
@RestController
public class GetTimelineRatesController {
    /**
     *  Gestionnaire des taches.
     */
    private ManagerComponent manager;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public GetTimelineRatesController(@Autowired ManagerComponent manager) {
        this.manager = manager;
    }

    /**
     *  Exécution.
     *  @param timeline Ligne temporelle requetée.
     *  @return Liste des dernières taux classés par niveau de lissage.
     *  @throws InnerException En cas d'erreur interne au serveur.
     */
    @RequestMapping("timelines/rate")
    @JsonView(CellViews.Public.class)
    public List<CellDTO> execute(@RequestBody TimelineEntity timeline) throws InnerException {
        return this.manager.getTimelineByEntity(timeline).getLast();
    }
}
