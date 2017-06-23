package com.akasoft.poneyrox.core.strategies.interfaces;

import com.akasoft.poneyrox.entities.positions.StrategyEntity;

/**
 *  Interface des stratégies.
 *  @param <TEntity> Type d'entité correspond à la stratégie.
 */
public interface StrategyITF<TEntity extends StrategyEntity> {
    /**
     *  Sérialise la stratégie dans une entité persistente.
     *  @return Entité correspondante.
     */
    TEntity asEntity();
}
