package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.core.mixins.batch.EntryBatch;
import com.akasoft.poneyrox.core.mixins.batch.ExitBatch;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.core.strategies.interfaces.*;
import com.akasoft.poneyrox.core.strategies.parameters.AbstractParameter;
import com.akasoft.poneyrox.core.time.cells.AbstractCell;
import com.akasoft.poneyrox.core.time.curves.AbstractCurve;
import com.akasoft.poneyrox.exceptions.AbstractException;
import com.akasoft.poneyrox.exceptions.InnerException;

import java.util.*;
import java.util.stream.Collectors;

/**
 *  Tache stratégique.
 *  Tache dédiée à l'exécution d'une stratégie sur les lignes temporelles générées.
 */
public class StrategyTask<TStrategy extends AbstractStrategy> extends StrategyTaskWrapper {
    /**
     *  Type.
     */
    private Class<TStrategy> type;

    /**
     *  Liste des instances.
     */
    private List<TStrategy> instances;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     *  @param type Type d'instance générée.
     *  @throws InnerException En cas d'erreur lors de la génération.
     */
    public StrategyTask(ManagerComponent manager, Class<TStrategy> type) throws InnerException {
        super(manager);
        this.type = type;
        this.instances = this.generate();
    }

    /**
     *  Retourne le type de stratégie.
     *  @return Type de stratégie.
     */
    public Class<TStrategy> getType() {
        return this.type;
    }

    /**
     *  Exécution.
     *  @throws AbstractException En cas d'erreur lors de la résolution.
     */
    @Override
    protected void execute() throws AbstractException {
        /* Vérification du buffer */
        if (super.getBufferSize() > 0) {
            /* Parcours des courbes placées dans le buffer.
            *  La liste des courbes est copiée dans un instance séparée afin d'éviter les conflits entre processus. */
            for (AbstractCurve curve : super.getBuffer().stream().collect(Collectors.toList())) {
                /* Positionnement alétoire des instances */
                Collections.shuffle(this.instances);

                /* Création des listes de résultats, à savoir :
                *       Liste des artefacts exploitables pour une prise de position
                *       Liste des artefacts exploitables pour une sortie de position */
                ArrayList<EntryArtifact> entries = new ArrayList<>();
                ArrayList<ExitArtifact> exits = new ArrayList<>();

                /* Extraction des cellules disponibles */
                List<AbstractCell> builds = curve.getBuilds();

                /* Parcours des stratégies disponibles */
                boolean exit = false;
                for (int i = 0; i < this.instances.size() && !exit; i++) {
                    /* Récupération de la stratégie */
                    AbstractStrategy strategy = this.instances.get(i);

                    /* Evaluation de la stratégie */
                    strategy.observe(curve, builds);

                    /* Mise en liaison avec la courbe.
                    *  Seules les tratégies pertinentes sont retenues. La variable "exit" permet de savoir si suffisamment de stratégies sont placées
                    *  dans l'échantillon afin de quitter la boucle. */
                    exit = true;
                    if (strategy.isPertinent()) {
                        /* Gestion des stratégies élligibles en entrée */
                        if (entries.size() < super.getWallet().getSampleSize()) {
                            exit = false;
                            if ((strategy instanceof EnterLongITF || strategy instanceof EnterShortITF)) {
                                EntryArtifact artifact = new EntryArtifact(strategy.clone());
                                entries.add(artifact);
                            }
                        }

                        /* Gestion des stratégies élligibles en sortie */
                        if (exits.size() <= super.getWallet().getSampleSize()) {
                            exit = false;
                            if ((strategy instanceof ExitLongITF || strategy instanceof ExitShortITF)) {
                                ExitArtifact artifact = new ExitArtifact(strategy.clone());
                                exits.add(artifact);
                            }
                        }
                    } else {
                        exit = false;
                    }
                }

                /* Diffusion des stratégies d'entrée. */
                if (entries.size() > 0) {
                    /* Création du lot */
                    EntryBatch entryBatch = new EntryBatch(this.type, curve, entries);

                    /* Diffusion */
                    super.getManager().diffuseEntryBatch(entryBatch);
                }

                /* Diffusion des stratégies de sortie */
                if (exits.size() > 0) {
                    /* Création du lot */
                    ExitBatch exitBatch = new ExitBatch(this.type, curve, exits);

                    /* Diffusion */
                    super.getManager().diffuseExitBatch(exitBatch);
                }
            }
        }

        /* Nettoyage du tampon */
        super.clearBuffer();
    }

    /**
     *  Génération des instances.
     *  Procède à la génération de l'ensemble des instances stratégiques obtensibles pour le
     *  type ciblé.
     *  @return Liste des instances stratégiques.
     *  @throws InnerException En cas d'erreur lors de la génération.
     */
    private List<TStrategy> generate() throws InnerException {
        try {
            /* Création d'une instance vierge */
            TStrategy instance = this.type.newInstance();

            /* Récupération des paramètres */
            List<AbstractParameter> parameters = instance.emitParameters();

            /* Instanciation récursive */
            return this.generateRecursive(parameters, 0);
        } catch (Exception cause) {
            throw new InnerException(cause, "Failed to generate strategy instances");
        }
    }

    /**
     *  Génération récursive.
     *  Fonction de génération récursive d'une liste d'instances stratégiques.
     *  @param list Liste traitée.
     *  @param idx Index de génération.
     *  @return En cas d'erreur lors du traitement.
     *  @throws Exception En cas d'erreur lors du traitement.
     */
    private List<TStrategy> generateRecursive(List<AbstractParameter> list, int idx) throws Exception {
        List<TStrategy> result = new ArrayList<>();
        for (Object value : list.get(idx).getInstances()) {
            if (idx == list.size() - 1) {
                /* Dernier élément */
                TStrategy instance = this.type.newInstance();
                instance.consumeParameter(list.get(idx).getKey(), value);
                result.add(instance);
            } else {
                /* Elément intermédiaire */
                List<TStrategy> sub = this.generateRecursive(list, idx + 1);
                for (TStrategy item : sub) {
                    item.consumeParameter(list.get(idx).getKey(), value);
                }
                result.addAll(sub);
            }
        }
        return result;
    }
}
