package com.akasoft.poneyrox.core.mixins.leads;

import com.akasoft.poneyrox.core.mixins.artifacts.AbstractArtifact;
import com.akasoft.poneyrox.core.strategies.categories.AbstractStrategy;
import com.akasoft.poneyrox.entities.positions.WalletEntity;
import com.akasoft.poneyrox.exceptions.InnerException;

/**
 *  Interface des classes de génération de pistes.
 *  @param <TArtifact> Type d'artefact employé.
 *  @param <TLead> Type de piste suivie.
 */
public interface AbstractLeadSupplierITF<TArtifact extends AbstractArtifact, TLead extends AbstractLead> {
    /**
     *  Génération d'une piste.
     *  @param ponderation Pondération appliquée.
     *  @param strategies Stratégies constitutives.
     *  @param mode Mode.
     *  @return Piste générée.
     */
    TLead supply(double[] ponderation, TArtifact[] strategies, boolean mode) throws InnerException;
}
