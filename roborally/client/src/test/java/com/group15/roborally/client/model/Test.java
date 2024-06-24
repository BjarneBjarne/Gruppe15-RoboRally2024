package com.group15.roborally.client.model;

import java.util.List;

import com.group15.roborally.client.utils.ServerCommunication;
import com.group15.roborally.server.model.Choice;
import com.group15.roborally.server.model.Player;

public class Test {
    public static void main(String[] args) {
        ServerCommunication sc = new ServerCommunication();
        
        Player p1 = new Player();
        p1.setPlayerName("Marcus");
        Player p2 = new Player();
        p2.setPlayerName("Gustav");
        Player p3 = new Player();
        p3.setPlayerName("Tobias");
        Player p4 = new Player();
        p4.setPlayerName("Michael");

        // Host game
        System.out.println("Creating game");
        Long gameId = sc.createGame("http://localhost:8080");
        System.out.println("Game created with ID " + gameId);
        System.out.println();

        // Join game
        System.out.println("Players joining game");
        p1 = sc.joinGame("http://localhost:8080", gameId, p1.getPlayerName());
        p2 = sc.joinGame("http://localhost:8080", gameId, p2.getPlayerName());
        p3 = sc.joinGame("http://localhost:8080", gameId, p3.getPlayerName());
        p4 = sc.joinGame("http://localhost:8080", gameId, p4.getPlayerName());
        System.out.println("Players: " 
            + "\n- " + p1.getPlayerName() + " [" + p1.getPlayerId() + "], "
            + "\n- " + p2.getPlayerName() + " [" + p2.getPlayerId() + "], "
            + "\n- " + p3.getPlayerName() + " [" + p3.getPlayerId() + "], "
            + "\n- " + p4.getPlayerName() + " [" + p4.getPlayerId() + "]"
            + "\n" + "joined game with ID " + gameId
        );
        System.out.println();

        // Update choices
        System.out.println("Updating choices");
        Choice p1Choice = new Choice();
        p1Choice.setChoice("move1");
        p1Choice.setPlayerId(p1.getPlayerId());
        p1Choice.setTurn(5);
        p1Choice.setMovement(2);
        sc.updateChoice(p1Choice);

        Choice p2Choice = new Choice();
        p2Choice.setChoice("move6");
        p2Choice.setPlayerId(p2.getPlayerId());
        p2Choice.setTurn(5);
        p2Choice.setMovement(2);
        sc.updateChoice(p2Choice);

        Choice p3Choice = new Choice();
        p3Choice.setChoice("move11");
        p3Choice.setPlayerId(p3.getPlayerId());
        p3Choice.setTurn(5);
        p3Choice.setMovement(2);
        sc.updateChoice(p3Choice);

        Choice p4Choice = new Choice();
        p4Choice.setChoice("move16");
        p4Choice.setPlayerId(p4.getPlayerId());
        p4Choice.setTurn(5);
        p4Choice.setMovement(2);
        sc.updateChoice(p4Choice);
        System.out.println("Choices updated");
        System.out.println();

        // Retrieving choices
        System.out.println("Retrieving choices");
        List<Choice> choices = sc.getChoices(gameId, 5, 2);
        for (Choice c : choices) {
            System.out.println("Player with ID '" + c.getPlayerId() + "' chose " + c.getChoice());
        }



    }
}
