package com.group15.roborally.server.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "choices")
// @IdClass(ChoiceId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Choice {

    public Choice(long playerId, String choice, int turn, int movement) {
        this.playerId = playerId;
        this.choice = choice;
        this.turn = turn;
        this.movement = movement;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long choiceId;

    long playerId;
    
    String choice;
    
    int turn;
    
    int movement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerId", referencedColumnName = "playerId", insertable = false, updatable = false)
    @JsonIgnore
    private Player player;

}
