package com.group15.roborally.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.roborally.server.model.Register;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    List<Register> findByPlayerId(Long playerId);

    List<Register> findByGameId(Long gameId);
}
