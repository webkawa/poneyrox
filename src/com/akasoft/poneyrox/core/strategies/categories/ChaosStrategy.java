package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.*;
import com.akasoft.poneyrox.core.strategies.parameters.*;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Stratégie de positionnement par le niveau de chaos.
 */
public class ChaosStrategy extends AbstractStrategy<ChaosStrategyEntity> implements ObserverITF<ChaosStrategyEntity>, EnterLongITF<ChaosStrategyEntity>, EnterShortITF<ChaosStrategyEntity>, ExitLongITF<ChaosStrategyEntity>, ExitShortITF<ChaosStrategyEntity> {
    /**
     *  Taille minimum.
     */
    public static final int SIZE_MINIMUM = 8;

    /**
     *  Taille maximum.
     */
    public static final int SIZE_MAXIMUM = 124;

    /**
     *  Grain de la taille.
     */
    public static final int SIZE_GRAIN = 8;

    /**
     *  Niveau minimum.
     */
    public static final double FLOOR_MINIMUM = 40;

    /**
     *  Niveau maximum.
     */
    public static final double FLOOR_MAXIMUM = 100;

    /**
     *  Grain du niveau.
     */
    public static final int FLOOR_GRAIN = 8;

    /**
     *  Type.
     *  Si true, évaluation sur la demande ; false pour offre.
     */
    private boolean type;

    /**
     *  Taille.
     */
    private int size;

    /**
     *  Niveau.
     */
    private double floor;

    /**
     *  Artefact.
     */
    private boolean artifact;

    /**
     *  Constructeur.
     */
    public ChaosStrategy() {
        super("Positionnement par le chaos");
    }

    /**
     *  Constructeur de sérialisation..
     *  @param size Taille.
     *  @param floor Seuil.
     *  @param type Type.
     */
    public ChaosStrategy(int size, double floor, boolean type) {
        this();
        this.type = type;
        this.size = size;
        this.floor = floor;
        this.artifact = false;
    }

    /**
     *  Constructeur de copie.
     *  @param source Objet copié.
     */
    private ChaosStrategy(ChaosStrategy source) {
        this(source.getSize(), source.getFloor(), source.getType());
        this.artifact = source.getArtifact();
    }

    /**
     *  Retourne le type de stratégie.
     *  @return Type.
     */
    public boolean getType() {
        return this.type;
    }

    /**
     *  Retourne le nombre de cellules évaluées.
     *  @return Nombre de cellules évaluées.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Retourne le seuil minimum.
     *  @return Seuil minimum.
     */
    public double getFloor() {
        return this.floor;
    }

    /**
     *  Retourne l'interfact.
     *  @return Artefact.
     */
    public boolean getArtifact() {
        return this.artifact;
    }

    /**
     *  Réalise la sérialisation interne.
     *  @return Entité sérialisée.
     */
    @Override
    protected ChaosStrategyEntity serializeInner() {
        ChaosStrategyEntity entity = new ChaosStrategyEntity();
        entity.setType(this.type);
        entity.setSize(this.size);
        entity.setFloor(this.floor);
        return entity;
    }

    /**
     *  Retourne la taille évaluée.
     *  @return Taille évaluée.
     */
    @Override
    public int size() {
        return this.size;
    }

    /**
     *  Emet les paramètres internes.
     *  @return Liste des paramètres internes.
     *  @throws InnerException En cas d'erreur interne.
     */
    @Override
    protected List<AbstractParameter> emitInnerParameters() throws InnerException {
        List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(new BinaryParameter("type"));
        parameters.add(new IntegerParameter("size", ChaosStrategy.SIZE_MINIMUM, ChaosStrategy.SIZE_MAXIMUM, ChaosStrategy.SIZE_GRAIN));
        parameters.add(new DoubleParameter("floor", ChaosStrategy.FLOOR_MINIMUM, ChaosStrategy.FLOOR_MAXIMUM, ChaosStrategy.FLOOR_GRAIN));
        return parameters;
    }

    /**
     *  Consomme un paramètre interne.
     *  @param key Clef du paramètre.
     *  @param value Valeur.
     *  @return Consommation.
     *  @throws InnerException En cas d'erreur interne.
     */
    @Override
    protected boolean consumeInnerParameter(String key, Object value) throws InnerException {
        if ("type".equals(key)) {
            this.type = (Boolean) value;
            return true;
        } else if ("size".equals(key)) {
            this.size = Math.toIntExact((Long) value);
            return true;
        } else if ("floor".equals(key)) {
            this.floor = (Double) value;
            return true;
        }
        return false;
    }

    /**
     *  Réalise l'analyse d'une courbe.
     *  @param curve Courbe traitée.
     *  @param cells Liste des cellules exploitées.
     */
    @Override
    public void consolidate(AbstractCurve curve, List<AbstractCell> cells) {
        double minimum = Double.MAX_VALUE;
        double maximum = 0;
        double[] spaces = new double[this.size];
        for (int i = 1; i < cells.size(); i++) {
            /* Sélection du noeud */
            Cluster previousCluster, currentCluster;
            if (this.type) {
                previousCluster = cells.get(i - 1).getAsk();
                currentCluster = cells.get(i).getAsk();
            } else {
                previousCluster = cells.get(i - 1).getBid();
                currentCluster = cells.get(i).getBid();
            }

            /* Sélection des taux */
            double previousRate = VariationParameter.getRateByVariation(previousCluster, this.getMode());
            double currentRate = VariationParameter.getRateByVariation(currentCluster, this.getMode());

            /* Prise en compte */
            if (previousRate < minimum) {
                minimum = previousRate;
            }
            if (previousRate > maximum) {
                maximum = previousRate;
            }
            spaces[i - 1] = Math.abs(currentRate - previousRate);
        }
        double diff = maximum - minimum;

        /* Comparaison */
        this.artifact = false;
        for (int i = 0; i < this.size && !this.artifact; i++) {
            double percent = (spaces[i] / diff) * 100;
            this.artifact = percent > this.floor;
        }
    }

    /**
     *  Indique si la stratégie doit entrer en position longue.
     *  @return Réponse.
     */
    @Override
    public boolean mustEnterLong() {
        return !this.artifact;
    }

    /**
     *  Indique si la stratégie doit entrer en position courte.
     *  @return Réponse.
     */
    @Override
    public boolean mustEnterShort() {
        return !this.artifact;
    }

    /**
     *  Indique si la stratégie doit sortir d'une position longue.
     *  @param entry Cout à l'entrée.
     *  @return Réponse.
     */
    @Override
    public boolean mustExitLong(double entry) {
        return this.artifact;
    }

    /**
     *  Indique si la stratégie doit sortir d'une position courte.
     *  @param entry Cout à l'entrée.
     *  @return Réponse.
     */
    @Override
    public boolean mustExitShort(double entry) {
        return this.artifact;
    }

    /**
     *  Retourne une copie de l'objet.
     *  @return Copie de l'objet.
     */
    @Override
    public ChaosStrategy clone() {
        return new ChaosStrategy(this);
    }

    /**
     *  Conversion en chaine de caractères.
     *  @return Chaine descriptive.
     */
    @Override
    public String toString() {
        return String.format(
                "CHAOS[%s/%b/%d/%f]",
                super.getMode(),
                this.type,
                this.size,
                this.floor);
    }
}
