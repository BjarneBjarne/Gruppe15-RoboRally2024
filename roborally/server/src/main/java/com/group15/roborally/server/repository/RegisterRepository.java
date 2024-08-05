package com.group15.roborally.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.group15.roborally.common.model.Register;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    Register findByPlayerId(Long playerId);

    @Query("SELECT r FROM Register r WHERE r.player.gameId = :gameId AND r.turn = :turn")
    List<Register> findAllByGameIdAndTurn(@Param("gameId") String gameId, @Param("turn") int turn);

    boolean existsByPlayerIdAndTurn(Long playerId, int turn);

    Optional<Register> findByPlayerIdAndTurn(long playerId, int turn);
}
