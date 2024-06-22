package com.group15.roborally.server.model;

import java.util.Arrays;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player/*  implements Serializable */ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long playerId;

    private long gameId;
    private String robotName;
    private String playerName;
    private int[] spawnPoint;
    private String spawnDirection;
    private int isReady;

    private String[] permCards;
    private String[] tempCards;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "games_gameId", insertable = false, updatable = false)
    private Game game;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    @JsonIgnore
    private Register registers;

    /**
     * Compares two player objects.
     * @param otherPlayerState The player at another point.
     * @return Whether the player has had any variables changed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public boolean hasChanges(Player otherPlayerState) {
        return  otherPlayerState == null ||
                this.playerId != otherPlayerState.playerId ||
                ((this.robotName != null || otherPlayerState.robotName != null) && !Objects.equals(this.robotName, otherPlayerState.robotName)) ||
                ((this.playerName != null || otherPlayerState.playerName != null) && !Objects.equals(this.playerName, otherPlayerState.playerName)) ||
                 this.isReady != otherPlayerState.isReady ||
                 !Arrays.equals(this.spawnPoint, otherPlayerState.spawnPoint);
    }
}
