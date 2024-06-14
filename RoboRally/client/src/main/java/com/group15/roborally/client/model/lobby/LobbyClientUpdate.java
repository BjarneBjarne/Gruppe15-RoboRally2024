package com.group15.roborally.client.model.lobby;

/**
 * Equivalent to LobbyServerReceive
 */
public record LobbyClientUpdate(long playerId,
                                String gameId,
                                String playerName,
                                String robotName,
                                int isReady,
                                String courseName) {
}
