package com.group15.roborally.server.model;

import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "players")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Player/*  implements Serializable */ {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long playerId;

    
    private Long gameId;

    private String robotName;

    private String playerName;

    private int isReady;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "games_gameId", insertable = false, updatable = false)
    private Game game;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Register> registers;
}
