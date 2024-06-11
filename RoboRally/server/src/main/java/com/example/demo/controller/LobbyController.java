package com.example.demo.controller;

import com.example.demo.model.GamePhase;
import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.httpBody.Lobby;
import com.example.demo.model.httpBody.LobbyRecieve;
import com.example.demo.model.httpBody.LobbySend;
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

    //================================================================================================
    /*
     * 
     * ENDPOINTS FOR LOBBY POST REQUESTS
     * 
     */
    //================================================================================================

    @PostMapping(value = "/hostGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lobby> hostGame(@RequestBody Lobby lobby){

        // Adding new game to 'Games' table
        Game newGame = new Game();
        newGame.setNrOfPlayers(1);
        newGame.setTurnId(1);
        newGame.setPhase(GamePhase.LOBBY);
        newGame.setMap(lobby.getMap());
        gameRepository.save(newGame);

        // Adding new Player to 'Players' table
        Player player = newPlayer(lobby, newGame.getGId());

        // Updating lobby object
        lobby.setPlayerId(player.getPlayerId());
        lobby.setGameId(newGame.getGId());

        return ResponseEntity.ok(lobby);
    }

    @PostMapping(value = "/joinGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lobby> joinGame(@RequestBody Lobby lobby){
        Game game = gameRepository.findById(lobby.getGameId()).orElse(null);

        if (game == null) {
            return ResponseEntity.badRequest().build();
        }

        // Adding new Player to 'Players' table
        Player player = newPlayer(lobby, game.getGId());
        game.setNrOfPlayers(game.getNrOfPlayers() + 1);
        gameRepository.save(game);
        lobby.setPlayerId(player.getPlayerId());

        // Updating lobby object
        updateLobby(lobby);

        return ResponseEntity.ok(lobby);
    }

    @PostMapping(value = "/updateClient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LobbySend> updateClient(@RequestBody LobbyRecieve body) {

        Player player = playerRepository.findById(body.getPlayerId()).orElse(null);
        if (player == null)
            return ResponseEntity.badRequest().build();

        player.setRobot(body.getRobotName());
        player.setIsReady(body.getIsReady());
        Game game = gameRepository.findById(player.getGId()).orElse(null);
        if (player.getPlayerId() == game.getHostId()) {
            game.setMap(body.getMap());
            gameRepository.save(game);
        }
        playerRepository.save(player);

        return ResponseEntity.ok(getLobbySend(player.getGId()));
    }

    @PostMapping(value = "/leaveGame", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> leaveGame(@RequestBody long playerId) {
        playerRepository.deleteById(playerId);
        return ResponseEntity.ok("Successfully left the game.");
    }

    //================================================================================================
    /*
     * 
     * PRIVATE UTIL FUNCTIONS
     * 
     */
    //================================================================================================

    private Player newPlayer(Lobby lobby, Long gId) {
        Player player = new Player();
        player.setGId(gId);
        player.setPName(lobby.getPlayerName());
        player.setIsReady(0);
        playerRepository.save(player);
        return player;
    }

    private LobbySend getLobbySend(Long gId) {
        LobbySend sendLobby = new LobbySend();

        Game game = gameRepository.findById(gId).orElse(null);
        List<Player> players = playerRepository.findAllBygId(gId);
        String[] playerNames = new String[players.size() - 1];
        String[] robots = new String[players.size() - 1];
        int[] areReady = new int[players.size() - 1];

        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getPlayerId() == game.getHostId()) {
                sendLobby.setHostName(player.getPName());
            }
            playerNames[i] = player.getPName();
            robots[i] = player.getRobot();
            areReady[i] = player.getIsReady();
        }
        sendLobby.setPlayerNames(playerNames);
        sendLobby.setRobots(robots);
        sendLobby.setAreReady(areReady);
        sendLobby.setMap(game.getMap());

        return sendLobby;
    }

    private void updateLobby(Lobby lobby) {
        // Updating info about other players
        List<Player> players = playerRepository.findAllBygId(lobby.getGameId());
        String[] pNames = new String[players.size() - 1];
        String[] robots = new String[players.size() - 1];
        int[] areReady = new int[players.size() - 1];
        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            // Skipping self
            if (player.getPName().equals(lobby.getPlayerName())) {
                continue;
            }
            pNames[i] = player.getPName();
            robots[i] = player.getRobot();
            areReady[i] = player.getIsReady();
        }
        lobby.setPlayerNames(pNames);
        lobby.setRobots(robots);
        lobby.setAreReady(areReady);

        // Updating info about map
        Game game = gameRepository.findById(lobby.getGameId()).orElse(null);
        lobby.setMap(game.getMap());
    }
}
