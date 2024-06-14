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

public class ServerRequest {
    
    String baseUrl;
    HttpHeaders headers;

    public ServerRequest(String baseURL) {
        this.baseUrl = baseURL;
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
    }

    public List<Player> getPlayers(Long gameId) throws URISyntaxException{
        URI uri = new URI(baseUrl + "/games/" + gameId + "/players");
        RequestEntity<Void> request = RequestEntity
            .get(uri)
            .headers(headers)
            .build();
        
        ResponseEntity<List<Player>> response = new RestTemplate().exchange(request, new ParameterizedTypeReference<List<Player>>() {});

        return response.getBody();
    }

    public Player joinGame(Long gameId, String playerName) throws URISyntaxException{
        URI uri = new URI(baseUrl + "/games/" + gameId + "/join");
        RequestEntity<String> request = RequestEntity
            .post(uri)
            .headers(headers)
            .body(playerName);
        
        ResponseEntity<Player> response = new RestTemplate().exchange(request, Player.class);

        return response.getBody();
    }

    public Long createGame() throws URISyntaxException{
        URI uri = new URI(baseUrl + "/games");
        RequestEntity<Void> request = RequestEntity
            .post(uri)
            .accept(MediaType.APPLICATION_JSON)
            .build();
        
        ResponseEntity<Long> response = new RestTemplate().exchange(request, Long.class);

        return response.getBody();
    }
}
