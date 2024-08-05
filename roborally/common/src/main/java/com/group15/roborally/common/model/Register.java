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
    private String[] moves = null;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    @JsonIgnore
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Register other = (Register) o;
        return id == other.id;
    }

    @JsonIgnore
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @JsonIgnore
    public boolean hasNull() {
        return moves == null;
    }

    @JsonIgnore
    public boolean hasChanges(Register otherRegisterState) {
        return  otherRegisterState == null ||
                this.id != otherRegisterState.id ||
                this.playerId != otherRegisterState.playerId ||
                this.turn != otherRegisterState.turn ||
                !Arrays.equals(this.moves, otherRegisterState.moves);
    }
}