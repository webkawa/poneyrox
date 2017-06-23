package com.akasoft.poneyrox.core.strategies.interfaces;

import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import java.util.List;

/**
 *  Interface des stratégies procédant à une observation du cours.
 *  @param <TEntity> Type d'entité.
 */
public interface ObserverITF<TEntity extends StrategyEntity> extends StrategyITF<TEntity> {
    /**
     *  Réalise les consolidations rattachées à une instance de la courbe.
     *  @param curve Courbe traitée.
     *  @param cells Liste des cellules exploitables.
     */
    void consolidate(AbstractCurve curve, List<AbstractCell> cells);

    /**
     *  Retourne le nombre de cellules nécessaires à l'exploitation de la stratégie.
     *  @return Nombre de cellules nécessaires.
     */
    int size();
}
