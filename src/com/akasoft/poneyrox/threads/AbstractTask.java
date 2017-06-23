package com.akasoft.poneyrox.threads;

import com.akasoft.poneyrox.components.ManagerComponent;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.AbstractException;

/**
 *  Tache.
 *  Tache planifiée exécutée à intervalles réguliers dans le cadre de l'application.
 */
public abstract class AbstractTask implements Runnable {
    /**
     *  Gestionnaire des taches.
     */
    private ManagerComponent manager;

    /**
     *  Constructeur.
     *  @param manager Gestionnaire des taches.
     */
    public AbstractTask(ManagerComponent manager) {
        this.manager = manager;
    }

    /**
     *  Retourne le gestionnaire des taches.
     *  @return Gestionnaire des taches.
     */
    public ManagerComponent getManager() {
        return this.manager;
    }

    /**
     *  Exécution de la tache.
     */
    @Override
    public void run() {
        long start = new java.util.Date().getTime();
        try {
            this.execute();
            long end = new java.util.Date().getTime();

            if ((end - start) > 3000) {
                System.out.println("    Task " + this.getClass().getSimpleName() + " took " + (end - start) + "ms");
            }
        } catch (AbstractException cause) {
            cause.printStackTrace();
        }
    }

    /**
     *  Retourne le portefeuille.
     *  @return Portefeuille.
     */
    public WalletEntity getWallet() {
        return this.getManager().getWallet();
    }

    /**
     *  Exécution spécifique.
     */
    protected abstract void execute() throws AbstractException;
}
