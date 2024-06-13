package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{

    List<Player> findAllByGameId(Long gameId);
    Player findByPlayerId(Long playerId);
    Player PlayerIdAndGameId(String playerName, Long gameId);
}
