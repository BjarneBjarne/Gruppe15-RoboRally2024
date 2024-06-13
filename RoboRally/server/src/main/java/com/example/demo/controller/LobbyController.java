package com.example.demo.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.GamePhase;
import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.httpBody.LobbyServerSend;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;

@RestController

public class LobbyController {
    
    PlayerRepository playerRepository;
    GameRepository gameRepository;

    public LobbyController(PlayerRepository playerRepository, GameRepository gameRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
    }

    @PostMapping(value = "/create-game", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Long> createGame() {
        
        Game game = new Game();
        game.setNrOfPlayers(0);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        return ResponseEntity.ok(game.getGameId());
    }

    @GetMapping(value = "/lobby/players", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> getLobby(@PathVariable("gameId") Long gameId){
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.badRequest().build();
        }

        List<Player> players = playerRepository.findAllBygameId(game.getGameId());
        String[] playerNames = new String[players.size() - 1];
        String[] robots = new String[players.size() - 1];
        int[] areReady = new int[players.size() - 1];
        String hostName = "";

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getPlayerId() == game.getHostId()) {
                hostName = player.getPlayerName();
            }
            playerNames[i] = player.getPlayerName();
            robots[i] = player.getRobotName();
            areReady[i] = player.getIsReady();
        }

        LobbyServerSend sendLobby = new LobbyServerSend(playerNames, robots, areReady, game.getCourseName(), hostName);

        return ResponseEntity.ok(sendLobby);
    }

    @GetMapping(value = "/lobby/players",consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> addPlayer(@RequestBody String playerName) {
        
        Player player = new Player();
        player.setPlayerName(playerName);
        player.setRobotName(null);
        player.setGameId(null);
        playerRepository.save(player);

        return ResponseEntity.ok("Player created");
    }
}
