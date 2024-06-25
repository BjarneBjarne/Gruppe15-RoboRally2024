package com.group15.roborally.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "interactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {

    public Interaction(long playerId, String choice, int turn, int movement) {
        this.playerId = playerId;
        this.choice = choice;
        this.movement = movement;
        this.turn = turn;
    }

    @Id
    long playerId;
    String choice;
    int turn;
    int movement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;
}
