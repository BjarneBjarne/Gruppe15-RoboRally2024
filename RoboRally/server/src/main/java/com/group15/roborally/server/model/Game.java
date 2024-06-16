package com.group15.roborally.server.model;

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
    private long hostId;
    private int nrOfPlayers = 0;
    private GamePhase phase;
    private String courseName;

    /**
     * Compares whether the game has had variables changed between "this" and the argument game.
     * @param game The game at another point. Must have same gameId.
     * @return Whether the game has had any variables changed.
     * @throws IllegalArgumentException If the argument game has another gameId than this.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean hasChanged(Game game) throws IllegalArgumentException {
        if (this.gameId != game.gameId) {
            throw new IllegalArgumentException("This gameId is: \"" + this.gameId + "\". Argument game has gameId: \"" + game.gameId + "\". Can't compare two different games.");
        }
        return  this.turnId != game.turnId ||
                this.hostId != game.hostId ||
                this.nrOfPlayers != game.nrOfPlayers ||
                this.phase != game.phase || (this.courseName != null && game.courseName != null && this.courseName.equals(game.courseName));
    }
}
