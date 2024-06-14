package com.group15.roborally.client.communication;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.Player;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

public class ServerCommunication {
    private final String baseUrl;
    private final HttpHeaders headers;

    private boolean isConnectedToServer = false;

    public ServerCommunication(String baseUrl) {
        this.baseUrl = baseUrl;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    // Connection to server //
    public long createGame() {
        try {
            URI uri = new URI(baseUrl + "/games");
            RequestEntity<Void> request = RequestEntity
                    .post(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .build();
            ResponseEntity<Long> response = new RestTemplate().exchange(request, Long.class);

            System.out.println("Create game status code: " + response.getStatusCode());
            if (response.getBody() == null) {
                throw new Exception("Received null body as response from server.");
            }
            System.out.println("Created new game.");

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to create new game.");
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public Player joinGame(long gameId, String playerName) {
        try {
            URI uri = new URI(baseUrl + "/games/" + gameId + "/join");
            RequestEntity<String> request = RequestEntity
                    .post(uri)
                    .headers(headers)
                    .body(playerName);
            ResponseEntity<Player> response = new RestTemplate().exchange(request, Player.class);

            System.out.println("Join game status code: " + response.getStatusCode());
            if (response.getBody() == null) {
                throw new Exception("Received null body as response from server.");
            }
            isConnectedToServer = true;
            System.out.println("Joined game.");

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to join game.");
            System.out.println(e.getMessage());
        }
        return null;
    }


    // ---------------- //
    public Game getGame(long gameId) {
        try {
            URI uri = new URI(baseUrl + "/games/" + gameId);
            RequestEntity<Void> request = RequestEntity
                    .get(uri)
                    .headers(headers)
                    .build();
            ResponseEntity<Game> response = new RestTemplate().exchange(request, Game.class);

            System.out.println("Get game status code: " + response.getStatusCode());
            if (response.getBody() == null) {
                throw new Exception("Received null body as response from server.");
            }
            System.out.println("Received game from server.");

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to receive game from server.");
            System.out.println(e.getMessage());
        }
        return null;
    }
    public List<Player> getPlayers(long gameId) {
        try {
            URI uri = new URI(baseUrl + "/games/" + gameId + "/players");
            RequestEntity<Void> request = RequestEntity
                    .get(uri)
                    .headers(headers)
                    .build();
            ResponseEntity<List<Player>> response = new RestTemplate().exchange(request, new ParameterizedTypeReference<>() {});

            System.out.println("Get players status code: " + response.getStatusCode());
            if (response.getBody() == null) {
                throw new Exception("Received null body as response from server.");
            }
            System.out.println("Received players from server.");

            return response.getBody();
        } catch (Exception e) {
            System.err.println("Failed to receive players from server.");
            System.out.println(e.getMessage());
        }
        return null;
    }

    public boolean getIsConnectedToServer() {
        return isConnectedToServer;
    }
}
