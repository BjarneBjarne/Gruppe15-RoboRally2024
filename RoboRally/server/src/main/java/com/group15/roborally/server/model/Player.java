package com.group15.roborally.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerId;
    private Long gameId;
    private String robotName;
    private String playerName;
    private int isReady;

    /**
     * Compares whether the player has had variables changed between "this" and the argument playerId.
     * @param player The player at another point. Must have same playerId.
     * @return Whether the player has had any variables changed.
     * @throws IllegalArgumentException If the argument player has another playerId than this.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean hasChanges(Player player) throws IllegalArgumentException {
        if (this.playerId != player.playerId) {
            throw new IllegalArgumentException("This playerId is: \"" + this.playerId + "\". Argument player has playerId: \"" + player.playerId + "\". Can't compare two different players.");
        }
        return !Objects.equals(this.robotName, player.robotName) || !Objects.equals(this.playerName, player.playerName) || this.isReady != player.isReady;
    }
}
