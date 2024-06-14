package com.group15.roborally.server.model;

import jakarta.persistence.*;
import java.util.Set;
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
public class Game  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gameId;

    private int nrOfPlayers;
    private GamePhase phase;
    private String courseName;
    
    @OneToMany(mappedBy = "gameId", cascade = CascadeType.ALL)
    private Set<Player> players;
}
