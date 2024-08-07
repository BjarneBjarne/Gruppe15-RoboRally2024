package com.group15.roborally.common.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    private String gameId;
    private int turnId = 0;
    private long hostId;
    private int nrOfPlayers = 0;
    private GamePhase phase = GamePhase.LOBBY;
    private String courseName;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Player> players = new ArrayList<>();

    @OneToOne(mappedBy = "game", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private UpgradeShop upgradeShop;

    public static class GameIdGenerator {
        @JsonIgnore
        final static String CHARACTERS = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789";
        @JsonIgnore
        final static int ID_LENGTH = 4;
        @JsonIgnore
        final static SecureRandom random = new SecureRandom();

        @JsonIgnore
        public static String generateGameId() {
            StringBuilder sb = new StringBuilder(ID_LENGTH);
            for (int i = 0; i < ID_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return sb.toString();
        }
    }

    /**
     * Compares whether the game has had variables changed between "this" and the argument game.
     * @param otherGameState The game at another point. Must have same gameId.
     * @return Whether the game has had any variables changed.
     * @throws IllegalArgumentException If the argument game has another gameId than this.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @JsonIgnore
    public boolean hasChanges(Game otherGameState) throws IllegalArgumentException {
        if (otherGameState == null) {
            return true;
        }
        if (!this.gameId.equals(otherGameState.gameId)) {
            throw new IllegalArgumentException("This gameId is: \"" + this.gameId + "\". Argument game has gameId: \"" + otherGameState.gameId + "\". Can't compare two different games.");
        }
        return  this.turnId != otherGameState.turnId ||
                this.hostId != otherGameState.hostId ||
                this.nrOfPlayers != otherGameState.nrOfPlayers ||
                this.phase != otherGameState.phase ||
                !Objects.equals(this.courseName, otherGameState.courseName);
    }
}
