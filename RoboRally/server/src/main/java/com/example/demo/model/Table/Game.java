package com.example.demo.model.Table;

import com.example.demo.model.GamePhase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long gameId;
    private int turnId;
    private int hostId;
    private int nrOfPlayers;
    private GamePhase phase;
    private String courseName;

}
