package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Table.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{
    List<Player> findAllByGameId(String gameId);
    Player findByPlayerId(Long playerId);
}
