package com.example.demo.model.httpBody;

public record LobbyServerSend(String[] playerNames, String[] robotNames, int[] areReady, String courseName, String hostName) {
}
