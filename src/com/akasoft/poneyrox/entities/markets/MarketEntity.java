package com.akasoft.poneyrox.entities.markets;

import com.akasoft.poneyrox.views.MarketViews;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.*;
import java.util.UUID;

/**
 *  Marché.
 *  Marché enregistré dans l'application et exploitable pour des prises de position.
 */
@Entity
@NamedQueries({
        @NamedQuery(
                name = "Market.getAll",
                query = "SELECT m " +
                        "FROM MarketEntity m " +
                        "ORDER BY m.label"
        ),
        @NamedQuery(
                name = "Market.getByKey",
                query = "SELECT m " +
                        "FROM MarketEntity m " +
                        "WHERE m.key = :key"
        )
})
public class MarketEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonView({
            MarketViews.Public.class
    })
    private UUID id;

    /**
     *  Clef d'accès.
     */
    @Column(nullable = false)
    @JsonView({
            MarketViews.Public.class
    })
    private String key;

    /**
     *  Libellé.
     */
    @Column(nullable = false)
    @JsonView({
            MarketViews.Public.class
    })
    private String label;

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant de l'entité.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne le libellé.
     *  @return Libellé du marché.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     *  Clef d'accès.
     *  @return Clef d'accès au marché.
     */
    public String getKey() {
        return this.key;
    }

    /**
     *  Définit le libellé.
     *  @param label Libellé.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     *  Définit la clef d'accès.
     *  @param key Clef d'accès.
     */
    public void setKey(String key) {
        this.key = key;
    }
}
