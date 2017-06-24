package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.core.mixins.artifacts.AbstractArtifact;
import com.akasoft.poneyrox.core.strategies.categories.*;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;
import com.akasoft.poneyrox.entities.strategies.*;
import com.akasoft.poneyrox.exceptions.InnerException;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  DAO des mixins.
 */
@Repository
public class MixinDAO extends AbstractDAO {
    /**
     *  DAO des stratégies basées sur le niveau de chaos.
     */
    private ChaosStrategyDAO chaosStrategyDAO;

    /**
     *  DAO des stratégies basées sur la croissance.
     */
    private GrowthStrategyDAO growthStrategyDAO;

    /**
     *  DAO des stratégies basées sur la marge.
     */
    private MarginStrategyDAO marginStrategyDAO;

    /**
     *  DAO des stratégies basées sur l'opposition.
     */
    private OppositesStrategyDAO oppositesStrategyDAO;

    /**
     *  DAO des stratégies basées sur l'avancement.
     */
    private ForwardStrategyDAO forwardStrategyDAO;

    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     *  @param chaosStrategyDAO DAO des stratégies basées sur le niveau de chaos.
     *  @param growthStrategyDAO DAO des stratégies basées sur le niveau de croissance.
     *  @param marginStrategyDAO DAO des stratégies basées sur la marge.
     *  @param oppositesStrategyDAO DAO des stratégies basées sur les oppositions.
     *  @param forwardStrategyDAO DAO des stratégies basées sur l'avancement.
     */
    public MixinDAO(
            @Autowired SessionFactory factory,
            @Autowired ChaosStrategyDAO chaosStrategyDAO,
            @Autowired GrowthStrategyDAO growthStrategyDAO,
            @Autowired MarginStrategyDAO marginStrategyDAO,
            @Autowired OppositesStrategyDAO oppositesStrategyDAO,
            @Autowired ForwardStrategyDAO forwardStrategyDAO) {
        super(factory);
        this.chaosStrategyDAO = chaosStrategyDAO;
        this.growthStrategyDAO = growthStrategyDAO;
        this.marginStrategyDAO = marginStrategyDAO;
        this.oppositesStrategyDAO = oppositesStrategyDAO;
        this.forwardStrategyDAO = forwardStrategyDAO;
    }

    /**
     *  Recherche et retourne une entité par équivalence.
     *  @param timeline Ligne temporelle liée.
     *  @param smooth Niveau de lissage.
     *  @param chaosInstance Stratégie de chaos.
     *  @param chaosWeight Pondération de la stratégie de chaos.
     *  @param growthInstance Stratégie de croissance.
     *  @param growthWeight Pondération de la stratégie de croissance.
     *  @param marginInstance Stratégie de marge.
     *  @param marginWeight Pondération de la sratégie de marge.
     *  @param oppositesInstance Stratégie d'opposition.
     *  @param oppositesWeight Poids de la stratégie d'opposition.
     *  @param forwardInstance Stratégie d'avancement.
     *  @param forwardWeight Poids de la stratégie d'avancement.
     *  @return Entité trouvée ou nul.
     */
    public MixinEntity selectByEquivalence(
            TimelineEntity timeline,
            int smooth,
            ChaosStrategyEntity chaosInstance,
            double chaosWeight,
            GrowthStrategyEntity growthInstance,
            double growthWeight,
            MarginStrategyEntity marginInstance,
            double marginWeight,
            OppositesStrategyEntity oppositesInstance,
            double oppositesWeight,
            ForwardStrategyEntity forwardInstance,
            double forwardWeight) {
        List<MixinEntity> result = super.getSession()
                .getNamedQuery("Mixin.selectByEquivalence")
                .setParameter("timeline", timeline)
                .setParameter("smooth", smooth)
                .setParameter("chaosInstance", chaosInstance)
                .setParameter("chaosWeight", chaosWeight)
                .setParameter("growthInstance", growthInstance)
                .setParameter("growthWeight", growthWeight)
                .setParameter("marginInstance", marginInstance)
                .setParameter("marginWeight", marginWeight)
                .setParameter("oppositesInstance", oppositesInstance)
                .setParameter("oppositesWeight", oppositesWeight)
                .setParameter("forwardInstance", forwardInstance)
                .setParameter("forwardWeight", forwardWeight)
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
    public MixinEntity selectByHashCode(int hash) {
        List<MixinEntity> result = super.getSession()
                .getNamedQuery("Mixin.selectByHashCode")
                .setParameter("hash", hash)
                .list();

        if (result.size() > 0) {
            return result.get(0);
        }

        return null;
    }

    /**
     *  Sélectionne ou enregistre une stratégie dans la base.
     *  @param timeline Ligne temporelle liée.
     *  @param smooth Niveau de lissage.
     *  @param ponderations Liste des pondérations.
     *  @param entities Liste des entités.
     *  @return Stratégie trouvée ou insérée.
     *  @throws InnerException En cas d'erreur interne.
     */
    public MixinEntity retrieveOrPersist(TimelineEntity timeline, int smooth, double[] ponderations, AbstractArtifact[] entities) throws InnerException {
        if (ponderations.length != entities.length) {
            throw new InnerException("Invalid set of ponderations and strategies");
        }

        /* Création de la stratégie */
        MixinEntity mixin = new MixinEntity();
        mixin.setTimeline(timeline);
        mixin.setSmooth(smooth);
        for (int i = 0; i < ponderations.length; i++) {
            StrategyEntity entity = entities[i].getStrategy().asEntity();

            /* Parcours des types */
            if (entity instanceof ChaosStrategyEntity) {
                /* Recherche de l'équivalence */
                ChaosStrategyEntity cast = (ChaosStrategyEntity) entity;
                cast = this.chaosStrategyDAO.retrieveOrPersist(cast);

                /* Affectation */
                mixin.setChaosInstance(cast);
                mixin.setChaosWeight(ponderations[i]);

            } else if (entity instanceof MarginStrategyEntity) {
                /* Recherche de l'équivalence */
                MarginStrategyEntity cast = (MarginStrategyEntity) entity;
                cast = this.marginStrategyDAO.retrieveOrPersist(cast);

                /* Affectation */
                mixin.setMarginInstance(cast);
                mixin.setMarginWeight(ponderations[i]);
            } else if (entity instanceof GrowthStrategyEntity) {
                /* Recherche de l'équivalence */
                GrowthStrategyEntity cast = (GrowthStrategyEntity) entity;
                cast = this.growthStrategyDAO.retrieveOrPersist(cast);

                /* Affectation */
                mixin.setGrowthInstance(cast);
                mixin.setGrowthWeight(ponderations[i]);
            } else if (entity instanceof OppositesStrategyEntity) {
                /* Recherche de l'équivalence */
                OppositesStrategyEntity cast = (OppositesStrategyEntity) entity;
                cast = this.oppositesStrategyDAO.retrieveOrPersist(cast);

                /* Affectations */
                mixin.setOppositesInstance(cast);
                mixin.setOppositesWeight(ponderations[i]);
            } else if (entity instanceof ForwardStrategyEntity) {
                /* Recherche de l'équivalence */
                ForwardStrategyEntity cast = (ForwardStrategyEntity) entity;
                cast = this.forwardStrategyDAO.retrieveOrPersist(cast);

                /* Affectations */
                mixin.setForwardInstance(cast);
                mixin.setForwardWeight(ponderations[i]);
            } else {
                throw new InnerException("Invalid strategy type");
            }
        }
        mixin.setHash(mixin.hashCode());

        /* Récupération */
        MixinEntity previous = this.selectByHashCode(mixin.hashCode());

        if (previous == null) {
            super.getSession().persist(mixin);
            return mixin;
        } else {
            return previous;
        }
    }

    public void refreshHashCodes() {
        List<ChaosStrategyEntity> l1 = super.getSession().createQuery("SELECT cs FROM ChaosStrategyEntity cs").list();
        for (ChaosStrategyEntity s : l1) {
            s.setHash(s.hashCode());
            super.getSession().saveOrUpdate(s);
        }


        List<GrowthStrategyEntity> l2 = super.getSession().createQuery("SELECT cs FROM GrowthStrategyEntity cs").list();
        for (GrowthStrategyEntity s : l2) {
            s.setHash(s.hashCode());
            super.getSession().saveOrUpdate(s);
        }


        List<OppositesStrategyEntity> l3 = super.getSession().createQuery("SELECT cs FROM OppositesStrategyEntity cs").list();
        for (OppositesStrategyEntity s : l3) {
            s.setHash(s.hashCode());
            super.getSession().saveOrUpdate(s);
        }



        List<MarginStrategyEntity> l4 = super.getSession().createQuery("SELECT cs FROM MarginStrategyEntity cs").list();
        for (MarginStrategyEntity s : l4) {
            s.setHash(s.hashCode());
            super.getSession().saveOrUpdate(s);
        }

        List<MixinEntity> l5 = super.getSession().createQuery("SELECT mxx FROM MixinEntity mxx LEFT JOIN mxx.oppositesInstance LEFT JOIN mxx.marginInstance LEFT JOIN mxx.growthInstance LEFT JOIN mxx.chaosInstance").list();
        for (MixinEntity s : l5) {
            s.setHash(s.hashCode());
            super.getSession().saveOrUpdate(s);
        }

    }
}
