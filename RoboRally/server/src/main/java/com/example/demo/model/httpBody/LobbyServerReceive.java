package com.example.demo.model.httpBody;

public record LobbyServerReceive(long playerId, String gameId, String playerName, String robotName, int isReady, String courseName) {
}
