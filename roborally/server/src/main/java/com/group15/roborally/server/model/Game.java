package com.group15.roborally.server.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

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

    private long hostId;

    private int nrOfPlayers = 0;
    
    private GamePhase phase;

    private String courseName;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL)
    private List<Player> players;
    
    /**
     * Compares whether the game has had variables changed between "this" and the argument game.
     * @param otherGameState The game at another point. Must have same gameId.
     * @return Whether the game has had any variables changed.
     * @throws IllegalArgumentException If the argument game has another gameId than this.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean hasChanges(Game otherGameState) throws IllegalArgumentException {
        if (otherGameState == null) {
            return true;
        }
        if (this.gameId != otherGameState.gameId) {
            throw new IllegalArgumentException("This gameId is: \"" + this.gameId + "\". Argument game has gameId: \"" + otherGameState.gameId + "\". Can't compare two different games.");
        }
        return  this.turnId != otherGameState.turnId ||
                this.hostId != otherGameState.hostId ||
                this.nrOfPlayers != otherGameState.nrOfPlayers ||
                this.phase != otherGameState.phase ||
                ((this.courseName != null || otherGameState.courseName != null) && !Objects.equals(this.courseName, otherGameState.courseName));
    }
}
