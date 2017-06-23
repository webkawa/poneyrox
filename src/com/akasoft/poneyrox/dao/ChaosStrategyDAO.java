package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.core.strategies.parameters.VariationType;
import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  DAO des stratégies basées sur le niveau de chaos.
 */
@Repository
public class ChaosStrategyDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public ChaosStrategyDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Recherche et retourne une entité par équivalence.
     *  @param mode Mode.
     *  @param floor Seuil.
     *  @param size Taille.
     *  @param type Type de stratégie.
     *  @return Entité trouvée ou nul.
     */
    public ChaosStrategyEntity selectByEquivalence(VariationType mode, double floor, int size, boolean type) {
        List<ChaosStrategyEntity> result = super.getSession()
                .getNamedQuery("ChaosStrategy.selectByEquivalence")
                .setParameter("mode", mode)
                .setParameter("floor", floor)
                .setParameter("size", size)
                .setParameter("type", type)
                .list();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    /**
     *  Recherche et retourne une entité par clef de hachage.
     *  @param hash Clef de hachage.
     *  @return Entité trouvée ou nul.
     */
    public ChaosStrategyEntity selectByHashCode(int hash) {
        List<ChaosStrategyEntity> result = super.getSession()
                .getNamedQuery("ChaosStrategy.selectByHashCode")
                .setParameter("hash", hash)
                .list();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    /**
     *  Retrouve ou insère une entrée dans la base.
     *  @param entity Entité évaluée.
     *  @return Entrée insérée ou récupérée.
     */
    public ChaosStrategyEntity retrieveOrPersist(ChaosStrategyEntity entity) {
        ChaosStrategyEntity result = this.selectByHashCode(entity.hashCode());
        if (result == null) {
            result = new ChaosStrategyEntity();
            result.setMode(entity.getMode());
            result.setFloor(entity.getFloor());
            result.setSize(entity.getSize());
            result.setType(entity.getType());
            result.setHash(entity.hashCode());
            super.getSession().persist(result);
        }
        return result;
    }
}
