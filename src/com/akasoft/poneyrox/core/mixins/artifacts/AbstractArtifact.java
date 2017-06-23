package com.akasoft.poneyrox.core.mixins.artifacts;

import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;

/**
 *  Artefact.
 *  Classe représentative d'une configuration particulière d'une stratégie, intégrée dans un lot plus large
 *  lui-meme rattaché à une courbe. Intègre des fonctions de calcul permettant la génération des résultats.
 */
public abstract class AbstractArtifact {
    /**
     *  Stratégie liée.
     */
    private AbstractStrategy strategy;

    /**
     *  Liste des opérations validables.
     *  Dans l'ordre : [Long, Cours]
     */
    private boolean[] authorizations;

    /**
     *  Liste des opérations validées.
     *  Dans l'ordre : [Long, Cours]
     */
    private boolean[] validations;

    /**
     *  Constructeur.
     *  @param strategy Stratégie intégrée.
     */
    public AbstractArtifact(AbstractStrategy strategy) {
        this.strategy = strategy;
        this.authorizations = new boolean[2];
        this.validations = new boolean[2];
        this.clearValidations();
    }

    /**
     *  Nettoie les autorisations et validations.
     */
    private void clearValidations() {
        for (int i = 0; i < 2; i++) {
            this.setAuthorization(i, false);
            this.setValidation(i, false);
        }
    }

    /**
     *  Retourne la stratégie rattachée.
     *  @return Stratégie rattachée.
     */
    public AbstractStrategy getStrategy() {
        return this.strategy;
    }

    /**
     *  Retourne les accréditations de la stratégie.
     *  @return Accréditations de la stratégie.
     */
    public boolean[] getAuthorizations() {
        return this.authorizations;
    }

    /**
     *  Retourne les validations de la stratégie.
     *  @return Validations de la stratégie.
     */
    public boolean[] getValidations() {
        return this.validations;
    }

    /**
     *  Retourne le niveau de cohérence de la stratégie.
     *  Permet la réduction des stratégies d'entrée lors du passage dans le mixer.
     *  @return Niveau de cohérence compris entre 0 et 1.
     */
    public abstract double getCoherency();

    /**
     *  Affecte une authorisation.
     *  @param idx Index de l'autorisation.
     *  @param value Valeur affectée.
     */
    protected void setAuthorization(int idx, boolean value) {
        this.authorizations[idx] = value;
    }

    /**
     *  Affecte une validation.
     *  @param idx Index de la validation.
     *  @param value Valeur affectée.
     */
    protected void setValidation(int idx, boolean value) {
        this.validations[idx] = value;
    }
}
