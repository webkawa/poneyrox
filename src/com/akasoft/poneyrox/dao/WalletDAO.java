package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.entities.positions.WalletEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * DAO des portefeuilles.
 */
@Repository
public class WalletDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public WalletDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Crée un portefeuille.
     *  @return Portefeuille créé.
     */
    public WalletEntity persistWallet() {
        WalletEntity result = new WalletEntity();

        super.getSession().persist(result);
        return result;
    }

    /**
     *  Procède au rafraichissement d'un portefeuille.
     *  @param wallet Portefeuille à rafraichir.
     *  @return Portefeuille rafraichi.
     */
    public WalletEntity refreshWallet(WalletEntity wallet) {
        return super.getSession().get(WalletEntity.class, wallet.getId());
    }
}
