package com.akasoft.poneyrox.core.time.curves;

import com.akasoft.poneyrox.core.time.cells.RawCell;
import com.akasoft.poneyrox.entities.markets.RateEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.akasoft.poneyrox.threads.TimelineTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *  Courbe brute.
 *  Courbe non-lissée de taille fixe.
 */
public class RawCurve extends AbstractCurve<RawCell> {
    /**
     *  Taille des cellules.
     *  Exprimée en secondes.
     */
    private final int size;

    /**
     * Constructeur.
     *
     * @param owner  Tache propriétaire.
     * @param size   Taille des cellules en secondes.
     */
    public RawCurve(TimelineTask owner, int size) {
        super(owner);
        this.size = size;
    }

    /**
     *  Retourne la taille des cellules.
     *  @return Taille des cellules.
     */
    public int getSize() {
        return this.size;
    }

    /**
     *  Retourne une liste des dernières entrées limitée à une taille maximale passée en
     *  paramètres.
     *  @param size Taille recherchée.
     *  @return Liste des entrées correspondantes.
     */
    public List<RawCell> getLast(int size) {
        List<RawCell> full = this.getCells();

        if (full.size() == 0) {
            return new ArrayList<>();
        } else {
            return full.subList(Math.max(0, full.size() - size), full.size());
        }
    }

    /**
     *  Retourne le niveau de lissage.
     *  @return Niveau de lissage.
     */
    @Override
    public int getSmooth() {
        return 1;
    }

    /**
     *  Intègre une liste de taux dans la courbe.
     *  @param rates Taux intégrés.
     */
    public List<RawCell> integrate(List<RateEntity> rates) throws InnerException {
        /* Récupération de l'historique */
        List<RawCell> cells = super.getCells();
        List<RawCell> result = new ArrayList<>();

        /* Parcours des taux à intégrer */
        for (RateEntity rate : rates) {
            if (cells.size() == 0) {
                /* Traitement du premier élément */
                RawCell add = this.add(new Date().getTime(), rate);
                result.add(add);
            } else {
                /* Traitement des éléments suivants */
                RawCell last = cells.get(cells.size() - 1);
                long limit = last.getStart() + (this.size * 1000);

                if (limit > rate.getTime()) {
                    /* Intégration à la dernière cellule */
                    last.integrate(rate);

                    /* Diffusion */
                    this.getOwner().getManager().diffuseCurve(this);
                } else {
                    /* Création d'une cellule complémentaire */
                    if (cells.size() > 1) {
                        last.complete(cells.get(cells.size() - 2));
                    } else {
                        last.complete();
                    }

                    /* Gestion des cellules intermédiaires */
                    for (long buffer = limit + (this.size * 1000); rate.getTime() > buffer; buffer += (this.size * 1000)) {
                        System.out.println("COMPLETION DE CELLULES VIDES A VERIFIER !");
                        RawCell add = this.add(limit, rate);
                        result.add(add);
                    }

                    /* Insertion de la celulle finale */
                    RawCell add = this.add(limit, rate);
                    result.add(add);
                }
            }
        }

        /* Nettoyage */
        super.clearCells();

        /* Renvoi */
        return result;
    }

    /**
     *  Ajoute une cellule à la liste.
     *  @param time Date d'ajout.
     *  @param rate Taux intégré.
     *  @return Cellule ajoutée.
     */
    private RawCell add(long time, RateEntity rate) {
        /* Récupération de la liste */
        List<RawCell> cells = super.getCells();

        /* Gestion de l'ajout */
        RawCell add = new RawCell(time, rate, this);
        if (cells.size() > 0) {
            add = new RawCell(time, rate, this, cells.get(cells.size() - 1));
        }

        /* Ajout */
        super.addCell(add);
        return add;
    }
}
