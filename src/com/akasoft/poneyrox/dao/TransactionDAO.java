package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.TransactionEntity;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *  DAO des transactions.
 */
@Repository
public class TransactionDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public TransactionDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Enregistre une transaction dans la base.
     *  @param foreign Clef d'accès.
     *  @param entry Taux à l'entrée.
     *  @param size Taille de la transaction.
     *  @param stopLoss Seuil de sécurité.
     *  @param stopGap Marge de sécurité.
     *  @param position Position liée.
     *  @return Transaction créée.
     */
    public TransactionEntity persistTransaction(
            String foreign,
            double entry,
            double size,
            double stopLoss,
            double stopGap,
            PositionEntity position,
            WalletEntity wallet) {
        TransactionEntity result = new TransactionEntity();
        result.setForeign(foreign);
        result.setEntry(entry);
        result.setSize(size);
        result.setStopLoss(stopLoss);
        result.setStopGap(stopGap);
        result.setPosition(position);
        result.setWallet(wallet);

        super.getSession().persist(result);
        return result;
    }

    /**
     *  Met à jour une transaction.
     *  @param entity Transaction mise à jour.
     */
    public void updateTransaction(TransactionEntity entity) {
        super.getSession().update(entity);
    }

    /**
     *  Réalise la fermeture d'une transaction.
     *  @param transaction Transaction fermée.
     *  @param exit Taux à la sortie.
     *  @param profit Profit réalisé.
     *  @param cost Cout de la transaction.
     *  @return Transaction mise à jour.
     */
    public TransactionEntity closeTransaction(TransactionEntity transaction, double exit, double profit, double cost) {
        transaction.setExit(exit);
        transaction.setProfit(profit);
        transaction.setCost(cost);

        super.getSession().update(transaction);
        return transaction;
    }
}
