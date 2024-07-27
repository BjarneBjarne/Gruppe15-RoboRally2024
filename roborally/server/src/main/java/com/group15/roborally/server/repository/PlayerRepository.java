package com.group15.roborally.server.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.persistence.MapKey;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.common.model.Player;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<List<Player>> findAllByGameId(Long gameId);

    Optional<Player> findByPlayerId(Long playerId);

    Optional<Player> findByPlayerNameAndGameId(String playerName, Long gameId);

    boolean existsByPlayerNameAndGameId(String playerName, Long gameId);
}
