package com.akasoft.poneyrox.dto;


import com.akasoft.poneyrox.entities.markets.TimelineEntity;
import com.akasoft.poneyrox.entities.positions.MixinEntity;
import com.akasoft.poneyrox.exceptions.InnerException;
import org.hibernate.transform.ResultTransformer;

import java.util.List;

/**
 *  Classe de transformation d'une requete brute en liste de performances.
 */
public class PerformanceTransformer implements ResultTransformer {
    /**
     *  Réalise la transformation d'un résultat brut.
     *  @param objects Liste des valeurs.
     *  @param strings Liste de libellés.
     *  @return Objet correspondant.
     */
    @Override
    public Object transformTuple(Object[] objects, String[] strings) {
        /* Création du résultat */
        PerformanceDTO result = new PerformanceDTO();

        /* Parcours des objets */
        for (int i = 0; i < strings.length; i++) {
            switch (strings[i]) {
                case "rawProfit":
                    result.setRawProfit((Double) objects[i]);
                    break;
                case "relativeProfit":
                    result.setRelativeProfit((Double) objects[i]);
                    break;
                case "dailyProfit":
                    result.setDailyProfit((Double) objects[i]);
                    break;
                case "confirmations":
                    result.setConfirmations((Integer) objects[i]);
                    break;
                case "wins":
                    result.setWins((Integer) objects[i]);
                    break;
                case "loss":
                    result.setLoss((Integer) objects[i]);
                    break;
                case "timeline":
                    result.setTimeline((TimelineEntity) objects[i]);
                    break;
                case "smooth":
                    result.setSmooth((Integer) objects[i]);
                    break;
                case "mode":
                    result.setMode((Boolean) objects[i]);
                    break;
                case "entryMix":
                    result.setEntryMix((MixinEntity) objects[i]);
                    break;
                case "exitMix":
                    result.setExitMix((MixinEntity) objects[i]);
                    break;
                default:
                    throw new RuntimeException("Failed to transform tupple for performance DTO");
            }
        }

        /* Renvoi */
        return result;
    }

    /**
     *  Retourne une liste transformée.
     *  @param list Liste traitée.
     *  @return Liste transformée.
     */
    @Override
    public List transformList(List list) {
        return list;
    }
}
