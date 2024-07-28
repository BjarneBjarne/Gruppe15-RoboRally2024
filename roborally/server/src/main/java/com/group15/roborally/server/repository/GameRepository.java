package com.group15.roborally.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.group15.roborally.common.model.Game;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {
    Game findByGameId(String gameId);
}
