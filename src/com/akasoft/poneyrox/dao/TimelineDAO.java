package com.akasoft.poneyrox.dao;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.List;
import java.util.UUID;

/**
 *  DAO des lignes temporelles.
 */
@Repository
public class TimelineDAO extends AbstractDAO {
    /**
     *  Constructeur.
     *  @param factory Générateur de sessions.
     */
    public TimelineDAO(@Autowired SessionFactory factory) {
        super(factory);
    }

    /**
     *  Référence une ligne temporelle dans la base.
     *  @param market Marché de rattachement.
     *  @param label Libellé.
     *  @param size Taille des cellules.
     *  @param active Statut d'activité.
     *  @return Ligne temporelle insérée.
     */
    public TimelineEntity persistTimeline(MarketEntity market, String label, int size, boolean active) {
        TimelineEntity timeline = new TimelineEntity();
        timeline.setMarket(market);
        timeline.setLabel(label);
        timeline.setSize(size);
        timeline.setActive(active);

        super.getSession().persist(timeline);
        return timeline;
    }
    /**
     *  Retourne la liste complète des vues.
     *  @return Liste des vues.
     */
    public List<TimelineEntity> getAll() {
        return super.getSession()
                .getNamedQuery("Timeline.getAll")
                .getResultList();
    }

    /**
     *  Retourne une liste de lignes temprelles filtrées par activité.
     *  @param active Statut d'activité.
     *  @return Liste des lignes temporelles.
     */
    public List<TimelineEntity> getByActivity(boolean active) {
        return super.getSession()
                .getNamedQuery("Timeline.getByActivity")
                .setParameter("activity", active)
                .getResultList();
    }

    /**
     *  Définit l'activité d'une ligne temporelle.
     *  @param id Identifiant de la ligne temporelle.
     *  @param activity Activité définie.
     *  @return Ligne temporelle mise à jour.
     */
    public TimelineEntity setActivity(UUID id, boolean activity) {
        TimelineEntity timeline = super.getSession().get(TimelineEntity.class, id);
        timeline.setActive(activity);

        super.getSession().save(timeline);
        return timeline;
    }
}
