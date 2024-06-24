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

    @PostMapping(value = "/{playerId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateChoice(@RequestBody List<Choice> choices){
        System.out.println("\n\nUpdating choices");
        for(Choice choice : choices){
            System.out.println("Inserting choice: " + choice.getChoice());
            choiceRepository.save(choice);
            System.out.println("Choice inserted");
        }
        System.out.println("Choices updated\n\n");
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getChoices(@PathVariable("gameId") long gameId, @RequestParam("turn") int turn, @RequestParam("movement") int movement){
        List<Choice> choices = choiceRepository.findAllByGameIdAndTurnAndMovement(gameId, turn, movement);
        int nrOfPlayers = gameRepository.findById(gameId).orElse(null).getNrOfPlayers();
        int nrOfPlayerInput = choiceRepository.countDistinctByTurnAndMovement(turn, movement);
        if(choices.isEmpty()){
            return ResponseEntity.notFound().build();
        } else if (nrOfPlayerInput < nrOfPlayers){
            System.out.println("Nr. of players: " + nrOfPlayers + " Nr. of choices: " + choices.size());
            return ResponseEntity.ok(null);
        } else{
            return ResponseEntity.ok(choices);
        }

    }
}
