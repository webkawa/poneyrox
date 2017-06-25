package com.akasoft.poneyrox.entities.positions;

import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.views.PositionViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import java.util.UUID;

/**
 *  Position.
 *  Position prise sur un marché.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Position.getTopPositions",
                query = "SELECT DISTINCT pos " +
                        "FROM PositionEntity pos " +
                        "INNER JOIN pos.timeline " +
                        "WHERE pos.open = false " +
                        "AND pos.timeout = false " +
                        "AND pos.type = :type " +
                        "AND pos.mode = :mode " +
                        "ORDER BY pos.profit DESC "
        ),
        @NamedQuery(
                name = "Position.getTopStrategiesForTesting",
                query = "SELECT " +
                        "   AVG(pos.profit) AS rawProfit, " +
                        "   AVG(pos.relativeProfit) AS relativeProfit, " +
                        "   AVG(pos.dailyProfit) AS dailyProfit, " +
                        "   COUNT(pos) AS confirmations, " +
                        "   SUM(pos.winScore) AS wins, " +
                        "   SUM(pos.lossScore) AS loss, " +
                        "   tl AS timeline, " +
                        "   pos.smooth AS smooth, " +
                        "   pos.mode AS mode, " +
                        "   mxEn AS entryMix, " +
                        "   mxEx AS exitMix " +
                        "FROM PositionEntity pos " +
                        "INNER JOIN pos.timeline AS tl " +
                        "INNER JOIN pos.entryMix AS mxEn " +
                        "INNER JOIN pos.exitMix AS mxEx " +
                        "WHERE pos.timeout = false " +
                        "AND pos.open = false " +
                        "AND pos.start > :start " +
                        "AND NOT EXISTS ( " +
                        "   SELECT 1 " +
                        "   FROM PositionEntity other " +
                        "   WHERE other.entryMix = pos.entryMix " +
                        "   AND other.exitMix = pos.exitMix " +
                        "   AND other.timeline = pos.timeline " +
                        "   AND other.smooth = pos.smooth " +
                        "   AND other.mode = pos.mode " +
                        "   AND other.open = true " +
                        "   AND (" +
                        "       other.type = :ttype" +
                        "       OR other.type = :vtype" +
                        "   )" +
                        ") " +
                        "GROUP BY tl, pos.smooth, pos.mode, mxEn, mxEx " +
                        "HAVING " +
                        "   COUNT(pos) < :confirmations " +
                        "   AND SUM(pos.timeoutScore) = 0 " +
                        "ORDER BY " +
                        "   AVG(pos.relativeProfit) DESC, " +
                        "   AVG(pos.dailyProfit) DESC"
        ),
        @NamedQuery(
                name = "Position.getTopStrategiesForProduction",
                query = "SELECT " +
                        "   AVG(pos.profit) AS rawProfit, " +
                        "   AVG(pos.relativeProfit) AS relativeProfit, " +
                        "   AVG(pos.dailyProfit) AS dailyProfit, " +
                        "   COUNT(pos) AS confirmations, " +
                        "   SUM(pos.winScore) AS wins, " +
                        "   SUM(pos.lossScore) AS loss, " +
                        "   tl AS timeline, " +
                        "   pos.smooth AS smooth, " +
                        "   pos.mode AS mode, " +
                        "   mxEn AS entryMix, " +
                        "   mxEx AS exitMix " +
                        "FROM PositionEntity pos " +
                        "INNER JOIN pos.timeline tl " +
                        "INNER JOIN pos.entryMix mxEn " +
                        "INNER JOIN pos.exitMix mxEx " +
                        "WHERE pos.open = false " +
                        "AND pos.start > :start " +
                        "AND (" +
                        "   pos.type = :ttype " +
                        "   OR pos.type = :vtype" +
                        ") " +
                        "AND NOT EXISTS(" +
                        "   SELECT 1 " +
                        "   FROM PositionEntity other " +
                        "   WHERE other.entryMix = pos.entryMix " +
                        "   AND other.exitMix = pos.exitMix " +
                        "   AND other.timeline = pos.timeline " +
                        "   AND other.smooth = pos.smooth " +
                        "   AND other.mode = pos.mode " +
                        "   AND other.open = true " +
                        "   AND other.type = :vtype" +
                        ") " +
                        "GROUP BY tl, pos.smooth, pos.mode, mxEn, mxEx " +
                        "HAVING " +
                        "   AVG(pos.dailyProfit) > :percent " +
                        "   AND COUNT(pos) > :confirmations " +
                        "   AND SUM(pos.timeoutScore) = 0 " +
                        "ORDER BY " +
                        "   AVG(pos.relativeProfit) DESC," +
                        "   AVG(pos.dailyProfit) DESC"),
        @NamedQuery(
                name = "Position.deleteExpiredSimulations",
                query = "DELETE FROM PositionEntity AS pos " +
                        "WHERE pos.type = :type " +
                        "AND pos.timeout = true"
        ),
        @NamedQuery(
                name = "Position.deleteUntestedSimulations",
                query = "DELETE FROM PositionEntity AS pos " +
                        "WHERE pos.id IN (" +
                        "   SELECT eq.id " +
                        "   FROM PositionEntity AS eq " +
                        "   LEFT JOIN PositionEntity AS cp " +
                        "   ON eq.timeline = cp.timeline " +
                        "   AND eq.smooth = cp.smooth " +
                        "   AND eq.mode = cp.mode " +
                        "   AND eq.entryMix = cp.entryMix " +
                        "   AND eq.exitMix = cp.exitMix " +
                        "   AND NOT(eq.id = cp.id) " +
                        "   WHERE cp IS NULL " +
                        "   AND eq.open = false " +
                        "   AND eq.start < :start " +
                        "   AND eq.type = :type " +
                        "   AND eq.dailyProfit < :profit" +
                        ")"
        ),
        @NamedQuery(
                name = "Position.deleteAllOpenPositions",
                query = "DELETE FROM PositionEntity AS pos " +
                        "WHERE pos.open = true"
        )
})
public class PositionEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            PositionViews.Public.class
    })
    private UUID id;

    /**
     *  Mode.
     *  true pour long, false pour court.
     */
    @Column(name = "position_mode")
    @JsonView({
            PositionViews.Public.class
    })
    private boolean mode;

    /**
     *  Ouverture.
     */
    @Column(name = "position_open")
    @JsonView({
            PositionViews.Public.class
    })
    private boolean open;

    /**
     *  Date de début.
     */
    @Column(name = "position_start")
    @JsonView({
            PositionViews.Public.class
    })
    private long start;

    /**
     *  Date de fin.
     */
    @Column(name = "position_end")
    @JsonView({
            PositionViews.Public.class
    })
    private long end;

    /**
     *  Prix à l'entrée.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private double entry;

    /**
     *  Score à l'entrée.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private double entryScore;

    /**
     *  Prix à la sortie.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private double exit;

    /**
     *  Score à la sortie.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private double exitScore;

    /**
     *  Profits réalisés.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private double profit;

    /**
     *  Pourcentage de profit relatif à l'entrée.
     */
    @Formula("(profit / entry) * 100")
    private double relativeProfit;

    /**
     *  Pourcentage de profit journalier.
     */
    @Formula("(((24 * 1000 * 60 * 60) / (position_end - position_start)) * profit / entry) * 100")
    private double dailyProfit;

    /**
     *  Indique si la position a expiré.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private boolean timeout;

    /**
     *  Egal à 1 si la position expiré, 0 sinon.
     */
    @Formula("CASE WHEN timeout = true THEN 1 ELSE 0 END")
    private int timeoutScore;

    /**
     *  Egal à 1 si la position a dégagé un bénéfice, 0 sinon.
     */
    @Formula("CASE WHEN profit > 0 THEN 1 ELSE 0 END")
    private int winScore;

    /**
     *  Egale à 1 si la position a subit une perte, 0 sinon.
     */
    @Formula("CASE WHEN profit <= 0 THEN 1 ELSE 0 END")
    private int lossScore;

    /**
     *  Ligne temporelle liée.
     */
    @ManyToOne
    @JoinColumn(nullable = false)
    @JsonView({
            PositionViews.Public.class
    })
    private TimelineEntity timeline;

    /**
     *  Niveau de lissage appliqué à la courbe de génération.
     */
    @JsonView({
            PositionViews.Public.class
    })
    private int smooth;

    /**
     *  Type de position.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "position_type")
    @JsonView({
            PositionViews.Public.class
    })
    private PositionType type;

    /**
     *  Stratégie d'entrée.
     */
    @ManyToOne
    private MixinEntity entryMix;

    /**
     *  Stratégie de sortie.
     */
    @ManyToOne
    private MixinEntity exitMix;

    /**
     *  Constructeur.
     */
    public PositionEntity() {
        this.start = new java.util.Date().getTime();
    }

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne la date de création.
     *  @return Date de création.
     */
    public long getStart() {
        return this.start;
    }

    /**
     *  Retourne la date de fin.
     *  @return Date de fin.
     */
    public long getEnd() {
        return this.end;
    }

    /**
     *  Retourne le mode.
     *  @return Mode.
     */
    public boolean getMode() {
        return this.mode;
    }

    /**
     *  Indique si la position est ouverte.
     *  @return true si la position est ouverte.
     */
    public boolean isOpen() {
        return this.open;
    }

    /**
     *  Retourne le prix d'entrée.
     *  @return Prix d'entrée.
     */
    public double getEntry() {
        return this.entry;
    }

    /**
     *  Retourne le score à l'entrée.
     *  @return Score à l'entrée.
     */
    public double getEntryScore() {
        return this.entryScore;
    }

    /**
     *  Retourne le prix de sortie.
     *  @return Prix de sortie.
     */
    public double getExit() {
        return this.exit;
    }

    /**
     *  Retourne le score à la sortie.
     *  @return Score à la sortie.
     */
    public double getExitScore() {
        return this.exitScore;
    }

    /**
     *  Retourne le profit réalisé.
     *  @return Profit réalisé.
     */
    public double getProfit() {
        return this.profit;
    }

    /**
     *  Retourne le pourcentage de profit comparé au seuil d'entrée.
     *  @return Pourcentage de profit.
     */
    public double getRelativeProfit() {
        return this.relativeProfit;
    }

    /**
     *  Retourne le pourcentage de profit journalier.
     *  @return Pourcentage de profit.
     */
    public double getDailyProfit() {
        return this.dailyProfit;
    }

    /**
     *  Retourne le type de position.
     *  @return Type de position.
     */
    public PositionType getType() {
        return this.type;
    }

    /**
     *  Retourne la stratégie d'entrée liée.
     *  @return Stratégie liée.
     */
    public MixinEntity getEntryMix() {
        return this.entryMix;
    }

    /**
     *  Retourne la stratégie de sortie liée.
     *  @return Stratégie liée
     */
    public MixinEntity getExitMix() {
        return this.exitMix;
    }

    /**
     *  Indique si la position a expiré.
     *  @return Expiration.
     */
    public boolean isTimeout() {
        return this.timeout;
    }

    /**
     *  Retourne le score rattaché à l'expiration.
     *  @return Score rattaché.
     */
    public int getTimeoutScore() {
        return this.timeoutScore;
    }

    /**
     *  Retourne le score rattaché à une victoire.
     *  @return Score rattaché.
     */
    public int getWinScore() {
        return this.winScore;
    }

    /**
     *  Retourne le score rattaché à une défaite.
     *  @return Score rattaché.
     */
    public int getLossScore() {
        return this.lossScore;
    }

    /**
     *  Retourne la ligne temporelle liée.
     *  @return Ligne temporelle.
     */
    public TimelineEntity getTimeline() {
        return this.timeline;
    }

    /**
     *  Retourne le niveau de lissage appliqué à la ligne temporelle rattachée.
     *  @return Niveau de lissage.
     */
    public int getSmooth() {
        return this.smooth;
    }

    /**
     *  Définit le mode.
     *  @param mode Mode exécuté.
     */
    public void setMode(boolean mode) {
        this.mode = mode;
    }

    /**
     *  Affecte le type de position.
     *  @param type Type de position.
     */
    public void setType(PositionType type) {
        this.type = type;
    }

    /**
     *  Définit l'ouverture.
     *  @param open Ouverture.
     */
    public void setOpen(boolean open) {
        this.open = open;
    }

    /**
     *  Affecte le prix d'entrée.
     *  @param entry Prix de sortie.
     */
    public void setEntry(double entry) {
        this.entry = entry;
    }

    /**
     *  Définit le score à l'entrée.
     *  @param score Score à l'entrée.
     */
    public void setEntryScore(double score) {
        this.entryScore = score;
    }

    /**
     *  Affecte le prix de sortie.
     *  @param exit Prix de sortie.
     */
    public void setExit(double exit) {
        this.exit = exit;
    }

    /**
     *  Affecte le score à la sortie.
     *  @param score Score à la sortie.
     */
    public void setExitScore(double score) {
        this.exitScore = score;
    }

    /**
     *  Affecte le profit réalisé.
     *  @param profit Profit réalisé.
     */
    public void setProfit(double profit) {
        this.profit = profit;
    }

    /**
     *  Définit la date de fin.
     *  @param end Date de fin définie.
     */
    public void setEnd(long end) {
        this.end = end;
    }

    /**
     *  Affecte la stratégie d'entrée liée.
     *  @param mixin Stratégie liée.
     */
    public void setEntryMix(MixinEntity mixin) {
        this.entryMix = mixin;
    }

    /**
     *  Affecte la stratégie de sortie liée.
     *  @param mixin Stratégie liée.
     */
    public void setExitMix(MixinEntity mixin) {
        this.exitMix = mixin;
    }

    /**
     *  Affecte l'expiration.
     *  @param timeout Valeur affectée.
     */
    public void setTimeout(boolean timeout) {
        this.timeout = timeout;
    }

    /**
     *  Affecte la ligne temporelle liée.
     *  @param timeline Ligne temporelle liée.
     */
    public void setTimeline(TimelineEntity timeline) {
        this.timeline = timeline;
    }

    /**
     *  Définit le niveau de lissage.
     *  @param smooth Niveau de lissage.
     */
    public void setSmooth(int smooth) {
        this.smooth = smooth;
    }
}
