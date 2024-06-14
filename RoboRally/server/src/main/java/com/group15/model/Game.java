package com.group15.model;

import org.springframework.lang.Nullable;

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
    @Nullable
    private int turnId;
    @Nullable
    private long hostId;
    @Nullable
    private int nrOfPlayers;
    @Nullable
    private GamePhase phase;
    @Nullable
    private String courseName;
}
