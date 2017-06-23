package com.akasoft.poneyrox.core.mixins.batch;

import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Lot d'entrée.
 *  Lot de stratégies permettant la prise de position.
 *  @param <TStrategy> Stratégie suivie.
 */
public class EntryBatch<TStrategy extends AbstractStrategy> extends AbstractBatch<EntryArtifact> {

    /**
     *  Constructeur sur position.
     *  @param strategy Stratégie rattachée.
     *  @param curve Courbe lue.
     *  @param source Liste de stratégies consolidées.
     */
    public EntryBatch(Class<TStrategy> strategy, AbstractCurve curve, List<EntryArtifact> source) {
        super(strategy, curve, source);
    }

    /**
     *  Retourne la liste des agents habilités en mode long.
     *  @return Liste des agents en entrée longue.
     */
    @Override
    public List<EntryArtifact> getLong() {
        /* Création du résultat */
        List<EntryArtifact> result = new ArrayList<>();

        /* Filtrage des entrées éligibles */
        List<EntryArtifact> buffer = this.getLongArtifacts()
                .stream()
                .filter(e -> e.getAuthorizations()[0])
                .collect(Collectors.toList());

        /* Placement en tete des entrées validantes */
        List<EntryArtifact> valid = buffer.stream()
                .filter(artifact -> ((EnterLongITF) artifact.getStrategy()).mustEnterLong())
                .collect(Collectors.toList());
        Collections.shuffle(valid);
        result.addAll(valid);

        /* Placement en queue des entrées non-validantes */
        List<EntryArtifact> invalid = buffer.stream()
                .filter(artifact -> !((EnterLongITF) artifact.getStrategy()).mustEnterLong())
                .collect(Collectors.toList());
        Collections.shuffle(invalid);
        result.addAll(invalid);

        /* Renvoi */
        return result;
    }

    /**
     *  Retourne la liste des agents en entrée courte.
     *  @return Liste des agents en entrée courte.
     */
    @Override
    public List<EntryArtifact> getShort() {
        /* Création du résultat */
        List<EntryArtifact> result = new ArrayList<>();

        /* Filtrage des entrées éligibles */
        List<EntryArtifact> buffer = this.getLongArtifacts()
                .stream()
                .filter(e -> e.getAuthorizations()[1])
                .collect(Collectors.toList());

        /* Placement en tete des entrées validantes */
        List<EntryArtifact> valid = buffer.stream()
                .filter(artifact -> ((EnterShortITF) artifact.getStrategy()).mustEnterShort())
                .collect(Collectors.toList());
        Collections.shuffle(valid);
        result.addAll(valid);

        /* Placement en queue des entrées non-validantes */
        List<EntryArtifact> invalid = buffer.stream()
                .filter(artifact -> !((EnterShortITF) artifact.getStrategy()).mustEnterShort())
                .collect(Collectors.toList());
        Collections.shuffle(invalid);
        result.addAll(invalid);

        /* Renvoi */
        return result;
    }
}
