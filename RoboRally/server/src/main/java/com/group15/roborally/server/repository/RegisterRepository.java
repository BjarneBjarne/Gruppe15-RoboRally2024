package com.group15.roborally.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Register;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    Register[] findByPlayerId(Long playerId);

    Register[] findByGameId(Long gameId);

    boolean existsByPlayerIdAndTurn(Long playerId, int turn);
}
