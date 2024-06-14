package com.group15.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group15.model.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
}
