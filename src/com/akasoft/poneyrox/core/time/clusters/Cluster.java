package com.akasoft.poneyrox.core.time.clusters;

import com.akasoft.poneyrox.core.strategies.parameters.VariationParameter;
import com.akasoft.poneyrox.core.strategies.parameters.VariationType;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.views.ClusterViews;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import net.finmath.marketdata.model.curves.ForwardCurve;
import net.finmath.marketdata.products.Forward;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *  Noeud.
 *  Classe représentative d'une consolidation de taux - offre ou demande - sur une période donnée.
 */
public class Cluster {
    /**
     *  Nombre maximum de cellules évaluées lors du calcul de la courbe d'avancement.
     */
    private static final int FORWARD_SIZE = 64;

    /**
     *  Cellule propriétaire.
     */
    @JsonIgnore
    private AbstractCell owner;

    /**
     *  Taux minimum.
     */
    @JsonView(
            ClusterViews.Public.class
    )
    private double minimum;

    /**
     *  Taux moyen.
     */
    @JsonView(
            ClusterViews.Public.class
    )
    private double average;

    /**
     *  Maximum.
     */
    @JsonView(
            ClusterViews.Public.class
    )
    private double maximum;

    /**
     *  Direction.
     */
    @JsonView(
            ClusterViews.Public.class
    )
    private boolean direction;

    /**
     *  Dernière entrée.
     */
    @JsonIgnore
    private double last;

    /**
     *  Indique si le noeud fait partie des sommets pour le taux minimum.
     */
    @JsonIgnore
    private boolean topMinimum;

    /**
     *  Indique si le noeud fait partie des sommets pour le taux moyen.
     */
    @JsonIgnore
    private boolean topAverage;

    /**
     *  Indique si le noeud fait partie des sommets pour le taux maximum.
     */
    @JsonIgnore
    private boolean topMaximum;

    /**
     *  Indique si le noeud fait partie des replis pour le taux minimum.
     */
    @JsonIgnore
    private boolean bottomMinimum;

    /**
     *  Indique si le noeud fait partie des replis pour le taux moyen.
     */
    @JsonIgnore
    private boolean bottomAverage;

    /**
     *  Indique si le noeud fait partie des replis pour le taux maximum.
     */
    @JsonIgnore
    private boolean bottomMaximum;

    /**
     *  Noeud précédent si applicable.
     */
    @JsonIgnore
    private Cluster previous;

    /**
     *  Courbe d'avancement basée sur le minimum.
     */
    @JsonIgnore
    private ForwardCurve curveMinimum;

    /**
     *  Courbe d'avancement basée sur la moyenne.
     */
    @JsonIgnore
    private ForwardCurve curveAverage;

    /**
     *  Courbe d'avancement basée sur le maximum.
     */
    @JsonIgnore
    private ForwardCurve curveMaximum;

    /**
     *  Constructeur.
     *  @param previous Noeud précédent si applicable.
     *  @param initial Taux initial.
     *  @param direction Direction initiale.
     */
    public Cluster(Cluster previous, double initial, boolean direction) {
        this.previous = previous;
        this.minimum = initial;
        this.average = initial;
        this.maximum = initial;
        this.last = initial;
        this.direction = direction;
    }

    /**
     *  Constructeur complet.
     *  @param previous Noeud précédent si applicable.
     *  @param minimum Taux minimum.
     *  @param average Taux moyen.
     *  @param maximum Taux maximum.
     *  @param direction Direction initiale.
     */
    public Cluster(Cluster previous, double minimum, double average, double maximum, boolean direction) {
        this.previous = previous;
        this.minimum = minimum;
        this.average = average;
        this.maximum = maximum;
        this.last = average;
        this.direction = direction;
    }

    /**
     *  Retourne la cellule propriétaire.
     *  @return Cellule propriétaire.
     */
    public AbstractCell getOwner() {
        return this.owner;
    }

    /**
     *  Retourne le taux minimum.
     *  @return Taux minimum.
     */
    public double getMinimum() {
        return this.minimum;
    }

    /**
     *  Retourne le taux moyen.
     *  @return Taux moyen.
     */
    public double getAverage() {
        return this.average;
    }

    /**
     *  Retourne le taux maximum.
     *  @return Taux maximum.
     */
    public double getMaximum() {
        return this.maximum;
    }

    /**
     *  Retourne la dernière valeur intégrée.
     *  @return Valeur intégrée.
     */
    public double getLast() {
        return this.last;
    }

    /**
     *  Retourne la direction.
     *  @return Direction.
     */
    public boolean getDirection() {
        return this.direction;
    }

    /**
     *  Indique si le noeud fait partie des sommets pour le taux minimum.
     *  @return true si vrai.
     */
    public boolean isTopMinimum() {
        return this.topMinimum;
    }

    /**
     *  Indique si le noeud fait partie des sommets pour le taux moyen.
     *  @return true si vrai.
     */
    public boolean isTopAverage() {
        return this.topAverage;
    }

    /**
     *  Indique si le noeud fait partie des sommets pour le taux maximum.
     *  @return true si vrai.
     */
    public boolean isTopMaximum() {
        return this.topMaximum;
    }

    /**
     *  Indique si le noeud fait partie des replis pour le taux minimum.
     *  @return true si vrai.
     */
    public boolean isBottomMinimum() {
        return this.bottomMinimum;
    }

    /**
     *  Indique si le noeud fait partie des replis pour le taux moyen.
     *  @return true si vrai.
     */
    public boolean isBottomAverage() {
        return this.bottomAverage;
    }

    /**
     *  Indique si le noeud fait partie des replis pour le taux maximum.
     *  @return true si vrai.
     */
    public boolean isBottomMaximum() {
        return this.bottomMaximum;
    }

    /**
     *  Retourne la courbe d'avancement minimum.
     *  @return Courbe d'avancement minimum.
     */
    public ForwardCurve getCurveMinimum() {
        return this.curveMinimum;
    }

    /**
     *  Retourne la courbe d'avancement moyenne.
     *  @return Courbe d'avancement moyenne.
     */
    public ForwardCurve getCurveAverage() {
        return this.curveAverage;
    }

    /**
     *  Retourne la courbe d'avancement maximum.
     *  @return Courbe d'avancement maximum.
     */
    public ForwardCurve getCurveMaximum() {
        return this.curveMaximum;
    }

    /**
     *  Retourne le noeud précédent.
     *  @return Noeud précédent.
     */
    public Cluster getPrevious() {
        return this.previous;
    }

    /**
     *  Définit la cellule propriétaire.
     *  @param owner Cellule propriétaire.
     */
    public void setOwner(AbstractCell owner) {
        this.owner = owner;
    }

    /**
     *  Définit la valeur minimum.
     *  @param minimum Valeur minimum.
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
    }

    /**
     *  Définit la moyenne.
     *  @param average Moyenne.
     */
    public void setAverage(double average) {
        this.average = average;
    }

    /**
     *  Définit la valeur maximum.
     *  @param maximum Valeur maximum.
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
    }

    /**
     *  Définit la dernière valeur.
     *  @param last Dernière valeur.
     */
    public void setLast(double last) {
        this.last = last;
    }

    /**
     *  Définit la direction.
     *  @param direction Direction.
     */
    public void setDirection(boolean direction) {
        this.direction = direction;
    }

    /**
     *  Définit le noeud fait partie des sommets pour le taux minimum.
     *  @param topMinimum Valeur affectée.
     */
    public void setTopMinimum(boolean topMinimum) {
        this.topMinimum = topMinimum;
    }

    /**
     *  Définit le noeud fait partie des sommets pour le taux moyen.
     *  @param topAverage Valeur affectée.
     */
    public void setTopAverage(boolean topAverage) {
        this.topAverage = topAverage;
    }

    /**
     *  Définit le noeud fait partie des sommets pour le taux maximum.
     *  @param topMaximum Valeur affectée.
     */
    public void setTopMaximum(boolean topMaximum) {
        this.topMaximum = topMaximum;
    }

    /**
     *  Définit le noeud fait partie des replis pour le taux minimum.
     *  @param bottomMinimum Valeur affectée.
     */
    public void setBottomMinimum(boolean bottomMinimum) {
        this.bottomMinimum = bottomMinimum;
    }

    /**
     *  Définit le noeud fait partie des replis pour le taux moyen.
     *  @param bottomAverage Valeur affectée.
     */
    public void setBottomAverage(boolean bottomAverage) {
        this.bottomAverage = bottomAverage;
    }

    /**
     *  Définit le noeud fait partie des replis pour le taux maximum.
     *  @param bottomMaximum Valeur affectée.
     */
    public void setBottomMaximum(boolean bottomMaximum) {
        this.bottomMaximum = bottomMaximum;
    }

    /**
     *  Définit la courbe d'avancement rattachée au taux minimum.
     *  @param curve Courbe affectée.
     */
    public void setCurveMinimum(ForwardCurve curve) {
        this.curveMinimum = curve;
    }

    /**
     *  Définit la courbe d'avancement rattachée au taux moyen.
     *  @param curve Courbe affectée.
     */
    public void setCurveAverage(ForwardCurve curve) {
        this.curveAverage = curve;
    }

    /**
     *  Définit la courbe d'avancement rattachée au taux maximum.
     *  @param curve Courbe affectée.
     */
    public void setCurveMaximum(ForwardCurve curve) {
        this.curveMaximum = curve;
    }

    /**
     *  Définit le noeud précédent.
     *  @param previous Noeud précédent.
     */
    public void setPrevious(Cluster previous) {
        this.previous = previous;
    }

    /**
     *  Finalise le noeud en comparaison du noeud précédent.
     *  @param previous Noeud précédent.
     */
    public void finalize(Cluster previous) {
        if (previous == null) {
            this.bottomMinimum = true;
            this.bottomAverage = true;
            this.bottomMaximum = true;
            this.topMinimum = true;
            this.topAverage = true;
            this.topMaximum = true;
        } else {
            for (VariationType type : VariationType.values()) {
                this.finalizeOpposites(true, type);
                this.finalizeOpposites(false, type);
                this.finalizeForward(type);
            }
        }
    }

    /**
     *  Finalise une liste d'oppositions.
     *  @param type Type d'opposition recherchée (true : sommet, false : repli).
     *  @param variation Variation évaluée.
     */
    private void finalizeOpposites(boolean type, VariationType variation) {
        /* Récupération des taux */
        double previousRate = VariationParameter.getRateByVariation(this.getPrevious(), variation);
        double currentRate = VariationParameter.getRateByVariation(this, variation);

        /* Indicateur.
           Valeur indiquant si le noeud courant constitue une nouvelle opposition (sommet ou repli). */
        boolean record = type ? previousRate < currentRate : previousRate > currentRate;

        /* Comparaison */
        if (previousRate == currentRate) {
            /* Egalité */
            boolean previousValue = VariationParameter.getOppositeByVariation(previous, variation, type);
            VariationParameter.setOppositeByVariation(this, variation, type, previousValue);
        } else if (record) {
            /* Nouveau record */
            VariationParameter.setOppositeByVariation(this, variation, type, true);

            /* Nettoyage des cellules précédentes */
            Cluster buffer = this.getPrevious();
            while (buffer != null && VariationParameter.getOppositeByVariation(buffer, variation, type)) {
                VariationParameter.setOppositeByVariation(buffer, variation, type, false);
                buffer = buffer.getPrevious();
            }
        } else {
            /* Inversion de la courbe */
            VariationParameter.setOppositeByVariation(this, variation, type, false);
        }
    }

    /**
     *  Calcule une courbe d'avancement.
     *  @param variation Type de variation évaluée.
     */
    private void finalizeForward(VariationType variation) {
        /* Création de l'historique */
        List<Cluster> history = new ArrayList<>();
        history.add(this);

        /* Récupération */
        Cluster buffer = this.getPrevious();
        for (int i = 0; i < Cluster.FORWARD_SIZE && buffer != null; i++) {
            history.add(buffer);
        }
        Collections.reverse(history);

        /* Récupération des taux */
        double[] rates = new double[history.size()];
        double[] times = new double[history.size()];
        for (int i = 0; i < history.size(); i++) {
            rates[i] = VariationParameter.getRateByVariation(history.get(i), variation);
            times[i] = history.get(i).getOwner().getMiddle();
        }

        /* Création de la courbe */
        ForwardCurve fc = ForwardCurve.createForwardCurveFromForwards("FC" + this.toString(), times, rates, 0);
        VariationParameter.setForwardByVariation(this, variation, fc);
    }
}
