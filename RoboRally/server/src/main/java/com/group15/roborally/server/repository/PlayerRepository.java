package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    List<Player> findAllByGameId(Long gameId);

    Player findByPlayerId(Long playerId);

    Player PlayerIdAndGameId(Long playerId, Long gameId);

    boolean existsByPlayerNameAndGameId(String playerName, Long gameId);
}
