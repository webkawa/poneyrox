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
                        "AND ms.margin= :margin "
        )
})
public class MarginStrategyEntity extends StrategyEntity {
    /**
     *  Marge ciblée.
     */
    private double margin;

    /**
     *  Retourne la marge ciblée.
     *  @return Marge ciblée.
     */
    public double getMargin() {
        return this.margin;
    }

    /**
     *  Affecte la marge ciblée.
     *  @param margin Marge ciblée.
     */
    public void setMargin(double margin) {
        this.margin = margin;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public AbstractStrategy asStrategy() {
        return new MarginStrategy(this.margin);
    }

    /**
     *  Retourne la clef de hachage spécifique.
     *  @return Clef de hachage.
     */
    @Override
    protected int hashCodeSpe() {
        return Objects.hash(this.margin);
    }
}
