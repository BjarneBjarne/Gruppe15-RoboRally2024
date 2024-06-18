package com.group15.roborally.server.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.repository.PlayerRepository;

@RestController
@RequestMapping("/players")
public class InitController {

    PlayerRepository playerRepository;

    public InitController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    /**
     * Set the spawnpoint of a player
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @param playerId
     * @return ResponseEntity with status 200 if successful, 404 if player not found
     */
    @PostMapping(value = "/{playerId}/spawnpoint", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postSpawnpoint(@PathVariable long playerId, @RequestBody int[] spawnpoint) {
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.notFound().build();
        }
        Player player = playerRepository.findById(playerId).get();
        if (spawnpoint.length != 2) {
            return ResponseEntity.status(422).build();
        }
        player.setSpawnpoint(spawnpoint);
        playerRepository.save(player);
        return ResponseEntity.ok().build();
    }

    /**
     * Set the spawn direction of a player
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * @param playerId
     * @return ResponseEntity with status 200 if successful, 404 if player not found
     */
    @PostMapping(value = "/{playerId}/spawndirection", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postSpawndirection(@PathVariable long playerId, @RequestBody String direction) {
        if (!playerRepository.existsById(playerId)) {
            return ResponseEntity.notFound().build();
        }
        Player player = playerRepository.findById(playerId).get();
        player.setSpawndirection(direction);
        playerRepository.save(player);
        return ResponseEntity.ok().build();
    }
}
