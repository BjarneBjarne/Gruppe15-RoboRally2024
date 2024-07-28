package com.group15.roborally.server.controller;

import com.group15.roborally.common.model.Game;
import com.group15.roborally.common.model.Player;
import com.group15.roborally.common.model.Register;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.PlayerRepository;
import com.group15.roborally.server.repository.RegisterRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProgController {
    private final GameRepository gameRepository;
    private final PlayerRepository playerRepository;
    private final RegisterRepository registerRepository;

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
     * @param moves
     * @param playerId
     * @param turn
     * @return ResponseEntity<String>
     */
    @PostMapping(value = "/players/{playerId}/registers/{turn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postRegister(@RequestBody String[] moves, @PathVariable("playerId") long playerId, @PathVariable("turn") int turn) {
        Optional<Register> optionalRegister = registerRepository.findById(playerId);
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalRegister.isEmpty()) {
            System.err.println("Register not found");
        }

        if (optionalPlayer.isEmpty()) {
            System.err.println("Player not found");
        }

        if (optionalRegister.isEmpty() || optionalPlayer.isEmpty()) return ResponseEntity.status(404).build();

        Register register = optionalRegister.get();
        register.setMoves(moves);
        register.setTurn(turn);
        if (register.hasNull()) return ResponseEntity.status(422).build();

        Game game = gameRepository.findByGameId(optionalPlayer.get().getGameId());
        if (game.getTurnId() < turn) {
            game.setTurnId(turn);
            gameRepository.save(game);
        }

        //System.out.println("Inserted register " + register);

        registerRepository.save(register);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to get all registers for a game that has been posted this turn
     *
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     *
     * @param gameId
     * @return ResponseEntity<List<Register>>
     */
    @GetMapping(value = "/games/{gameId}/registers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Register>> getRegisters(@PathVariable("gameId") String gameId) {
        //System.out.println("Finding registers for game " + gameId);
        if (!gameRepository.existsById(gameId)) return ResponseEntity.status(404).build();

        int currentTurn = gameRepository.findByGameId(gameId).getTurnId();
        List<Register> registers = registerRepository.findAllByGameIdAndTurn(gameId, currentTurn);

        //System.out.println("Found " + registers.size() + " registers");

        return ResponseEntity.ok(registers);
    }
}
