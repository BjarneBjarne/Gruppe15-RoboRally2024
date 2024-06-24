package com.group15.roborally.server.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "choices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Choice {
    
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
