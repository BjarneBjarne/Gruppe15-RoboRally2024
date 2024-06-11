package com.gruppe15.communication;

import com.google.gson.Gson;
import com.gruppe15.model.lobby.LobbyClientUpdate;
import com.gruppe15.model.lobby.LobbyClientJoin;
import com.gruppe15.model.lobby.LobbyData;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerCommunication {
    private boolean isConnectedToServer = false;
    private final Gson gson = new Gson();

    public LobbyData createLobby(String playerName) {
        // Prepare message to send
        String createLobbyMessageAsJson = gson.toJson(playerName);
        // Send message and receive response
        HttpResponse<String> createLobbyResponse = lobbyPostRequest(createLobbyMessageAsJson, "createLobby");
        // Handle received message
        LobbyData lobbyData = gson.fromJson(createLobbyResponse != null ? createLobbyResponse.body() : null, LobbyData.class);
        if (lobbyData != null) {
            System.out.println("Successfully created new lobby with gameId: " + lobbyData.gameId() + ".");
            isConnectedToServer = true;
        } else {
            System.out.println("Failed to create a new lobby.");
        }

        return lobbyData;
    }

    public LobbyData joinLobby(String gameId, String playerName) {
        // Prepare message to send
        LobbyClientJoin joinLobbyMessage = new LobbyClientJoin(gameId, playerName);
        String joinLobbyMessageAsJson = gson.toJson(joinLobbyMessage);
        // Send message and receive response
        HttpResponse<String> joinLobbyResponse = lobbyPostRequest(joinLobbyMessageAsJson, "joinLobby");
        // Handle received message
        LobbyData lobbyData = gson.fromJson(joinLobbyResponse != null ? joinLobbyResponse.body() : null, LobbyData.class);
        if (lobbyData != null) {
            System.out.println("Connected to game with gameId: " + lobbyData.gameId() + ".");
            isConnectedToServer = true;
        } else {
            System.out.println("Failed to connect to game with gameId: " + gameId + ".");
        }

        return lobbyData;
    }


    // In-lobby messages
    public LobbyData getUpdatedLobby(LobbyData lobbyData) {
        LobbyClientUpdate updateCourseMessage = new LobbyClientUpdate(
                lobbyData.playerId(),
                lobbyData.gameId(),
                lobbyData.playerNames()[0],
                lobbyData.robotNames()[0],
                lobbyData.areReady()[0],
                lobbyData.courseName());
        return requestUpdatedLobby(updateCourseMessage);
    }
    public LobbyData changeCourse(LobbyData lobbyData, String newCourseName) {
        LobbyClientUpdate changeCourseMessage = new LobbyClientUpdate(
                lobbyData.playerId(),
                lobbyData.gameId(),
                lobbyData.playerNames()[0],
                lobbyData.robotNames()[0],
                lobbyData.areReady()[0],
                newCourseName);
        return requestUpdatedLobby(changeCourseMessage);
    }
    public LobbyData setIsReady(LobbyData lobbyData, int isReady) {
        LobbyClientUpdate toggleReadyMessage = new LobbyClientUpdate(
                lobbyData.playerId(),
                lobbyData.gameId(),
                lobbyData.playerNames()[0],
                lobbyData.robotNames()[0],
                isReady,
                lobbyData.courseName());
        return requestUpdatedLobby(toggleReadyMessage);
    }
    public LobbyData changeRobot(LobbyData lobbyData, String robotName) {
        LobbyClientUpdate changeRobotMessage = new LobbyClientUpdate(
                lobbyData.playerId(),
                lobbyData.gameId(),
                lobbyData.playerNames()[0],
                robotName,
                lobbyData.areReady()[0],
                lobbyData.courseName());
        return requestUpdatedLobby(changeRobotMessage);
    }

    public void leaveGame(long playerId) {
        String leaveGameMessageAsJson = gson.toJson(playerId);
        HttpResponse<String> leaveGameResponse = lobbyPostRequest(leaveGameMessageAsJson, "leaveGame");

        // Handle received message
        if (leaveGameResponse != null) {
            System.out.println(leaveGameResponse.body());
            isConnectedToServer = false;
        } else {
            System.out.println("Failed to leave game.");
        }
    }


    // Utility methods
    private LobbyData requestUpdatedLobby(LobbyClientUpdate updateLobbyMessage) {
        // Prepare message to send
        String updateLobbyMessageAsJson = gson.toJson(updateLobbyMessage);
        // Send message and receive response
        HttpResponse<String> updateLobbyResponse = lobbyPostRequest(updateLobbyMessageAsJson, "updateLobby");
        // Handle received message
        return gson.fromJson(updateLobbyResponse != null ? updateLobbyResponse.body() : null, LobbyData.class);
    }

    private HttpResponse<String> lobbyPostRequest(String message, String uriEndPoint) {
        HttpResponse<String> serverResponse = null;
        try {
            HttpRequest postRequest;
            postRequest = HttpRequest.newBuilder()
                    .uri(new URI("http://localhost:8080/Lobby/" + uriEndPoint))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(message))
                    .build();
            HttpClient httpClient = HttpClient.newHttpClient();
            serverResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
            // TODO: Handle lobbyClientToServer error message
            //System.out.println(serverResponse.statusCode());
        } catch (ConnectException e) {
            System.out.println("Failed to connect to server.");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            System.out.println(e.getMessage());
        }
        return serverResponse;
    }

    public boolean getIsConnectedToServer() {
        return isConnectedToServer;
    }
}
