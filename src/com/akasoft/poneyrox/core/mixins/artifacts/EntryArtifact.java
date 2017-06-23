package com.akasoft.poneyrox.core.mixins.artifacts;

import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;

/**
 *  Artefact d'entrée.
 */
public class EntryArtifact extends AbstractArtifact {

    /**
     *  Constructeur.
     *  @param strategy Stratégie analysée.
     */
    public EntryArtifact(AbstractStrategy strategy) {
        super(strategy);
        this.compute();
    }

    /**
     *  Retourne le niveau de cohérence.
     *  @return Niveau de cohérence.
     */
    @Override
    public double getCoherency() {
        boolean[] v = super.getValidations();
        if ((v[0] && !v[1]) || (!v[0] && v[1])) {
            return 1;
        }
        return 0;
    }

    /**
     *  Réalise le calcul de l'artefact pour la courbe courante.
     *  En stratégie d'entrée, le calcul est réalisé dès la création de l'artefact, de meme que l'affectation
     *  des autorisations.
     */
    private void compute() {
        if (super.getStrategy() instanceof EnterLongITF) {
            this.setAuthorization(0, true);
            this.setValidation(0, ((EnterLongITF) super.getStrategy()).mustEnterLong());
        }

        /* Entrée en mode cours */
        if (super.getStrategy() instanceof EnterShortITF) {
            this.setAuthorization(1, true);
            this.setValidation(1, ((EnterShortITF) super.getStrategy()).mustEnterShort());
        }
    }
}
