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

import java.util.Objects;
import java.util.Optional;

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
     * Endpoint to get the upgradeShop of a game
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<UpgradeShop> - the upgradeShop of the game
     */
    @GetMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UpgradeShop> getUpgradeShop(@PathVariable("gameId") String gameId) {
        UpgradeShop upgradeShop = upgradeShopRepository.findByGameId(gameId);
        if (upgradeShop == null) return ResponseEntity.status(404).build();

        return ResponseEntity.ok().body(upgradeShop);
    }

    /**
     * Endpoint to update the upgradeShop of a game
     * 
     * @param upgradeShop - the updated upgradeShop
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<String> - a message indicating the success of the operation
     */
    @PutMapping(value = "/{gameId}/{turn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postUpgradeShop(@RequestBody UpgradeShop upgradeShop, @PathVariable("gameId") String gameId, @PathVariable("turn") int turn) {
        if (upgradeShop == null) return ResponseEntity.notFound().build();
        if (!gameRepository.existsById(gameId)) return ResponseEntity.notFound().build();
        if (!Objects.equals(upgradeShop.getGameId(), gameId)) return ResponseEntity.status(422).build();
        if (upgradeShopRepository.findByGameId(gameId) == null) return ResponseEntity.notFound().build();

        if (upgradeShop.getTurn() < turn) {
            upgradeShop.setTurn(turn);
        }

        upgradeShopRepository.save(upgradeShop);
        return ResponseEntity.ok().build();
    }
}
