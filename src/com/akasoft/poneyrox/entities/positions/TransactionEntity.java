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
     *  Seuil de sécurité.
     *  Seuil auquel la transaction est automatiquement annulée par le programme.
     */
    private double stopLoss;

    /**
     *  Seuil de succès.
     */
    private double stopSuccess;

    /**
     *  Marge de sécurité.
     *  Marge de sécurité prévue à la création de la transaction.
     */
    private double stopGap;

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
     *  Portefeuille lié.
     */
    @ManyToOne
    private WalletEntity wallet;

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
     *  Retourne le seuil de sécurité de la transaction.
     *  @return Seuil de sécurité.
     */
    public double getStopLoss() {
        return this.stopLoss;
    }

    /**
     *  Retourne le seuil de succès de la transaction.
     *  @return Seuil de succès.
     */
    public double getStopSuccess() {
        return this.stopSuccess;
    }

    /**
     *  Retourne la marge de sécurité de la transaction.
     *  @return Marge de sécurité.
     */
    public double getStopGap() {
        return this.stopGap;
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
     *  Retourne le portefeuille lié.
     *  @return Portefeuille lié.
     */
    public WalletEntity getWallet() {
        return this.wallet;
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
     *  Définit le seuil de sécurité.
     *  @param stopLoss Seuil de sécurité.
     */
    public void setStopLoss(double stopLoss) {
        this.stopLoss = stopLoss;
    }

    /**
     *  Définit le seuil de succès.
     *  @param stopSuccess Seuil de succès.
     */
    public void setStopSuccess(double stopSuccess) {
        this.stopSuccess = stopSuccess;
    }

    /**
     *  Définit la marge de sécurité.
     *  @param stopGap Marge de sécurité.
     */
    public void setStopGap(double stopGap) {
        this.stopGap = stopGap;
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

    /**
     *  Définit le portefeuille lié.
     *  @param wallet Valeur affectée.
     */
    public void setWallet(WalletEntity wallet) {
        this.wallet = wallet;
    }
}
