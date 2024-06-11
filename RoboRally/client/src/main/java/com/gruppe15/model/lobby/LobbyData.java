package com.gruppe15.model.lobby;

/**
 * To be serialized/deserialized and synchronized with the server.
 */
public class LobbyData {
    private long playerId;
    private long gameId;
    private String playerName;
    private String robotName;
    private int isReady; // Boolean
    private int hasChanged; // Boolean

    private String[] playerNames;
    private String[] robots;
    private int[] areReady; // Booleans
    private String map;
    private String hName;

    public LobbyData() {
        this(-1L, -1L, "", "", -1, -1, new String[]{""}, new String[]{""}, new int[]{-1}, "", "");
    }

    public LobbyData(long playerId, long gameId, String playerName, String robotName, int isReady, int hasChanged, String[] playerNames, String[] robots, int[] areReady, String map, String hName) {
        this.playerId = playerId;
        this.gameId = gameId;
        this.playerName = playerName;
        this.robotName = robotName;
        this.isReady = isReady;
        this.hasChanged = hasChanged;
        this.playerNames = playerNames;
        this.robots = robots;
        this.areReady = areReady;
        this.map = map;
        this.hName = hName;
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

    public String getPlayerName() {
        return playerName;
    }
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getRobotName() {
        return robotName;
    }
    public void setRobotName(String robotName) {
        this.robotName = robotName;
    }

    public int getIsReady() {
        return isReady;
    }
    public void setIsReady(int isReady) {
        this.isReady = isReady;
    }

    public int getHasChanged() {
        return hasChanged;
    }
    public void setHasChanged(int hasChanged) {
        this.hasChanged = hasChanged;
    }

    public String[] getPlayerNames() {
        return playerNames;
    }
    public void setPlayerNames(String[] playerNames) {
        this.playerNames = playerNames;
    }

    public String[] getRobots() {
        return robots;
    }
    public void setRobots(String[] robots) {
        this.robots = robots;
    }

    public int[] getAreReady() {
        return areReady;
    }
    public void setAreReady(int[] areReady) {
        this.areReady = areReady;
    }

    public String getMap() {
        return map;
    }
    public void setMap(String map) {
        this.map = map;
    }

    public String gethName() {
        return hName;
    }
    public void sethName(String hName) {
        this.hName = hName;
    }
}
