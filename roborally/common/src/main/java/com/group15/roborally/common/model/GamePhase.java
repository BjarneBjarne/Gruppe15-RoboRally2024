package com.group15.roborally.common.model;

public enum GamePhase {
    LOBBY,
    INITIALIZATION,
    PROGRAMMING,
    PLAYER_ACTIVATION,
    BOARD_ACTIVATION,
    UPGRADE;

    public static GamePhase getNextPhase(GamePhase currentPhase, int register) {
        return switch(currentPhase) {
            case LOBBY -> INITIALIZATION;
            case INITIALIZATION, UPGRADE -> PROGRAMMING;
            case PROGRAMMING -> PLAYER_ACTIVATION;
            case PLAYER_ACTIVATION -> BOARD_ACTIVATION;
            case BOARD_ACTIVATION -> register <= 4 ? PLAYER_ACTIVATION : UPGRADE;
        };
    }
}
