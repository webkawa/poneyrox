package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.core.strategies.parameters.VariationType;
import com.akasoft.poneyrox.entities.strategies.OppositesStrategyEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * DAO des stratégies d'opposition.
 */
@Repository
public class OppositesStrategyDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public OppositesStrategyDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Sélectionne une entrée par équivalence.
     *  @param mode Mode de variation.
     *  @param size Taille
     *  @param reverse Inversion des tests.
     *  @param incomingProximity Niveau de proximité en approche.
     *  @param exitingProximity Niveau de proximité en sortie.
     *  @return Entrée trouvée.
     */
    public OppositesStrategyEntity selectByEquivalence(VariationType mode, int size, boolean reverse, double incomingProximity, double exitingProximity) {
        List<OppositesStrategyEntity> result = super.getSession()
                .getNamedQuery("OppositesStrategy.selectByEquivalence")
                .setParameter("mode", mode)
                .setParameter("size", size)
                .setParameter("reverse", reverse)
                .setParameter("incomingProximity", incomingProximity)
                .setParameter("exitingProximity", exitingProximity)
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
    public OppositesStrategyEntity selectByHashCode(int hash) {
        List<OppositesStrategyEntity> result = super.getSession()
                .getNamedQuery("OppositesStrategy.selectByHashCode")
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
    public OppositesStrategyEntity retrieveOrPersist(OppositesStrategyEntity entity) {
        OppositesStrategyEntity result = this.selectByHashCode(entity.hashCode());
        if (result == null) {
            result = new OppositesStrategyEntity();
            result.setMode(entity.getMode());
            result.setSize(entity.getSize());
            result.setReverse(entity.isReverse());
            result.setExitingProximity(entity.getExitingProximity());
            result.setIncomingProximity(entity.getIncomingProximity());
            result.setHash(entity.hashCode());
            super.getSession().persist(result);
        }
        return result;
    }
}
