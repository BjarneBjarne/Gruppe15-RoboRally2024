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
        boolean idAssigned = false;
        while (!idAssigned) {
            String generatedId = Game.GameIdGenerator.generateGameId();
            Game existing = gameRepository.findByGameId(generatedId);
            if (existing == null) {
                game.setGameId(generatedId);
                idAssigned = true;
            }
        }
        game.setNrOfPlayers(1);
        game.setTurnId(1);
        game.setPhase(GamePhase.LOBBY);
        game.setCourseName(null);

        Player player = new Player();
        player.setGameId(game.getGameId());
        player.setPlayerName(playerName);
        player.setRobotName(null);
        playerRepository.save(player);

        game.setHostId(player.getPlayerId());
        gameRepository.save(game);

        return ResponseEntity.ok(getLobbyServerSend(player.getPlayerId()));
    }

    @PostMapping(value = "/joinLobby", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> joinLobby(@RequestBody LobbyJoin lobby) {
        Game game = gameRepository.findByGameId(lobby.gameId());
        if (game == null)
            return ResponseEntity.badRequest().build();

        Player player = new Player();
        player.setGameId(lobby.gameId());
        player.setPlayerName(lobby.playerName());
        player.setRobotName(null);
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbyServerSend(player.getPlayerId()));
    }

    @PostMapping(value = "/updateLobby", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbyServerSend> updateLobby(@RequestBody LobbyServerReceive body) {
        Game game = gameRepository.findByGameId(body.gameId());
        Player player = playerRepository.findByPlayerId(body.playerId());
        if (player == null)
            return ResponseEntity.badRequest().build();

        player.setRobotName(body.robotName());
        player.setIsReady(body.isReady());
        if (player.getPlayerId() == game.getHostId()) {
            game.setCourseName(body.courseName());
            gameRepository.save(game);
        }
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbyServerSend(player.getPlayerId()));
    }

    @PostMapping(value = "/leaveGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> leaveGame(@RequestBody long playerId) {
        Player player = playerRepository.findByPlayerId(playerId);
        if (player == null) {
            return ResponseEntity.badRequest().build();
        }
        String gameId = player.getGameId();
        playerRepository.delete(player);

        if (playerRepository.findAllByGameId(gameId).isEmpty()) {
            Game game = gameRepository.findByGameId(gameId);
            gameRepository.delete(game);
            return ResponseEntity.ok("Successfully left and closed the game.");
        }

        return ResponseEntity.ok("Successfully left the game.");
    }

    // ================================================================================================
    // PRIVATE UTIL FUNCTIONS
    // ================================================================================================

    private LobbyServerSend getLobbyServerSend(Long playerId) {
        Player playerSend = playerRepository.findByPlayerId(playerId);
        Game game = gameRepository.findByGameId(playerSend.getGameId());
        List<Player> players = playerRepository.findAllByGameId(game.getGameId());
        String[] playerNames = new String[players.size()];
        String[] robots = new String[players.size()];
        int[] areReady = new int[players.size()];
        int hostIndex = -1;

        // Setting the data of the player to receive the message at index 0.
        playerNames[0] = playerSend.getPlayerName();
        robots[0] = playerSend.getRobotName();
        areReady[0] = playerSend.getIsReady();
        if (playerSend.getPlayerId() == game.getHostId()) {
            hostIndex = 0;
        }

        // Looping though players to find host and set other player's data.
        int playerIndex = 1;
        players.remove(playerSend);
        for (Player player : players) {
            if (player.getPlayerId() == game.getHostId()) {
                hostIndex = playerIndex;
            }

            // Other players.
            playerNames[playerIndex] = player.getPlayerName();
            robots[playerIndex] = player.getRobotName();
            areReady[playerIndex] = player.getIsReady();
            playerIndex++;
        }

        return new LobbyServerSend(playerId, game.getGameId(), playerNames, robots, areReady, game.getCourseName(), hostIndex);
    }
}