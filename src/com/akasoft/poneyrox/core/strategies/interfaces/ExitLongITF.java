package com.akasoft.poneyrox.core.strategies.interfaces;


import com.akasoft.poneyrox.entities.positions.StrategyEntity;

/**
 *  Interface descriptive d'une stratégie générant des sorties de
 *  position longues.
 *  @param <TEntity> Type d'entité.
 */
public interface ExitLongITF<TEntity extends StrategyEntity> extends StrategyITF<TEntity> {
    /**
     *  Indique si le dernier rafraichissement de la stratégie incite
     *  à une sortie de position longue.
     *  @param entry Cout à l'entrée.
     *  @return true si la position doit etre quittée, false sinon.
     */
    boolean mustExitLong(double entry);
}
