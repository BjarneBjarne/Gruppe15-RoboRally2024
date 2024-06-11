package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.demo.model.Table.Game;

public interface GameRepository extends JpaRepository<Game, Long>{

    Game findByGameId(long gameId);
}
