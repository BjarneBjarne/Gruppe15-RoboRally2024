package com.example.demo.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Register;
import com.example.demo.model.httpBody.ProgData;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;
import com.example.demo.repository.RegisterRepository;

@RestController
//Base endpoint
@RequestMapping("/Lobby")

public class ProgController {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;
    private RegisterRepository registerRepository;

    public ProgController(GameRepository gameRepository, PlayerRepository playerRepository, RegisterRepository registerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
        this.registerRepository = registerRepository;
    }

    @PostMapping(value = "/registers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProgData> receiveRegisters(@RequestBody ProgData progData){

        Register register = new Register(
            progData.getPlayerId(), 
            progData.getOwnMoves()[0],
            progData.getOwnMoves()[1],
            progData.getOwnMoves()[2],
            progData.getOwnMoves()[3],
            progData.getOwnMoves()[4],
            progData.getTurnId() 
        );
        registerRepository.save(register);
        
        Game game = gameRepository.findById(playerRepository.findById(progData.getPlayerId()).orElse(null).getGameId()).orElse(null);
        if(progData.getTurnId() > game.getTurnId()){
            game.setTurnId(progData.getTurnId());
            gameRepository.save(game);
        }

        if(playersReady(progData.getPlayerId())){
            progData = updateProgData(progData);
            ResponseEntity.ok(progData);
        }

        return ResponseEntity.ok(progData);
    }

    @GetMapping(value = "/registers", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ProgData> getRegisters(@RequestBody ProgData progData){

        if (playersReady(progData.getPlayerId())) {
            progData = updateProgData(progData);
            return ResponseEntity.ok(progData);
        }

        return ResponseEntity.badRequest().build();
    }

    private ProgData updateProgData(ProgData progData){
        /*
         * TODO: Implement this method
         * 
         * This method updates the ProgData object with the registers received from the players
         */
        return progData;
    }

    private boolean playersReady(Long pId){
        /*
         * TODO: Implement this method
         * 
         * This method checks if the amount of registers received is equal to the amount of players in the game
         * 
         * SELECT COUNT(*) AS NrReady
         * Register AS r JOIN Player AS p 
         * WHERE r.pId = p.pId AND p.gId = game.gId
         * 
         * if(NrReady == game.nrOfPlayers) return true;
         * else return false;
         * 
         */
        return false;
    }
}