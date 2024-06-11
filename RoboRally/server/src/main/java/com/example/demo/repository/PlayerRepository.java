package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Table.Player;

public interface PlayerRepository extends JpaRepository<Player, Long>{

    List<Player> findAllBygId(Long gId);
    Player findByPlayerId(Long playerId);
}
