package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yaml.snakeyaml.error.Mark;

import com.example.demo.model.Market;
import com.example.demo.model.Player;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.MarketRepository;
import com.example.demo.repository.PlayerRepository;

@RestController
@RequestMapping("/market")

public class UpgradeController {

    PlayerRepository playerRepository;
    GameRepository gameRepository;
    MarketRepository markRepository;

    public UpgradeController(PlayerRepository playerRepository, GameRepository gameRepository, MarketRepository markRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.markRepository = markRepository;
    }
    
    @GetMapping(value = "/{gameId}/turn", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getTurn(@PathVariable("gameId") long gameId) {
        return ResponseEntity.ok().body(markRepository.findById(gameId).orElse(null).getTurn());
    }

    @GetMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Market> getMarket(@PathVariable("gameId") long gameId) {
        return ResponseEntity.ok(markRepository.findById(gameId).orElse(null));
    }

    @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postMarket(@RequestBody String[] marketCards, @PathVariable("gameId") long gameId) {
        Market market = markRepository.findById(gameId).orElse(null);
        market.setCards(marketCards); 
        markRepository.save(market);
        return ResponseEntity.ok().build();
    }
}
