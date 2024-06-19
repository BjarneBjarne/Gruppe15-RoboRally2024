package com.group15.roborally.server.controller;

import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.PlayerRepository;
import com.group15.roborally.server.repository.RegisterRepository;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgController {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private RegisterRepository registerRepository;

    public ProgController(GameRepository gameRepository, PlayerRepository playerRepository, RegisterRepository registerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.registerRepository = registerRepository;
    }

    /**
     * Endpoint to post a register for a player in a game
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @param register
     * @param playerId
     * @param turn
     * @return ResponseEntity<String> 
     */
    @PostMapping(value = "/players/{playerId}/registers/{turn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postRegister(@RequestBody Register register, @PathVariable("playerId") long playerId, @PathVariable("turn") int turn) {
        Player player = playerRepository.findById(playerId).orElse(null);
        if (player == null) {
            return ResponseEntity.status(404).build();
        }
        /*
         * TO FIX
         */
        // if (playerId <= 0L || turn <= 0 || player.getGameId() != register.getGameId()) {
        //     return ResponseEntity.status(422).build();
        // }
        if (register.hasNull()) {
            return ResponseEntity.status(422).build();
        }
        registerRepository.save(register);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to get all registers for a game
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @param gameId
     * @return ResponseEntity<Register[]> 
     */
    @GetMapping(value = "/games/{gameId}/registers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Register>> getRegisters(@PathVariable("gameId") long gameId) {
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.status(404).build();
        }
        int currentTurn = gameRepository.findById(gameId).orElse(null).getTurnId();
        List<Register> registers = registerRepository.findAllByGameId(gameId);
        for (Register register : registers) {
            if (register.getTurn() != currentTurn) {
                return ResponseEntity.ok(null);
            }
        }
        return ResponseEntity.ok(registers);
        // List<Player> players = playerRepository.findAllByGameId(gameId).orElse(null);
        // if (players == null) {
        //     return ResponseEntity.status(404).build();
        // }
        // Register[] registers = new Register[players.size()];
        // for (int i = 0; i < players.size(); i++) {
        //     Player player = players.get(i);
        //     Register register = registerRepository.findById(player.getPlayerId()).orElse(null);
        //     if (register.getTurn() != currentTurn) {
        //         return ResponseEntity.ok(null);
        //     }
        //     registers[i] = register;
        // }
        /*
         * TO FIX
         */
        // Register[] registers = registerRepository.findByGameId(gameId);
    }
}
