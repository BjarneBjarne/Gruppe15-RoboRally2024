package com.example.demo.model.httpBody;

public record LobbyServerSend(long playerId,
                              String gameId,
                              String[] playerNames,
                              String[] robotNames,
                              int[] areReady,
                              String courseName,
                              int hostIndex) {
}
