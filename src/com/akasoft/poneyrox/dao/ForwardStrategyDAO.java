package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.ForwardStrategyEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *  DAO des stratégies basées sur les courbes d'avancement.
 */
@Repository
public class ForwardStrategyDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public ForwardStrategyDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Recherche et retourne une entité par clef de hachage.
     *  @param hash Clef de hachage.
     *  @return Entité trouvée ou nul.
     */
    public ForwardStrategyEntity selectByHashCode(int hash) {
        List<ForwardStrategyEntity> result = super.getSession()
                .getNamedQuery("ForwardStrategy.selectByHashCode")
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
    public ForwardStrategyEntity retrieveOrPersist(ForwardStrategyEntity entity) {
        ForwardStrategyEntity result = this.selectByHashCode(entity.hashCode());
        if (result == null) {
            result = new ForwardStrategyEntity();
            result.setMode(entity.getMode());
            result.setForward(entity.getForward());
            result.setBackward(entity.getBackward());
            result.setOffset(entity.getOffset());
            result.setDifference(entity.getDifference());
            result.setHash(entity.hashCode());
            super.getSession().persist(result);
        }
        return result;
    }
}
