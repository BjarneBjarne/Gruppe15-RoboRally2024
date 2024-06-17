package com.group15.roborally.server.model;

import java.util.List;

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
    private Long gameId;

    private int nrOfPlayers;

    private GamePhase phase;

    private String courseName;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Player> players;
}
