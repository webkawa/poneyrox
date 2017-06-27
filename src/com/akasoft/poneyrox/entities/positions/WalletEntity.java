package com.akasoft.poneyrox.entities.positions;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.UUID;

/**
 *  Portefeuille.
 */
@Entity
public class WalletEntity {
    /**
     *  Identifiant.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    /**
     *  Date de début.
     *  Exprimée en millisecondes.
     */
    private long start;

    /**
     *  Durée de rétention des positions.
     *  Durée maximale durant laquelle les positions ouvertes ou expirées sont conservées dans la base.
     */
    private long retentionDelay;

    /**
     *  Rentabilité retenue.
     *  Rentabilité minimale retenue lors du nettoyage des simulations ou de tests. Doit rester négatif pour permettre
     *  la remontée des pistes.
     */
    private double retentionProfit;

    /**
     *  Confirmations retenues.
     *  Nombre maximum de confirmations attendues avant la suppression d'une série de tests.
     */
    private int retentionConfirmations;

    /**
     *  Durée maximale avant l'expiration d'une transaction.
     */
    private long timeout;

    /**
     *  Barrière à l'entrée.
     *  Score minimum attendu pour la rétention d'une prise de position.
     */
    private int barrierEntry;

    /**
     *  Barrière à la sortie.
     *  Score minimum attendu pour la rétention d'une sortie de position.
     */
    private int barrierExit;

    /**
     *  Taille des échantillons.
     *  Nombre d'instances retournées alétoirement préalablement au passage dans le mixer.
     */
    private int sampleSize;

    /**
     *  Finesse des pondérations.
     *  Niveau de finesse des pondérations individuelles appliquées.
     */
    private int mixerPonderationsGrain;

    /**
     *  Profondeur du mix.
     *  Nombre de stratégies évaluables sélectionnées pour chaque stratégie lors de l'exécution du mixer.
     */
    private int mixerDeepth;

    /**
     *  Pool de simulations.
     *  Nombre maximum de positions simulables simultanément.
     */
    private int simulationPool;

    /**
     *  Finesse des simulations.
     *  Nombre maximum de simulations évaluées et/ou créées à chaque exécution de la tache de placement.
     */
    private int simulationGrain;

    /**
     *  Pool de test.
     *  Nombre maximum de positions testées simultanément.
     */
    private int testPool;

    /**
     *  Finesse des tests.
     *  Nombre de maximum de tests évalués et/ou créés à chaque exécution de la tache de consolidation.
     */
    private int testGrain;

    /**
     *  Durée de consolidation des test.
     *  Durée maximale évaluée à chaque exécution de la tache de consolidation pour la sélection des positions de test.
     */
    private long testPeriod;

    /**
     *  Nombre maximum de confirmations.
     *  Nombre maximum de validations tentées sur une meme stratégie lors de la phase de test.
     */
    private int testLimit;

    /**
     *  Pool de production.
     *  Nombre maximum de positions prises simultanément en production.
     */
    private int prodPool;

    /**
     *  Balance de la production.
     *  Part accordée au taux moyen brut lors d'une évaluation de performance en comparaison du taux moyen journalier,
     *  exprimé en base 1.
     */
    private double prodBalancing;

    /**
     *  Risques acceptables par la production.
     *  Ratio minimum accepté entre le nombre de succès et le nombre d'échecs pour une prise de position en production.
     */
    private double prodRisk;

    /**
     *  Niveau de sécurité de la production.
     *  Pourcentage de perte toléré, en production, relativement au pourcentage espéré.
     */
    private double prodSecurity;

    /**
     *  Finesse de la production.
     *  Nombre maximum de positions évaluées et/ou testées pour passage en production à chaque exécution de la tache de
     *  consolidation.
     */
    private int prodGrain;

    /**
     *  Durée de consolidation de la production.
     *  Durée maximale évaluée à chaque exécution de la tache de consolidation pour la sélection des positions de production.
     *
     */
    private long prodPeriod;

    /**
     *  Pourcentage ciblé en production.
     *  Pourcentage journalier ciblé pour la sélection d'une liste de stratégies évaluables en production.
     */
    private double prodPercent;

    /**
     *  Pourcentage de frais retenus sur les bénéfices.
     */
    private double feeSpread;

    /**
     *  Nombre de confirmations requises.
     *  Nombre minimum de confirmations requises pour la sélection d'une stratégie de production.
     */
    private int prodConfirmations;

    /**
     *  Taille des placement.
     *  Taille unitaire des placements réalisés en production ($).
     */
    private int prodSize;


    /**
     *  Constructeur.
     */
    public WalletEntity() {
        this.start = new java.util.Date().getTime();
        this.timeout = 4 * 60 * 60 * 1000;
        this.retentionDelay = 36 * 60 * 60 * 1000;
        this.retentionProfit = -1;
        this.retentionConfirmations = 16;
        this.barrierEntry = 75;
        this.barrierExit = 75;
        this.sampleSize = 1024;
        this.mixerPonderationsGrain = 10;
        this.mixerDeepth = 16;
        this.simulationPool = 4096 * 32;
        this.simulationGrain = 320;
        this.testPool = 4096 * 8;
        this.testGrain = 4096 * 4;
        this.testPeriod = 36 * 60 * 60 * 1000;
        this.testLimit = 16;
        this.prodPool = 4;
        this.prodGrain = 2048;
        this.prodPeriod = 36 * 60 * 60 * 1000;
        this.prodPercent = 2;
        this.prodBalancing = 3;
        this.prodRisk = 3;
        this.prodSecurity = 200;
        this.prodConfirmations = 8;
        this.prodSize = 1000;
        this.feeSpread = 0.34;
    }

    /**
     *  Retourne l'identifiant.
     *  @return Identifiant.
     */
    public UUID getId() {
        return this.id;
    }

    /**
     *  Retourne la date de début.
     *  @return Date de début.
     */
    public long getStart() {
        return this.start;
    }

    /**
     *  Retourne la durée de rétention.
     *  @return Durée de rétention.
     */
    public long getRetentionDelay() {
        return this.retentionDelay;
    }

    /**
     *  Retourne le seuil de rétention.
     *  @return Seuil de rétention.
     */
    public double getRetentionProfit() {
        return this.retentionProfit;
    }

    /**
     *  Retourne le nombre de confirmations maximal retenu.
     *  @return Nombre maximal de confirmations.
     */
    public int getRetentionConfirmations() {
        return this.retentionConfirmations;
    }

    /**
     *  Retourne la durée avant expiration d'une position.
     *  @return Durée avant expiration.
     */
    public long getTimeout() {
        return this.timeout;
    }

    /**
     *  Retourne la barrière à l'entrée.
     *  @return Barrière à l'entrée.
     */
    public int getBarrierEntry() {
        return this.barrierEntry;
    }

    /**
     *  Retourne la barrière à la sortie.
     *  @return Barrière à la sortie.
     */
    public int getBarrierExit() {
        return this.barrierExit;
    }

    /**
     *  Retourne la taille des échantillons livrés au mixer.
     *  @return Taille des échantillons.
     */
    public int getSampleSize() {
        return this.sampleSize;
    }

    /**
     *  Retourne la finesse des pondérations.
     *  @return Finesse des pondérations.
     */
    public int getMixerPonderationsGrain() {
        return this.mixerPonderationsGrain;
    }

    /**
     *  Retourne le niveau de profondeur du mixeur.
     *  @return Niveau de profondeur.
     */
    public int getMixerDeepth() {
        return this.mixerDeepth;
    }

    /**
     *  Retourne la taille du pool de simulation.
     *  @return Taille du pool de simulation.
     */
    public int getSimulationPool() {
        return this.simulationPool;
    }

    /**
     *  Retourne le niveau de finesse des simulations.
     *  @return Niveau de finesse des simulations.
     */
    public int getSimulationGrain() {
        return this.simulationGrain;
    }

    /**
     *  Retourne la taille du pool de test.
     *  @return Taille du pool de test.
     */
    public int getTestPool() {
        return this.testPool;
    }

    /**
     *  Retourne la finesse des tests.
     *  @return Finesse des test.
     */
    public int getTestGrain() {
        return this.testGrain;
    }

    /**
     *  Retourne la période de test.
     *  @return Période de test.
     */
    public long getTestPeriod() {
        return this.testPeriod;
    }

    /**
     *  Retourne le nombre maximum de confirmations évaluées.
     *  @return Nombre maximum de confirmations.
     */
    public int getTestLimit() {
        return this.testLimit;
    }

    /**
     *  Retourne la taille du pool de production.
     *  @return Taille du pool de production.
     */
    public int getProdPool() {
        return this.prodPool;
    }

    /**
     *  Retourne la finesse des positions de production.
     *  @return Niveau de finesse de production.
     */
    public int getProdGrain() {
        return this.prodGrain;
    }

    /**
     *  Retourne la période de production.
     *  @return Période de production.
     */
    public long getProdPeriod() {
        return this.prodPeriod;
    }

    /**
     *  Retourne le pourcentage nécessaire pour la prise de position en production.
     *  @return Pourcentage nécessaire.
     */
    public double getProdPercent() {
        return this.prodPercent;
    }

    /**
     *  Retourne la part accordée au taux relatif face au taux journalier moyen lors de l'évaluation.
     *  @return Part accordée.
     */
    public double getProdBalancing() {
        return this.prodBalancing;
    }

    /**
     *  Retourne le seuil de risque maximum accepté pour un placement en production.
     *  @return Seuil de risque toléré.
     */
    public double getProdRisk() {
        return this.prodRisk;
    }

    /**
     *  Retourne le seuil de sécurité en production.
     *  @return Sueil de sécurité.
     */
    public double getProdSecurity() {
        return this.prodSecurity;
    }

    /**
     *  Retourne le nombre de confirmations nécessaires pour la production.
     *  @return Nombre de confirmations.
     */
    public int getProdConfirmations() {
        return this.prodConfirmations;
    }

    /**
     *  Retourne la taille des placements de production.
     *  @return Taille des placements.
     */
    public int getProdSize() {
        return this.prodSize;
    }

    /**
     *  Retourne les frais sur spread.
     *  @return Frais sur spread.
     */
    public double getFeeSpread() {
        return this.feeSpread;
    }
}
