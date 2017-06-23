package com.akasoft.poneyrox.core.strategies.interfaces;

import com.akasoft.poneyrox.entities.positions.StrategyEntity;

/**
 *  Interface descriptive d'une stratégie générant des prises de
 *  position longues.
 *  @param <TEntity> Type d'entité.
 */
public interface EnterLongITF<TEntity extends StrategyEntity> extends StrategyITF<TEntity> {
    /**
     *  Indique si le dernier rafraichissement de la stratégie invite
     *  à une prise de position longue.
     *  @return true si la position doit etre prise, false sinon.
     */
    boolean mustEnterLong();
}
