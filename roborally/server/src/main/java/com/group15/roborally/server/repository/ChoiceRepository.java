package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.common.model.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    @Query("SELECT c FROM Choice c WHERE c.player.gameId = :gameId AND c.turn = :turn AND c.movement = :movement")
    List<Choice> findAllByGameIdAndTurnAndMovement(@Param("gameId") long gameId, @Param("turn") int turn, @Param("movement") int movement);

    @Query("SELECT c FROM Choice c WHERE c.player.gameId = :gameId")
    List<Choice> findAllByGameId(@Param("gameId") long gameId);

    @Query("SELECT COUNT(DISTINCT c.playerId) FROM Choice c WHERE c.player.gameId = :gameId AND c.turn = :turn AND c.movement = :movement")
    int countDistinctByGameIdAndTurnAndMovement(@Param("gameId") long gameId, @Param("turn") int turn, @Param("movement") int movement);
}
