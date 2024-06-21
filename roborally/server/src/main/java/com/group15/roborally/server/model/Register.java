package com.group15.roborally.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import com.example.demo.model.Keys.RegisterId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
// @IdClass(RegisterId.class)
public class Register {
    @Id
    private long playerId;

    private int turn;

    private String[] moves;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    @Override
    public boolean equals(Object o) {
        Register other = (Register) o;
        return playerId == other.playerId
                && turn == other.turn
                && movesEqual(other.moves);
    }

    private boolean movesEqual(String[] otherMoves) {
        if (moves.length != otherMoves.length)
            return false;
        for (int i = 0; i < moves.length; i++)
            if (!moves[i].equals(otherMoves[i]))
                return false;
        return true;
    }

    public boolean hasNull() {
        for (String move : moves) {
            if (move == null || move.equals("")) {
                return true;
            }
        }
        return false;
    }
}