package com.group15.roborally.common.model;

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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long interactionId;

    long playerId;
    String choice;
    int turn;
    int movement;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    public Interaction(long playerId, String choice, int turn, int movement) {
        this.playerId = playerId;
        this.choice = choice;
        this.movement = movement;
        this.turn = turn;
    }
}
