package com.akasoft.poneyrox.core.mixins.artifacts;

/**
 *  Générateur d'artefacts de sortie.
 */
public class ExitArtifactSupplier implements AbstractArtifactSupplierITF<ExitArtifact> {
    /**
     *  Génère un tableau d'artefacts de sortie.
     *  @param size Taille du tableau.
     *  @return Tableau.
     */
    @Override
    public ExitArtifact[] supply(int size) {
        return new ExitArtifact[size];
    }
}
