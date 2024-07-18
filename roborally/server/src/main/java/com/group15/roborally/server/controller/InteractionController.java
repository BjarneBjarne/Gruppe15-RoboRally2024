package com.group15.roborally.server.controller;

import java.util.List;

import com.group15.roborally.common.model.InteractionDTO;
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
    GameRepository gameRepository;

    public InteractionController(InteractionRepository interactionRepository, GameRepository gameRepository) {
        this.interactionRepository = interactionRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addInteraction(@RequestBody InteractionDTO interactionDTO) {
        Interaction interaction = new Interaction(interactionDTO.playerId(), interactionDTO.interaction(), interactionDTO.turn(), interactionDTO.movement());
        interactionRepository.save(interaction);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{playerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Interaction> getInteraction(@PathVariable("playerId") long playerId, @RequestParam("turn") int turn, @RequestParam("movement") int movement) {
        System.out.println("Received request!");
        if (!interactionRepository.existsByPlayerIdAndTurnAndMovement(playerId, turn, movement)) {
            return ResponseEntity.ok(null);
        }
        Interaction interaction = interactionRepository.findByPlayerIdAndTurnAndMovement(playerId, turn, movement);
        return ResponseEntity.ok(interaction);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Interaction>> getAllInteractions() {
        List<Interaction> interactions = interactionRepository.findAll();
        return ResponseEntity.ok(interactions);
    }
}

