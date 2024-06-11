package com.gruppe15.model.lobby;

/**
 * To be serialized/deserialized and synchronized with the server.
 */
public class LobbyData {
    private long playerId; // The player making the request

    // Lobby constants
    private long gameId;
    private long hostId;

    // Lobby variables
    private String courseName;

    // Player constants
    private long[] playerIds;
    private String[] playerNames;

    // Player variables
    private String[] playerRobots;
    private int[] playersReady; // Booleans

    public LobbyData() {
        this(-1, -1, -1, "", new long[]{-1L}, new String[]{""}, new String[]{""}, new int[]{-1});
    }

    public LobbyData(long playerId, long gameId, long hostId, String courseName, long[] playerIds, String[] playerNames, String[] playerRobots, int[] playersReady) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.hostId = hostId;
        this.courseName = courseName;
        this.playerIds = playerIds;
        this.playerNames = playerNames;
        this.playerRobots = playerRobots;
        this.playersReady = playersReady;
    }

    public long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public long getGameId() {
        return gameId;
    }
    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public long getHostId() {
        return hostId;
    }
    public void setHostId(long hostId) {
        this.hostId = hostId;
    }

    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public long[] getPlayerIds() {
        return playerIds;
    }
    public void setPlayerIds(long[] playerIds) {
        this.playerIds = playerIds;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }
    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
    }
    public void setPlayerName(int index, String playerName) {
        this.playerNames[index] = playerName;
    }

    public String[] getPlayerRobots() {
        return playerRobots;
    }
    public void setPlayerRobots(String[] playerRobots) {
        this.playerRobots = playerRobots;
    }

    public int[] getPlayersReady() {
        return playersReady;
    }
    public void setPlayersReady(int[] playersReady) {
        this.playersReady = playersReady;
    }
}
