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
     *  @param stopSuccess Seuil de succès.
     *  @param stopGap Marge de sécurité.
     *  @param position Position liée.
     *  @return Transaction créée.
     */
    public TransactionEntity persistTransaction(
            String foreign,
            double entry,
            double size,
            double stopLoss,
            double stopSuccess,
            double stopGap,
            PositionEntity position,
            WalletEntity wallet) {
        TransactionEntity result = new TransactionEntity();
        result.setForeign(foreign);
        result.setEntry(entry);
        result.setSize(size);
        result.setStopLoss(stopLoss);
        result.setStopSuccess(stopSuccess);
        result.setStopGap(stopGap);
        result.setPosition(position);
        result.setWallet(wallet);

        super.getSession().persist(result);
        return result;
    }

    /**
     *  Réalise la mise à jour du seuil de sécurité.
     *  @param target Transaction ciblée.
     *  @param stopLoss Seuil ciblé.
     */
    public void updateStopLoss(TransactionEntity target, double stopLoss) {
        target.setStopLoss(stopLoss);
        super.getSession().update(target);
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
