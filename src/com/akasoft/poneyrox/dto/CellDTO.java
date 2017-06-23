package com.akasoft.poneyrox.dto;

import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.views.CellViews;
import com.fasterxml.jackson.annotation.JsonView;

import java.util.Date;

/**
 *  DTO représentatif d'une cellule calculée.
 */
public class CellDTO {
    /**
     *  Niveau.
     */
    @JsonView({
            CellViews.Public.class
    })
    private int level;

    /**
     *  Contenu.
     */
    @JsonView({
            CellViews.Public.class
    })
    private AbstractCell data;

    /**
     *  Constructeur.
     *  @param level Niveau de lissage.
     *  @param data Données.
     */
    public CellDTO(int level, AbstractCell data) {
        this.level = level;
        this.data = data;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    public int getLevel() {
        return this.level;
    }

    /**
     *  Retourne les données.
     *  @return Données.
     */
    public AbstractCell getData() {
        return this.data;
    }
}
