package com.group15.roborally.server.controller;

import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.PlayerRepository;
import com.group15.roborally.server.repository.RegisterRepository;
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
        if (register.getM1().equals(null)
            || register.getM2().equals(null) 
            || register.getM3().equals(null) 
            || register.getM4().equals(null) 
            || register.getM5().equals(null)
            || register.getM1().equals("")
            || register.getM2().equals("")
            || register.getM3().equals("")
            || register.getM4().equals("")
            || register.getM5().equals("")) {
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
    public ResponseEntity<Register[]> getRegisters(@PathVariable("gameId") long gameId) {
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.status(404).build();
        }
        /*
         * TO FIX
         */
        // Register[] registers = registerRepository.findByGameId(gameId);
        return ResponseEntity.ok(null);
    }
}
