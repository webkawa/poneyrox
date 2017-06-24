package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ObserverITF;
import com.akasoft.poneyrox.core.strategies.parameters.*;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.strategies.ForwardStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.GrowthStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import net.finmath.marketdata.model.AnalyticModelInterface;
import net.finmath.marketdata.model.curves.ForwardCurve;

import java.util.ArrayList;
import java.util.List;

/**
 *  Stratégie de positionnement par les courbes d'avancement.
 */
public class ForwardStrategy extends AbstractStrategy<ForwardStrategyEntity> implements ObserverITF<ForwardStrategyEntity>, EnterLongITF<ForwardStrategyEntity>, EnterShortITF<ForwardStrategyEntity> {
    /**
     *  Nombre minimum de cellules d'avance.
     */
    private static final int MINIMUM_FORWARD = 1;

    /**
     *  Nombre maximum de cellules d'avance.
     */
    private static final int MAXIMUM_FORWARD = 16;

    /**
     *  Finesse des cellules d'avance.
     */
    private static final int GRAIN_FORWARD = ForwardStrategy.MAXIMUM_FORWARD - ForwardStrategy.MINIMUM_FORWARD;

    /**
     *  Nombre minimum de cellules de retard.
     */
    private static final int MINIMUM_BACKWARD = 1;

    /**
     *  Nombre maximum de cellules de retard.
     */
    private static final int MAXIMUM_BACKWARD = 16;

    /**
     *  Finesse des cellules de retard.
     */
    private static final int GRAIN_BACKWARD = ForwardStrategy.MAXIMUM_BACKWARD - ForwardStrategy.MINIMUM_BACKWARD;

    /**
     *  Décalage minimum.
     */
    private static final int MINIMUM_OFFSET = 0;

    /**
     *  Décalage maximum.
     */
    private static final int MAXIMUM_OFFSET = 16;

    /**
     *  Finesse du décalage.
     */
    private static final int GRAIN_OFFSET = 4;

    /**
     *  Différence minimum.
     */
    private static final double MINIMUM_DIFFERENCE = -50;

    /**
     *  Différence maximum.
     */
    private static final double MAXIMUM_DIFFERENCE = 150;

    /**
     *  Grain de la différence.
     */
    private static final int GRAIN_DIFFERENCE = 24;

    /**
     *  Nombre de cellules d'avance.
     */
    private int forward;

    /**
     *  Nombre de cellules de recul.
     */
    private int backward;

    /**
     *  Décalage.
     */
    private int offset;

    /**
     *  Différence ciblée.
     */
    private double difference;

    /**
     *  Score réalisé sur la demande.
     *  Exprimé en pourcentage de différence avec l'estimation initiale.
     */
    private double askScore;

    /**
     *  Score réalisé sur l'offre.
     */
    private double bidScore;

    /**
     *  Constructeur.
     */
    public ForwardStrategy() {
        super("Positionnement sur courbe d'avance");
    }

    /**
     *  Constructeur complet.
     *  @param forward Cellules d'avance.
     *  @param backward Cellules de recul.
     *  @param offset Déclage.
     *  @param difference Différence tolérée.
     */
    public ForwardStrategy(int forward, int backward, int offset, double difference) {
        this();
        this.forward = forward;
        this.backward = backward;
        this.offset = offset;
        this.difference = difference;
    }

    /**
     *  Constructeur par copie.
     *  @param source Objet source.
     */
    public ForwardStrategy(ForwardStrategy source) {
        this();
        this.forward = source.getForward();
        this.backward = source.getBackward();
        this.offset = source.getOffset();
        this.difference = source.getDifference();
    }

    /**
     *  Retourne le nombre de cellules d'avance.
     *  @return Nombre de cellules d'avance.
     */
    public int getForward() {
        return this.forward;
    }

    /**
     *  Retourne le nombre de cellules de retard.
     *  @return Nombre de cellules de retard.
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
     *  Retourne la différence recherchée.
     *  @return Différence.
     */
    public double getDifference() {
        return this.difference;
    }

    /**
     *  Sérialise les informations internes à la stratégie.
     *  @return Entité sérialisée.
     */
    @Override
    protected ForwardStrategyEntity serializeInner() {
        ForwardStrategyEntity entity = new ForwardStrategyEntity();
        entity.setForward(this.forward);
        entity.setBackward(this.backward);
        entity.setOffset(this.offset);
        entity.setDifference(this.difference);
        return entity;
    }

    /**
     *  Retourne la taille évaluée.
     *  @return Taille évaluée.
     */
    @Override
    public int size() {
        return this.offset + this.backward;
    }

    /**
     *  Emet les paramètres internes.
     *  @return Liste des paramètres internes.
     *  @throws InnerException En cas d'erreur lors de l'émission.
     */
    @Override
    protected List<AbstractParameter> emitInnerParameters() throws InnerException {
        List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(new IntegerParameter("forward", ForwardStrategy.MINIMUM_FORWARD, ForwardStrategy.MAXIMUM_FORWARD, ForwardStrategy.GRAIN_FORWARD));
        parameters.add(new IntegerParameter("backward", ForwardStrategy.MINIMUM_BACKWARD, ForwardStrategy.MAXIMUM_BACKWARD, ForwardStrategy.GRAIN_BACKWARD));
        parameters.add(new IntegerParameter("offset", ForwardStrategy.MINIMUM_OFFSET, ForwardStrategy.MAXIMUM_OFFSET, ForwardStrategy.GRAIN_OFFSET));
        parameters.add(new DoubleParameter("difference", ForwardStrategy.MINIMUM_DIFFERENCE, ForwardStrategy.MAXIMUM_DIFFERENCE, ForwardStrategy.GRAIN_DIFFERENCE));
        return parameters;
    }

    /**
     *  Intègre un paramètre interne.
     *  @param key Clef du paramètre.
     *  @param value Valeur.
     *  @return Consommation.
     *  @throws InnerException En cas d'erreur lors de l'intégration.
     */
    @Override
    protected boolean consumeInnerParameter(String key, Object value) throws InnerException {
        if ("forward".equals(key)) {
            this.forward = (Integer) value;
            return true;
        } else if ("backward".equals(key)) {
            this.backward = (Integer) value;
            return true;
        } else if ("offset".equals(key)) {
            this.offset = (Integer) value;
            return true;
        } else if ("difference".equals(key)) {
            this.difference = (Double) value;
            return true;
        }
        return false;
    }

    /**
     *  Intégration d'une liste de cellules.
     *  @param curve Courbe traitée.
     *  @param cells Liste des cellules exploitées.
     */
    @Override
    public void consolidate(AbstractCurve curve, List<AbstractCell> cells) {
        /* Extraction de la taille des cellules */
        long size = curve.getEntity().getSize() * 1000;

        /* Récupération de la cellule de départ de la courbe */
        AbstractCell start = cells.get(0);
        AbstractCell middle = cells.get(cells.size() - this.offset - 1);

        /* Calcul de l'instant évalué */
        long target = middle.getMiddle() + ((this.offset + this.forward) * size);

        /* Recherche d'une cellule correspondant à l'instant évalué */
        AbstractCell buffer = cells.get(cells.size() - 1);
        AbstractCell end = null;
        while (target < buffer.getStart() + size) {
            end = buffer;
            buffer = buffer.getPrevious();
        }

        /* Affectation */
        this.askScore = this.consolidateOne(true, start, middle, target, end);
        this.bidScore = this.consolidateOne(false, start, middle, target, end);
    }

    /**
     *  Indique si la stratégie doit engager une position longue.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustEnterLong() {
        return this.bidScore > this.difference;
    }

    /**
     *  Indique si la stratégie doit engager une position courte.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustEnterShort() {
        return this.askScore > this.difference;
    }

    /**
     *  Retourne une copie de l'objet.
     *  @return Copie de l'objet.
     */
    @Override
    public ForwardStrategy clone() {
        return new ForwardStrategy(this);
    }

    /**
     *  Conversion en chaine de caractères.
     *  @return Chaine descriptive.
     */
    @Override
    public String toString() {
        return String.format(
                "FORWARD[%d/%d/%d/%f]",
                this.forward,
                this.backward,
                this.offset,
                this.difference);
    }

    /**
     *  Consolidation du résultat pour un type de courbe.
     *  @param type Type évalué (true : offre, false : demande).
     *  @param start Cellule de départ.
     *  @param middle Cellule intermédiaire.
     *  @param target Point évalué.
     *  @param end Cellule de fin si disponible.
     */
    private double consolidateOne(boolean type, AbstractCell start, AbstractCell middle, long target, AbstractCell end) {
        /* Récupération des noeuds */
        Cluster startCluster, middleCluster, endCluster;
        if (type) {
            startCluster = start.getAsk();
            middleCluster = middle.getAsk();
            endCluster = end == null ? null : end.getAsk();
        } else {
            startCluster = start.getBid();
            middleCluster = middle.getBid();
            endCluster = end == null ? null : end.getBid();
        }

        /* Récupération de la courbe */
        ForwardCurve fc = VariationParameter.getCurveByVariation(startCluster, super.getMode());

        /* Récupération de la valeur */
        double projection = fc.getValue(target);

        /* Traitement.
         * Si la cellule de fin est précisée, le calcul s'effectue sur la différence entre le taux constaté sur cette
         * dernière et les prévisions initiales.
         * Sinon, le calcul s'effectue sur la base de la croissance moyenne entre le point de départ et le point
         * intermédiaire, en comparaison des prévisions initiales. */
        double startRate = VariationParameter.getRateByVariation(startCluster, super.getMode());
        double endRate = 0;
        if (end == null) {
            /* Récupération du taux intermédiaire */
            double middleRate = VariationParameter.getRateByVariation(middleCluster, super.getMode());

            /* Calcul de l'évolution */
            double evolution = (middleRate - startRate) / this.backward;

            /* Calcul du taux final espéré */
            endRate = middleRate + (evolution * this.forward);
        } else {
            /* Calcul direct du taux en fin */
            endRate = VariationParameter.getRateByVariation(endCluster, super.getMode());
        }

        /* Calcul du score */
        return (endRate - projection) / Math.abs(projection - startRate) * 100;
    }
}
