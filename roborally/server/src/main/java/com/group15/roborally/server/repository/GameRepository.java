package com.group15.roborally.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.common.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    public Game findByGameId(Long gameId);
}
