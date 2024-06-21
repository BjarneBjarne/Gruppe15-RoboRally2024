package com.group15.roborally.server;

import java.util.List;

import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.server.utils.ServerCommunication;

public class test {
    
    public static void main(String[] args) {
        Player p1 = new Player();
        p1.setPlayerName("Marcus");
        Player p2 = new Player();
        p2.setPlayerName("Gustav");
        Player p3 = new Player();
        p3.setPlayerName("Tobias");
        Player p4 = new Player();
        p4.setPlayerName("Michael");

        ServerCommunication sc = new ServerCommunication("http://localhost:8080");

        // Host game
        System.out.println("Creating game");
        Long gameId = sc.createGame();
        System.out.println("Game created with ID " + gameId);
        System.out.println();

        // Join game
        System.out.println("Players joining game");
        p1 = sc.joinGame(gameId, p1.getPlayerName());
        p2 = sc.joinGame(gameId, p2.getPlayerName());
        p3 = sc.joinGame(gameId, p3.getPlayerName());
        p4 = sc.joinGame(gameId, p4.getPlayerName());
        System.out.println("Players: " 
            + "\n- " + p1.getPlayerName() + " [" + p1.getPlayerId() + "], "
            + "\n- " + p2.getPlayerName() + " [" + p2.getPlayerId() + "], "
            + "\n- " + p3.getPlayerName() + " [" + p3.getPlayerId() + "], "
            + "\n- " + p4.getPlayerName() + " [" + p4.getPlayerId() + "]"
            + "\n- " + "joined game with ID " + gameId
        );
        System.out.println();
        
        // Update register
        System.out.println("Updating registers");
        String[] registerP1 = {"move1", "move2", "move3", "move4", "move5"};
        String[] registerP2 = {"move6", "move7", "move8", "move9", "move10"};
        String[] registerP3 = {"move11", "move12", "move13", "move14", "move15"};
        String[] registerP4 = {"move16", "move17", "move18", "move19", "move20"};
        
        sc.updateRegister(registerP1, p1.getPlayerId(), 5);
        sc.updateRegister(registerP2, p2.getPlayerId(), 5);
        sc.updateRegister(registerP3, p3.getPlayerId(), 5);
        sc.updateRegister(registerP4, p4.getPlayerId(), 5);
        System.out.println("Registers updated");
        System.out.println();

        // Retrieving registers
        System.out.println("Retrieving registers");
        List<Register> registers = sc.getRegisters(gameId);

        if(!(registers == null)) {
            System.out.println();

            for (Register r : registers) {
                System.out.println("Player " + r.getPlayerId() + " has the following moves:");
                for (String move : r.getMoves()) {
                    System.out.println("- " + move);
                }
                System.out.println();
            }
        } else {
            System.out.println("No registers found");
            System.out.println();
        }

        // Update register
        System.out.println("Updating registers of p1 and p2");
        String[] registerP1New = {"move21", "move22", "move23", "move24", "move25"};
        String[] registerP2New = {"move26", "move27", "move28", "move29", "move30"};

        sc.updateRegister(registerP1New, p1.getPlayerId(), 6);
        sc.updateRegister(registerP2New, p2.getPlayerId(), 6);

        System.out.println("Registers updated");
        System.out.println();

        // Retrieving registers
        registers = sc.getRegisters(gameId);

        if(!(registers == null)) {
            System.out.println();

            for (Register r : registers) {
                System.out.println("Player " + r.getPlayerId() + " has the following moves:");
                for (String move : r.getMoves()) {
                    System.out.println("- " + move);
                }
                System.out.println();
            }
        } else {
            System.out.println("No registers found");
            System.out.println();
        }

        // Update register
        System.out.println("Updating registers of p3 and p4");
        String[] registerP3New = {"move31", "move32", "move33", "move34", "move35"};
        String[] registerP4New = {"move36", "move37", "move38", "move39", "move40"};
        sc.updateRegister(registerP3New, p3.getPlayerId(), 6);
        sc.updateRegister(registerP4New, p4.getPlayerId(), 6);

        System.out.println("Registers updated");
        System.out.println();

        // Retrieving registers
        registers = sc.getRegisters(gameId);
        if(!(registers == null)) {
            System.out.println();

            for (Register r : registers) {
                System.out.println("Player " + r.getPlayerId() + " has the following moves:");
                for (String move : r.getMoves()) {
                    System.out.println("- " + move);
                }
                System.out.println();
            }
        } else {
            System.out.println("No registers found");
            System.out.println();
        }
    }
}
