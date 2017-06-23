package com.akasoft.poneyrox.entities.strategies;

import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.categories.MarginStrategy;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import javax.persistence.*;
import java.util.Objects;

/**
 *  Stratégie de sortie par la marge.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "MarginStrategy.selectByHashCode",
                query = "SELECT st " +
                        "FROM MarginStrategyEntity st " +
                        "WHERE st.hash = :hash"
        ),
        @NamedQuery(
                name = "MarginStrategy.selectByEquivalence",
                query = "SELECT ms " +
                        "FROM MarginStrategyEntity ms " +
                        "WHERE ms.mode = :mode " +
                        "AND ms.profit = :profit " +
                        "AND ms.loss = :loss"
        )
})
public class MarginStrategyEntity extends StrategyEntity {
    /**
     *  Profit maximum toléré.
     *  Exprimé en pourcentage de la marge initiale entre offre et demande.
     */
    private double profit;

    /**
     *  Perte maximum tolérée.
     *  Exprimée en pourcentage de la marge initiale entre offre et demande sans prise
     *  en compte du déficit initial.
     */
    private double loss;

    /**
     *  Retourne le profit maximum toléré.
     *  @return Profit maximum.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     *  Retourne la perte maximum tolérée.
     *  @return Perte maximum.
     */
    public double getLoss() {
        return this.loss;
    }

    /**
     *  Définit le profit maximum toléré.
     *  @param profit Profit maximum.
     */
    public void setProfit(double profit) {
        this.profit = profit;
    }

    /**
     *  Définit le profit minimum toléré.
     *  @param loss Perte maximum.
     */
    public void setLoss(double loss) {
        this.loss = loss;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public AbstractStrategy asStrategy() {
        return new MarginStrategy(this.profit, this.loss);
    }

    /**
     *  Retourne la clef de hachage spécifique.
     *  @return Clef de hachage.
     */
    @Override
    protected int hashCodeSpe() {
        return Objects.hash(this.profit, this.loss);
    }
}
