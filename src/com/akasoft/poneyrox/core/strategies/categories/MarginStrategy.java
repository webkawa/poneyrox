package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.ExitLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitShortITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ObserverITF;
import com.akasoft.poneyrox.core.strategies.parameters.AbstractParameter;
import com.akasoft.poneyrox.core.strategies.parameters.DoubleParameter;
import com.akasoft.poneyrox.core.strategies.parameters.VariationParameter;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.strategies.MarginStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;

/**
 *  Stratégie de sortie par bénéfice réalisé.
 */
public class MarginStrategy extends AbstractStrategy<MarginStrategyEntity> implements ExitLongITF<MarginStrategyEntity>, ExitShortITF<MarginStrategyEntity>, ObserverITF<MarginStrategyEntity> {
    /**
     *  Pourcentage de profit minimum toléré.
     */
    private static final double MINIMUM_PROFIT = 1;

    /**
     *  Pourcentage de profit maximum toléré.
     */
    private static final double MAXIMUM_PROFIT = 1200;

    /**
     *  Niveau de finesse du profit toléré.
     */
    private static final int GRAIN_PROFIT = 24;

    /**
     *  Pourcentage de perte minimum tolérée.
     */
    private static final double MINIMUM_LOSS = 100;

    /**
     *  Pourcentage de perte maximum tolérée.
     */
    private static final double MAXIMUM_LOSS = 1200;

    /**
     *  Niveau de finesse de la perte tolérée.
     */
    private static final int GRAIN_LOSS = 24;

    /**
     *  Pourcentage de profit maximum toléré.
     */
    private double profit;

    /**
     *  Pourcentage de perte maximum toléré.
     */
    private double loss;

    /**
     *  Dernier niveau de demande.
     */
    private double ask;

    /**
     *  Dernier niveau d'offre.
     */
    private double bid;

    /**
     *  Différence entre l'offre et la demande au moment de l'évaluation.
     */
    private double space;

    /**
     *  Constructeur.
     */
    public MarginStrategy() {
        super("Sortie par marge");
        this.profit = MarginStrategy.MINIMUM_PROFIT;
        this.loss = MarginStrategy.MINIMUM_LOSS;
    }

    /**
     *  Constructeur complet.
     *  @param profit Profit toléré.
     *  @param loss Perte tolérée.
     */
    public MarginStrategy(double profit, double loss) {
        this();
        this.profit = profit;
        this.loss = loss;
    }

    /**
     *  Constructeur par copie.
     *  @param source Objet copié.
     */
    private MarginStrategy(MarginStrategy source) {
        this(source.getProfit(), source.getLoss());
        this.ask = source.getAsk();
        this.bid = source.getBid();
        this.space = this.ask - this.bid;
    }

    /**
     *  Retourne le pourcentage de profit maximum ciblé.
     *  @return Pourcentage de profit maximum.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     *  Retourne le pourcentage de perte maximum ciblé.
     *  @return Pourcentage de perte maximum.
     */
    public double getLoss() {
        return this.loss;
    }

    /**
     *  Retourne le taux de la demande.
     *  @return Taux de la demande.
     */
    public double getAsk() {
        return this.ask;
    }

    /**
     *  Retourne le taux de l'offre.
     *  @return Taux de l'offre.
     */
    public double getBid() {
        return this.bid;
    }

    /**
     *  Sérialisation.
     *  @return Entité correspondante.
     */
    @Override
    public MarginStrategyEntity serializeInner() {
        MarginStrategyEntity result = new MarginStrategyEntity();
        result.setProfit(this.profit);
        result.setLoss(this.loss);
        return result;
    }

    /**
     *  Emission des paramètres.
     *  @return Liste des paramètres.
     */
    @Override
    public List<AbstractParameter> emitInnerParameters() throws InnerException {
        List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(new DoubleParameter("profit", MarginStrategy.MINIMUM_PROFIT, MarginStrategy.MAXIMUM_PROFIT, MarginStrategy.GRAIN_PROFIT));
        parameters.add(new DoubleParameter("loss", MarginStrategy.MINIMUM_LOSS, MarginStrategy.MAXIMUM_LOSS, MarginStrategy.GRAIN_LOSS));
        return parameters;
    }

    /**
     *  Consommation d'un paramètre.
     *  @param key Clef du paramètre.
     *  @param value Valeur.
     *  @return Consommation.
     *  @throws InnerException En cas d'erreur interne.
     */
    @Override
    public boolean consumeInnerParameter(String key, Object value) throws InnerException {
        if ("profit".equals(key)) {
            this.profit = (Double) value;
            return true;
        } else if ("loss".equals(key)) {
            this.loss = (Double) value;
            return true;
        }
        return false;
    }

    /**
     *  Traite une courbe.
     *  @param curve Courbe traitée.
     *  @param cells Liste des cellules exploitées.
     */
    @Override
    public void consolidate(AbstractCurve curve, List<AbstractCell> cells) {
        this.ask = VariationParameter.getRateByVariation(cells.get(cells.size() - 1).getAsk(), this.getMode());
        this.bid = VariationParameter.getRateByVariation(cells.get(cells.size() - 1).getBid(), this.getMode());
        this.space = this.ask - this.bid;
    }

    /**
     *  Retourne la taille ciblée.
     *  @return Taille ciblée.
     */
    @Override
    public int size() {
        return 1;
    }

    /**
     *  Indique si la stratégie valide une sortie de position longue.
     *  @param entry Cout à l'entrée.
     *  @return Validation.
     */
    @Override
    public boolean mustExitLong(double entry) {
        return this.mustExit(entry, true);
    }


    /**
     *  Indique si la stratégie valide une sortie de position courte.
     *  @param entry Cout à l'entrée.
     *  @return Validation.
     */
    @Override
    public boolean mustExitShort(double entry) {
        return this.mustExit(entry, false);
    }

    /**
     *  Indique si la stratégie valide une sortie de position.
     *  @param entry Cout à l'entrée.
     *  @param mode Mode (true pour long, false pour court).
     *  @return Validation.
     */
    private boolean mustExit(double entry, boolean mode) {
        /* Détermine si le pourcentage de profit dégagé dépasse l'objectif */
        double winMargin = mode ? this.bid - entry : entry - this.ask;
        boolean winResult = (winMargin / this.space) * 100 > this.profit;

        /* Détermine si le pourcentage de perte dégagé dépasse l'objectif */
        double looseMargin = mode ? entry - this.bid : this.ask - entry;
        boolean looseResult = (looseMargin / this.space) * 100 > this.loss;

        /* Renvoi */
        return winResult || looseResult;
    }

    /**
     *  Retourne une copie de l'objet.
     *  @return Copie de l'objet.
     */
    @Override
    public MarginStrategy clone() {
        return new MarginStrategy(this);
    }

    /**
     *  Conversion en chaine de caractères.
     *  @return Chaine descriptive.
     */
    @Override
    public String toString() {
        return String.format(
                "MARGIN[%s/%f/%f]",
                super.getMode(),
                this.profit,
                this.loss);
    }
}
