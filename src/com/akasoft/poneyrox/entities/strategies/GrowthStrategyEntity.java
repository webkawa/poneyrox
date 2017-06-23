package com.akasoft.poneyrox.entities.strategies;

import com.akasoft.poneyrox.core.strategies.categories.GrowthStrategy;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

/**
 *  Stratégie de positionnement par croissance ou décroissance.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "GrowthStrategy.selectByHashCode",
                query = "SELECT st " +
                        "FROM GrowthStrategyEntity st " +
                        "WHERE st.hash = :hash"
        ),
        @NamedQuery(
                name = "GrowthStrategy.selectByEquivalence",
                query = "SELECT gs " +
                        "FROM GrowthStrategyEntity gs " +
                        "WHERE gs.mode = :mode " +
                        "AND gs.type = :type " +
                        "AND gs.size = :size " +
                        "AND gs.level = :level"
        )
})
public class GrowthStrategyEntity extends StrategyEntity<GrowthStrategy> {
    /**
     *  Type d'évaluation.
     *  Si true, évaluation sur la demande ; false pour offre.
     */
    private boolean type;

    /**
     *  Nombre de cellules évaluées.
     */
    private int size;

    /**
     *  Pourcentage de croissance supplémentaire attendue de
     *  chaque cellule.
     */
    private double level;

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
     *  Retourne le pourcentage de croissance attendue.
     *  @return Pourcentage de croissance attendue.
     */
    public double getLevel() {
        return this.level;
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
     *  Affecte le niveau de croissance attendu.
     *  @param level Niveau de croissance.
     */
    public void setLevel(double level) {
        this.level = level;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public GrowthStrategy asStrategy() {
        return new GrowthStrategy(this.type, this.size, this.level);
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
                this.level);
    }
}
