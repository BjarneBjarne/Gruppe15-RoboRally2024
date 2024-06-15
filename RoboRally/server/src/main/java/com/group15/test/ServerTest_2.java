package com.group15.test;

import java.net.URISyntaxException;
import java.util.List;

import com.group15.model.Player;

public class ServerTest_2 {
    public static void main(String[] args) throws URISyntaxException{
        ServerRequest serverRequest = new ServerRequest("http://localhost:8080");

        // Create game
        Long gameId = serverRequest.createGame();
        System.out.println(gameId);

        // Join game
        Player player = serverRequest.joinGame(gameId, "John");
        System.out.println(player.getGameId());

        // List of players
        List<Player> players = serverRequest.getPlayers(gameId);
        players.forEach(p -> System.out.println(p.getPlayerName()));

    }
}