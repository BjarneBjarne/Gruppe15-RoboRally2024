package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyRecieve {
    private long playerId;
    // private long gameId;
    private String robotName;
    private int isReady;
    private String map;
}
