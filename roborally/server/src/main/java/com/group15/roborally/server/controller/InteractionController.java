package com.group15.roborally.server.controller;

import java.util.List;
import java.util.Optional;

import com.group15.roborally.common.model.InteractionDTO;
import com.group15.roborally.common.model.Player;
import com.group15.roborally.server.repository.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.common.model.Interaction;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.InteractionRepository;

@RestController
@RequestMapping("/interactions")
public class InteractionController {
    private final InteractionRepository interactionRepository;
    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public InteractionController(InteractionRepository interactionRepository, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.interactionRepository = interactionRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addInteraction(@RequestBody InteractionDTO interactionDTO) {
        Optional<Player> optionalPlayer = playerRepository.findById(interactionDTO.playerId());

        if (optionalPlayer.isEmpty()) {
            System.err.println("Player not found");
            return ResponseEntity.status(404).build();
        }

        Player player = optionalPlayer.get();
        Interaction interaction = new Interaction(interactionDTO.playerId(), interactionDTO.interaction(), interactionDTO.interactionNo());

        interaction.setPlayer(player); // Set the player reference

        interactionRepository.save(interaction);
        playerRepository.save(player); // Save the player to persist the changes

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Interaction> getInteraction(@PathVariable("playerId") long playerId, @RequestParam("interactionNo") int interactionNo) {
        if (!interactionRepository.existsByPlayerIdAndInteractionNo(playerId, interactionNo)) {
            return ResponseEntity.ok(null);
        }
        Interaction interaction = interactionRepository.findByPlayerIdAndInteractionNo(playerId, interactionNo);
        return ResponseEntity.ok(interaction);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Interaction>> getAllInteractions() {
        List<Interaction> interactions = interactionRepository.findAll();
        return ResponseEntity.ok(interactions);
    }
}

