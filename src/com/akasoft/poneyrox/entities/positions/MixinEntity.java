package com.akasoft.poneyrox.entities.positions;

import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.mixins.leads.AbstractLead;
import com.akasoft.poneyrox.core.mixins.leads.EntryLead;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.strategies.ChaosStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.GrowthStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.MarginStrategyEntity;
import com.akasoft.poneyrox.entities.strategies.OppositesStrategyEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

import javax.persistence.*;
import java.util.*;

/**
 *  Combinaison.
 *  Entité représentatif d'un mix réalisé entre plusieurs stratégies.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Mixin.selectByHashCode",
                query = "SELECT mx " +
                        "FROM MixinEntity mx " +
                        "WHERE mx.hash = :hash"
        ),
        @NamedQuery(
                name = "Mixin.selectByEquivalence",
                query = "SELECT mx " +
                        "FROM MixinEntity mx " +
                        "WHERE mx.timeline = :timeline " +
                        "AND mx.smooth = :smooth " +
                        "AND mx.growthInstance = :growthInstance " +
                        "AND mx.growthWeight = :growthWeight " +
                        "AND mx.marginInstance = :marginInstance " +
                        "AND mx.marginWeight = :marginWeight " +
                        "AND mx.chaosInstance = :chaosInstance " +
                        "AND mx.chaosWeight = :chaosWeight " +
                        "AND mx.oppositesInstance = :oppositesInstance " +
                        "AND mx.oppositesWeight = :oppositesWeight"
        ),
        @NamedQuery(
                name = "Mixin.selectAverageEntryPonderations",
                query = "SELECT " +
                        "   AVG(mx.growthWeight)," +
                        "   AVG(mx.marginWeight)," +
                        "   AVG(mx.chaosWeight)," +
                        "   AVG(mx.oppositesWeight)" +
                        "FROM PositionEntity pos " +
                        "INNER JOIN pos.entryMix mx " +
                        "WHERE pos.open = false " +
                        "AND pos.type = :type"
        )
})
public class MixinEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     *  Ligne temporelle liée.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    private TimelineEntity timeline;

    /**
     *  Niveau de lissage.
     */
    @Column(nullable = false)
    private int smooth;

    /**
     *  Stratégie de croissance liée.
     */
    @ManyToOne
    private GrowthStrategyEntity growthInstance;

    /**
     *  Poids de la stratégie de croissance.
     */
    @Column(nullable = false)
    private double growthWeight;

    /**
     *  Stratégie de marge liée.
     */
    @ManyToOne
    private MarginStrategyEntity marginInstance;

    /**
     *  Poids de la stratégie de marge.
     */
    @Column(nullable = false)
    private double marginWeight;

    /**
     *  Stratégie de chaos lié.
     */
    @ManyToOne
    private ChaosStrategyEntity chaosInstance;

    /**
     *  Poids de la stratégie de chaos.
     */
    @Column(nullable = false)
    private double chaosWeight;

    /**
     *  Stratégie d'opposition.
     */
    @ManyToOne
    private OppositesStrategyEntity oppositesInstance;

    /**
     *  Poids de la stratégie d'opposition.
     */
    @Column(nullable = false)
    private double oppositesWeight;

    /**
     *  Clef de hachage.
     */
    private int hash;

    /**
     *  Liste des positions liées en entrée.
     */
    @OneToMany(mappedBy = "entryMix")
    private Set<PositionEntity> entryPositions;

    /**
     *  Liste des positions mappées en sortie.
     */
    @OneToMany(mappedBy = "exitMix")
    private Set<PositionEntity> exitPositions;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne la ligne temporelle liée.
     *  @return Ligne temporelle.
     */
    public TimelineEntity getTimeline() {
        return this.timeline;
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    public int getSmooth() {
        return this.smooth;
    }

    /**
     *  Retourne la stratégie de croissance.
     *  @return Stratégie de croissance.
     */
    public GrowthStrategyEntity getGrowthInstance() {
        return this.growthInstance;
    }

    /**
     *  Retourne le poids de la stratégie de croissance.
     *  @return Poids de la stratégie de croissance.
     */
    public double getGrowthWeight() {
        return this.growthWeight;
    }

    /**
     *  Retourne la stratégie de marge.
     *  @return Stratégie de marge.
     */
    public MarginStrategyEntity getMarginInstance() {
        return this.marginInstance;
    }

    /**
     *  Retourne le poids de la stratégie de marge.
     *  @return Poids de la stratégie de marge.
     */
    public double getMarginWeight() {
        return this.marginWeight;
    }

    /**
     *  Retourne la stratégie de chaos.
     *  @return Stratégie de chaos.
     */
    public ChaosStrategyEntity getChaosInstance() {
        return this.chaosInstance;
    }

    /**
     *  Retourne le poids de la stratégie de chaos.
     *  @return Poids de la stratégie de chaos.
     */
    public double getChaosWeight() {
        return this.chaosWeight;
    }

    /**
     *  Retourne la stratégie d'opposition.
     *  @return Poids de la stratégie d'opposition.
     */
    public OppositesStrategyEntity getOppositesInstance() {
        return this.oppositesInstance;
    }

    /**
     *  Retourne le poids de la stratégie d'opposition.
     *  @return Poids de la stratégie d'opposition.
     */
    public double getOppositesWeight() {
        return this.oppositesWeight;
    }

    /**
     *  Retourne la clef de hachage.
     *  @return Clef de hachage.
     */
    protected int getHash() {
        return this.hash;
    }

    /**
     *  Affecte la ligne temporelle.
     *  @param timeline Ligne temporelle.
     */
    public void setTimeline(TimelineEntity timeline) {
        this.timeline = timeline;
    }

    /**
     *  Affecte le niveau de lissage.
     *  @param smooth Niveau de lissage.
     */
    public void setSmooth(int smooth) {
        this.smooth = smooth;
    }

    /**
     *  Affecte la stratégie de croissange.
     *  @param growthInstance Stratégie de croissance.
     */
    public void setGrowthInstance(GrowthStrategyEntity growthInstance) {
        this.growthInstance = growthInstance;
    }

    /**
     *  Affecte le poids de la stratégie de croissance.
     *  @param growthWeight Poids de la stratégie de croissance.
     */
    public void setGrowthWeight(double growthWeight) {
        this.growthWeight = growthWeight;
    }

    /**
     *  Affecte la stratégie de marge.
     *  @param marginInstance Stratégie de marge.
     */
    public void setMarginInstance(MarginStrategyEntity marginInstance) {
        this.marginInstance = marginInstance;
    }

    /**
     *  Affecte le poids de la stratégie de marge.
     *  @param marginWeigth Poids de la stratégie de marge.
     */
    public void setMarginWeight(double marginWeigth) {
        this.marginWeight = marginWeigth;
    }

    /**
     *  Affecte la stratégie de chaos.
     *  @param chaosInstance Stratégie de chaos.
     */
    public void setChaosInstance(ChaosStrategyEntity chaosInstance) {
        this.chaosInstance = chaosInstance;
    }

    /**
     *  Affecte le poids de la stratégie de chaos.
     *  @param chaosWeigth Poids de la stratégie de chaos.
     */
    public void setChaosWeight(double chaosWeigth) {
        this.chaosWeight = chaosWeigth;
    }

    /**
     *  Affecte la stratégie d'opposition.
     *  @param oppositesInstance Stratégie d'opposition.
     */
    public void setOppositesInstance(OppositesStrategyEntity oppositesInstance) {
        this.oppositesInstance = oppositesInstance;
    }

    /**
     *  Affecte le poids de la stratégie d'opposition.
     *  @param oppositesWeight Poids de la stratégie d'opposition.
     */
    public void setOppositesWeight(double oppositesWeight) {
        this.oppositesWeight = oppositesWeight;
    }

    /**
     *  Définit la clef de hachage.
     *  @param hash Clef de hachage.
     */
    public void setHash(int hash) {
        this.hash = hash;
    }

    /**
     *  Convertit l'objet en piste d'entrée.
     *  @param curve Courbe observée.
     *  @param builds Cellules évaluées (performance).
     *  @param mode Mode.
     *  @return Piste d'entrée.
     *  @throws InnerException En cas d'erreur interne.
     */
    public EntryLead asEntryLead(AbstractCurve curve, List<AbstractCell> builds, boolean mode) throws InnerException {
        /* Création du tableau de résultat */
        List<Object[]> pre = this.asBasicLead();

        /* Création des tableaux */
        EntryArtifact[] artifacts = new EntryArtifact[pre.size()];
        double[] ponderations = new double[pre.size()];
        for (int i = 0; i < pre.size(); i++) {
            /* Récupération de la stratégie */
            AbstractStrategy strategy = (AbstractStrategy) pre.get(i)[0];
            strategy.observe(curve, builds);

            /* Affectation */
            artifacts[i] = new EntryArtifact(strategy);
            ponderations[i] = (double) pre.get(i)[1];
        }

        /* Renvoi */
        return new EntryLead(ponderations, artifacts, mode);
    }

    /**
     *  Convertit l'objet en piste de sortie.
     *  @param curve Courbe.
     *  @param builds Cellules évaluées (performance).
     *  @param mode Mode évalué.
     *  @return Piste de sortie.
     *  @throws InnerException En cas d'erreur interne.
     */
    public ExitLead asExitLead(AbstractCurve curve, List<AbstractCell> builds, boolean mode) throws InnerException {
        /* Création du tableau de résultat */
        List<Object[]> pre = this.asBasicLead();

        /* Création des tableaux */
        ExitArtifact[] artifacts = new ExitArtifact[pre.size()];
        double[] ponderations = new double[pre.size()];
        for (int i = 0; i < pre.size(); i++) {
            /* Récupération de la stratégie */
            AbstractStrategy strategy = (AbstractStrategy) pre.get(i)[0];
            strategy.observe(curve, builds);

            /* Affectation */
            artifacts[i] = new ExitArtifact(strategy);
            ponderations[i] = (double) pre.get(i)[1];
        }

        /* Renvoi */
        return new ExitLead(ponderations, artifacts, mode);
    }

    /**
     *  Renvoi une liste de couple stratégie/pondération applicables à l'entité.
     *  @return Liste des combinaisons.
     */
    private List<Object[]> asBasicLead() {
        List<Object[]> result = new ArrayList<>();
        if (this.marginInstance != null) {
            result.add(new Object[] { this.marginInstance.asStrategy(), this.marginWeight});
        }
        if (this.growthInstance != null) {
            result.add(new Object[] { this.growthInstance.asStrategy(), this.growthWeight });
        }
        if (this.chaosInstance != null) {
            result.add(new Object[] { this.chaosInstance.asStrategy(), this.chaosWeight});
        }
        if (this.oppositesInstance != null) {
            result.add(new Object[] { this.oppositesInstance.asStrategy(), this.oppositesWeight });
        }
        return result;
    }

    /**
     *  Retourne la clef de hachage.
     *  @return Clef de hachage.
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                this.timeline.getId(),
                this.smooth,
                this.growthInstance == null ? 0 : this.growthInstance.getId(),
                this.growthWeight,
                this.marginInstance == null ? 0 : this.marginInstance.getId(),
                this.marginWeight,
                this.chaosInstance == null ? 0 : this.chaosInstance.getId(),
                this.chaosWeight,
                this.oppositesInstance == null ? 0 : this.oppositesInstance.getId(),
                this.oppositesWeight);
    }
}
