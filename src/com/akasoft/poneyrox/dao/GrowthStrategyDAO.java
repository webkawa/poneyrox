package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.core.strategies.parameters.VariationType;
import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.GrowthStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.MarginStrategyEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  DAO de positionnement sur la base de la croissance.
 */
@Repository
public class GrowthStrategyDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public GrowthStrategyDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Sélectionne une entrée par équivalence.
     *  @param mode Mode de variation.
     *  @param level Niveau.
     *  @param size Taille
     *  @param type Type.
     *  @return Entrée trouvée.
     */
    public GrowthStrategyEntity selectByEquivalence(VariationType mode, double level, int size, boolean type) {
        List<GrowthStrategyEntity> result = super.getSession()
                .getNamedQuery("GrowthStrategy.selectByEquivalence")
                .setParameter("mode", mode)
                .setParameter("level", level)
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
    public GrowthStrategyEntity selectByHashCode(int hash) {
        List<GrowthStrategyEntity> result = super.getSession()
                .getNamedQuery("GrowthStrategy.selectByHashCode")
                .setParameter("hash", hash)
                .list();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    /**
     *  Sélectione ou génère une entrée par équivalence.
     *  @param entity Entité évaluée.
     *  @return Entrée trouvée ou insérée.
     */
    public GrowthStrategyEntity retrieveOrPersist(GrowthStrategyEntity entity) {
        GrowthStrategyEntity result = this.selectByHashCode(entity.hashCode());
        if (result == null) {
            result = new GrowthStrategyEntity();
            result.setMode(entity.getMode());
            result.setLevel(entity.getLevel());
            result.setSize(entity.getSize());
            result.setType(entity.getType());
            result.setHash(entity.hashCode());
            super.getSession().persist(result);
        }
        return result;
    }
}
