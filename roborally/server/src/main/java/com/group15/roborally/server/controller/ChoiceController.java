package com.group15.roborally.server.controller;
import java.util.List;

import com.group15.roborally.common.model.ChoiceDTO;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.common.model.Choice;
import com.group15.roborally.server.repository.ChoiceRepository;
import com.group15.roborally.server.repository.GameRepository;

@RestController
@RequestMapping("/choices")

public class ChoiceController {
    private final ChoiceRepository choiceRepository;
    GameRepository gameRepository;

    public ChoiceController(ChoiceRepository choiceRepository, GameRepository gameRepository) {
        this.choiceRepository = choiceRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateChoice(@RequestBody ChoiceDTO choiceDTO) {
        Choice choice = new Choice(choiceDTO.gameId(), choiceDTO.playerId(), choiceDTO.code(), choiceDTO.turn(), choiceDTO.resolveStatus());
        choiceRepository.save(choice);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getChoices(@PathVariable("gameId") long gameId, @RequestParam("turn") int turn) {
        List<Choice> choices = choiceRepository.findAllByGameIdAndTurn(gameId, turn, Sort.by(Sort.Direction.ASC, "choiceId"));
        if (choices.isEmpty()) {
            return ResponseEntity.ok().build();
        } else{
            return ResponseEntity.ok(choices);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getAllChoices(){
        return ResponseEntity.ok(choiceRepository.findAll());
    }
}
