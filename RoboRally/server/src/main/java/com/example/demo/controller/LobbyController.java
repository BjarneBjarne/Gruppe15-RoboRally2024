package com.example.demo.controller;

import com.example.demo.model.GamePhase;
import com.example.demo.model.Table.Game;
import com.example.demo.model.Table.Player;
import com.example.demo.model.httpBody.Lobby;
import com.example.demo.repository.GameRepository;
import com.example.demo.repository.PlayerRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
//Base endpoint
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
        lobby.setPlayerId(player.getPlayerId());

        // Updating lobby object
        updateLobby(lobby);

        return ResponseEntity.ok(lobby);
    }

    @PostMapping(value = "/updateClient", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Lobby> updateClient(@RequestBody Lobby lobby){
        /* TO DO:
         * Logic for updating tables based on client updates
         * and returning with changes made by other players
         */
        return ResponseEntity.ok(lobby);
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
