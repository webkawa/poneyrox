package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.artifacts.*;
import com.akasoft.poneyrox.core.mixins.batch.AbstractBatch;
import com.akasoft.poneyrox.core.mixins.batch.EntryBatch;
import com.akasoft.poneyrox.core.mixins.batch.ExitBatch;
import com.akasoft.poneyrox.core.mixins.leads.*;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.core.time.curves.SmoothCurve;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  Tache de mixage.
 *  Tache chargé du mixage d'une liste consolidée de stratégies.
 */
public class MixerTask extends MixerTaskWrapper {
    /**
     *  Générateur de pistes d'entrées.
     */
    private static final EntryLeadSupplier ENTRY_LEAD_SUPPLIER = new EntryLeadSupplier();

    /**
     *  Générateur de pistes de sortie.
     */
    private static final ExitLeadSupplier EXIT_LEAD_SUPPLIER = new ExitLeadSupplier();

    /**
     *  Générateur d'artefacts d'entrée.
     */
    private static final EntryArtifactSupplier ENTRY_ARTIFACT_SUPPLIER = new EntryArtifactSupplier();

    /**
     *  Générateur d'artefacts de sortie.
     */
    private static final ExitArtifactSupplier EXIT_ARTIFACT_SUPPLIER = new ExitArtifactSupplier();

    /**
     *  Liste des combinaisons.
     */
    private List<double[]>[] ponderations;

    /**
     *  Nombre de validations nécessaires en entrée.
     *  Carte descriptive du nombre minimum de validations nécessaires, en entrée, pour la validation
     *  d'une position.
     */
    private Map<double[], Integer> entryValidations;

    /**
     *  Nombre de validations nécessaires en sortie.
     */
    private Map<double[], Integer> exitValidations;

    /**
     *  Distribution d'entrée.
     *  Tableau représentant, pour chaque pondération à index identique, le nombre d'éléments contenus à
     *  chaque niveau et pondérés - dans un tableau dont l'élément 0 correspond à un validation dès 1,
     *  l'élément 1 dès 2 (etc.) et le dernier élément le nombre total d'éléments.
     */
    private List<double[]> entryDistributions;

    /**
     *  Distribution de sortie.
     */
    private List<double[]> exitDistribution;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public MixerTask(ManagerComponent manager) {
        super(manager);
        int size = this.getManager().getStrategies().size();

        /* Paramètres */
        this.ponderations = new ArrayList[size];
        this.entryValidations = new HashMap<>();
        this.exitValidations = new HashMap<>();
        this.entryDistributions = new ArrayList<>();
        this.exitDistribution = new ArrayList<>();

        /* Génération des pondérations et distributions */
        for (int i = 1; i <= this.getManager().getStrategies().size(); i++) {
            /* Génération des pondérations */
            List<double[]> generated = this.generate(i);
            this.ponderations[i - 1] = generated;

            /* Initialisation de la distribution */
            int max = generated.get(0).length;
            double[] entryDistribution = new double[max + 1];
            double[] exitDistribution = new double[max + 1];

            /* Parcours des possibilités */
            for (int j = 0; j < generated.size(); j++) {
                /* Récupération des valeurs */
                List<Double> values = new ArrayList<>();
                for (int k = 0; k < generated.get(j).length; k++) {
                    values.add(generated.get(j)[k]);
                }
                Collections.sort(values);

                /* Calcul des nombres minimum de validation */
                boolean exitEntry = false;
                boolean exitExit = false;
                double current = 0;
                int entryValidations = 0;
                int exitValidations = 0;
                double entryAdd = 0;
                double exitAdd = 0;
                for (int k = values.size() - 1; k >= 0 && !exitEntry && !exitExit; k--) {
                    current += values.get(k);
                    if (!exitEntry && current > super.getWallet().getBarrierEntry()) {
                        entryValidations = values.size() - k;
                        entryAdd = Math.pow(2, entryValidations);
                        exitEntry = true;
                    }
                    if (!exitExit && current > super.getWallet().getBarrierExit()) {
                        exitValidations = values.size() - k;
                        exitAdd = Math.pow(2, exitValidations);
                        exitExit = true;
                    }
                }

                /* Complétion des validations */
                this.entryValidations.put(generated.get(j), entryValidations);
                this.exitValidations.put(generated.get(j), exitValidations);

                /* Complétion de la distribution */
                entryDistribution[entryValidations - 1] += entryAdd;
                entryDistribution[entryDistribution.length - 1] += entryAdd;
                exitDistribution[exitValidations - 1] += exitAdd;
                exitDistribution[exitDistribution.length - 1] += exitAdd;
            }

            /* Mise à jour de la distribution */
            this.entryDistributions.add(entryDistribution);
            this.exitDistribution.add(exitDistribution);
        }
    }

    /**
     *  Exécution.
     *  @throws AbstractException En cas d'erreur lors du traitement.
     */
    @Override
    protected void execute() throws AbstractException {
        /* Vérification du tampon d'entrée */
        if (this.getEntryBufferSize() > 0) {
            /* Réalisation du groupement des stratégies d'entrée */
            int size = this.getManager().getEntryStrategies().size();
            List<List<EntryBatch>> entries = this.group(this.getEntryBuffer(), size);

            /* Création des pistes */
            if (entries.size() > 0) {
                /* Récupération de la courbe */
                AbstractCurve curve = entries.get(0).get(0).getCurve();

                /* Création de la liste de pistes */
                List<EntryLead> leads = this.executeFor(
                        MixerTask.ENTRY_LEAD_SUPPLIER,
                        MixerTask.ENTRY_ARTIFACT_SUPPLIER,
                        true,
                        curve.getOwner().getCurrent(),
                        super.getWallet().getBarrierEntry(),
                        size,
                        entries);

                /* Diffusions */
                super.getManager().diffuseEntryLeads(curve, leads);

                /* Nettoyage */
                for (EntryBatch entry : entries.stream().flatMap(e -> e.stream()).collect(Collectors.toList())) {
                    super.removeEntryBuffer(entry);
                }
            }
        }

        /* Vérification du tampon de sortie */
        if (this.getExitBufferSize() > 0) {
            /* Réalisation du groupement des stratégies de sortie */
            int size = this.getManager().getExitStrategies().size();
            List<List<ExitBatch>> exits = this.group(this.getExitBuffer(), size);

            /* Création des pistes */
            if (exits.size() > 0) {
                /* Récupération de la courbe */
                AbstractCurve curve = exits.get(0).get(0).getCurve();

                /* Création de la liste de pistes */
                List<ExitLead> leads = this.executeFor(
                        MixerTask.EXIT_LEAD_SUPPLIER,
                        MixerTask.EXIT_ARTIFACT_SUPPLIER,
                        false,
                        curve.getOwner().getCurrent(),
                        super.getWallet().getBarrierExit(),
                        size,
                        exits);

                /* Diffusion */
                super.getManager().diffuseExitLeads(curve, leads);

                /* Nettoyage */
                for (ExitBatch exit : exits.stream().flatMap(e -> e.stream()).collect(Collectors.toList())) {
                    super.removeExitBuffer(exit);
                }
            }
        }
    }

    /**
     *  Exécution pour un type de position donné.
     *  @param leadSupplier Générateur de pistes.
     *  @param artifactSupplier Générateur d'artefacts.
     *  @param direction Direction (true : entrée, false : sortie).
     *  @param rate Taux à l'entrée.
     *  @param barrier Barrière à l'entrée.
     *  @param size Nombre de lots.
     *  @param source Liste de lots source.
     *  @param <TLeadSupplier> Type du générateur de pistes.
     *  @param <TArtifactSupplier> Type du générateur d'artefacts.
     *  @param <TArtifact> Type d'artefact généré.
     *  @param <TBatch> Type de lot généré.
     *  @param <TLead> Type de piste généré.
     *  @return Liste des pistes générées.
     *  @throws AbstractException En cas d'erreur lors du traitement.
     */
    private <TLeadSupplier extends AbstractLeadSupplierITF<TArtifact, TLead>, TArtifactSupplier extends AbstractArtifactSupplierITF<TArtifact>, TArtifact extends AbstractArtifact, TBatch extends AbstractBatch, TLead extends AbstractLead> List<TLead> executeFor(
            TLeadSupplier leadSupplier,
            TArtifactSupplier artifactSupplier,
            boolean direction,
            RateEntity rate,
            double barrier,
            int size,
            List<List<TBatch>> source) throws AbstractException {
        /* Création du résultat */
        List<TLead> leads = new ArrayList<>();

        /* Création des pistes */
        if (source.size() > 0) {
            /* Parcours des combinaisons de lots */
            for (List<TBatch> batches : source) {
                /* Extraction des stratégies valables en mode long */
                List<List<TArtifact>> artifacts = new ArrayList<>();
                for (int i = 0; i < batches.size(); i++) {
                    List<TArtifact> prefilter = batches.get(i).getLong();
                    artifacts.add(prefilter);
                }
                List<TLead> adds = this.mix(leadSupplier, artifactSupplier, rate, artifacts, direction, size, true);
                leads.addAll(adds);

                /* Extraction des stratégies valables en mode court */
                artifacts.clear();
                for (int i = 0; i < batches.size(); i++) {
                    List<TArtifact> prefilter = batches.get(i).getShort();
                    artifacts.add(prefilter);
                }
                adds = this.mix(leadSupplier, artifactSupplier, rate, artifacts, direction, size, false);
                leads.addAll(adds);
            }
        }

        /* Renvoi */
        return leads;
    }

    /**
     *  Réalise le mix d'un lot de stratégies et de pondérations.
     *  @param leadSupplier Générateur de pistes.
     *  @param artifactSupplier Générateur d'artefacts.
     *  @param rate Taux à l'entrée.
     *  @param batches Liste des lots.
     *  @param direction Direction.
     *  @param size Nombre de stratégies.
     *  @param mode Mode (long ou court).
     *  @param <TArtifact> Type d'artefact généré.
     *  @param <TLead> Type de piste généré.
     *  @return Liste des pistes générées.
     *  @throws InnerException En cas d'erreur interne.
     */
    private <TArtifact extends AbstractArtifact, TLead extends AbstractLead> List<TLead> mix(
            AbstractLeadSupplierITF<TArtifact, TLead> leadSupplier,
            AbstractArtifactSupplierITF<TArtifact> artifactSupplier,
            RateEntity rate,
            List<List<TArtifact>> batches,
            boolean direction,
            int size,
            boolean mode) throws InnerException {
        /* Création du résultat */
        List<TLead> result = new ArrayList<>();

        /* Pré-mix.
        *  Opération consistant aux mixages de différentes stratégies disponibles sans prise en compte de
        *  la pondération. */
        List<TArtifact[]> premix = this.mixRecursive(artifactSupplier, batches, size, new ArrayList<>(), 0);

        /* Post-mix.
        *  Application des pondérations disponibles sur le pré-mix calculé précédemment. Le nombre d'entrées affectées à
         * chaque pondération dépend du nombre minimum de validations nécessaires. Les pistes nécessitant le plus de
         * validations sont sur-représentées en suivant une fonction exponentielle. */

        /* Récupération de la barrière et de la population totale pour les pondérations en cours */
        int barrier = direction ? super.getWallet().getBarrierEntry() : super.getWallet().getBarrierExit();
        double population = 0;
        if (direction) {
            population = this.entryDistributions.get(size - 1)[size];
        } else {
            population = this.exitDistribution.get(size - 1)[size];
        }

        /* Création du conteneur.
           Les pistes créées sont référencées par nombre de validations minimales, dans un tableau débutant par le niveau 1. */
        List<TLead>[] container = new ArrayList[size];
        for (int i = 0; i < container.length; i++) {
            container[i] = new ArrayList<>();
        }

        /* Parcours des pondérations */
        Collections.shuffle(this.ponderations[size - 1]);
        for (int i = 0; i < this.ponderations[size - 1].size(); i++) {
            /* Récupération de la pondération */
            double[] ponderation = this.ponderations[size - 1].get(i);

            /* Récupération des éléments */
            double target = 0;
            int validations = 0;
            if (direction) {
                validations = this.entryValidations.get(ponderation);
                target = this.entryDistributions.get(size - 1)[validations - 1];
            } else {
                validations = this.exitValidations.get(ponderation);
                target = this.exitDistribution.get(size - 1)[validations - 1];

            }

            /* Calcul du nombre d'entrées à crééer pour le niveau */
            double objective = premix.size() * (target / population);

            /* Vérification du niveau atteind */
            if (container[validations - 1].size() < objective) {
                /* Mélange des stratégies */
                Collections.shuffle(premix);

                /* Parcours */
                boolean exit = false;
                for (int j = 0; j < premix.size() && !exit; j++) {
                    /* Création de la piste */
                    TLead add = leadSupplier.supply(ponderation, premix.get(i), mode);
                    add.score(mode, rate);

                    /* Vérification du score */
                    if (direction) {
                        if (mode) {
                            exit = add.getLongScore() > barrier;
                        } else {
                            exit = add.getShortScore() > barrier;
                        }
                    } else {
                        if (mode) {
                            exit = add.getLongScore() < barrier;
                        } else {
                            exit = add.getShortScore() < barrier;
                        }
                    }

                    /* Injection si pertinent */
                    if (exit) {
                        container[validations - 1].add(add);
                    }
                }
            }
        }

        /* Calcul du pourcentage le plus faible atteind */
        double lowest = 1;
        for (int j = 0; j < container.length; j++) {
            /* Récupération du nombre maximum d'entrées atteignable */
            double maximum = direction ? this.entryDistributions.get(size - 1)[j] : this.exitDistribution.get(size - 1)[j];

            /* Mise à jour du minimum */
            if (maximum != 0) {
                double percent = container[j].size() / maximum;
                if (lowest > percent) {
                    lowest = percent;
                }
            }
        }

        /* Réduction des listes en excedent */
        for (int j = 0; j < container.length; j++) {
                /* Récupération du nombre maximum d'entrées atteignable */
            double maximum = direction ? this.entryDistributions.get(size - 1)[j] : this.exitDistribution.get(size - 1)[j];

            if (maximum != 0) {
                /* Calcul du pourcentage atteint */
                double done = container[j].size() / maximum;

                /* Calcul du pourcentage à conserver */
                int skip = (int) Math.round((done - lowest) * maximum);

                /* Filtrage */
                List<TLead> buffer = container[j].subList(0, container[j].size() - skip);
                result.addAll(buffer);
            }
        }

        /* Renvoi */
        Collections.shuffle(result);
        return result;
    }

    /**
     *  Mixage récursif d'une liste de lots.
     *  @param supplier Générateur de pistes.
     *  @param batches Lots mixés.
     *  @param size Taille des lots.
     *  @param state Etat récursif.
     *  @param idx Index.
     *  @param <TArtifact> Type de lot consommé.
     *  @return Lots mixés.
     */
    private <TSupplier extends AbstractArtifactSupplierITF<TArtifact>, TArtifact extends AbstractArtifact> List<TArtifact[]> mixRecursive(
            TSupplier supplier,
            List<List<TArtifact>> batches,
            int size,
            List<TArtifact[]> state,
            int idx) {
        /* Création du tampon */
        List<TArtifact[]> buffer = new ArrayList<>();

        if (state.size() == 0) {
            /* Création des entrées initiales */
            for (TArtifact initial : batches.get(0).subList(0, super.getWallet().getMixerDeepth())) {
                TArtifact[] start = supplier.supply(size);
                start[0] = initial;
                buffer.add(start);
            }
        } else {
            /* Complétion */
            for (TArtifact[] current : state) {
                /* Extraction et préparation des valeurs disponibles */
                List<TArtifact> disponible = batches.get(idx);
                Collections.shuffle(disponible);
                disponible = disponible.subList(0, (super.getWallet().getMixerDeepth() / (int) Math.pow(2, idx)));

                /* Complétion du tableau */
                for (TArtifact next : disponible) {
                    TArtifact[] add = supplier.supply(size);
                    for (int i = 0; i < idx; i++) {
                        add[i] = current[i];
                    }
                    add[idx] = next;
                    buffer.add(add);
                }
            }
        }

        /* Appel récursif */
        if (idx < size - 1) {
            buffer = this.mixRecursive(supplier, batches, size, buffer, idx + 1);
        }

        /* Renvoi */
        return buffer;
    }

    /**
     *  Extrait les groupes de lots prets pour l'évaluation.
     *  @param treatment Liste initiale.
     *  @param size Taille recherché.
     *  @param <TBatch> Type de lot traité.
     *  @return Liste des groupes.
     *  @throws InnerException En cas d'erreur durant le groupement.
     */
    private <TBatch extends AbstractBatch> List<List<TBatch>> group(Set<TBatch> treatment, int size) throws InnerException {
        /* Création de la liste des groupes */
        List<List<TBatch>> groups = new ArrayList<>();

        /* Parcours */
        for (TBatch source : treatment) {
            /* Extraction de la sélection éligible */
            List<TBatch> selection = treatment.stream().filter(comparison -> {
                boolean take = true;
                take &= source.getCurve().equals(comparison.getCurve());
                return take;
            }).collect(Collectors.toList());

            /* Ajout aux entrées à traiter */
            if (selection.size() == size) {
                groups.add(selection);
            } else if (selection.size() > size) {
                throw new InnerException("Too much batches found during grouping");
            }
        }

        /* Renvoi */
        return groups;
    }

    /**
     *  Génère un lot de combinaisons exploitables.
     *  @param target Taille ciblée.
     *  @return Combinaisons exploitables.
     */
    private List<double[]> generate(int target) {
        return this.generateRecursive(target, new ArrayList<>(), 0).stream().filter(e -> {
            int i = 0;
            for (int j = 0; j < e.length; j++) {
                i += e[j];
            }
            return i == 100;
        }).collect(Collectors.toList());
    }

    /**
     *  Génération récursive.
     *  @param target Taille ciblée.
     *  @param state Etat courant.
     *  @param idx Index en cours.
     *  @return Combinaisons générées.
     */
    private List<double[]> generateRecursive(int target, List<double[]> state, int idx) {
        /* Calcul de l'espacement */
        int space = 100 / super.getWallet().getMixerPonderationsGrain();

        /* Création du résultat */
        List<double[]> buffer = new ArrayList<>();
        for (int i = 0; i <= space; i++) {
            if (state.size() == 0) {
                /* Création des tableaux */
                double[] add = new double[target];
                add[0] = i * space;
                buffer.add(add);
            } else {
                for (double[] entry : state) {
                    /* Parcours des entrées pré-existantes */
                    double[] add = new double[target];
                    for (int j = 0; j < idx; j++) {
                        add[j] = entry[j];
                    }

                    /* Complétion */
                    add[idx] = i * space;

                    /* Ajout */
                    buffer.add(add);
                }
            }
        }

        /* Appel du niveau suivant */
        if (idx < target - 1) {
            buffer = this.generateRecursive(target, buffer, idx + 1);
        }

        /* Renvoi */
        return buffer;
    }
}