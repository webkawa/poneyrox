package com.akasoft.poneyrox.entities.strategies;

import com.akasoft.poneyrox.core.strategies.categories.ChaosStrategy;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

/**
 *  Stratégie d'évaluation par le chaos.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "ChaosStrategy.selectByHashCode",
                query = "SELECT st " +
                        "FROM ChaosStrategyEntity st " +
                        "WHERE st.hash = :hash"
        ),
        @NamedQuery(
                name = "ChaosStrategy.selectByEquivalence",
                query = "SELECT cst " +
                        "FROM ChaosStrategyEntity cst " +
                        "WHERE cst.mode = :mode " +
                        "AND cst.type = :type " +
                        "AND cst.size = :size " +
                        "AND cst.floor = :floor"
        )
})
public class ChaosStrategyEntity extends StrategyEntity<ChaosStrategy> {
    /**
     *  Type d'évaluation.
     *  Si true, évaluation sur la demande ; false pour offre.
     */
    private boolean type;

    /**
     *  Taille évaluée.
     */
    private int size;

    /**
     *  Seuil.
     *  Exprimée en pourcentage entre le cours maximal et le cours minimal.
     */
    private double floor;

    /**
     *  Retourne le type.
     *  @return Type.
     */
    public boolean getType() {
        return this.type;
    }

    /**
     *  Retourne la taille évaluée.
     *  @return Taille évaluée.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Retourne le seuil.
     *  @return Seuil.
     */
    public double getFloor() {
        return this.floor;
    }

    /**
     *  Affecte le type.
     *  @param type Type.
     */
    public void setType(boolean type) {
        this.type = type;
    }

    /**
     *  Affecte la taille évaluée.
     *  @param size Taille évaluée.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *  Affecte le seuil.
     *  @param floor Seuil.
     */
    public void setFloor(double floor) {
        this.floor = floor;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public ChaosStrategy asStrategy() {
        return new ChaosStrategy(this.size, this.floor, this.type);
    }

    /**
     *  Retourne la clef de hachage spécifique.
     *  @return Clef de hachage.
     */
    @Override
    protected int hashCodeSpe() {
        return Objects.hash(
                this.type,
                this.size,
                this.floor);
    }
}
