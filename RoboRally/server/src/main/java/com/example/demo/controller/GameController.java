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


    /**
     * Endpoint to create a new game and insert it into the database in 'Games' table
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     *          Tobias 
     * 
     * @return ResponseEntity<Long> - the generated id of the game created
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createGame() {
        
        Game game = new Game();
        game.setNrOfPlayers(0);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        return ResponseEntity.ok().body(game.getGameId());
    }

    /**
     * Endpoint to join an already existing game, update the number of players in game 
     * and insert new player into the database
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     *          Tobias 
     * 
     * @param playerId - the name of the player joining the game
     * @param gameId - the id of the game to be joined
     * 
     * @return ResponseEntity<Long> - the generated id of the player created
     */
    @PutMapping(value = "/{gameId}/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> joinGame(@RequestBody String playerName, @PathVariable("gameId") Long gameId){
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }
        if(playerRepository.existsByPlayerNameAndGameId(playerName)){
            return ResponseEntity.badRequest().build();
        }

        Player newPlayer = new Player();
        newPlayer.setPlayerName(playerName);
        newPlayer.setGameId(gameId);
        playerRepository.save(newPlayer);

        game.setNrOfPlayers(game.getNrOfPlayers() + 1);
        gameRepository.save(game);

        return ResponseEntity.ok().body(newPlayer.getPlayerId());

    }

    /**
     * Endpoint to get the list of players in a game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<List<Player>> - a response entity with the list of players in the game
     */
    @GetMapping(value = "/{gameId}/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Player>> getLobby(@PathVariable("gameId") Long gameId){
        List<Player> players = playerRepository.findAllByGameId(gameId);
        return ResponseEntity.ok(players);
    }

    /**
     * Endpoint to get a game by its id
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<Game> - a response entity with the game
     */
    @GetMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Game> getGame(@PathVariable("gameId") Long gameId){
        Game game = gameRepository.findById(gameId).orElse(null);
        return ResponseEntity.ok(game);
    }
}
