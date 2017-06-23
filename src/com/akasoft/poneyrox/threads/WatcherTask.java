package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.api.whaleclub.dao.WhaleClubAccess;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubRateDTO;
import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.dao.RateDAO;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Observateur.
 *  Tache en charge de l'observation des marchés actifs.
 */
public class WatcherTask extends WatcherTaskWrapper {
    /**
     *  Accès à l'API WhaleClub.
     */
    private WhaleClubAccess access;

    /**
     *  DAO des taux.
     */
    private RateDAO dao;

    /**
     *  Liste des marchés observés.
     */
    private List<MarketEntity> markets;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     *  @param access Accès à l'API WhaleClub.
     *  @param dao DAO des taux.
     */
    public WatcherTask(ManagerComponent manager, WhaleClubAccess access, RateDAO dao) {
        super(manager);
        this.access = access;
        this.dao = dao;
        this.markets = new ArrayList<>();
    }

    /**
     *  Exécution de la tache.
     *  @throws AbstractException En cas d'erreur lors de l'exécution.
     */
    @Override
    protected void execute() throws AbstractException {
        /* Extraction de la liste des clefs */
        List<String> keys = this.markets.stream().map(e -> e.getKey()).collect(Collectors.toList());

        /* Parcours des taux les plus récents */
        for (WhaleClubRateDTO rate : this.access.getRates(keys)) {
            /* Extraction du marché */
            MarketEntity market = this.markets.stream().filter(e -> e.getKey().equals(rate.getMarket())).findFirst().get();

            /* Vérification de la date de rafraichissement */
            if (!super.hasRate(market) || super.getRate(market).getTime() < rate.getDate()) {

                /* Enregistrement en base */
                RateEntity add = this.dao.persistRate(market, rate.getDate(), rate.getAsk(), rate.getBid());

                /* Mise à jour */
                super.addRate(market, add);

                /* Diffusion */
                super.getManager().diffuseRate(add);
            }
        }
    }

    /**
     *  Ajoute un marché dans la liste d'observation.
     *  @param market Marché observé.
     */
    public void watch(MarketEntity market) {
        if (!this.markets.stream().anyMatch(m -> m.getKey().equals(market.getKey()))) {
            this.markets.add(market);
        }
    }

    /**
     *  Retire un marché de la liste d'observation.
     *  @param market Marché retiré.
     */
    public void ignore(MarketEntity market) {
        this.markets.removeIf(m -> market.getKey().equals(m.getKey()));
    }
}
