package com.akasoft.poneyrox.core.mixins.artifacts;

/**
 *  Interface des classes de génération d'artefacts.
 *  @param <TArtifact> Type d'artefact généré.
 */
public interface AbstractArtifactSupplierITF<TArtifact extends AbstractArtifact> {
    /**
     *  Génère un tableau d'artefacts de taille fixe.
     *  @param size Taille du tableau.
     *  @return Tableau d'artefacts.
     */
    TArtifact[] supply(int size);
}
