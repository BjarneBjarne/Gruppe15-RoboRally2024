package com.example.demo.model.httpBody;

public record LobbyServerSend(long playerId, long gameId, String[] playerNames, String[] robotNames, int[] areReady, String courseName, String hostName) {
}
