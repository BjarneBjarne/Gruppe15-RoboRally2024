package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyServerReceive {
    private long playerId;
    private long gameId;
    private String playerName;
    private String robotName;
    private int isReady;
    private String map;
}
