package com.group15.roborally.server.controller;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.model.Choice;
import com.group15.roborally.server.repository.ChoiceRepository;
import com.group15.roborally.server.repository.GameRepository;

@RestController
@RequestMapping("/choices")

public class ChoiceController {
    ChoiceRepository choiceRepository;
    GameRepository gameRepository;

    public ChoiceController(ChoiceRepository choiceRepository, GameRepository gameRepository) {
        this.choiceRepository = choiceRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateChoice(@RequestBody Choice choice){
        if(choiceRepository.existsById(choice.getPlayerId())){
            choiceRepository.save(choice);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getChoices(@PathVariable("gameId") long gameId, @RequestParam("turn") int turn, @RequestParam("movement") int movement){
        List<Choice> choices = choiceRepository.findAllByGameIdAndTurnAndMovement(gameId, turn, movement);
        int nrOfPlayers = gameRepository.findById(gameId).orElse(null).getNrOfPlayers();
        if(choices.isEmpty()){
            return ResponseEntity.notFound().build();
        } else if (choices.size() != nrOfPlayers){
            System.out.println("Nr. of players: " + nrOfPlayers + " Nr. of choices: " + choices.size());
            return ResponseEntity.ok(null);
        } else{
            return ResponseEntity.ok(choices);
        }

    }
}
