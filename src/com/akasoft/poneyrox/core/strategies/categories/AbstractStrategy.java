package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.ObserverITF;
import com.akasoft.poneyrox.core.strategies.interfaces.StrategyITF;
import com.akasoft.poneyrox.core.strategies.parameters.AbstractParameter;
import com.akasoft.poneyrox.core.strategies.parameters.VariationParameter;
import com.akasoft.poneyrox.core.strategies.parameters.VariationType;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.positions.StrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Stratégie.
 *  Classe dédiée à l'analyse d'une courbe afin de déterminer la pertinence d'une prise
 *  ou sortie de position.
 *  @param <TEntity> Type d'entité persistente rattachée à la stratégie.
 */
public abstract class AbstractStrategy<TEntity extends StrategyEntity> implements StrategyITF<TEntity>, Cloneable {
    /**
     *  Nom empirique.
     */
    private final String name;

    /**
     *  Mode.
     */
    private VariationType mode;

    /**
     *  Pertinence.
     *  Indique si la stratégie présente des résultats pertinents pour la dernière courbe évaluée.
     */
    private boolean pertinent;

    /**
     *  Constructeur.
     *  @param name Nom empirique.
     */
    protected AbstractStrategy(String name) {
        this.name = name;
        this.mode = VariationType.AVERAGE;
    }

    /**
     *  Constructeur complet.
     *  @param name Nom empirique.
     *  @param mode Mode.
     */
    protected AbstractStrategy(String name, VariationType mode) {
        this.name = name;
        this.mode = mode;
    }

    /**
     *  Retourne le nom de la stratégie.
     *  @return Nom de la stratégie.
     */
    public String getName() {
        return this.name;
    }

    /**
     *  Retourne le mode.
     *  @return Mode.
     */
    public VariationType getMode() {
        return this.mode;
    }

    /**
     *  Indique si la dernière évaluation réalisée sur la courbe a retourné des résultats
     *  pertinents au vu du nombre d'entrées disponibles.
     *  @return true si la stratégie est pertinente.
     */
    public boolean isPertinent() {
        return this.pertinent;
    }

    /**
     *  Réalise la sérialisation externe.
     *  @return Entité sérialisée.
     */
    @Override
    public TEntity asEntity() {
        TEntity entity = this.serializeInner();
        entity.setMode(this.mode);
        return entity;
    }

    /**
     *  Exécute les opérations d'observation d'une courbe pouvant etre requise par la stratégie
     *  préalablement à l'évaluation. Met à jour l'indicateur de pertinence en conséquence.
     *  @param builds Cellules consolidées préalablement extraites (performance).
     *  @param curve Courbe observée.
     */
    public void observe(AbstractCurve curve, List<AbstractCell> builds) {
        if (this instanceof ObserverITF) {
            /* Conversion */
            ObserverITF obs = (ObserverITF) this;

            /* Vérification du recul */
            if (obs.size() <= builds.size()) {
                /* Sélection de la sous-liste de cellules */
                List<AbstractCell> sub = builds.subList(builds.size() - obs.size(), builds.size());

                /* Consolidation */
                obs.consolidate(curve, sub);

                /* Renvoi */
                this.pertinent = true;
            } else {
                /* Exclusion */
                this.pertinent = false;
            }
        } else {
            /* Par défaut */
            this.pertinent = true;
        }
    }

    /**
     *  Retourne la liste des paramètres initiaux rattachés à la stratégie.
     *  @return Liste des paramètres.
     *  @throws InnerException En cas d'erreur lors de la génération.
     */
    public List<AbstractParameter> emitParameters() throws InnerException {
        ArrayList<AbstractParameter> result = new ArrayList<>();
        result.addAll(this.emitInnerParameters());
        result.add(new VariationParameter("mode"));
        return result;
    }

    /**
     *  Consomme un paramètre initial.
     *  @param key Clef du paramètre.
     *  @param value Valeur.
     *  @throws InnerException En cas d'erreur interne.
     */
    public void consumeParameter(String key, Object value) throws InnerException {
        if ("mode".equals(key)) {
            this.mode = (VariationType) value;
        } else {
            if (!this.consumeInnerParameter(key, value)) {
                throw new InnerException("Parameter %s has not been consumed", key);
            }
        }
    }

    /**
     *  Retourne une copie de la stratégie.
     *  @return Copie de l'objet.
     */
    public abstract AbstractStrategy<TEntity> clone();

    /**
     *  Réalise la sérialisation spécifique.
     *  @return Entité générée.
     */
    protected abstract TEntity serializeInner();

    /**
     *  Retourne la liste des paramètres spécifiques à la stratégie.
     *  @return Liste des paramètres spécifiques.
     */
    protected abstract List<AbstractParameter> emitInnerParameters() throws InnerException;

    /**
     *  Consomme un paramètre spécifique.
     *  @param key Clef du paramètre.
     *  @param value Valeur.
     *  @return Consommation.
     *  @throws InnerException En cas d'erreur interne.
     */
    protected abstract boolean consumeInnerParameter(String key, Object value) throws InnerException;
}
