package gruppe15.roborally.communication;

import com.google.gson.Gson;
import gruppe15.roborally.model.lobby.LobbyData;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerCommunication {
    private LobbyData lobbyData = new LobbyData();
    private final Gson gson = new Gson();

    public LobbyData createLobby(String playerName) throws URISyntaxException, IOException, InterruptedException {
        lobbyData.setpName(playerName);
        return lobbyPostRequest("hostGame");
    }

    public LobbyData joinLobby(Long gameID, String playerName) throws URISyntaxException, IOException, InterruptedException {
        lobbyData.setgId(gameID);
        lobbyData.setpName(playerName);
        return lobbyPostRequest("joinGame");
    }

    public LobbyData requestUpdatedLobby() throws URISyntaxException, IOException, InterruptedException {
        return lobbyPostRequest("updateClient");
    }

    private LobbyData lobbyPostRequest(String uriEndPoint) throws URISyntaxException, IOException, InterruptedException {
        String lobbyAsJson = gson.toJson(lobbyData);

        HttpRequest postRequest;
        postRequest = HttpRequest.newBuilder()
                .uri(new URI("http://localhost:8080/Lobby/" + uriEndPoint))
                .POST(HttpRequest.BodyPublishers.ofString(lobbyAsJson))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());
        lobbyData = gson.fromJson(postResponse != null ? postResponse.body() : null, LobbyData.class);

        System.out.println("Transcription completed");

        return lobbyData;
    }

    public void leaveLobby() {
        // TODO: tell the server that the player has left
    }

    public void playerReady() {
        // TODO: tell the server that the player has left
    }

    public void changeSelectionHost(String map,String robot) {
        // TODO: change map and robot from
    }

    public void changeSelectionPlayer(String robot) {
        // TODO: send new robot to server
    }
}
