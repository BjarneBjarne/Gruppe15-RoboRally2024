package com.group15.roborally.server.controller;

import java.util.List;

import com.group15.roborally.server.repository.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.GamePhase;
import com.group15.roborally.server.model.Market;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.MarketRepository;

@RestController
@RequestMapping("/games")

public class GameController {
    PlayerRepository playerRepository;
    GameRepository gameRepository;
    MarketRepository marketRepository;

    public GameController(PlayerRepository playerRepository, GameRepository gameRepository, MarketRepository marketRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.marketRepository = marketRepository;
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

        Market market = new Market();
        market.setGameId(game.getGameId());
        market.setTurn(1);
        marketRepository.save(market);

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
            return ResponseEntity.badRequest().build();
        }
        if(playerRepository.existsByPlayerNameAndGameId(playerName, gameId)){
            return ResponseEntity.badRequest().build();
        }

        Player player = new Player();
        player.setPlayerName(playerName);
        player.setGameId(gameId);
        playerRepository.save(player);

        boolean isHost = game.getNrOfPlayers() == 0;
        if (isHost) {
            game.setHostId(player.getPlayerId());
        }
        gameRepository.findById(gameId).ifPresent(this::updateNoOfPlayersByGame);

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

    @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateGame(@RequestBody Game game, @PathVariable("gameId") Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.badRequest().build();
        }
        gameRepository.save(game);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the number of players in the game. If there are no players in the game, the game is deleted.
     * @param game The game to update.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void updateNoOfPlayersByGame(Game game) {
        int newNoOfPlayers = playerRepository.findAllByGameId(game.getGameId()).size();
        if (newNoOfPlayers == 0) {
            gameRepository.delete(game);
        } else {
            game.setNrOfPlayers(newNoOfPlayers);
            gameRepository.save(game);
        }
    }
}
