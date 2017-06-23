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
 *  DAO des stratégies de sortie par marge.
 */
@Repository
public class MarginStrategyDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public MarginStrategyDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Sélectionne une entrée par équivalence.
     *  @param mode Mode.
     *  @param margin Marge ciblée.
     *  @return Entrée trouvée.
     */
    public MarginStrategyEntity selectByEquivalence(VariationType mode, double margin) {
        List<MarginStrategyEntity> result = super.getSession()
                .getNamedQuery("MarginStrategy.selectByEquivalence")
                .setParameter("mode", mode)
                .setParameter("margin", margin)
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
    public MarginStrategyEntity selectByHashCode(int hash) {
        List<MarginStrategyEntity> result = super.getSession()
                .getNamedQuery("MarginStrategy.selectByHashCode")
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
    public MarginStrategyEntity retrieveOrPersist(MarginStrategyEntity entity) {
        MarginStrategyEntity result = this.selectByHashCode(entity.hashCode());
        if (result == null) {
            result = new MarginStrategyEntity();
            result.setMode(entity.getMode());
            result.setMargin(entity.getMargin());
            result.setHash(entity.hashCode());
            super.getSession().persist(result);
        }
        return result;
    }
}
