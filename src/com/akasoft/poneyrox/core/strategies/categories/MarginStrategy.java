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
     *  Marge minimum exigée.
     */
    public static final double MINIMUM = 0.05;

    /**
     *  Marge maximum évaluée.
     */
    public static final double MAXIMUM = 20;

    /**
     *  Niveau de finesse.
     */
    public static final int GRAIN = 92;

    /**
     *  Pourcentage de marge ciblée.
     */
    private double margin;

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
        this.margin = MarginStrategy.MINIMUM;
    }

    /**
     *  Constructeur complet.
     *  @param margin Marge affectée.
     */
    public MarginStrategy(double margin) {
        this();
        this.margin = margin;
    }

    /**
     *  Constructeur par copie.
     *  @param source Objet copié.
     */
    private MarginStrategy(MarginStrategy source) {
        this(source.getMargin());
        this.ask = source.getAsk();
        this.bid = source.getBid();
        this.space = this.ask - this.bid;
    }

    /**
     *  Retourne la marge ciblée.
     *  @return Marge ciblée.
     */
    public double getMargin() {
        return this.margin;
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
        result.setMargin(this.margin);
        return result;
    }

    /**
     *  Emission des paramètres.
     *  @return Liste des paramètres.
     */
    @Override
    public List<AbstractParameter> emitInnerParameters() throws InnerException {
        List<AbstractParameter> parameters = new ArrayList<>();
        parameters.add(new DoubleParameter("margin", MarginStrategy.MINIMUM, MarginStrategy.MAXIMUM, MarginStrategy.GRAIN));
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
        if ("margin".equals(key)) {
            this.margin = (Double) value;
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
        double margin = mode ? this.bid - entry : entry - this.ask;
        return (this.space / margin) * 100 > this.margin;
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
                "MARGIN[%s/%f]",
                super.getMode(),
                this.margin);
    }
}
