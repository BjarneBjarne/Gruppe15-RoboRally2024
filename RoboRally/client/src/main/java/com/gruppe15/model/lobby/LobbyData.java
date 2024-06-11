package com.gruppe15.model.lobby;

/**
 * Equivalent to LobbyServerSend
 */
public record LobbyData(long playerId,
                        String gameId,
                        String[] playerNames,
                        String[] robotNames,
                        int[] areReady,
                        String courseName,
                        int hostIndex) {
}
