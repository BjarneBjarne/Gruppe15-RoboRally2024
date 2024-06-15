package com.group15.test;

import java.net.URISyntaxException;
import java.util.List;

import com.group15.model.Player;

public class ServerTest_2 {
    public static void main(String[] args) throws URISyntaxException{
        ServerRequest serverRequest = new ServerRequest("http://localhost:8080");

        // Create game
        System.out.println("Creating game");
        Long gameId = serverRequest.createGame();
        System.out.println("ID of new game " + gameId);
        System.out.println();

        // Join game
        String p1Name = "John";
        System.out.println(p1Name + " joining game");
        Player player = serverRequest.joinGame(gameId, p1Name);
        System.out.println(player.getPlayerName() + " joined game with Game ID " + player.getGameId());
        System.out.println();

        // List of players
        System.out.println("List of players");
        List<Player> players = serverRequest.getPlayers(gameId);
        players.forEach(p -> System.out.println("Player: " + p.getPlayerName() + " - Player ID: " + p.getPlayerId()));

        System.out.println();

        // Join game
        String p2Name = "Marcus";
        System.out.println(p2Name + " joining game");
        Player player2 = serverRequest.joinGame(gameId, p2Name);
        System.out.println(player2.getPlayerName() + " joined game with Game ID " + player2.getGameId());
        System.out.println();

        // List of players
        System.out.println("List of player before name update");
        players = serverRequest.getPlayers(gameId);
        players.forEach(p -> System.out.println("Player: " + p.getPlayerName() + " - Player ID: " + p.getPlayerId()));
        System.out.println();

        // Update player name
        player2.setPlayerName("Smith");
        serverRequest.updatePlayer(player2);
        System.out.println("List of players after name update (Marcus -> Smith)");
        players = serverRequest.getPlayers(gameId);
        players.forEach(p -> System.out.println("Player: " + p.getPlayerName() + " - Player ID: " + p.getPlayerId()));
        System.out.println();

        // Inserting Market
        System.out.println("Updating Market");
        String[] cards = {"Card1", "Card2", "Card3", "Card4", "Card5"};
        serverRequest.updateMarket(cards, gameId);
        System.out.println();

        // Get Market
        System.out.println("Getting Market");
        String[] market = serverRequest.getMarket(gameId);
        for (String card : market) {
            System.out.println(card);
        }
        System.out.println();

        // Update Market
        System.out.println("Updating Market");
        String[] newCards = {"Card6", "Card7", "Card8", "Card9", "Card10"};
        serverRequest.updateMarket(newCards, gameId);
        System.out.println();

        // Get Market
        System.out.println("Getting Market");
        market = serverRequest.getMarket(gameId);
        for (String card : market) {
            System.out.println(card);
        }
        System.out.println();

    }
}