package com.akasoft.poneyrox.core.mixins.artifacts;

/**
 *  Générateur d'artefacts d'entrée.
 */
public class EntryArtifactSupplier implements AbstractArtifactSupplierITF<EntryArtifact> {
    /**
     *  Retourne un tableau d'artefacts de taille fixe.
     *  @param size Taille du tableau.
     *  @return Tableau.
     */
    @Override
    public EntryArtifact[] supply(int size) {
        return new EntryArtifact[size];
    }
}
