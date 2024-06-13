package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Table.Register;

public interface RegisterRepository extends JpaRepository<Register, Long>{
    Register findByPlayerId(Long playerId);
    Register[] findByGameId(long gameId);
}
