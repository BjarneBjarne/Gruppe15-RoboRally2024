package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.Table.Register;
import com.example.demo.model.httpBody.PlayerUpdate;
import com.example.demo.model.httpBody.ProgData;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.RegisterRepository;

@RestController
//Base endpoint
@RequestMapping("/players")

public class PlayerController {

    PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createPlayer(@RequestBody String playerName) {
        Player player = new Player();
        player.setPlayerName(playerName);
        playerRepository.save(player);
        return ResponseEntity.ok(player.getPlayerId());
    }

    @PutMapping(value = "/{playerId}",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updatePlayer(@RequestBody PlayerUpdate playerUpdate) {
        Player player = playerRepository.findById(playerUpdate.getPlayerId()).orElse(null);
        if (player == null) {
            return ResponseEntity.badRequest().build();
        }
        player.setRobotName(playerUpdate.getRobotName());
                
        playerRepository.save(player);
        return ResponseEntity.ok().build();
    }
    
}
