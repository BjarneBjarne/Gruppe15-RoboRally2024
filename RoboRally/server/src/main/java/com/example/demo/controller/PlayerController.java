package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Player;
import com.example.demo.repository.PlayerRepository;

@RestController
@RequestMapping("/players")
public class PlayerController {

    PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
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
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param player - the player to be updated
     * @param playerId - the id of the player to be updated
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @PutMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayer(@RequestBody Player player, @PathVariable("playerId") Long playerId) {
        Player playerUpdate = playerRepository.findById(playerId).orElse(null);
        if (player == null) {
            return ResponseEntity.badRequest().build();
        }
        playerUpdate.setRobotName(player.getRobotName());
        playerUpdate.setPlayerName(player.getPlayerName());
        playerUpdate.setIsReady(player.getIsReady());

        playerRepository.save(playerUpdate);
        return ResponseEntity.ok().build();
    }
}
