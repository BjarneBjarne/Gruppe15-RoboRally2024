package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ProgData {
    private long playerId;
    private long gameId;
    private int turnId;
    private String[] ownMoves;
    private String[][] otherMoves;
}
