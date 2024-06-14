package com.group15.communication;

import com.google.gson.Gson;
import com.group15.model.Robots;
import com.group15.model.lobby.LobbyData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerCommunication {
    private boolean isConnectedToServer = false;
    private final Gson gson = new Gson();

    public LobbyData createLobby(LobbyData lobbyData, String playerName) {
        lobbyData.setPlayerName(playerName);
        try {
            lobbyData = lobbyPostRequest(lobbyData, "hostGame");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            // TODO: Handle lobbyData error message
            System.out.println("Couldn't create lobby.");
        }
        return lobbyData;
    }

    public LobbyData joinLobby(LobbyData lobbyData, long gameID, String playerName) {
        lobbyData.setGameId(gameID);
        lobbyData.setPlayerName(playerName);
        try {
            lobbyData = lobbyPostRequest(lobbyData, "joinGame");
        } catch (IOException | InterruptedException | URISyntaxException e) {
            // TODO: Handle lobbyData error message
            System.out.println("Couldn't join lobby.");
        }
        return lobbyData;
    }

    public LobbyData requestUpdatedLobby(LobbyData lobbyData) throws URISyntaxException, IOException, InterruptedException {
        return lobbyPostRequest(lobbyData, "updateClient");
    }

    private LobbyData lobbyPostRequest(LobbyData lobbyData, String uriEndPoint) throws URISyntaxException, IOException, InterruptedException {
        String lobbyAsJson = gson.toJson(lobbyData);

        HttpRequest postRequest;
        postRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/Lobby/" + uriEndPoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(lobbyAsJson))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        lobbyData = gson.fromJson(postResponse != null ? postResponse.body() : null, LobbyData.class);

        isConnectedToServer = true;

        System.out.println("Got data with gameID: " + lobbyData.getGameId());

        return lobbyData;
    }

    public void leaveLobby() {
        // TODO: tell the server that the player has left
        isConnectedToServer = false;
    }

    public void playerReady() {
        // TODO: tell the server that the player has left
    }

    public void changePlayerRobot(Robots robot) {
        // TODO: send new robot to server
    }

    public void changeCourse(String courseName) {
        // TODO: change map and robot from
    }

    public boolean getIsConnectedToServer() {
        return isConnectedToServer;
    }
}
