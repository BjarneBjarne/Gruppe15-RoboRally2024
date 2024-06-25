package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.server.model.Interaction;

public interface InteractionRepository extends JpaRepository<Interaction, Long> {

    @Query("SELECT c FROM Interaction c WHERE c.player.gameId = :gameId AND c.movement = :movement")
    List<Interaction> findAllByGameIdAndMovement(@Param("gameId") long gameId, @Param("movement") int movement);

    @Query("SELECT c FROM Interaction c WHERE c.player.gameId = :gameId")
    List<Interaction> findAllByGameId(@Param("gameId") long gameId);

    @Query("SELECT COUNT(DISTINCT c.playerId) FROM Interaction c WHERE c.player.gameId = :gameId AND c.movement = :movement")
    int countDistinctByGameIdAndMovement(@Param("gameId") long gameId, @Param("movement") int movement);

    Interaction findByIdAndMovement(long playerId, int movement);
}
