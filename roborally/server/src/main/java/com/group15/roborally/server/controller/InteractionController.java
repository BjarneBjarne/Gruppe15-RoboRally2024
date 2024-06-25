package com.group15.roborally.server.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.model.Choice;
import com.group15.roborally.server.model.Interaction;
import com.group15.roborally.server.repository.ChoiceRepository;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.InteractionRepository;

import jakarta.persistence.criteria.CriteriaBuilder.In;

@RestController
@RequestMapping("/interaction")

public class InteractionController {
    private InteractionRepository interactionRepository;
    private GameRepository gameRepository;

    public InteractionController(InteractionRepository interactionRepository, GameRepository gameRepository) {
        this.interactionRepository = interactionRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateInteraction(@RequestBody Interaction interaction){
        interactionRepository.save(interaction);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Interaction>> getInteractions(@PathVariable("gameId") long gameId, @RequestParam("movement") int movement){
        List<Interaction> interactions = interactionRepository.findAllByGameIdAndMovement(gameId, movement);
        int nrOfPlayers = gameRepository.findById(gameId).orElse(null).getNrOfPlayers();
        if(interactions.size() != nrOfPlayers){
            return ResponseEntity.ok(null);
        }
        if(interactions.isEmpty()){
            return ResponseEntity.notFound().build();
        } else{
            return ResponseEntity.ok(interactions);
        }
    }
}
