package com.example.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Game;
import com.example.demo.model.GamePhase;
import com.example.demo.model.Player;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;

@RestController
@RequestMapping("/games")

public class GameController {

    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public GameController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }
    
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createGame() {
        
        Game game = new Game();
        game.setNrOfPlayers(0);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        return ResponseEntity.ok().body(game.getGameId());
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

    @GetMapping(value = "/{gameId}/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getLobby(@PathVariable("gameId") Long gameId){
        List<Player> players = playerRepository.findAllByGameId(gameId);
        return ResponseEntity.ok(players);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @RequestMapping(value = "/{gameId}")
    public ResponseEntity<Long> addPlayer(@RequestBody String playerName, @PathVariable("gameId") Long gameId){
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }

        Player player = new Player();
        player.setPlayerName(playerName);
        player.setRobotName(null);
        player.setGameId(game.getGameId());
        playerRepository.save(player);

        game.setNrOfPlayers(game.getNrOfPlayers() + 1);
        gameRepository.save(game);

        return ResponseEntity.ok().body(player.getPlayerId());
    }
}
