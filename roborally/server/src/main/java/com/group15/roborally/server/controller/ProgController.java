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

    @PostMapping(value = "/players/{playerId}/registers/{turn}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> postRegister(@RequestBody String[] moves, @PathVariable("playerId") long playerId, @PathVariable("turn") int turn) {
        Optional<Player> optionalPlayer = playerRepository.findById(playerId);

        if (optionalPlayer.isEmpty()) {
            System.err.println("Player not found");
            return ResponseEntity.status(404).build();
        }

        Player player = optionalPlayer.get();

        // Find existing register or create a new one
        Register register = registerRepository.findByPlayerIdAndTurn(playerId, turn)
                .orElse(new Register());

        register.setPlayerId(playerId);
        register.setTurn(turn);
        register.setMoves(moves);

        if (register.hasNull()) {
            System.err.println("Register is null for player " + player.getPlayerName() + " on turn " + turn);
            return ResponseEntity.status(422).build();
        }

        Game game = gameRepository.findByGameId(player.getGameId());
        if (game.getTurnId() < turn) {
            game.setTurnId(turn);
            gameRepository.save(game);
        }

        registerRepository.save(register);

        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/games/{gameId}/registers/{turn}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Register>> getRegisters(@PathVariable("gameId") String gameId, @PathVariable("turn") int turn) {
        if (!gameRepository.existsById(gameId)) return ResponseEntity.status(404).build();

        List<Register> registers = registerRepository.findAllByGameIdAndTurn(gameId, turn);

        return ResponseEntity.ok(registers);
    }
}
