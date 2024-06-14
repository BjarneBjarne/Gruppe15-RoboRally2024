package com.group15.roborally.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<List<Player>> findAllByGameId(Long gameId);

    Optional<Player> findByPlayerId(Long playerId);

    Optional<Player> findByPlayerNameAndGameId(String playerName, Long gameId);

    boolean existsByPlayerNameAndGameId(String playerName, Long gameId);
}
