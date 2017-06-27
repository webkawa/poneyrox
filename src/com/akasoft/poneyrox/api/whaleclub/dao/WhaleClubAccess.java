package com.akasoft.poneyrox.api.whaleclub.dao;

import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubMarketDTO;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubPositionDTO;
import com.akasoft.poneyrox.api.whaleclub.dto.WhaleClubRateDTO;
import com.akasoft.poneyrox.entities.markets.MarketEntity;
import com.akasoft.poneyrox.entities.positions.PositionEntity;
import com.akasoft.poneyrox.entities.positions.PositionType;
import com.akasoft.poneyrox.entities.positions.TransactionEntity;
import com.akasoft.poneyrox.exceptions.ApiException;
import com.akasoft.poneyrox.exceptions.InnerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mashape.unirest.request.BaseRequest;
import com.mashape.unirest.request.GetRequest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 *  API WhaleClub.
 *  Point d'accès à l'API WhaleClub.
 */
@Component
public class WhaleClubAccess {
    /**
     *  Point d'accès à l'API.
     */
    public static final String API_ENDPOINT = "https://api.whaleclub.co/v1/";

    /**
     *  Clef d'accès de test.
     */
    public static final String API_TOKEN_TEST = "0a2d1b25-9cfc-4a77-9746-012584d8bbb1";

    /**
     *  Clef d'accès de production.
     */
    public static final String API_TOKEN_PROD = "TODO";

    /**
     *  Codes de réponses HTTP validés par défaut.
     */
    public static final int[] API_VALIDATORS = new int[] {200, 201, 202, 203};

    /**
     *  Utilitaire de sérialisation.
     */
    private ObjectMapper mapper;

    /**
     *  Constructeur.
     */
    public WhaleClubAccess() {
        this.mapper = new ObjectMapper();
    }

    /**
     *  Récupération de la liste des marchés disponibles.
     *  @return Liste des marchés.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    public List<WhaleClubMarketDTO> getMarkets() throws ApiException {
        /* Création du résultat */
        List<WhaleClubMarketDTO> result = new ArrayList<>();

        /* Appel HTTP */
        JsonNode node = this.doGet(true, "markets", WhaleClubAccess.API_VALIDATORS);

        /* Création de l'objet */
        for (String key : node.getObject().keySet()) {
            /* Récupération du noeud */
            JSONObject object = node.getObject().getJSONObject(key);

            /* Complétion */
            WhaleClubMarketDTO market = new WhaleClubMarketDTO(key, object.getString("display_name"));
            result.add(market);
        }

        /* Renvoi */
        return result;
    }

    /**
     *  Récupération d'une liste de taux.
     *  @param keys Liste des clefs requetées.
     *  @return Liste des taux correspondants.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    public List<WhaleClubRateDTO> getRates(List<String> keys) throws ApiException {
        /* Création du résultat */
        List<WhaleClubRateDTO> result = new ArrayList<>();

        /* Parcours */
        for (int i = 0; i < keys.size(); i += 5) {
            List<String> sub = keys.subList(i, i + 5 > keys.size() ? keys.size() : i + 5);

            /* Appel HTTP */
            JsonNode node = this.doGet(true, "price/" + String.join(",", sub), WhaleClubAccess.API_VALIDATORS);

            /* Parcours de noeuds */
            JSONObject object = node.getObject();
            for (String key : object.keySet()) {
                JSONObject market = object.getJSONObject(key);
                WhaleClubRateDTO rate = new WhaleClubRateDTO(
                        key,
                        market.getLong("last_updated") * 1000,
                        market.getDouble("ask"),
                        market.getDouble("bid"));
                result.add(rate);
            }
        }

        /* Renvoi */
        return result;
    }

    /**
     *  Réalise une ouverture de position.
     *  @param test Mode.
     *  @param direction Direction de la position.
     *  @param market Marché ciblé.
     *  @param leverage Niveau de levier.
     *  @param size Taille.
     *  @return Position ouverte.
     *  @throws ApiException En cas d'erreur de l'API.
     *  @throws InnerException En cas d'erreur interne.
     */
    public WhaleClubPositionDTO takePosition(
            boolean test,
            boolean direction,
            MarketEntity market,
            double leverage,
            double size) throws ApiException, InnerException {
        /* Création des paramètres */
        Map<String, Object> params = new HashMap<>();
        params.put("direction", direction ? "long" : "short");
        params.put("market", market.getKey());
        params.put("leverage", leverage);
        params.put("size", size);

        /* Appel HTTP */
        JsonNode node = this.doPost(test, "position/new", WhaleClubAccess.API_VALIDATORS, params);

        /* Renvoi */
        return this.as(node, WhaleClubPositionDTO.class);
    }

    /**
     *  Ferme une position préalablement ouverte.
     *  @param transaction Transaction fermée.
     *  @return Position retournée par le serveur.
     *  @throws ApiException En cas d'erreur de l'API.
     *  @throws InnerException En cas d'erreur interne.
     */
    public WhaleClubPositionDTO closePosition(TransactionEntity transaction) throws ApiException, InnerException {
        /* Mode d'appel */
        boolean test = true;
        if (transaction.getPosition().getType() == PositionType.REAL) {
            test = false;
        }

        /* Appel HTTP */
        JsonNode node = this.doPut(test, "position/close/" + transaction.getForeign(), WhaleClubAccess.API_VALIDATORS);

        /* Renvoi */
        return this.as(node, WhaleClubPositionDTO.class);
    }

    /**
     *  Exécute une requete HTTP en mode GET.
     *  @param test Mode d'appel.
     *  @param uri URI appelée.
     *  @param validators Liste des codes de réponse validés.
     *  @return Réponse du service.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    private JsonNode doGet(boolean test, String uri, int[] validators) throws ApiException {
        GetRequest request = Unirest.get(WhaleClubAccess.API_ENDPOINT + "/" + uri).header("Authorization", this.doAuth(test));
        try {
            HttpResponse<JsonNode> response = request.asJson();
            this.doCheck(response, validators);
            return response.getBody();
        } catch (ApiException cause) {
            throw new ApiException(cause.getCode(), cause, "HTTP GET call to URI '%s' failed", uri);
        } catch (UnirestException cause) {
            throw new ApiException(500, cause, "Serialization for HTTP GET on URI '%s' failed", uri);
        }
    }

    /**
     *  Exécute une requete en mode PUT.
     *  @param test Mode d'appel.
     *  @param uri URI appelée.
     *  @param validators Liste des codes de réponse validés.
     *  @return Réponse du service.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    private JsonNode doPut(boolean test, String uri, int[] validators) throws ApiException {
        HttpRequestWithBody request = Unirest.put(WhaleClubAccess.API_ENDPOINT + "/" + uri).header("Authorization", this.doAuth(test));
        try {
            HttpResponse<JsonNode> response = request.asJson();
            this.doCheck(response, validators);
            return response.getBody();
        } catch (ApiException cause) {
            throw new ApiException(cause.getCode(), cause, "HTTP PUT call to URI '%s' failed", uri);
        } catch (UnirestException cause) {
            throw new ApiException(500, cause, "Serialization for HTTP PUT on URI '%s' failed", uri);
        }
    }

    /**
     *  Exécute une requete HTTP en mode POST.
     *  @param test Mode d'appel.
     *  @param uri URI appelée.
     *  @param validators Liste des codes de réponse validés.
     *  @param params Paramètres.
     *  @return Réponse du service.
     *  @throws ApiException En cas d'erreur de l'API.
     */
    private JsonNode doPost(boolean test, String uri, int[] validators, Map<String, Object> params) throws ApiException {
        /* Traitement des paramètres */
        for (String key : params.keySet()) {
            if (params.get(key) instanceof Double) {
                params.put(key, String.format("%f", params.get(key)));
            }
        }

        /* Création de la requete */
        HttpRequestWithBody request = Unirest.post(WhaleClubAccess.API_ENDPOINT + "/" + uri).header("Authorization", this.doAuth(test));
        request.fields(params);

        /* Appel */
        try {
            HttpResponse<JsonNode> response = request.asJson();
            this.doCheck(response, validators);
            return response.getBody();
        } catch (ApiException cause) {
            throw new ApiException(cause.getCode(), cause, "HTTP POST call to URI '%s' failed", uri);
        } catch (UnirestException cause) {
            throw new ApiException(500, cause, "Serialization for HTTP POST on URI '%s' failed", uri);
        }
    }

    /**
     *  Génère un jeton d'authentification valable pour un mode d'utilisation donné.
     *  @param test Mode d'authentification.
     *  @return Jeton correspondant.
     */
    private String doAuth(boolean test) {
        if (test) {
            return String.format("Bearer %s", WhaleClubAccess.API_TOKEN_TEST);
        } else {
            return String.format("Bearer %s", WhaleClubAccess.API_TOKEN_PROD);
        }
    }

    /**
     *  Vérifie le code de réponse d'un appel à l'API.
     *  @param response Réponse HTTP.
     *  @param validators Liste des codes valides.
     *  @throws ApiException En cas de code invalide.
     */
    private void doCheck(HttpResponse response, int[] validators) throws ApiException {
        if (!Arrays.stream(validators).anyMatch(code -> response.getStatus() == code)) {
            throw new ApiException(response.getStatus(), "Invalid API response code %d", response.getStatus());
        }
    }

    /**
     *  Désérialise un noeud JSON.
     *  @param node Noeud désérialisé.
     *  @param clazz Classe.
     *  @param <TOutput> Type de résultat attendu.
     *  @return Objet désérialisé.
     *  @throws InnerException En cas d'erreur interne.
     */
    private <TOutput> TOutput as(JsonNode node, Class<TOutput> clazz) throws InnerException {
        try {
            return (TOutput) this.mapper.readValue(node.toString(), clazz);
        } catch (IOException ex) {
            throw new InnerException(ex, "Failed to deserialize JSON node '%s'", node.toString());
        }
    }
}
