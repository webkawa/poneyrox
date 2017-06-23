package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.dao.PositionDAO;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.views.PositionViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *  Retourne les positions ayant dégagé le plus de profit.
 */
@RestController
public class GetTopPositionsController {
    /**
     *  DAO des positions.
     */
    private PositionDAO positionDAO;

    /**
     *  Constructeur.
     *  @param positionDAO DAO des positions.
     */
    public GetTopPositionsController(@Autowired PositionDAO positionDAO) {
        this.positionDAO = positionDAO;
    }

    /**
     *  Exécution.
     *  @param type Type de position recherchée.
     *  @param mode Mode.
     *  @param limit Taille maximum.
     *  @return Liste des positions.
     */
    @RequestMapping("positions/top/{type}/{mode}/{limit}")
    @JsonView(PositionViews.Public.class)
    public List<PositionEntity> execute(@PathVariable("type") PositionType type, @PathVariable("mode") boolean mode, @PathVariable("limit") int limit) {
        return this.positionDAO.getTopPositions(type, mode, limit);
    }
}
