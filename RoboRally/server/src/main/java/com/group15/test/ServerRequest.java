package com.group15.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import com.group15.model.Player;

import org.springframework.http.ResponseEntity;

public class ServerRequest {
    
    String baseUrl;
    HttpHeaders headers;

    public ServerRequest(String baseURL) {
        this.baseUrl = baseURL;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    /**
     * Get list of players in a game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return List of players in the game
     */
    public List<Player> getPlayers(Long gameId) {
        
        List<Player> players = sendRequest(
            baseUrl + "/games/" + gameId + "/players", 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<List<Player>>() {}
        );

        return players;
    }

    /**
     * Join a game - the returning player object contains the generated playerId 
     * of the joined player
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @param playerName - name of the player joining
     * @return Player object of the player joining
     */
    public Player joinGame(Long gameId, String playerName) {
        
        Player player = sendRequest(
            baseUrl + "/games/" + gameId + "/join", 
            HttpMethod.POST, 
            playerName, 
            new ParameterizedTypeReference<Player>() {}
        );
        
        return player;
    }

    /**
     * Create a new game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @return gameId - id of the created game
     */
    public Long createGame() {
        
        Long gameId = sendRequest(
            baseUrl + "/games", 
            HttpMethod.POST, 
            null, 
            new ParameterizedTypeReference<Long>() {}
        );

        return gameId;
    }

    /**
     * Update a player in the database
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param player - player object to update
     * @param playerId - id of the player to update
     * @return message - message from the server
     */
    public String updatePlayer(Player player, Long playerId) {
        
        String message = sendRequest(
            baseUrl + "/players/" + playerId, 
            HttpMethod.PUT, 
            player, 
            new ParameterizedTypeReference<String>() {}
        );

        return message;
    }

    /**
     * Delete a player from the database
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param playerId - id of the player to delete
     * @return message - message from the server
     */
    public String deletePlayer(Long playerId) {
        
        String message = sendRequest(
            baseUrl + "/players/" + playerId, 
            HttpMethod.DELETE, 
            null, 
            new ParameterizedTypeReference<String>() {}
        );

        return message;
    }

    /**
     * Get the current turn of the game during the upgrade phase
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return turn - current turn of the game
     */
    public Integer getUpgradeTurn(Long gameId) {
        
        Integer turn = sendRequest(
            baseUrl + "/market/" + gameId + "/turn", 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<Integer>() {}
        );

        return turn;
    }

    /**
     * Get the market cards of the game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return marketCards - array of market cards
     */
    public String[] getMarket(Long gameId) {
        
        String[] marketCards = sendRequest(
            baseUrl + "/market/" + gameId, 
            HttpMethod.GET, 
            null, 
            new ParameterizedTypeReference<String[]>() {}
        );

        return marketCards;
    }

    /**
     * Update the market cards of the game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param marketCards - array of market cards
     * @param gameId - id of the game
     * @return message - message from the server
     */
    public String updateMarket(String[] marketCards, Long gameId) {
        
        String message = sendRequest(
            baseUrl + "/market/" + gameId, 
            HttpMethod.PUT, 
            marketCards, 
            new ParameterizedTypeReference<String>() {}
        );

        return message;
    }

    /**
     * Customize a server request
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param <T> - type of the request body
     * @param <R> - type of the response body
     * @param uriSt - uri of the request
     * @param method - http method of the request
     * @param body - body of the request
     * @param responseType - type of the response
     * @return response body of type R
     */
    private <T, R> R sendRequest(String uriSt, HttpMethod method, T body, ParameterizedTypeReference<R> responseType) {
        URI uri = URI.create(uriSt);
        RequestEntity<T> request = RequestEntity.method(method, uri)
                .headers(headers)
                .body(body);
        ResponseEntity<R> response = new RestTemplate().exchange(request, responseType);
        return response.getBody();
    }
}
