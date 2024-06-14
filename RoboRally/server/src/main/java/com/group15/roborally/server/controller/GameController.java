package com.group15.roborally.server.controller;

import java.util.List;

import com.group15.roborally.server.repository.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.GamePhase;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.repository.GameRepository;

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
    @GetMapping
    public ResponseEntity<Long> createGame() {
        
        Game game = new Game();
        game.setNrOfPlayers(0);
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
     * @param playerName - the name of the player joining the game
     * @param gameId - the id of the game to be joined
     * 
     * @return ResponseEntity<Long> - the generated id of the player created
     */
    @PostMapping(value = "/{gameId}/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> joinGame(@RequestBody String playerName, @PathVariable("gameId") Long gameId){

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        if(playerRepository.existsByPlayerNameAndGameId(playerName, gameId)){
            return ResponseEntity.status(409).build();
        }

        Player player = new Player();
        player.setPlayerName(playerName);
        player.setGameId(gameId);
        playerRepository.save(player);

        game.setNrOfPlayers(game.getNrOfPlayers() + 1);
        gameRepository.save(game);

        return ResponseEntity.ok().body(player);
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
    @GetMapping(value = "/{gameId}/players")
    public ResponseEntity<List<Player>> getLobby(@PathVariable("gameId") Long gameId){
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.notFound().build();
        }

        List<Player> players = playerRepository.findAllByGameId(gameId).orElse(null);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }

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
    @GetMapping(value = "/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable("gameId") Long gameId){
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(game);
    }
}
