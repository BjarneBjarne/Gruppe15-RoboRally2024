package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.common.model.Choice;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    List<Choice> findAllByGameIdAndWaitCount(String gameId, int waitCount, Sort sort);

    @Query("SELECT COUNT(DISTINCT c.playerId) FROM Choice c WHERE c.player.gameId = :gameId AND c.waitCount = :waitCount")
    int countDistinctByGameIdAndTurnAndMovement(@Param("gameId") String gameId, @Param("waitCount") int waitCount);
}
