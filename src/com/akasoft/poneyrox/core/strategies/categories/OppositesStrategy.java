package com.akasoft.poneyrox.core.strategies.categories;

import com.akasoft.poneyrox.core.strategies.interfaces.*;
import com.akasoft.poneyrox.core.strategies.parameters.*;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.clusters.Cluster;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.strategies.OppositesStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 *  Stratégie de comparaison par les opposés.
 */
public class OppositesStrategy extends AbstractStrategy<OppositesStrategyEntity> implements ObserverITF<OppositesStrategyEntity>, EnterLongITF<OppositesStrategyEntity>, EnterShortITF<OppositesStrategyEntity>, ExitLongITF<OppositesStrategyEntity>, ExitShortITF<OppositesStrategyEntity> {

    /**
     *  Taille minimum évaluée.
     */
    private static final int SIZE_MINIMUM = 4;

    /**
     *  Taille maximum évaluée.
     */
    private static final int SIZE_MAXIMUM = 128;

    /**
     *  Finesse de l'évaluation de taille.
     */
    private static final int SIZE_GRAIN = 12;

    /**
     *  Niveau de proximité minimum évalué.
     */
    private static final double PROXIMITY_MINIMUM = 0;

    /**
     *  Niveau de proximité maximum évalué.
     */
    private static final double PROXIMITY_MAXIMUM = 40;

    /**
     *  Finesse de l'évaluation de proximité.
     */
    private static final int PROXIMITY_GRAIN = 12;

    /**
     *  Taille évaluée.
     */
    private int size;

    /**
     *  Indique si le test doit etre réalisé en mode inversé.
     *  Le cas échéant, la courbe d'offre est évaluée pour les entrées en long et les sorties en court, et la courbe
     *  de demande pour les entrées en cours et les sorties en long.
     */
    private boolean reverse;

    /**
     *  Niveau de proximité d'une extrémité approchante.
     */
    private double incomingProximity;

    /**
     *  Niveau de proximité d'une extrémité sortante.
     */
    private double exitingProximity;

    /**
     *  Taux courant.
     */
    private AbstractCell currentRate;

    /**
     *  Taux courant de l'offre.
     */
    private double currentBidRate;

    /**
     *  Taux courant de la demande.
     */
    private double currentAskRate;

    /**
     *  Direction courante de l'offre.
     */
    private boolean currentBidDirection;

    /**
     *  Direction courante de la demande.
     *  Si true, indique que la courbe de demande est en croissance ; si false, en décroissance.
     */
    private boolean currentAskDirection;

    /**
     *  Liste des plages descriptives de l'approche d'un sommet applicable
     *  à l'offre.
     */
    private List<Double[]> bidIncomingTops;

    /**
     *  Liste des plages descriptives de la sortie d'un sommet applicable
     *  à l'offre.
     */
    private List<Double[]> bidExitingTops;

    /**
     *  Liste des plages descritptives de l'approche d'un repli applicable
     *  à l'offre.
     */
    private List<Double[]> bidIncomingBottoms;

    /**
     *  Liste des plages descriptives de la sortie d'un repli applicable
     *  à la demande.
     */
    private List<Double[]> bidExitingBottoms;

    /**
     *  Liste des plages descriptives de l'approche d'un sommet applicable
     *  à la demande.
     */
    private List<Double[]> askIncomingTops;

    /**
     *  Liste des plages descriptives de la sortie d'un sommet applicable
     *  à la demande.
     */
    private List<Double[]> askExitingTops;

    /**
     *  Liste des plages descritptives de l'approche d'un repli applicable
     *  à la demande.
     */
    private List<Double[]> askIncomingBottoms;

    /**
     *  Liste des plages descriptives de la sortie d'un repli applicable
     *  à la demande.
     */
    private List<Double[]> askExitingBottoms;

    /**
     *  Constructeur.
     */
    public OppositesStrategy() {
        super("Positionnement par les opposés");
        this.reverse = true;
        this.incomingProximity = 0;
        this.exitingProximity = 0;
        this.bidIncomingTops = new ArrayList<>();
        this.bidIncomingBottoms = new ArrayList<>();
        this.bidExitingTops = new ArrayList<>();
        this.bidExitingBottoms = new ArrayList<>();
        this.askIncomingTops = new ArrayList<>();
        this.askIncomingBottoms = new ArrayList<>();
        this.askExitingTops = new ArrayList<>();
        this.askExitingBottoms = new ArrayList<>();
    }

    /**
     *  Constructeur complet.
     *  @param size Taille évaluée.
     *  @param reverse Inversion des tests.
     *  @param incomingProximity Proximité d'une extrémité en approche.
     *  @param exitingProximity Proximité d'une extrémité en sortie.
     */
    public OppositesStrategy(int size, boolean reverse, double incomingProximity, double exitingProximity) {
        this();
        this.size = size;
        this.reverse = reverse;
        this.incomingProximity = incomingProximity;
        this.exitingProximity = exitingProximity;
    }

    /**
     *  Constructeur par copie.
     *  @param source Objet copié.
     */
    private OppositesStrategy(OppositesStrategy source) {
        this(source.getSize(), source.isReverse(), source.getIncomingProximity(), source.getExitingProximity());
        this.currentRate = source.getCurrentRate();
        this.currentBidRate = source.getCurrentBidRate();
        this.currentAskRate = source.getCurrentAskRate();
        this.currentAskDirection = source.getCurrentAskDirection();
        this.currentBidDirection = source.getCurrentBidDirection();
        this.bidIncomingTops = new ArrayList<>(source.getBidIncomingTops());
        this.bidIncomingBottoms = new ArrayList<>(source.getBidIncomingBottoms());
        this.bidExitingTops = new ArrayList<>(source.getBidExitingTops());
        this.bidExitingBottoms = new ArrayList<>(source.getBidExitingBottoms());
        this.askIncomingTops = new ArrayList<>(source.getAskIncomingTops());
        this.askIncomingBottoms = new ArrayList<>(source.getAskIncomingBottoms());
        this.askExitingTops = new ArrayList<>(source.getAskExitingTops());
        this.askExitingBottoms = new ArrayList<>(source.getAskExitingBottoms());
    }

    /**
     *  Retourne le nombre de cellules évaluées.
     *  @return Nombre de cellules.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Indique si la stratégie s'exécute en mode inversé.
     *  @return Exécution en mode inversée.
     */
    public boolean isReverse() {
        return this.reverse;
    }

    /**
     *  Retourne la proximité en approche.
     *  @return Proximité en approche.
     */
    public double getIncomingProximity() {
        return this.incomingProximity;
    }

    /**
     *  Retourne la proximité en sortie.
     *  @return Proximité en sortie.
     */
    public double getExitingProximity() {
        return this.exitingProximity;
    }

    /**
     *  Retourne le taux courant.
     *  @return Taux courant.
     */
    public AbstractCell getCurrentRate() {
        return this.currentRate;
    }

    /**
     *  Retourne le taux courant de l'offre.
     *  @return Taux de l'offre.
     */
    public double getCurrentBidRate() {
        return this.currentBidRate;
    }

    /**
     *  Retourne le taux courant de la demande.
     *  @return Taux de la demande.
     */
    public double getCurrentAskRate() {
        return this.currentAskRate;
    }

    /**
     *  Retourne la direction de la courbe d'offre.
     *  @return Direction de la courbe d'offre.
     */
    public boolean getCurrentBidDirection() {
        return this.currentBidDirection;
    }

    /**
     *  Retourne la direction de la courbe de demande.
     *  @return Direction de la courbe.
     */
    public boolean getCurrentAskDirection() {
        return this.currentAskDirection;
    }

    /**
     *  Retourne la liste des sommets en approche pour l'offre.
     *  @return Sommets en approche.
     */
    public List<Double[]> getBidIncomingTops() {
        return this.bidIncomingTops;
    }

    /**
     *  Retourne la liste des sommets en sortie pour l'offre.
     *  @return Sommets en sortie.
     */
    public List<Double[]> getBidExitingTops() {
        return this.bidExitingTops;
    }

    /**
     *  Retourne la liste de replis en approche pour l'offre.
     *  @return Replis en approche.
     */
    public List<Double[]> getBidIncomingBottoms() {
        return this.bidIncomingBottoms;
    }

    /**
     *  Retourne la liste des replis en sortie pour l'offre.
     *  @return Replis en sortie.
     */
    public List<Double[]> getBidExitingBottoms() {
        return this.bidExitingBottoms;
    }

    /**
     *  Retourne la liste des sommets en approche pour la demande.
     *  @return Sommets en approche.
     */
    public List<Double[]> getAskIncomingTops() {
        return this.askIncomingTops;
    }

    /**
     *  Retourne la liste des sommets en sortie pour la demande.
     *  @return Sommets en sortie.
     */
    public List<Double[]> getAskExitingTops() {
        return this.askExitingTops;
    }

    /**
     *  Retourne la liste des replis en approche pour la demande.
     *  @return Replis en approche.
     */
    public List<Double[]> getAskIncomingBottoms() {
        return this.askIncomingBottoms;
    }

    /**
     *  Retourne la liste des replis en sortie pour la demande.
     *  @return Replis en sortie.
     */
    public List<Double[]> getAskExitingBottoms() {
        return this.askExitingBottoms;
    }

    /**
     *  Réalise la sérialisation interne.
     *  @return Entité sérialisée.
     */
    @Override
    protected OppositesStrategyEntity serializeInner() {
        OppositesStrategyEntity entity = new OppositesStrategyEntity();
        entity.setSize(this.size);
        entity.setReverse(this.reverse);
        entity.setIncomingProximity(this.incomingProximity);
        entity.setExitingProximity(this.exitingProximity);
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
        parameters.add(new IntegerParameter("size", OppositesStrategy.SIZE_MINIMUM, OppositesStrategy.SIZE_MAXIMUM, OppositesStrategy.SIZE_GRAIN));
        parameters.add(new BinaryParameter("reverse"));
        parameters.add(new DoubleParameter("incomingProximity", OppositesStrategy.PROXIMITY_MINIMUM, OppositesStrategy.PROXIMITY_MAXIMUM, OppositesStrategy.PROXIMITY_GRAIN));
        parameters.add(new DoubleParameter("exitingProximity", OppositesStrategy.PROXIMITY_MINIMUM, OppositesStrategy.PROXIMITY_MAXIMUM, OppositesStrategy.PROXIMITY_GRAIN));
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
        if ("size".equals(key)) {
            this.size = Math.toIntExact((Long) value);
            return true;
        } else if ("reverse".equals(key)) {
            this.reverse = (Boolean) value;
            return true;
        } else if ("incomingProximity".equals(key)) {
            this.incomingProximity = (Double) value;
            return true;
        } else if ("exitingProximity".equals(key)) {
            this.exitingProximity = (Double) value;
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
        /* Récupération du dernier taux */
        this.currentRate = cells.get(cells.size() - 1);

        /* Nettoyage des données précédentes */
        this.bidIncomingTops.clear();
        this.bidIncomingBottoms.clear();
        this.bidExitingTops.clear();
        this.bidExitingBottoms.clear();
        this.askIncomingTops.clear();
        this.askIncomingBottoms.clear();
        this.askExitingTops.clear();
        this.askExitingBottoms.clear();

        /* Récupération du cours de l'offre */
         this.consolidateOne(
                 cells.stream().map(e -> e.getBid()).collect(Collectors.toList()),
                 this.bidIncomingTops,
                 this.bidIncomingBottoms,
                 this.bidExitingTops,
                 this.bidExitingBottoms);
         this.consolidateOne(
                 cells.stream().map(e -> e.getAsk()).collect(Collectors.toList()),
                 this.askIncomingTops,
                 this.askIncomingBottoms,
                 this.askExitingTops,
                 this.askExitingBottoms);

         /* Récupération des cours */
         this.currentBidRate = VariationParameter.getRateByVariation(this.currentRate.getBid(), super.getMode());
         this.currentAskRate = VariationParameter.getRateByVariation(this.currentRate.getAsk(), super.getMode());

         /* Récupération de la direction du cours.
          * Si le dernier élément de la courbe est un sommet, alors la courbe est considérée en croissance. */
         this.currentBidDirection = VariationParameter.getTopByVariation(this.currentRate.getBid(), super.getMode());
         this.currentAskDirection = VariationParameter.getTopByVariation(this.currentRate.getAsk(), super.getMode());
    }

    /**
     *  Indique si la stratégie doit entrer en position longue.
     *  La stratégie entre en position longue dès lors qu'elle approche (en décroissance) ou quitte (en croissance) une
     *  zone de repli de la demande (standard) ou de l'offre (inversé).
     *  @return Réponse.
     */
    @Override
    public boolean mustEnterLong() {
        if (this.currentRate == null) {
            return false;
        } else {
            if (this.reverse) {
                if (this.currentBidDirection) {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidExitingBottoms);
                } else {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidIncomingBottoms);
                }
            } else {
                if (this.currentAskDirection) {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askExitingBottoms);
                } else {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askIncomingBottoms);
                }
            }
        }
    }

    /**
     *  Indique si la stratégie doit entrer en position courte.
     *  La stratégie entre en position longue dès lors qu'elle approche (en croissance) ou quitte (en décroissance) une
     *  zone de sommet de l'offre (standard) ou de la demande (inversé).
     *  @return Réponse.
     */
    @Override
    public boolean mustEnterShort() {
        if (this.currentRate == null) {
            return false;
        } else {
            if (this.reverse) {
                if (this.currentAskDirection) {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askIncomingTops);
                } else {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askExitingTops);
                }
            } else {
                if (this.currentBidDirection) {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidIncomingTops);
                } else {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidExitingTops);
                }
            }
        }
    }

    /**
     *  Indique si la stratégie doit sortir d'une position longue.
     *  La stratégie sort de position longue dès lors qu'elle approche (en croissance) ou quitte (en décroissance) une
     *  zone de sommet de la demande (standard) ou de l'offre (inversé).
     *  @param entry Cout à l'entrée.
     *  @return Réponse.
     */
    @Override
    public boolean mustExitLong(double entry) {
        if (this.currentRate == null) {
            return false;
        } else {
            if (this.reverse) {
                if (this.currentBidDirection) {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidIncomingTops);
                } else {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidExitingTops);
                }
            } else {
                if (this.currentAskDirection) {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askIncomingTops);
                } else {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askExitingTops);
                }
            }
        }
    }

    /**
     *  Indique si la stratégie doit sortir d'une position courte.
     *  La stratégie sort de position courte dès lors qu'elle approche (en décroissance) ou quitte (en croissance) une
     *  zone de repli de l'offre (standard) ou de la demande (inversé).
     *  @param entry Cout à l'entrée.
     *  @return Réponse.
     */
    @Override
    public boolean mustExitShort(double entry) {
        if (this.currentRate == null) {
            return false;
        } else {
            if (this.reverse) {
                if (this.currentAskDirection) {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askExitingBottoms);
                } else {
                    return OppositesStrategy.inRange(this.currentAskRate, this.askIncomingBottoms);
                }
            } else {
                if (this.currentBidDirection) {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidExitingBottoms);
                } else {
                    return OppositesStrategy.inRange(this.currentBidRate, this.bidIncomingBottoms);
                }
            }
        }
    }

    /**
     *  Retourne une copie de l'objet courant.
     *  @return Copie de l'objet.
     */
    @Override
    public OppositesStrategy clone() {
        return new OppositesStrategy(this);
    }

    /**
     *  Conversion en chaine de caractères.
     *  @return Chaine descriptive.
     */
    @Override
    public String toString() {
        return String.format(
                "OPPOSITES[%s/%d/%b/%f/%f]",
                super.getMode(),
                this.size,
                this.reverse,
                this.incomingProximity,
                this.exitingProximity);
    }

    /**
     *  Consolide une liste de noeuds.
     *  @param source Liste des noeuds traitée.
     *  @param incomingTops Liste des plages d'approche d'un sommet.
     *  @param incomingBottoms Liste des plages d'approche d'un repli.
     *  @param exitingTops Liste des plages de sortie d'un sommet.
     *  @param exitingBottoms Liste des plages de sortie d'un repli.
     */
    private void consolidateOne(
            List<Cluster> source,
            List<Double[]> incomingTops,
            List<Double[]> incomingBottoms,
            List<Double[]> exitingTops,
            List<Double[]> exitingBottoms) {
        /* Création du résultat */
        ArrayList<Double> pre = new ArrayList<>();

        /* Pré-filtrage.
         * Retire les cellules inutiles situées en tete du tableau de façon à débuter avec un sommet ou un
         * repli. */
        int sub = 0;
        boolean pertinent = false;
        while (sub < source.size() && !pertinent) {
            pertinent = VariationParameter.getOppositeByVariation(source.get(sub), super.getMode(), true);
            pertinent |= VariationParameter.getOppositeByVariation(source.get(sub), super.getMode(), false);
            sub++;
        }

        /* Vérification */
        if (pertinent) {
            /* Filtrage */
            List<Cluster> filter = source.subList(sub - 1, source.size());

            /* Tampon.
             * Si true, indique que la dernière valeur trouvée était un sommet. Débute par déterminer si la première
             * extrémité disponible est un sommet.
             */
            double current = VariationParameter.getRateByVariation(filter.get(0), super.getMode());
            boolean first = VariationParameter.getTopByVariation(filter.get(0), super.getMode());
            boolean last = first;

            /* Parcours */
            Double previous = null;
            pre.add(current);
            for (Cluster cluster : filter) {
                current = VariationParameter.getRateByVariation(cluster, super.getMode());
                boolean isDifferent = previous == null ? false : previous != current;
                if (last) {
                    boolean isBottom = VariationParameter.getBottomByVariation(cluster, super.getMode());
                    if (isBottom && isDifferent) {
                        pre.add(current);
                        last = !last;
                    }
                } else {
                    boolean isTop = VariationParameter.getTopByVariation(cluster, super.getMode());
                    if (isTop && isDifferent) {
                        pre.add(current);
                        last = !last;
                    }
                }
                previous = current;
            }

            /* Parcours des éléments consolidés */
            last = first;
            for (int i = 1; i < pre.size(); i++) {
                /* Recherche du minimum et du maximum */
                double lowest = Math.min(pre.get(i), pre.get(i - 1));
                double highest = Math.max(pre.get(i), pre.get(i - 1));

                /* Définition des seuils de différence
                 * Par défaut, la différence applicable à une courbe en descente est calculée. */
                double topDifference, bottomDifference;
                if (last) {
                    topDifference = ((highest - lowest) * (this.exitingProximity / 100));
                    bottomDifference = ((highest - lowest) * (this.incomingProximity / 100));
                } else {
                    topDifference = ((highest - lowest) * (this.incomingProximity / 100));
                    bottomDifference = ((highest - lowest) * (this.exitingProximity / 100));
                }

                /* Calcul des valeurs intermédiaires */
                double low = lowest + bottomDifference;
                double high = highest - topDifference;

                /* Ajout */
                if (last) {
                    /* Si le point de départ est un sommet */
                    exitingTops.add(new Double[] {high, highest});
                    incomingBottoms.add(new Double[] {lowest, low});
                } else {
                    /* Si le point de départ est un repli */
                    exitingBottoms.add(new Double[] {lowest, low});
                    incomingTops.add(new Double[] {high, highest});
                }
                last = !last;
            }

        }
    }

    /**
     *  Indique si une valeur se trouve dans une liste de plages.
     *  @param value Valeur évaluée.
     *  @param ranges Liste des plages.
     *  @return true si la valeur se trouve dans une liste de plages.
     */
    private static boolean inRange(double value, List<Double[]> ranges) {
        for (Double[] range : ranges) {
            if (value > range[0] && value < range[1]) {
                return true;
            }
        }
        return false;
    }
}
