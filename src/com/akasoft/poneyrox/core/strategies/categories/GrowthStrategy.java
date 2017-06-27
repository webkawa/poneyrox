package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.*;
import com.akasoft.poneyrox.core.strategies.parameters.*;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.strategies.GrowthStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Stratégie batie sur la croissance des derniers paliers.
 */
public class GrowthStrategy extends AbstractStrategy<GrowthStrategyEntity> implements ObserverITF<GrowthStrategyEntity>, EnterLongITF<GrowthStrategyEntity>, EnterShortITF<GrowthStrategyEntity>, ExitLongITF<GrowthStrategyEntity>, ExitShortITF<GrowthStrategyEntity> {
    /**
     *  Taille minimum.
     */
    public static final int MINIMUM_SIZE = 2;

    /**
     *  Taille maximum.
     */
    public static final int MAXIMUM_SIZE = 16;

    /**
     *  Grain de la taille.
     */
    public static final int GRAIN_SIZE = GrowthStrategy.MAXIMUM_SIZE - GrowthStrategy.MINIMUM_SIZE;

    /**
     *  Croissance minimum.
     */
    public static final double MINIMUM_LEVEL = 1;

    /**
     *  Croissance maximum.
     */
    public static final double MAXIMUM_LEVEL = 100 / GrowthStrategy.MAXIMUM_SIZE;

    /**
     *  Grain de la croissance.
     */
    public static final int GRAIN_LEVEL = 24;

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
    private double level;

    /**
     *  Taux minimum constaté sur la période.
     */
    private double minArtifact;

    /**
     *  Taux maximum constaté sur la période.
     */
    private double maxArtifact;

    /**
     *  Vérifications en croissance.
     */
    private double[] upArtifact;

    /**
     *  Vérifications en décroissance.
     */
    private double[] downArtifact;

    /**
     *  Consolidation en croissance.
     */
    private boolean upState;

    /**
     *  Consolidation en décroissance.
     */
    private boolean downState;

    /**
     *  Constructeur.
     */
    public GrowthStrategy() {
        super("Positionnement sur croissance");
    }

    /**
     *  Constructeur complet.
     *  @param type Type.
     *  @param size Taille.
     *  @param level Niveau.
     */
    public GrowthStrategy(boolean type, int size, double level) {
        this();
        this.type = type;
        this.size = size;
        this.level = level;
        this.minArtifact = 0;
        this.maxArtifact = 0;
        this.upArtifact = new double[0];
        this.downArtifact = new double[0];
        this.upState = false;
        this.downState = false;
    }

    /**
     *  Constructeur par copie.
     *  @param source Objet source.
     */
    private GrowthStrategy(GrowthStrategy source) {
        this(source.getType(), source.getSize(), source.getLevel());
        this.minArtifact = source.getMinArtifact();
        this.maxArtifact = source.getMaxArtifact();
        this.upArtifact = source.getUpArtifact();
        this.downArtifact = source.getDownArtifact();
        this.upState = source.isUpState();
        this.downState = source.isDownState();
    }

    /**
     *  Retourne le type.
     *  @return Type.
     */
    public boolean getType() {
        return this.type;
    }

    /**
     *  Retourne la taille.
     *  @return Taille.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Retourne le niveau.
     *  @return Niveau.
     */
    public double getLevel() {
        return this.level;
    }

    /**
     *  Retourne l'artefact du minimum constaté.
     *  @return Artefact du minimum.
     */
    public double getMinArtifact() {
        return this.minArtifact;
    }

    /**
     *  Retourne l'artefact du maximum constaté.
     *  @return Artefact du maximum.
     */
    public double getMaxArtifact() {
        return this.maxArtifact;
    }

    /**
     *  Retourne l'artefact des augmentations de taux.
     *  @return Artefact des augmentations.
     */
    public double[] getUpArtifact() {
        return this.upArtifact;
    }

    /**
     *  Retourne l'artefact des baisses de taux.
     *  @return Artefact des baisses.
     */
    public double[] getDownArtifact() {
        return this.downArtifact;
    }

    /**
     *  Retourne l'artefact de croissance.
     *  @return Artefact de croissance.
     */
    public boolean isUpState() {
        return this.upState;
    }

    /**
     *  Retourne l'artefact de décroissance.
     *  @return Artefact de décroissance.
     */
    public boolean isDownState() {
        return this.downState;
    }

    /**
     *  Sérialise les informations internes à la stratégie.
     *  @return Entité sérialisée.
     */
    @Override
    protected GrowthStrategyEntity serializeInner() {
        GrowthStrategyEntity entity = new GrowthStrategyEntity();
        entity.setType(this.type);
        entity.setLevel(this.level);
        entity.setSize(this.size);
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
     *  @throws InnerException En cas d'erreur lors de l'émission.
     */
    @Override
    protected List<AbstractParameter> emitInnerParameters() throws InnerException {
        List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(new BinaryParameter("type"));
        parameters.add(new IntegerParameter("size", GrowthStrategy.MINIMUM_SIZE, GrowthStrategy.MAXIMUM_SIZE, GrowthStrategy.GRAIN_SIZE));
        parameters.add(new DoubleParameter("level", GrowthStrategy.MINIMUM_LEVEL, GrowthStrategy.MAXIMUM_LEVEL, GrowthStrategy.GRAIN_LEVEL));
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
        if ("type".equals(key)) {
            this.type = (Boolean) value;
            return true;
        } else if ("size".equals(key)) {
            this.size = Math.toIntExact((Long) value);
            return true;
        } else if ("level".equals(key)) {
            this.level = (Double) value;
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
        /* Initialisation des artefacts */
        this.upArtifact = new double[this.size - 1];
        this.downArtifact = new double[this.size - 1];
        this.minArtifact = Double.MAX_VALUE;
        this.maxArtifact = 0;
        this.upState = false;
        this.downState = false;

        /* Calcul des artefacts */
        for (int i = 0; i < cells.size(); i++) {
            /* Récupération du taux pour la cellule courante */
            Cluster currentCluster = this.type ? cells.get(i).getAsk() : cells.get(i).getBid();
            double currentRate = VariationParameter.getRateByVariation(currentCluster, this.getMode());

            /* Gestion du minimum/maximum constaté */
            this.minArtifact = this.minArtifact > currentRate ? currentRate : this.minArtifact;
            this.maxArtifact = this.maxArtifact < currentRate ? currentRate : this.maxArtifact;

            /* Traitements principal */
            if (i > 0) {
                /* Récupération du taux pour la cellule précédente */
                Cluster previousCluster = this.type ? cells.get(i - 1).getAsk() : cells.get(i - 1).getBid();
                double previousRate = VariationParameter.getRateByVariation(previousCluster, this.getMode());

                /* Comparaison */
                this.upArtifact[i - 1] = ((currentRate / previousRate) - 1) * 100;
                this.downArtifact[i - 1] = ((previousRate / currentRate) - 1) * 100;
            }
        }

        /* Calcul du pourcentage de marge maximum constaté sur la période */
        double difference = this.maxArtifact - this.minArtifact;
        double margin =  difference / this.size;
        double target = margin * (this.level / 100);

        /* Consolidation */
        if (margin == 0) {
            this.upState = false;
            this.downState = false;
        } else {
            this.upState = this.computeUp(target);
            this.downState = this.computeDown(target);
        }
    }

    /**
     *  Indique si la stratégie doit engager une position longue.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustEnterLong() {
        return this.upState;
    }

    /**
     *  Indique si la stratégie doit engager une position courte.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustEnterShort() {
        return this.downState;
    }

    /**
     *  Indique si la stratégie doit quitter une position longue.
     *  @param entry Cout à l'acquisition.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustExitLong(double entry) {
        return !this.upState;
    }

    /**
     *  Indique si la stratégie doit quitter une position courte.
     *  @param entry Cout à l'acquisition.
     *  @return Résultat du calcul.
     */
    @Override
    public boolean mustExitShort(double entry) {
        return !this.downState;
    }

    /**
     *  Indique si la stratégie est engagée sur une courbe croissante.
     *  @param target Score ciblé.
     *  @return Résultat du calcul.
     */
    private boolean computeUp(double target) {
        /* Parcours */
        for (double up : this.upArtifact) {
            if (up < target) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Indique si la stratégie est engagée sur une courbe décroissante.
     *  @param target Score ciblé.
     *  @return Résultat du calcul.
     */
    private boolean computeDown(double target) {
        /* Parcours */
        for (double down : this.downArtifact) {
            if (down > 0 - target) {
                return false;
            }
        }
        return true;
    }

    /**
     *  Retourne une copie de l'objet.
     *  @return Copie de l'objet.
     */
    @Override
    public GrowthStrategy clone() {
        return new GrowthStrategy(this);
    }

    /**
     *  Conversion en chaine de caractères.
     *  @return Chaine descriptive.
     */
    @Override
    public String toString() {
        return String.format(
                "GROWTH[%s/%b/%d/%f]",
                super.getMode(),
                this.type,
                this.size,
                this.level);
    }
}
