package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Lobby {
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
}
