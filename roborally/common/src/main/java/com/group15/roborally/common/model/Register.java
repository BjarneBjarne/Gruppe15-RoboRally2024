package com.group15.roborally.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

// import com.example.demo.model.Keys.RegisterId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "registers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Register {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long playerId;
    private int turn;
    private String[] moves;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (!o.getClass().equals(this.getClass())) return false;
        Register other = (Register) o;
        return playerId == other.playerId
                && turn == other.turn
                && movesEqual(other.moves);
    }

    @JsonIgnore
    private boolean movesEqual(String[] otherMoves) {
        if (moves.length != otherMoves.length)
            return false;
        for (int i = 0; i < moves.length; i++)
            if (!moves[i].equals(otherMoves[i]))
                return false;
        return true;
    }

    @JsonIgnore
    public boolean hasNull() {
        return moves == null;
    }

    @JsonIgnore
    public boolean hasChanges(Register otherRegisterState) {
        return otherRegisterState == null ||
                this.playerId != otherRegisterState.playerId ||
                this.turn != otherRegisterState.turn ||
                !Arrays.equals(this.moves, otherRegisterState.moves);
    }
}