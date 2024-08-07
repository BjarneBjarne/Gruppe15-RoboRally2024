package com.group15.roborally.server.controller;
import java.util.List;
import java.util.Optional;

import com.group15.roborally.common.model.ChoiceDTO;
import com.group15.roborally.common.model.Player;
import com.group15.roborally.server.repository.PlayerRepository;
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
    private final PlayerRepository playerRepository;
    GameRepository gameRepository;

    public ChoiceController(ChoiceRepository choiceRepository, PlayerRepository playerRepository, GameRepository gameRepository) {
        this.choiceRepository = choiceRepository;
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateChoice(@RequestBody ChoiceDTO choiceDTO) {
        Optional<Player> optionalPlayer = playerRepository.findById(choiceDTO.playerId());

        if (optionalPlayer.isEmpty()) {
            System.err.println("Player not found");
            return ResponseEntity.status(404).build();
        }

        Player player = optionalPlayer.get();
        Choice choice = new Choice(choiceDTO.gameId(), choiceDTO.playerId(), choiceDTO.code(), choiceDTO.waitCount(), choiceDTO.resolveStatus());

        choice.setPlayer(player);

        choiceRepository.save(choice);
        playerRepository.save(player);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/{gameId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Choice>> getChoices(@PathVariable("gameId") String gameId, @RequestParam("waitCount") int waitCount) {
        List<Choice> choices = choiceRepository.findAllByGameIdAndWaitCount(gameId, waitCount, Sort.by(Sort.Direction.ASC, "choiceId"));
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
