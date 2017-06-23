package com.akasoft.poneyrox.entities.positions;

import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.parameters.VariationType;

import javax.persistence.*;
import java.util.Objects;
import java.util.UUID;

/**
 *  Stratégie.
 *  Stratégie référencée dans la base de données.
 *  @param <TStrategy> Type de stratégie employée.
 */
@MappedSuperclass
public abstract class StrategyEntity<TStrategy extends AbstractStrategy> {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     *  Mode.
     */
    @Enumerated(EnumType.STRING)
    private VariationType mode;

    /**
     *  Clef de hachage.
     */
    private int hash;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne le mode.
     *  @return Mode.
     */
    public VariationType getMode() {
        return this.mode;
    }

    /**
     *  Retourne la clef de hachage.
     *  @return Clef de hachage.
     */
    protected int getHash() {
        return this.hash;
    }

    /**
     *  Affecte le mode.
     *  @param mode Mode affecté.
     */
    public void setMode(VariationType mode) {
        this.mode = mode;
    }

    /**
     *  Affecte la clef de hachage.
     *  @param hash Clef de hachage.
     */
    public void setHash(int hash) {
        this.hash = hash;
    }

    /**
     *  Retourne la clef de hachage.
     *  @return Clef de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                this.mode,
                this.hashCodeSpe());
    }

    /**
     *  Convertit l'entité en stratégie réelle.
     *  @return Stratégie réelle.
     */
    public abstract TStrategy asStrategy();

    /**
     *  Retourne la clef de hachage spécifique.
     *  @return Clef de hachage spécifique.
     */
    protected abstract int hashCodeSpe();
}
