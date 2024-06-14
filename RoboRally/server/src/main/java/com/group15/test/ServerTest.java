package com.group15.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import com.group15.model.Player;

import org.springframework.http.ResponseEntity;

public class ServerTest {

    static String url = "http://localhost:8080";
    static HttpHeaders headers;
    
    public static void main(String[] args) throws URISyntaxException {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        
        String playerName = "Marcus";
        
        // Create game
        Long gameId = createGame(new URI(url + "/games"));
        System.out.println(gameId);

        // Join game
        Player player = joinGame(new URI(url + "/games/" + gameId + "/join"), playerName);
        System.out.println(player.getGameId());

        // List of players
        List<Player> players = getPlayers(new URI(url + "/games/" + gameId + "/players"));
        players.forEach(p -> System.out.println(p.getPlayerName()));
    }

    private static List<Player> getPlayers(URI uri){
        RequestEntity<Void> request = RequestEntity
            .get(uri)
            .headers(headers)
            .build();
        
        ResponseEntity<List<Player>> response = new RestTemplate().exchange(request, new ParameterizedTypeReference<List<Player>>() {});

        return response.getBody();
    }

    private static Player joinGame(URI uri, String playerName){
        RequestEntity<String> request = RequestEntity
            .post(uri)
            .headers(headers)
            .body(playerName);
        
        ResponseEntity<Player> response = new RestTemplate().exchange(request, Player.class);

        return response.getBody();
    }

    private static Long createGame(URI uri){
        RequestEntity<Void> request = RequestEntity
            .post(uri)
            .accept(MediaType.APPLICATION_JSON)
            .build();
        
        ResponseEntity<Long> response = new RestTemplate().exchange(request, Long.class);

        return response.getBody();
    }
}
