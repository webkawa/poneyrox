package com.akasoft.poneyrox.core.mixins.leads;

import com.akasoft.poneyrox.core.mixins.artifacts.EntryArtifact;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Générateur de piste d'entrées.
 */
public class EntryLeadSupplier implements AbstractLeadSupplierITF<EntryArtifact, EntryLead> {
    /**
     *  Génération d'une piste.
     *  @param ponderation Pondération appliquée.
     *  @param strategies Stratégies constitutives.
     *  @param mode Mode.
     *  @return Piste générée.
     */
    @Override
    public EntryLead supply(double[] ponderation, EntryArtifact[] strategies, boolean mode) throws InnerException {
        return new EntryLead(ponderation, strategies, mode);
    }
}
