package com.akasoft.poneyrox.entities.strategies;

import com.akasoft.poneyrox.core.strategies.categories.OppositesStrategy;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

/**
 *  Stratégie de comparaison aux extremes.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "OppositesStrategy.selectByHashCode",
                query = "SELECT st " +
                        "FROM OppositesStrategyEntity st " +
                        "WHERE st.hash = :hash"
        ),
        @NamedQuery(
                name = "OppositesStrategy.selectByEquivalence",
                query = "SELECT op " +
                        "FROM OppositesStrategyEntity op " +
                        "WHERE op.mode = :mode " +
                        "AND op.size = :size " +
                        "AND op.reverse = :reverse " +
                        "AND op.incomingProximity = :incomingProximity " +
                        "AND op.exitingProximity = :exitingProximity"
        )
})
public class OppositesStrategyEntity extends StrategyEntity<OppositesStrategy> {
    /**
     *  Nombre de cellules évaluées.
     */
    private int size;

    /**
     *  Inversion du test.
     */
    private boolean reverse;

    /**
     *  Pourcentage de proximité avec une extrémité en approche.
     *  Exprimé en pourcentage de différence avec l'extreme précédent.
     */
    private double incomingProximity;

    /**
     *  Pourcentage de proximité avec une extrémité en sortie.
     *  Exprimé en pourcentage de différence avec l'extreme précédent.
     */
    private double exitingProximity;

    /**
     *  Retourne le nombre de cellules évaluées.
     *  @return Nombre de cellules.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Indique si les tests sont menés en mode inversé.
     *  @return true si les tests sont menés en inversé.
     */
    public boolean isReverse() {
        return this.reverse;
    }

    /**
     *  Retourne le niveau de proximité en approche.
     *  @return Niveau de proximité en approche.
     */
    public double getIncomingProximity() {
        return this.incomingProximity;
    }

    /**
     *  Retourne le niveau de proximité en sortie.
     *  @return Niveau de proximité en sortie.
     */
    public double getExitingProximity() {
        return this.exitingProximity;
    }

    /**
     *  Définit le niveau de proximité.
     *  @param size Nombre de cellules.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *  Définit l'inversion des tests.
     *  @param reverse Valeur affectée.
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    /**
     *  Définit le niveau de proximité en entrée.
     *  @param proximity Valeur affectée.
     */
    public void setIncomingProximity(double proximity) {
        this.incomingProximity = proximity;
    }

    /**
     *  Définit le niveau de proximité en sortie.
     *  @param proximity Valeur affectée.
     */
    public void setExitingProximity(double proximity) {
        this.exitingProximity = proximity;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public OppositesStrategy asStrategy() {
        return new OppositesStrategy(this.size, this.reverse, this.incomingProximity, this.exitingProximity);
    }

    /**
     *  Retourne la clef de hachage spécifique.
     *  @return Clef de hachage.
     */
    @Override
    protected int hashCodeSpe() {
        return Objects.hash(
                this.size,
                this.reverse,
                this.incomingProximity,
                this.exitingProximity);
    }
}
