package com.group15.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.model.Register;

public interface RegisterRepository extends JpaRepository<Register, Long> {
    Register findByPlayerId(Long playerId);

    Register[] findByGameId(long gameId);
}
