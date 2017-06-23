package com.akasoft.poneyrox.entities.strategies;

import com.akasoft.poneyrox.core.strategies.categories.ForwardStrategy;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.util.Objects;

/**
 *  Stratégie d'évaluation par les courbes d'avancement.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "ForwardStrategy.selectByHashCode",
                query = "SELECT fw " +
                        "FROM ForwardStrategyEntity fw " +
                        "WHERE fw.hash = :hash"
        )
})
public class ForwardStrategyEntity extends StrategyEntity<ForwardStrategy> {
    /**
     *  Nombre cellules d'avance considérées.
     */
    private int forward;

    /**
     *  Nombre de cellules de recul considérées.
     */
    private int backward;

    /**
     *  Décalage de l'évaluation exprimé en cellules de recul.
     */
    private int offset;

    /**
     *  Pourcentage de différence toléré par rapport à la prévision.
     */
    private double difference;

    /**
     *  Retourne le nombre de cellules d'avance.
     *  @return Nombre de cellules d'avance.
     */
    public int getForward() {
        return this.forward;
    }

    /**
     *  Retourne le nombre de cellules de recul.
     *  @return Nombre de cellules de recul.
     */
    public int getBackward() {
        return this.backward;
    }

    /**
     *  Retourne le décalage.
     *  @return Décalage.
     */
    public int getOffset() {
        return this.offset;
    }

    /**
     *  Retourne le pourcentage de différence toléré.
     *  @return Pourcentage de différence toléré.
     */
    public double getDifference() {
        return this.difference;
    }

    /**
     *  Définit le nombre de cellules d'avance.
     * @param forward Nombre de cellules d'avance.
     */
    public void setForward(int forward) {
        this.forward = forward;
    }

    /**
     *  Définit le nombre de cellules de recul.
     *  @param backward Nombre de cellules de recul.
     */
    public void setBackward(int backward) {
        this.backward = backward;
    }

    /**
     *  Définit le décalage.
     *  @param offset Décalage.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     *  Définit le pourcentage de différence toléré.
     *  @param difference Différence tolérée.
     */
    public void setDifference(double difference) {
        this.difference = difference;
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    @Override
    public ForwardStrategy asStrategy() {
        return new ForwardStrategy(this.forward, this.backward, this.offset, this.difference);
    }

    /**
     *  Retourne le code de l'objet.
     *  @return Code de l'objet.
     */
    @Override
    protected int hashCodeSpe() {
        return Objects.hash(this.forward, this.backward, this.offset, this.difference);
    }
}
