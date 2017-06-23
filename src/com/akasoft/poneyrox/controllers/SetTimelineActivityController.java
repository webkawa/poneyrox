package com.akasoft.poneyrox.controllers;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.dao.TimelineDAO;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.views.TimelineViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  Controleur permettant l'activation ou la désactivation d'une ligne
 *  de temps.
 */
@RestController
public class SetTimelineActivityController {
    /**
     *  Gestionnaire des taches.
     */
    private ManagerComponent manager;

    /**
     *  DAO d'accès aux lignes de temps.
     */
    private TimelineDAO dao;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     *  @param dao DAO d'accès aux lignes temporelles.
     */
    public SetTimelineActivityController(
            @Autowired ManagerComponent manager,
            @Autowired TimelineDAO dao) {
        this.manager = manager;
        this.dao = dao;
    }

    /**
     *  Exécution.
     *  @param source Ligne source.
     *  @param activity Statut affecté.
     *  @return Ligne modifiée.
     *  @throws InnerException En cas d'erreur lors de l'ajout.
     */
    @RequestMapping("timelines/set/activity/{activity}")
    @JsonView(TimelineViews.Public.class)
    public TimelineEntity execute(
            @RequestBody TimelineEntity source,
            @PathVariable("activity") boolean activity) throws InnerException {
        if (activity) {
            this.manager.addTimeline(source);
        } else {
            this.manager.removeTimeline(source);
        }
        return this.dao.setActivity(source.getId(), activity);
    }
}