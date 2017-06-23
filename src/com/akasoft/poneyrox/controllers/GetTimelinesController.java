package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.dao.TimelineDAO;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.views.TimelineViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 *  Récupération des lignes temporelles.
 *  Récupère et retourne l'ensemble des lignes temporelles référencées dans la base.
 */
@RestController
public class GetTimelinesController {
    /**
     *  DAO des lignes temporelles.
     */
    private TimelineDAO dao;

    /**
     *  Constructeur.
     *  @param dao DAO des lignes temporelles.
     */
    public GetTimelinesController(@Autowired TimelineDAO dao) {
        this.dao = dao;
    }

    /**
     *  Exécution.
     *  @return Liste des lignes temporelles.
     */
    @RequestMapping("timelines/get")
    @JsonView(TimelineViews.Public.class)
    public List<TimelineEntity> execute() {
        return this.dao.getAll();
    }
}
