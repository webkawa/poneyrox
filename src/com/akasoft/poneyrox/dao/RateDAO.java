package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 *  DAO d'accès aux taux.
 */
@Repository
public class RateDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public RateDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Réalise l'enregistrement d'un taux dans la base.
     *  @param market Marché du taux.
     *  @param time Date du taux.
     *  @param ask Cours de la demande.
     *  @param bid Cours de l'offre.
     *  @return Taux créé.
     */
    public RateEntity persistRate(MarketEntity market, long time, double ask, double bid) {
        RateEntity rate = new RateEntity();
        rate.setMarket(market);
        rate.setTime(time);
        rate.setAsk(ask);
        rate.setBid(bid);

        super.getSession().persist(rate);
        return rate;
    }
}
