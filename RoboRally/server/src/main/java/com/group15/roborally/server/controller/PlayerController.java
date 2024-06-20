package com.group15.roborally.server.controller;

import com.group15.roborally.server.model.Player;
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

import com.group15.roborally.server.model.Game;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<Long> createPlayer(@RequestBody String playerName) {
        Player player = new Player();
        player.setPlayerName(playerName);
        playerRepository.save(player);
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

        List<Player> players = Arrays.asList(playerRepository.findById(playerId).get());
        //List<Player> players = Arrays.asList(playerRepository.findById(playerId).get());
        for(Player p : players){
            if(p.getPlayerId() != player.getPlayerId() && Arrays.equals(p.getSpawnPoint(), player.getSpawnPoint())){
                return ResponseEntity.ok().build();
            }
        }

        playerRepository.save(player);

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
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.badRequest().build();
        }
        long gameId = playerRepository.findByPlayerId(playerId).get().getGameId();
        playerRepository.deleteById(playerId);

        //Update the number of players in the game
        gameRepository.findById(gameId).ifPresent(gameController::updateNoOfPlayersByGame);
        return ResponseEntity.ok().build();
    }
}
