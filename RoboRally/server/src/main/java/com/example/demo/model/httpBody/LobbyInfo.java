package com.example.demo.model.httpBody;

public record LobbyInfo(String[] playerNames, String[] robotNames, int[] areReady, String courseName, String hostName) {
}
