package com.akasoft.poneyrox.core.mixins.artifacts;

import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitShortITF;
import com.akasoft.poneyrox.entities.positions.PositionEntity;

/**
 *  Artefact de sortie.
 */
public class ExitArtifact extends AbstractArtifact {
    /**
     *  Valeur aléatoire exploitée pour la gestion de la cohérence.
     */
    private final double random;

    /**
     *  Constructeur.
     *  @param strategy Stratégie.
     */
    public ExitArtifact(AbstractStrategy strategy) {
        super(strategy);
        this.random = Math.random();
        this.affect();
    }

    /**
     *  Retourne le niveau de cohérence.
     *  Dans le cas d'une stratégie, les solutions sont choisies de manière aléatoire étant donné
     *  qu'il n'est pas possible de connaitre leur efficacité sans les comparer à une position.
     *  @return Niveau de cohérence.
     */
    public double getCoherency() {
        return this.random;
    }

    /**
     *  Affecte les autorisations.
     */
    public void affect() {
        if (super.getStrategy() instanceof ExitLongITF) {
            super.setAuthorization(0, true);
        }
        if (super.getStrategy() instanceof ExitShortITF) {
            super.setAuthorization(1, true);
        }
    }

    /**
     *  Réalise le calcul de l'artefact pour une position passée en paramètre.
     *  En stratégie de sortie, le calcul doit etre réalisé à partir de la liste des positions en cours.
     */
    public void compute(PositionEntity position) {
        if (super.getStrategy() instanceof ExitLongITF) {
            super.setValidation(0, ((ExitLongITF) super.getStrategy()).mustExitLong(position.getEntry()));
        }
        if (super.getStrategy() instanceof ExitShortITF) {
            super.setValidation(1, ((ExitShortITF) super.getStrategy()).mustExitShort(position.getEntry()));
        }
    }
}
