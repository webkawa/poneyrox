package com.akasoft.poneyrox.core.mixins.batch;

import com.akasoft.poneyrox.core.mixins.artifacts.AbstractArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Lot.
 *  Combinaison de configurations distinctes d'une meme stratégie applicable en mode court
 *  ou long.
 */
public abstract class AbstractBatch<TArtifact extends AbstractArtifact>  {
    /**
     *  Stratégie.
     */
    private final Class strategy;

    /**
     *  Ligne temporelle traitée.
     */
    private final AbstractCurve curve;

    /**
     *  Listes stratégies valables en long.
     */
    private List<TArtifact> longArtifacts;

    /**
     *  Liste des stratégies valables en court.
     */
    private List<TArtifact> shortArtifacts;

    /**
     *  Constructeur.
     *  @param strategy Type de stratégie appliquée.
     *  @param curve Courbe d'application.
     *  @param source Liste des artefacts source.
     */
    public AbstractBatch(Class strategy, AbstractCurve curve, List<TArtifact> source) {
        /* Affectation des paramètres */
        this.strategy = strategy;
        this.curve = curve;
        this.longArtifacts = new ArrayList<>();
        this.shortArtifacts = new ArrayList<>();

        /* Filtrage des stratégies éligibles */
        for (TArtifact artifact : source) {
            boolean[] authorizations = artifact.getAuthorizations();
            if (authorizations[0]) {
                this.longArtifacts.add(artifact);
            }
            if (authorizations[1]) {
                this.shortArtifacts.add(artifact);
            }
        }
    }

    /**
     *  Retourne la stratégie rattachée.
     *  @return Stratégie rattachée.
     */
    public Class getStrategy() {
        return this.strategy;
    }

    /**
     *  Retourne la courbe d'appartenance.
     *  @return Courbe d'appartenance.
     */
    public AbstractCurve getCurve() {
        return this.curve;
    }

    /**
     *  Retourne la liste des agents habilités en mode long.
     *  @return Liste des agents en entrée longue.
     */
    public abstract List<TArtifact> getLong();

    /**
     *  Retourne la liste des agents en entrée courte.
     *  @return Liste des agents en entrée courte.
     */
    public abstract List<TArtifact> getShort();

    /**
     *  Retourne la liste complète des artefacts en mode long.
     *  @return Liste complète des artefacts.
     */
    protected List<TArtifact> getLongArtifacts() {
        return this.longArtifacts;
    }

    /**
     *  Retourne la liste complète des artefacts en mode court.
     *  @return Liste complète des artefacts.
     */
    protected List<TArtifact> getShortArtifacts() {
        return this.shortArtifacts;
    }
}
