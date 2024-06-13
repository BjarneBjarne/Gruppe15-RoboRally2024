package com.example.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.GamePhase;
import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.httpBody.PlayerUpdate;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;

@RestController

public class LobbyController {
    
    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public LobbyController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @PostMapping(value = "/create-game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createGame() {
        
        Game game = new Game();
        game.setNrOfPlayers(0);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        return ResponseEntity.ok(game.getGameId());
    }

    @PutMapping(value = "/join-game/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> joinGame(@RequestBody Long playerId, @PathVariable("gameId") Long gameId){
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }
        
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null) {
            return ResponseEntity.badRequest().build();
        }
        
        player.setGameId(game.getGameId());
        playerRepository.save(player);

        game.setNrOfPlayers(game.getNrOfPlayers() + 1);
        gameRepository.save(game);

        return ResponseEntity.ok().body(gameId);

    }

    @GetMapping(value = "/games/{gameId}/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getLobby(@PathVariable("gameId") Long gameId){
        List<Player> players = playerRepository.findAllByGameId(gameId);
        return ResponseEntity.ok(players);
    }
}
