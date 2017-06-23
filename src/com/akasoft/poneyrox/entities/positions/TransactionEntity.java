package com.akasoft.poneyrox.entities.positions;

import javax.persistence.*;
import java.util.UUID;

/**
 *  Transaction.
 */
@Entity
@NamedQueries(
        @NamedQuery(
                name = "Transaction.getOpenTransactions",
                query = "SELECT tx " +
                        "FROM TransactionEntity tx " +
                        "INNER JOIN tx.position pos " +
                        "WHERE pos.open = true"
        )
)
public class TransactionEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     *  Clef d'accès à la transaction coté broker.
     */
    @Column(name = "transaction_fk")
    private String foreign;

    /**
     *  Prix à l'entrée.
     */
    private double entry;

    /**
     *  Prix à la sortie.
     */
    private double exit;

    /**
     *  Profit réalisé.
     */
    private double profit;

    /**
     *  Montant de la position.
     */
    private double size;

    /**
     *  Cout de la transaction.
     */
    private double cost;

    /**
     *  Position liée.
     */
    @ManyToOne
    private PositionEntity position;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne la clef étrangère.
     *  @return Clef étrangère.
     */
    public String getForeign() {
        return this.foreign;
    }

    /**
     *  Retourne le taux à l'entrée.
     *  @return Taux à l'entrée.
     */
    public double getEntry() {
        return this.entry;
    }

    /**
     *  Retourne le taux à la sortie.
     *  @return Taux à la sortie.
     */
    public double getExit() {
        return this.exit;
    }

    /**
     *  Retourne le profit réalisé.
     *  @return Profit réalisé.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     *  Retourne la taille de la transaction.
     *  @return Taille.
     */
    public double getSize() {
        return this.size;
    }

    /**
     *  Retourne le cout de la transaction.
     *  @return Cout de la transaction.
     */
    public double getCost() {
        return this.cost;
    }

    /**
     *  Retourne la position rattachée.
     *  @return Position rattachée.
     */
    public PositionEntity getPosition() {
        return this.position;
    }

    /**
     *  Définit la clef étrangère.
     *  @param foreign Valeur affectée.
     */
    public void setForeign(String foreign) {
        this.foreign = foreign;
    }

    /**
     *  Définit le taux à l'entrée.
     *  @param entry Cout à l'entrée.
     */
    public void setEntry(double entry) {
        this.entry = entry;
    }

    /**
     *  Définit le taux à la sortie.
     *  @param exit Cout à la sortie.
     */
    public void setExit(double exit) {
        this.exit = exit;
    }

    /**
     *  Définit le profit réalisé.
     *  @param profit Profit réalisé.
     */
    public void setProfit(double profit) {
        this.profit = profit;
    }

    /**
     *  Définit la taille de la transaction.
     *  @param size Taille de la transaction.
     */
    public void setSize(double size) {
        this.size = size;
    }

    /**
     *  Définit le cout de la transaction.
     *  @param cost Cout de la transaction.
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     *  Définit la position.
     *  @param position Position définie.
     */
    public void setPosition(PositionEntity position) {
        this.position = position;
    }
}
