package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.dao.TimelineDAO;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.views.TimelineViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 *  Création d'une ligne temporelle.
 *  Génère, insère et retourne une ligne temporelle active dans la base.
 */
@RestController
public class AddTimelineController {
    /**
     *  Gestionnaire de taches.
     */
    private ManagerComponent manager;

    /**
     *  DAO d'accès aux lignes temporelles.
     */
    private TimelineDAO dao;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire de taches.
     *  @param dao DAO d'accès aux lignes temporelles.
     */
    public AddTimelineController(
            @Autowired ManagerComponent manager,
            @Autowired TimelineDAO dao) {
        this.manager = manager;
        this.dao = dao;
    }

    /**
     *  Initialisation.
     *  @throws InnerException En cas d'erreur lors de l'ajout.
     */
    @PostConstruct
    public void postConstruct() throws InnerException {
        for (TimelineEntity timeline : this.dao.getByActivity(true)) {
            this.manager.getWatcher().watch(timeline.getMarket());
            this.manager.addTimeline(timeline);
        }
    }

    /**
     *  Exécution.
     *  @param timeline Entité modèle.
     *  @return Entitée insérée.
     *  @throws InnerException En cas d'erreur lors de l'ajout.
     */
    @RequestMapping(path = "timelines/add", method = RequestMethod.POST)
    @JsonView(TimelineViews.Public.class)
    public TimelineEntity execute(@RequestBody TimelineEntity timeline) throws InnerException {
        TimelineEntity result = this.dao.persistTimeline(timeline.getMarket(), timeline.getLabel(), timeline.getSize(), true);
        this.manager.getWatcher().watch(timeline.getMarket());
        this.manager.addTimeline(result);
        return result;
    }
}
