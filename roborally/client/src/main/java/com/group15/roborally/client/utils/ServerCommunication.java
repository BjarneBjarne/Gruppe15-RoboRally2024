package com.group15.roborally.client.utils;

import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import com.group15.roborally.client.RoboRally;
import com.group15.roborally.common.model.*;
import com.group15.roborally.common.observer.Subject;

import javafx.application.Platform;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import static com.group15.roborally.client.ApplicationSettings.TIME_BEFORE_TIMEOUT_SECONDS;

/**
 * @author Michael Sylvest Bendtsen, s214954@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class ServerCommunication extends Subject {
    private String baseUrl;
    private final HttpHeaders headers;

    // Connection tracking
    private Instant startTimeOfConnectionLost = null;
    @Getter
    private boolean isConnectedToServer = false;
    @Getter
    private Duration timeSinceConnectionLost;

    public ServerCommunication() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    /**
     * Create a new game.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @return gameId - id of the created game
     */
    public long createGame(String baseUrl) {
        this.baseUrl = baseUrl;
        Long gameId = null;
        try {
            gameId = sendRequest(
                    "/games",
                    HttpMethod.POST,
                    new ParameterizedTypeReference<>() {},
                    null
            );
        } catch (HttpClientErrorException e) {
            //System.out.println(e.getStatusCode());
        }
        return gameId != null ? gameId : -1;
    }

    /**
     * Join a game - the returning player object contains the generated playerId of the joined player.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @param playerName - name of the player joining
     * @return Player object of the player joining
     */
    public Player joinGame(String baseUrl, long gameId, String playerName) {
        this.baseUrl = baseUrl;
        Player player = null;
        try {
            player = sendRequest(
                    "/games/" + gameId + "/join",
                    HttpMethod.POST,
                    new ParameterizedTypeReference<>() {},
                    playerName
            );
            setIsConnectedToServer(true);
        } catch (HttpClientErrorException e) {
            //System.out.println(e.getStatusCode());
        }
        return player;
    }


    // Put
    /**
     * Update a player in the database.
     *
     * @param player - player object to update
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    public void putPlayer(Player player) {
        sendRequest(
                "/players/" + player.getPlayerId(),
                HttpMethod.PUT,
                new ParameterizedTypeReference<>() {},
                player
        );
    }

    /**
     * Updates the game on the server.
     *
     * @param game The object of the game to be set on the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void putGame(Game game) {
        sendRequest(
                "/games/" + game.getGameId(),
                HttpMethod.PUT,
                new ParameterizedTypeReference<>() {},
                game
        );
    }

    /**
     * Update the upgradeShop cards of the game.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param upgradeShopCards - array of upgradeShop cards
     * @param gameId - id of the game
     */
    public void putUpgradeShop(String[] upgradeShopCards, long gameId) {
        sendRequest(
                "/upgradeShop/" + gameId,
                HttpMethod.PUT,
                new ParameterizedTypeReference<>() {},
                upgradeShopCards
        );
    }

    public void putInteraction(InteractionDTO interaction) {
        sendRequest(
                "/interactions",
                HttpMethod.PUT,
                new ParameterizedTypeReference<>() {},
                interaction
        );
    }

    public void putChoice(ChoiceDTO choice) {
        sendRequest(
                "/choices",
                HttpMethod.PUT,
                new ParameterizedTypeReference<String>() {},
                choice
        );
    }


    // Post
    public void postRegister(String[] commandCards, long playerId, int turn) {
        sendRequest(
                "/players/" + playerId + "/registers/" + turn,
                HttpMethod.POST,
                new ParameterizedTypeReference<>() {},
                commandCards
        );
    }


    // Get
    /**
     * @param gameId The gameId of the game to retrieve.
     * @return The game object from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public Game getGame(long gameId) {
        return sendRequest(
                "/games/" + gameId,
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    /**
     * Get list of players in a game.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return List of players in the game
     */
    public List<Player> getPlayers(long gameId) {
        return sendRequest(
                "/games/" + gameId + "/players",
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    public List<Register> getRegisters(long gameId) {
        return sendRequest(
                "/games/" + gameId + "/registers",
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    public List<Choice> getChoices(long gameId, int turn) {
        return sendRequest(
                "/choices/" + gameId + "?turn=" + turn,
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    public Interaction getInteraction(long playerId, int turn, int movement) {
        return sendRequest(
                "/interactions/" + playerId + "?turn=" + turn + "&movement=" + movement, 
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    /**
     * Get the upgradeShop cards of the game.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return upgradeShopCards - array of upgradeShop cards
     */
    public String[] getUpgradeShop(long gameId) {
        return sendRequest(
                "/upgradeShop/" + gameId,
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    /**
     * Get the current turn of the game during the upgrade phase.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param gameId - id of the game
     * @return turn - current turn of the game
     */
    public Integer getUpgradeTurn(long gameId) {
        return sendRequest(
                "/upgradeShop/" + gameId + "/turn",
                HttpMethod.GET,
                new ParameterizedTypeReference<>() {},
                null
        );
    }


    // Delete
    /**
     * Delete a player from the database.
     *
     * @param player - The player to delete
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    public void deletePlayer(Player player) {
        setIsConnectedToServer(false);
        sendRequest(
                "/players/" + player.getPlayerId(),
                HttpMethod.DELETE,
                new ParameterizedTypeReference<>() {},
                null
        );
    }

    /**
     * Customize a server request.
     * @param <T>    - type of the request body
     * @param <R>    - type of the response body
     * @param uriSt  - uri of the request
     * @param method - http method of the request
     * @param body   - body of the request
     * @return response body of type R
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    private <T, R> R sendRequest(String uriSt, HttpMethod method, ParameterizedTypeReference<R> responseType, T body) {
        URI uri = URI.create(baseUrl + uriSt);
        RequestEntity<T> request = RequestEntity.method(method, uri)
                .headers(headers)
                .body(body);
        try {
            ResponseEntity<R> response = new RestTemplate().exchange(request, responseType);
            evaluateTimeout(true);
            notifyChange();
            return response.getBody();
        } catch (ResourceAccessException | HttpClientErrorException | HttpServerErrorException e) {
            evaluateTimeout(false);
            System.err.println("Server communication error: URI: " + uriSt + ". Body: " + body + ". \n" + e.getMessage());
            notifyChange();
            return null;
        }
    }

    private void evaluateTimeout(boolean couldConnect) {
        Platform.runLater(() -> {
            if (couldConnect) {
                // Reset timeout.
                if (startTimeOfConnectionLost != null) {
                    startTimeOfConnectionLost = null;
                    System.out.println("Reestablished connection to server.");
                    RoboRally.setDebugText("", 0);
                }
            } else if (startTimeOfConnectionLost == null) {
                // Start timeout "timer".
                startTimeOfConnectionLost = Instant.now();
                System.out.println("Server not responding. Trying to reestablish connection to server...");
            } else {
                // Evaluate timeout
                timeSinceConnectionLost = Duration.between(startTimeOfConnectionLost, Instant.now());
                RoboRally.setDebugText("Server not responding. " + String.format("%.2f", (double)(timeSinceConnectionLost.toMillis() / 1000.0)), 0);
                if (timeSinceConnectionLost.toSeconds() >= TIME_BEFORE_TIMEOUT_SECONDS) {
                    setIsConnectedToServer(false);
                }
            }
        });
    }

    private void setIsConnectedToServer(boolean isConnectedToServer) {
        this.isConnectedToServer = isConnectedToServer;
        notifyChange();
    }
}
