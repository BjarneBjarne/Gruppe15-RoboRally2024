package com.gruppe15.model.lobby;

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
