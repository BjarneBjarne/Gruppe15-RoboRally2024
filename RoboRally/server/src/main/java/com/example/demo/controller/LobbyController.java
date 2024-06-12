package com.example.demo.controller;

import com.example.demo.model.GamePhase;
import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.httpBody.LobbyJoin;
import com.example.demo.model.httpBody.LobbyServerReceive;
import com.example.demo.model.httpBody.LobbyServerSend;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/Lobby")

public class LobbyController {

    private GameRepository gameRepository;
    private PlayerRepository playerRepository;

    public LobbyController(GameRepository gameRepository, PlayerRepository playerRepository) {
        this.gameRepository = gameRepository;
        this.playerRepository = playerRepository;
    }

    // ================================================================================================
    // ENDPOINTS FOR LOBBY POST REQUESTS
    // ================================================================================================

    @PostMapping(value = "/createLobby", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> createLobby(@RequestBody String playerName) {

        Game game = new Game();
        game.setNrOfPlayers(1);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);
        game.setCourseName(null);
        gameRepository.save(game);

        Player player = new Player();
        player.setGameId(game.getGameId());
        player.setPlayerName(playerName);
        player.setRobotName(null);
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbyServerSend(game.getGameId()));
    }

    @PostMapping(value = "/joinLobby", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> joinLobby(@RequestBody LobbyJoin lobby) {
        Game game = gameRepository.findById(lobby.gameId()).orElse(null);
        if (game == null)
            return ResponseEntity.badRequest().build();

        Player player = new Player();
        player.setGameId(lobby.gameId());
        player.setPlayerName(lobby.playerName());
        player.setRobotName(null);
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbyServerSend(game.getGameId()));
    }

    @PostMapping(value = "/updateLobby", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> updateLobby(@RequestBody LobbyServerReceive body) {

        Player player = playerRepository.findById(body.playerId()).orElse(null);
        if (player == null)
            return ResponseEntity.badRequest().build();

        player.setRobotName(body.robotName());
        player.setIsReady(body.isReady());
        Game game = gameRepository.findById(player.getGameId()).orElse(null);
        if (player.getPlayerId() == game.getHostId()) {
            game.setCourseName(body.courseName());
            gameRepository.save(game);
        }
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbyServerSend(player.getGameId()));
    }

    @PostMapping(value = "/leaveGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> leaveGame(@RequestBody long playerId) {
        playerRepository.deleteById(playerId);
        return ResponseEntity.ok("Successfully left the game.");
    }

    // ================================================================================================
    // PRIVATE UTIL FUNCTIONS
    // ================================================================================================

    private LobbyServerSend getLobbyServerSend(Long playerId) {
        Player player = playerRepository.findById(playerId).orElse(null);
        Game game = gameRepository.findById(player.getGameId()).orElse(null);
        List<Player> players = playerRepository.findAllBygameId(game.getGameId());
        String[] playerNames = new String[players.size() - 1];
        String[] robots = new String[players.size() - 1];
        int[] areReady = new int[players.size() - 1];
        String hostName = "";

        for (int i = 0; i < players.size(); i++) {
            player = players.get(i);
            if (player.getPlayerId() == game.getHostId()) {
                hostName = player.getPlayerName();
            }
            playerNames[i] = player.getPlayerName();
            robots[i] = player.getRobotName();
            areReady[i] = player.getIsReady();
        }

        LobbyServerSend sendLobby = new LobbyServerSend(playerId, game.getGameId(), playerNames, robots, areReady, game.getCourseName(), hostName);
        return sendLobby;
    }
}