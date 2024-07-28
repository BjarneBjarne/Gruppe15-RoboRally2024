package com.group15.roborally.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

    private String gameId;
    private String robotName;
    private String playerName;
    private int[] spawnPoint;
    private String spawnDirection;
    private GamePhase readyForPhase = GamePhase.LOBBY;

    private String[] permCards;
    private String[] tempCards;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "games_gameId", insertable = false, updatable = false)
    @JsonIgnore
    private Game game;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private Register registers;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Choice> choices = new ArrayList<>();

    /**
     * Compares two player objects.
     * @param otherPlayerState The player at another point.
     * @return Whether the player has had any variables changed.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @JsonIgnore
    public boolean hasChanges(Player otherPlayerState) {
        return  otherPlayerState == null ||
                this.playerId != otherPlayerState.playerId ||
                !Objects.equals(this.robotName, otherPlayerState.robotName) ||
                !Objects.equals(this.playerName, otherPlayerState.playerName) ||
                this.readyForPhase != otherPlayerState.readyForPhase ||
                !Arrays.equals(this.spawnPoint, otherPlayerState.spawnPoint) ||
                !Objects.equals(this.spawnDirection, otherPlayerState.spawnDirection);
    }
}
