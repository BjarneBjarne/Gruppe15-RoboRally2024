package com.group15.roborally.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
