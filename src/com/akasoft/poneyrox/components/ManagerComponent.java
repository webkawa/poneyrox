package com.akasoft.poneyrox.components;

import com.akasoft.poneyrox.api.whaleclub.dao.WhaleClubAccess;
import com.akasoft.poneyrox.configuration.TaskSchedulerConfiguration;
import com.akasoft.poneyrox.core.mixins.batch.EntryBatch;
import com.akasoft.poneyrox.core.mixins.batch.ExitBatch;
import com.akasoft.poneyrox.core.mixins.leads.EntryLead;
import com.akasoft.poneyrox.core.mixins.leads.ExitLead;
import com.akasoft.poneyrox.core.strategies.categories.*;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.EnterShortITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitLongITF;
import com.akasoft.poneyrox.core.strategies.interfaces.ExitShortITF;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.dao.*;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.entities.positions.TransactionEntity;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.ApiException;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.threads.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  Gestionnaire.
 *  Composant central dans l'utilisation de l'application, gérant l'ensemble des taches actives ainsi que plusieurs
 *  actions d'initialisation.
 */
@Component
@Scope(scopeName = "singleton")
public class ManagerComponent {
    /**
     *  Gestionnaire des taches.
     */
    private TaskScheduler scheduler;

    /**
     *  Tache d'observation.
     */
    private WatcherTask watcher;

    /**
     *  Mixeur.
     */
    private MixerTask mixer;

    /**
     *  Suiveur.
     */
    private FollowerTask follower;

    /**
     *  Consolidation.
     */
    private ConsolidationTask consolidation;

    /**
     *  Agent de placement.
     */
    private PlaceholderTask placeholder;

    /**
     *  Tache(s) d'observation du temps.
     */
    private final List<TimelineTask> timelines;

    /**
     *  Tache(s) de validation des stratégies.
     */
    private final List<StrategyTask> strategies;

    /**
     *  Portefeuille.
     */
    private WalletEntity wallet;

    /**
     *  Constructeur.
     *  @param scheduler Gestionnaire des taches.
     *  @param access Accès à l'API Whale Club.
     *  @param rateDAO DAO des taux.
     *  @param positionDAO DAO des positions.
     *  @param transactionDAO DAO des transactions.
     *  @param mixinDAO DAO des stratégies.
     *  @param walletDAO DAO des portefeuilles.
     */
    public ManagerComponent(
            @Autowired TaskSchedulerConfiguration scheduler,
            @Autowired WhaleClubAccess access,
            @Autowired RateDAO rateDAO,
            @Autowired PositionDAO positionDAO,
            @Autowired TransactionDAO transactionDAO,
            @Autowired MixinDAO mixinDAO,
            @Autowired WalletDAO walletDAO) {
        /* Paramètres de base */
        this.scheduler = scheduler;
        this.watcher = new WatcherTask(this, access, rateDAO);
        this.timelines = new ArrayList<>();
        this.strategies = new ArrayList<>();

        /* Agent de placement */
        this.placeholder = new PlaceholderTask(this, positionDAO, mixinDAO);
        this.scheduler.scheduleAtFixedRate(this.placeholder, 5000);

        /* Agent de suivi */
        this.follower = new FollowerTask(this, access, positionDAO, transactionDAO);
        this.scheduler.scheduleAtFixedRate(this.follower, 5000);

        /* Agent de consolidation */
        this.consolidation = new ConsolidationTask(this, access, positionDAO, transactionDAO, walletDAO);
        this.scheduler.scheduleAtFixedRate(this.consolidation, 30000);

        /* Paramètres */
        positionDAO.deleteAllOpenPositions();;
        this.wallet = walletDAO.persistWallet();
    }

    /**
     *  Initialisation.
     */
    @PostConstruct
    public void postConstruct() throws ApiException, InnerException {
        /* Observateur */
        this.scheduler.scheduleAtFixedRate(this.watcher, 5000);

        /* Stratégies */
        StrategyTask task = new StrategyTask(this, ChaosStrategy.class);
        this.strategies.add(task);
        this.scheduler.scheduleAtFixedRate(task,10000);

        task = new StrategyTask(this, GrowthStrategy.class);
        this.strategies.add(task);
        this.scheduler.scheduleAtFixedRate(task, 10000);

        task = new StrategyTask(this, MarginStrategy.class);
        this.strategies.add(task);
        this.scheduler.scheduleAtFixedRate(task, 10000);

        task = new StrategyTask(this, OppositesStrategy.class);
        this.strategies.add(task);
        this.scheduler.scheduleAtFixedRate(task, 10000);

        task = new StrategyTask(this, ForwardStrategy.class);
        this.strategies.add(task);
        this.scheduler.scheduleAtFixedRate(task, 10000);

        /* Mixeur */
        this.mixer = new MixerTask(this);
        this.scheduler.scheduleAtFixedRate(this.mixer, 15000);
    }

    /**
     *  Retourne l'observateur de taux.
     *  @return Observateur de taux.
     */
    public WatcherTask getWatcher() {
        return this.watcher;
    }

    /**
     *  Retourne le portefeuille.
     *  @return Portefeuille.
     */
    public WalletEntity getWallet() {
        return this.wallet;
    }

    /**
     *  Retourne la tache rattachée à une ligne temporelle.
     *  @param entity Entité source.
     *  @return Tache correspondante.
     */
    public TimelineTask getTimelineByEntity(TimelineEntity entity) throws InnerException {
        /* Récupération des lignes en thread-safe */
        List<TimelineTask> tls = new ArrayList<>();
        synchronized (this.timelines) {
            tls.addAll(this.timelines);
        }

        /* Filtrage */
        Optional<TimelineTask> result = tls.stream()
                .filter(e -> e.getTimeline().getId().equals(entity.getId()))
                .findFirst();

        if (result.isPresent()) {
            return result.get();
        } else {
            throw new InnerException("Failed to retrieve timeline task by timeline");
        }
    }

    /**
     *  Retourne une courbe par le biais d'une entité et d'un niveau de lissage.
     *  @param entity Entité ciblée.
     *  @param smooth Niveau de lissage.
     *  @return Courbe.
     *  @throws InnerException En cas de courbe introuvable.
     */
    public AbstractCurve getCurve(TimelineEntity entity, int smooth) throws InnerException {
        return this.getTimelineByEntity(entity).getCurve(smooth);
    }

    /**
     *  Récupération de la liste complète des courbes.
     *  @return Liste des courbes.
     */
    public List<AbstractCurve> getAllCurves() {
        return this.timelines.stream().flatMap(e -> e.getCurves().stream()).collect(Collectors.toList());
    }

    /**
     *  Retourne la liste complète des types de stratégies.
     *  @return Liste des types.
     */
    public List<Class> getStrategies() {
        return this.strategies
                .stream()
                .map(e -> e.getType())
                .collect(Collectors.toList());
    }

    /**
     *  Retourne la liste des types de stratégies d'entrée.
     *  @return Liste des stratégies d'entrée.
     */
    public List<Class> getEntryStrategies() {
        return this.getStrategies()
                .stream()
                .filter(e -> {
                    return EnterLongITF.class.isAssignableFrom(e) || EnterShortITF.class.isAssignableFrom(e);
                })
                .collect(Collectors.toList());
    }

    /**
     *  Retourne la liste des types de stratégies de sortie.
     *  @return Liste des stratégies de sortie.
     */
    public List<Class> getExitStrategies() {
        return this.getStrategies()
                .stream()
                .filter(e -> {
                    return ExitLongITF.class.isAssignableFrom(e) || ExitShortITF.class.isAssignableFrom(e);
                })
                .collect(Collectors.toList());
    }

    /**
     *  Retourne le nombre total de positions aléatoires.
     *  @return Nombre de positions.
     */
    public int getRandomPositionsCount() {
        return this.follower.getRandomBufferSize();
    }

    /**
     *  Retourne le nombre total de positions ciblées.
     *  @return Nombre de positions.
     */
    public int getTargetedPositionsCount() {
        return this.follower.getTargetedBufferSize();
    }

    public int getVirtualTransactionsCount() {
        return this.follower.getVirtualBufferSize();
    }

    /**
     *  Retourne la liste des positions ciblées suivies.
     *  @return Liste des positions aléatoires.
     */
    public Set<PositionEntity> getTargetedPositions() {
        return new HashSet<>();
    }

    /**
     *  Définit le portefeuille.
     * @param wallet Valeur affectée.
     */
    public synchronized void setWallet(WalletEntity wallet) {
        synchronized (this.wallet) {
            this.wallet = wallet;
        }
    }

    /**
     *  Référence une ligne temporelle.
     *  @return Entité source.
     */
    public TimelineTask addTimeline(TimelineEntity source) throws InnerException {
        if (this.timelines.stream().filter(e -> e.getTimeline().getId().equals(source.getId())).count() > 0) {
            throw new InnerException("Timeline %s already registered", source.getId());
        } else {
            TimelineTask task = new TimelineTask(this, source);
            task.setFuture(this.scheduler.scheduleAtFixedRate(task, 5000));

            this.timelines.add(task);
            return task;
        }
    }

    /**
     *  Supprime une ligne temporelle.
     *  @param source Entité source.
     */
    public void removeTimeline(TimelineEntity source) {
        TimelineTask task = this.timelines.stream().filter(e -> e.getTimeline().getId().equals(source.getId())).findFirst().get();
        task.getFuture().cancel(false);
        this.timelines.remove(task);
    }

    /**
     *  Réalise la diffusion d'un taux dans les lignes temporelles concernées.
     *  @param rate Taux.
     */
    public void diffuseRate(RateEntity rate) {
        for (TimelineTask task : this.timelines) {
            if (task.getTimeline().getMarket().getKey().equals(rate.getMarket().getKey())) {
                task.addBuffer(rate);
            }
        }
    }

    /**
     *  Réalise la diffusion d'une courbe dans les agents d'évaluation concernés.
     *  @param curve Courbe diffusée.
     */
    public void diffuseCurve(AbstractCurve curve) {
        this.consolidation.addCurve(curve);
        for (StrategyTask task : this.strategies) {
            task.addBuffer(curve);
        }
    }

    /**
     *  Réalise la diffusion d'un lot d'options d'entrée.
     *  @param batch Lot diffusé.
     */
    public void diffuseEntryBatch(EntryBatch batch) throws InnerException {
        this.mixer.addEntryBuffer(batch);
    }

    /**
     *  Réalise la diffusion d'un lot de sortie.
     *  @param batch Lot de sortie.
     */
    public void diffuseExitBatch(ExitBatch batch) {
        this.mixer.addExitBuffer(batch);
    }

    /**
     *  Réalise la diffusion d'une liste de pistes d'entrée.
     *  @param curve Courbe analysée.
     *  @param leads Pistes diffusées.
     */
    public void diffuseEntryLeads(AbstractCurve curve, List<EntryLead> leads) {
        for (EntryLead lead : leads) {
            this.placeholder.addEntryBuffer(curve, lead);
        }
    }

    /**
     *  Réalise la diffusion d'une liste de pistes de sortie.
     *  @curve Courbe analysée.
     *  @param leads Pistes diffusées.
     */
    public void diffuseExitLeads(AbstractCurve curve, List<ExitLead> leads) {
        for (ExitLead lead : leads) {
            this.placeholder.addExitBuffer(curve, lead);
        }
    }

    /**
     *  Publie une position prise.
     *  @param mode Mode (vrai pour aléatoire, false pour ciblée).
     *  @param curve Courve de rattachement.
     *  @param position Position prise.
     */
    public void publishPosition(boolean mode, AbstractCurve curve, PositionEntity position) {
        if (mode) {
            this.follower.addRandomBuffer(curve, position);
        } else {
            this.follower.addTargetedBuffer(curve, position);
        }
    }

    /**
     *  Publie une transaction ouverte.
     *  @param test Mode (vrai pour virtuel, false pour réel).
     *  @param curve Courbe de rattachement.
     *  @param transaction Transaction.
     */
    public void publishTransaction(boolean test, AbstractCurve curve, TransactionEntity transaction) {
        if (test) {
            this.follower.addVirtualBuffer(curve, transaction);
        } else {
            /* TODO */
        }
    }
}