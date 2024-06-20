package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.server.model.Register;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    Register[] findByPlayerId(Long playerId);

    @Query("SELECT r FROM Register r WHERE r.player.gameId = :gameId")
    List<Register> findAllByGameId(@Param("gameId") long gameId);

    boolean existsByPlayerIdAndTurn(Long playerId, int turn);
}
