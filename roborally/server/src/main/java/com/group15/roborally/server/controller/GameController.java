package com.group15.roborally.server.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.group15.roborally.common.model.*;
import com.group15.roborally.server.repository.PlayerRepository;
import com.group15.roborally.server.repository.RegisterRepository;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group15.roborally.server.repository.GameRepository;
import com.group15.roborally.server.repository.UpgradeShopRepository;

@RestController
@RequestMapping("/games")
public class GameController {
    private final PlayerRepository playerRepository;
    private final GameRepository gameRepository;
    private final UpgradeShopRepository upgradeShopRepository;
    private final RegisterRepository registerRepository;

    public GameController(PlayerRepository playerRepository, GameRepository gameRepository,
                          UpgradeShopRepository upgradeShopRepository, RegisterRepository registerRepository) {
        this.playerRepository = playerRepository;
        this.gameRepository = gameRepository;
        this.upgradeShopRepository = upgradeShopRepository;
        this.registerRepository = registerRepository;
    }

    /**
     * Endpoint to create a new game and insert it into the database in 'Games' table
     * 
     * @author  Marcus Rémi Lemser Eychenne, s230985
     *          Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @return ResponseEntity<String> - the generated id of the game created
     */
    @PostMapping
    public ResponseEntity<String> createGame() {
        Game game = new Game();
        boolean idAssigned = false;
        while (!idAssigned) {
            String generatedId = Game.GameIdGenerator.generateGameId();
            if (!gameRepository.existsById(generatedId)) {
                game.setGameId(generatedId);
                idAssigned = true;
            }
        }
        game.setNrOfPlayers(0);
        game.setPhase(GamePhase.LOBBY);

        gameRepository.save(game);

        UpgradeShop upgradeShop = new UpgradeShop();
        upgradeShop.setGameId(game.getGameId());
        upgradeShop.setTurn(-1);
        upgradeShopRepository.save(upgradeShop);

        return ResponseEntity.ok(game.getGameId());
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
     * @return ResponseEntity<Player> - the player created
     */
    @PostMapping(value = "/{gameId}/join", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Player> joinGame(@RequestBody String playerName, @PathVariable("gameId") String gameId){
        Game game = gameRepository.findById(gameId).orElse(null);
        if (game == null) return ResponseEntity.notFound().build();
        if (game.getNrOfPlayers() >= 6) return ResponseEntity.noContent().build();
        if (playerRepository.existsByPlayerNameAndGameId(playerName, gameId)) return ResponseEntity.status(409).build();

        Player player = new Player();
        player.setPlayerName(playerName);
        player.setGameId(gameId);
        playerRepository.save(player);

        boolean isHost = game.getNrOfPlayers() == 0;
        if (isHost) {
            game.setHostId(player.getPlayerId());
            gameRepository.save(game);
        }
        gameRepository.findById(gameId).ifPresent(this::updateNoOfPlayersByGame);

        Register register = new Register();
        register.setPlayerId(player.getPlayerId());
        register.setTurn(0);
        registerRepository.save(register);

        return ResponseEntity.ok(player);
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
    public ResponseEntity<Map<Long, Player>> getLobby(@PathVariable("gameId") String gameId) {
        if (!gameRepository.existsById(gameId)) {
            return ResponseEntity.notFound().build();
        }

        Optional<List<Player>> o_players = playerRepository.findAllByGameId(gameId);
        Optional<Map<Long, Player>> o_playerMap = o_players.map(players -> players.stream()
                .collect(Collectors.toMap(Player::getPlayerId, player -> player)));

        return o_playerMap.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
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
    public ResponseEntity<Game> getGame(@PathVariable("gameId") String gameId) {
        Game game = gameRepository.findByGameId(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(game);
    }

    @PutMapping(value = "/{gameId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> updateGame(@RequestBody Game game, @PathVariable("gameId") String gameId) {
        if (!Objects.equals(game.getGameId(), gameId) || !gameRepository.existsById(gameId)) {
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
    public ResponseEntity<String> deleteGame(@PathVariable("gameId") String gameId) {
        if (!gameRepository.existsById(gameId)) {
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
        Optional<List<Player>> o_players = playerRepository.findAllByGameId(game.getGameId());
        if (o_players.isPresent()) {
            int newNoOfPlayers = o_players.get().size();
            if (newNoOfPlayers == 0) {
                gameRepository.delete(game);
            } else {
                game.setNrOfPlayers(newNoOfPlayers);
                gameRepository.save(game);
            }
        } else {
            gameRepository.delete(game);
        }
    }
}
