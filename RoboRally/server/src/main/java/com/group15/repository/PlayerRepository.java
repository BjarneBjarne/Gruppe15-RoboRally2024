package com.group15.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findAllByGameId(Long gameId);

    Player findByPlayerId(Long playerId);

    Player PlayerIdAndGameId(String playerName, Long gameId);
}
