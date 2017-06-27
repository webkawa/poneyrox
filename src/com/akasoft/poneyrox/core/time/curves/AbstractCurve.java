package com.akasoft.poneyrox.core.time.curves;

import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.threads.TimelineTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Courbe.
 *  Courbe de taux observée pouvant inclure un lissage sur une période donnée.
 *  @param <TCell> Type de cellule.
 */
public abstract class AbstractCurve<TCell extends AbstractCell> extends AbstractCurveWrapper<TCell> {
    /**
     *  Nombre maximum de cellules retenues.
     */
    public static final int WIDTH = 320;

    /**
     *  Tache propriétaire.
     */
    private final TimelineTask owner;

    /**
     *  Entité.
     */
    private final TimelineEntity entity;

    /**
     *  Constructeur.
     *  @param owner Tache propriétaire.
     */
    public AbstractCurve(TimelineTask owner) {
        this.owner = owner;
        this.entity = owner.getTimeline();
    }

    /**
     *  Retourne la tache propriétaire.
     *  @return Tache propriétaire.
     */
    public TimelineTask getOwner() {
        return this.owner;
    }

    /**
     *  Retourne l'entité liée.
     *  @return Entité liée.
     */
    public TimelineEntity getEntity() {
        return this.entity;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    public abstract int getSmooth();

    /**
     *  Convertit la courbe en valeur textuelle.
     *  @return Valeur textuelle.
     */
    @Override
    public String toString() {
        return String.format(
                "%s[%d'@%s|%s]",
                this.entity.getLabel(),
                this.entity.getSize(),
                this.getSmooth(),
                this.entity.getMarket().getLabel());
    }
}
