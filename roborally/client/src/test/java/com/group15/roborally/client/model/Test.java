package com.group15.roborally.client.model;

import com.group15.roborally.client.utils.ServerCommunication;
import com.group15.roborally.common.model.Interaction;
import com.group15.roborally.common.model.Player;

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

        // Update interaction
        Interaction interaction = new Interaction(p1.getPlayerId(), "RebootInteraction", 5, 2);

        Interaction received = sc.getInteraction(p1.getPlayerId(),5,2);
        if(received == null) {
            System.out.println("No interaction found");
        } else {
            System.out.println("Interaction found: " + received.getChoice());
        }

        sc.putInteraction(interaction);
        
        received = sc.getInteraction(p1.getPlayerId(),5,2);
        if(received == null) {
            System.out.println("No interaction found");
        } else {
            System.out.println("Interaction found: " + received.getChoice());
        }

        // // Update choices
        // System.out.println("Updating choices");
        // int turn = 5;
        // int movement = 2;
        // List<Choice> p1Choices = new ArrayList<>();
        // for (int i = 0; i < 4; i++) {
        //     p1Choices.add(new Choice(p1.getPlayerId(), "p1Move" + (i + 1), turn, movement));
        // }
        // sc.updateChoice(p1Choices, p1.getPlayerId());

        // List<Choice> p2Choices = new ArrayList<>();
        // for (int i = 0; i < 3; i++) {
        //     p2Choices.add(new Choice(p2.getPlayerId(), "p2Move" + (i + 1), turn, movement));
        // }
        // sc.updateChoice(p2Choices, p2.getPlayerId());

        // System.out.println("Attempting to print choice mid-update");
        // List<Choice> choices = sc.getChoices(gameId, 5, 2);
        // if (choices == null) {
        //     System.out.println("No choices found");
        // } else {
        //     for (Choice c : choices) {
        //         System.out.println("Player with ID '" + c.getPlayerId() + "' chose " + c.getChoice());
        //     }
        // }
        // System.out.println();

        // List<Choice> p3Choices = new ArrayList<>();
        // for (int i = 0; i < 5; i++) {
        //     p3Choices.add(new Choice(p3.getPlayerId(), "p3Move" + (i + 1), turn, movement));
        // }
        // sc.updateChoice(p3Choices, p3.getPlayerId());

        // List<Choice> p4Choices = new ArrayList<>();
        // for (int i = 0; i < 2; i++) {
        //     p4Choices.add(new Choice(p4.getPlayerId(), "p4Move" + (i + 1), turn, movement));
        // }
        // sc.updateChoice(p4Choices, p4.getPlayerId());
        // System.out.println("Choices updated");
        // System.out.println();

        // // Retrieving choices
        // System.out.println("Retrieving choices");
        // choices = sc.getChoices(gameId, 5, 2);
        // if (choices == null) {
        //     System.out.println("No choices found");
        // } else {
        //     for (Choice c : choices) {
        //         System.out.println("Player with ID '" + c.getPlayerId() + "' chose " + c.getChoice());
        //     }
        // }



    }
}
