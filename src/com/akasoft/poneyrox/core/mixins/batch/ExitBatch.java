package com.akasoft.poneyrox.core.mixins.batch;

import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;

import java.util.List;
import java.util.stream.Collectors;

/**
 *  Lot de sortie.
 *  Lot de stratégies permettant la sortie de position.
 *  @param <TStrategy> Stratégie suivie.
 */
public class ExitBatch<TStrategy extends AbstractStrategy> extends AbstractBatch<ExitArtifact> {
    /**
     *  Constructeur.
     *  @param strategy Stratégie liée.
     *  @param curve Courbe traitée.
     *  @param source Liste d'artefact consolidés.
     */
    public ExitBatch(Class<TStrategy> strategy, AbstractCurve curve, List<ExitArtifact> source) {
        super(strategy, curve, source);
    }

    /**
     *  Retourne la liste des agents habilités en mode long.
     *  @return Liste des agents en entrée longue.
     */
    @Override
    public List<ExitArtifact> getLong() {
        return this.getLongArtifacts()
                .stream()
                .filter(e -> e.getAuthorizations()[0])
                .collect(Collectors.toList());
    }

    /**
     *  Retourne la liste des agents en entrée courte.
     *  @return Liste des agents en entrée courte.
     */
    @Override
    public List<ExitArtifact> getShort() {
        return this.getShortArtifacts()
                .stream()
                .filter(e -> e.getAuthorizations()[1])
                .collect(Collectors.toList());
    }
}
