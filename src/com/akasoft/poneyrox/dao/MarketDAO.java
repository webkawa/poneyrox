package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.entities.markets.MarketEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;

/**
 *  DAO d'accès aux marchés.
 *  Classe d'accès aux marchés enregistrés dans la base.
 */
@Repository
public class MarketDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public MarketDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Réalise l'enregistrement d'un marché dans la base.
     *  @param key Clef d'accès au marché.
     *  @param label Nom du marché.
     *  @return Marché créé.
     */
    public MarketEntity persistMarket(String key, String label) {
        MarketEntity market = new MarketEntity();
        market.setKey(key);
        market.setLabel(label);

        super.getSession().persist(market);
        return market;
    }

    /**
     *  Retourne un marché par le biais de sa clef.
     *  @param key Clef d'accès.
     *  @return Marché trouvé.
     */
    public MarketEntity getMarketByKey(String key) {
        return (MarketEntity) super.getSession()
                .getNamedQuery("Market.getByKey")
                .setParameter("key", key)
                .getSingleResult();
    }

    /**
     *  Retourne la liste complète des marchés.
     *  @return Liste des marchés.
     */
    public List<MarketEntity> getAllMarkets() {
        return super.getSession()
                .getNamedQuery("Market.getAll")
                .getResultList();
    }
}
