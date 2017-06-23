package com.akasoft.poneyrox.entities.markets;

import com.akasoft.poneyrox.views.TimelineViews;
import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import java.io.Serializable;
import java.util.UUID;

/**
 *  Ligne temporelle.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Timeline.getAll",
                query = "SELECT t " +
                        "FROM TimelineEntity t " +
                        "LEFT JOIN t.market m"
        ),
        @NamedQuery(
                name = "Timeline.getByActivity",
                query = "SELECT t " +
                        "FROM TimelineEntity t " +
                        "LEFT JOIN t.market m " +
                        "WHERE t.active = :activity"
        )
})
public class TimelineEntity implements Serializable {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            TimelineViews.Public.class
    })
    private UUID id;

    /**
     *  Libellé.
     */
    @Column(nullable = false)
    @JsonView({
            TimelineViews.Public.class
    })
    private String label;

    /**
     *  Taille des cellules.
     *  Exprimée en secondes.
     */
    @Column(nullable = false)
    @JsonView({
            TimelineViews.Public.class
    })
    private int size;

    /**
     *  Indicateur d'activité.
     *  Si true, la ligne temporelle est active.
     */
    @Column(nullable = false)
    @JsonView({
            TimelineViews.Public.class
    })
    private boolean active;

    /**
     *  Marché analysé.
     */
    @ManyToOne
    @JsonView({
            TimelineViews.Public.class
    })
    private MarketEntity market;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne le libellé.
     *  @return Libellé.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     *  Retourne la taille des cellules.
     *  @return Taille des cellules.
     */
    public int getSize() {
        return size;
    }

    /**
     *  Retourne le statut d'activité.
     *  @return Statut d'activité.
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     *  Retourne le marché analysé.
     *  @return Marché analysé.
     */
    public MarketEntity getMarket() {
        return market;
    }

    /**
     *  Définit le libellé.
     *  @param label Libellé.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *  Définit la taille des cellules.
     *  @param size Taille des cellules.
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     *  Définit l'activité.
     *  @param active Activité.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     *  Définit le marché.
     *  @param market Marché.
     */
    public void setMarket(MarketEntity market) {
        this.market = market;
    }
}
