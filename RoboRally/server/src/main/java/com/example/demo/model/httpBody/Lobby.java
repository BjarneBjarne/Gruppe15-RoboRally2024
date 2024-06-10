package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class Lobby {
    private Long playerId;
    private Long gameId;
    private String playerName;
    private int slotNr;
    private int isReady; // Boolean
    private int hasChanged; // Boolean
    private String[] playerNames;
    private String[] robots;
    private int[] areReady; // Booleans
    private String map;
}
