package com.group15.roborally.server.controller;

import com.group15.roborally.common.model.UpgradeShop;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.UpgradeShopRepository;
import com.group15.roborally.server.repository.PlayerRepository;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/upgradeShop")

public class UpgradeController {
    PlayerRepository playerRepository;
    GameRepository gameRepository;
    UpgradeShopRepository upgradeShopRepository;

    public UpgradeController(PlayerRepository playerRepository, GameRepository gameRepository, UpgradeShopRepository upgradeShopRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.upgradeShopRepository = upgradeShopRepository;
    }

    /**
     * Endpoint to get the priority of who will upgrade next, in a game
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<Integer> - the priority number of who will upgrade next
     */
    @GetMapping(value = "/{gameId}/turn", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Integer> getTurn(@PathVariable("gameId") long gameId) {
        return ResponseEntity.ok().body(upgradeShopRepository.findById(gameId).orElse(null).getTurn());
    }

    /**
     * Endpoint to get the upgradeShop of a game
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<UpgradeShop> - the upgradeShop of the game
     */
    @GetMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String[]> getUpgradeShop(@PathVariable("gameId") long gameId) {
        UpgradeShop upgradeShop = upgradeShopRepository.findById(gameId).orElse(null);
        String[] upgradeShopCards = upgradeShop.getCards();
        return ResponseEntity.ok().body(upgradeShopCards);
    }

    /**
     * Endpoint to update the upgradeShop of a game
     * 
     * @param upgradeShopCards - the cards to be updated in the upgradeShop
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postUpgradeShop(@RequestBody String[] upgradeShopCards, @PathVariable("gameId") long gameId) {
        UpgradeShop upgradeShop = upgradeShopRepository.findById(gameId).orElse(null);
        upgradeShop.setCards(upgradeShopCards); 
        upgradeShopRepository.save(upgradeShop);
        return ResponseEntity.ok().build();
    }
}
