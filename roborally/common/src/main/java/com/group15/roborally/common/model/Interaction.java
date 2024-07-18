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
    private long playerId;
    private String code;
    private int turn;
    private int movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

    public Interaction(long playerId, String code, int turn, int movement) {
        this.playerId = playerId;
        this.code = code;
        this.turn = turn;
        this.movement = movement;
    }
}
