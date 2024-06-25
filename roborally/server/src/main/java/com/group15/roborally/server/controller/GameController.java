package com.group15.roborally.server.controller;

import java.util.List;

import com.group15.roborally.server.repository.PlayerRepository;
import com.group15.roborally.server.repository.RegisterRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.model.Choice;
import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.GamePhase;
import com.group15.roborally.server.model.Interaction;

import static com.group15.roborally.server.model.GamePhase.*;
import com.group15.roborally.server.model.UpgradeShop;
import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;
import com.group15.roborally.server.repository.ChoiceRepository;
import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.UpgradeShopRepository;

@RestController
@RequestMapping("/games")

public class GameController {
    PlayerRepository playerRepository;
    GameRepository gameRepository;
    UpgradeShopRepository upgradeShopRepository;
    RegisterRepository registerRepository;
    ChoiceRepository choiceRepository;
    InteractionController interactionController;

    public GameController(PlayerRepository playerRepository, GameRepository gameRepository, 
                UpgradeShopRepository upgradeShopRepository, RegisterRepository registerRepository, 
                ChoiceRepository choiceRepository, InteractionController interactionController) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.upgradeShopRepository = upgradeShopRepository;
        this.registerRepository = registerRepository;
        this.choiceRepository = choiceRepository;
        this.interactionController = interactionController;
    }

    /**
     * Endpoint to create a new game and insert it into the database in 'Games' table
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     *          Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @return ResponseEntity<Long> - the generated id of the game created
     */
    @PostMapping
    public ResponseEntity<Long> createGame() {
        Game game = new Game();
        game.setNrOfPlayers(0);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        UpgradeShop upgradeShop = new UpgradeShop();
        upgradeShop.setGameId(game.getGameId());
        upgradeShop.setTurn(0);
        upgradeShopRepository.save(upgradeShop);

        return ResponseEntity.ok().body(game.getGameId());
    }

    /**
     * Endpoint to join an already existing game, update the number of players in game 
     * and insert new player into the database
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     *
     * @param playerName - the name of the player joining the game
     * @param gameId - the id of the game to be joined
     * 
     * @return ResponseEntity<Long> - the generated id of the player created
     */
    @PostMapping(value = "/{gameId}/join", consumes = MediaType.APPLICATION_JSON_VALUE) // TODO: change to post to players instead of join?
    public ResponseEntity<Player> joinGame(@RequestBody String playerName, @PathVariable("gameId") Long gameId){

        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        if(playerRepository.existsByPlayerNameAndGameId(playerName, gameId)){
            return ResponseEntity.status(409).build();
        }

        Player player = new Player();
        player.setPlayerName(playerName);
        player.setGameId(gameId);
        playerRepository.save(player);

        boolean isHost = game.getNrOfPlayers() == 0;
        if (isHost) {
            game.setHostId(player.getPlayerId());
        }
        gameRepository.findById(gameId).ifPresent(this::updateNoOfPlayersByGame);
        
        Register register = new Register();
        register.setPlayerId(player.getPlayerId());
        register.setTurn(0);
        registerRepository.save(register);

        Interaction interaction = new Interaction(player.getPlayerId(), null, -1, -1);
        interactionController.updateInteraction(interaction);

        return ResponseEntity.ok().body(player);
    }

    /**
     * Endpoint to get the list of players in a game
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<List<Player>> - a response entity with the list of players in the game
     */
    @GetMapping(value = "/{gameId}/players")
    public ResponseEntity<List<Player>> getLobby(@PathVariable("gameId") Long gameId){
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.notFound().build();
        }

        List<Player> players = playerRepository.findAllByGameId(gameId).orElse(null);
        if (players == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(players);
    }

    /**
     * Endpoint to get a game by its id
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * 
     * @param gameId - the id of the game
     * 
     * @return ResponseEntity<Game> - a response entity with the game
     */
    @GetMapping(value = "/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable("gameId") Long gameId){
        
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(game);
    }

    @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateGame(@RequestBody Game game, @PathVariable("gameId") Long gameId) {
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.badRequest().build();
        }
        gameRepository.save(game);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to delete a game in the database
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @param gameId - the id of the game to be deleted
     * @return ResponseEntity<String>
     */
    @DeleteMapping(value = "/{gameId}")
    public ResponseEntity<String> deleteGame(@PathVariable("gameId") Long gameId) {
        boolean gameExists = gameRepository.existsById(gameId);
        if (!gameExists) {
            return ResponseEntity.badRequest().build();
        }
        gameRepository.deleteById(gameId);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates the number of players in the game. If there are no players in the game, the game is deleted.
     * @param game The game to update.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void updateNoOfPlayersByGame(Game game) {
        int newNoOfPlayers = playerRepository.findAllByGameId(game.getGameId()).get().size();
        if (newNoOfPlayers == 0) { // Evt. også hvis "game.phase == GamePhase.LOBBY", hvis vi skal gemme spillere under spillet.
            gameRepository.delete(game);
        } else {
            game.setNrOfPlayers(newNoOfPlayers);
            gameRepository.save(game);
        }
    }
}
