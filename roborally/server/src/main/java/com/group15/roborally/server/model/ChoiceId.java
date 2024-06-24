package com.group15.roborally.server.model;

import java.io.Serializable;
import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChoiceId implements Serializable {
    private long playerId;
    private int turn;
    int movement;

    // Default constructor
    public ChoiceId() {}

    // Parameterized constructor
    public ChoiceId(long playerId, int turn, int movement) {
        this.playerId = playerId;
        this.turn = turn;
        this.movement = movement;
    }


    // Override equals and hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChoiceId choiceId = (ChoiceId) o;
        return playerId == choiceId.playerId && turn == choiceId.turn && movement == choiceId.movement;
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerId, turn, movement);
    }
}
