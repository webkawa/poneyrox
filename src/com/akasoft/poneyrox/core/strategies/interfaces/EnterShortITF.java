package com.akasoft.poneyrox.core.strategies.interfaces;

import com.akasoft.poneyrox.entities.positions.StrategyEntity;

/**
 *  Interface descriptive d'une stratégie générant des prises de
 *  position courtes.
 *  @param <TEntity> Type d'entité.
 */
public interface EnterShortITF<TEntity extends StrategyEntity> extends StrategyITF<TEntity> {
    /**
     *  Indique si le dernier rafraichissement de la stratégie invite
     *  à une prise de position courte.
     *  @return true si la position doit etre prise, false sinon.
     */
    boolean mustEnterShort();
}
