package com.group15.roborally.server.controller;

import com.group15.roborally.common.model.Game;
import com.group15.roborally.common.model.GamePhase;
import com.group15.roborally.common.model.Player;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/players")
public class PlayerController {
    private final GameController gameController;
    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public PlayerController(PlayerRepository playerRepository, GameRepository gameRepository, GameController gameController) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.gameController = gameController;
    }

    /**
     * Endpoint to create a new player and insert it into the database in 'Players' table
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param playerName - the name of the player to be created
     * 
     * @return ResponseEntity<Long> - the generated id of the player created
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPlayer(@RequestBody String playerName, @PathVariable("gameId") String gameId) {
        Optional<Game> optionalGame = gameRepository.findById(gameId);
        if (optionalGame.isEmpty()) {
            return ResponseEntity.status(404).build();
        }

        Game game = optionalGame.get();
        if (game.getNrOfPlayers() >= 6) {
            return ResponseEntity.status(403).build();
        }
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setReadyForPhase(GamePhase.LOBBY);

        game.getPlayers().add(player);
        player.setGame(game);
        player.setGameId(gameId);

        playerRepository.save(player);
        gameRepository.save(game);

        return ResponseEntity.ok(player.getPlayerId());
    }

    /**
     * Endpoint to update a player in the database in 'Players' table
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param player - the player to be updated
     * @param playerId - the id of the player to be updated
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @PutMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayer(@RequestBody Player player, @PathVariable("playerId") Long playerId) {
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.badRequest().build();
        }

        // If no players - ERROR
        Optional<List<Player>> players = playerRepository.findAllByGameId(player.getGameId());
        if (players.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        // Validate player property change
        AtomicBoolean illegalPlayerProperty = new AtomicBoolean(false);

        gameRepository.findById(player.getGameId()).ifPresent(game -> {
            // When not in lobby
            if (!game.getPhase().equals(GamePhase.LOBBY)) {
                if (players.get().stream().anyMatch(p -> p.getPlayerId() != playerId && Arrays.equals(p.getSpawnPoint(), player.getSpawnPoint()))) {
                    illegalPlayerProperty.set(true);
                }
            }
        });

        if (!illegalPlayerProperty.get()) {
            playerRepository.save(player);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a player in the database in 'Players' table
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     *
     * @param playerId - the id of the player to be deleted
     *
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @DeleteMapping(value = "/{playerId}")
    public ResponseEntity<String> deletePlayer(@PathVariable("playerId") Long playerId) {
        Optional<Player> o_player = playerRepository.findByPlayerId(playerId);
        if (o_player.isPresent()) {
            Player player = o_player.get();
            String gameId = player.getGameId();
            playerRepository.deleteById(playerId);
            //Update the number of players in the game
            gameRepository.findById(gameId).ifPresent(gameController::updateNoOfPlayersByGame);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
