package com.akasoft.poneyrox.core.mixins.leads;

import com.akasoft.poneyrox.core.mixins.artifacts.ExitArtifact;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Génération d'une piste de sortie.
 */
public class ExitLeadSupplier implements AbstractLeadSupplierITF<ExitArtifact, ExitLead> {
    /**
     *  Génération d'une piste.
     *  @param ponderation Pondération appliquée.
     *  @param strategies Stratégies constitutives.
     *  @param mode Mode.
     *  @return Piste générée.
     */
    @Override
    public ExitLead supply(double[] ponderation, ExitArtifact[] strategies, boolean mode) throws InnerException {
        return new ExitLead(ponderation, strategies, mode);
    }
}
