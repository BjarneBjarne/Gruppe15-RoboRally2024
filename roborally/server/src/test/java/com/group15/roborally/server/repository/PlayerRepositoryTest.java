package com.group15.roborally.server.repository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.group15.roborally.server.model.Player;

// Test class for PlayerRepository
@DataJpaTest
public class PlayerRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    Player testPlayer1;
    Player testPlayer2;

    /**
     * Initialize the test environment by deleting all players from the repository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @BeforeEach
    public void initialize() {
        playerRepository.deleteAll();

        testPlayer1 = new Player();
        testPlayer1.setPlayerName("Player Name");
        testPlayer1.setRobotName("Robot Name");
        testPlayer1.setIsReady(0);
        testPlayer1.setGameId(1L);
        playerRepository.save(testPlayer1);

        testPlayer2 = new Player();
        testPlayer2.setPlayerName("Player Name 2");
        testPlayer2.setRobotName("Robot Name 2");
        testPlayer2.setIsReady(0);
        testPlayer2.setGameId(1L);
        playerRepository.save(testPlayer2);
    }

    /**
     * Test the save method in PlayerRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void SavePlayerTest() {
        assert (playerRepository.existsByPlayerNameAndGameId(testPlayer1.getPlayerName(), testPlayer1.getGameId()));

        assert (playerRepository.existsByPlayerNameAndGameId(testPlayer2.getPlayerName(), testPlayer2.getGameId()));

        assert (playerRepository.existsByPlayerNameAndGameId("Nonexistent Player Name", 0L) == false);
    }

    /**
     * Test the findByPlayerNameAndGameId method in PlayerRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void FindByPlayerNameAndGameIdTest() {
        Player returnedPlayer1 = playerRepository
                .findByPlayerNameAndGameId(testPlayer1.getPlayerName(), testPlayer1.getGameId()).orElse(null);
        assert (testPlayer1 == returnedPlayer1);

        Player returnedPlayer2 = playerRepository
                .findByPlayerNameAndGameId(testPlayer2.getPlayerName(), testPlayer2.getGameId()).orElse(null);
        assert (testPlayer2 == returnedPlayer2);

        assert (playerRepository.findByPlayerNameAndGameId("Nonexistent Player Name", 0L).orElse(null) == null);
    }

    /**
     * Test the findByPlayerId method in PlayerRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void FindPlayerByPlayerIdTest() {
        Player returnedPlayer1 = playerRepository.findByPlayerId(testPlayer1.getPlayerId()).orElse(null);
        assert (testPlayer1 == returnedPlayer1);

        Player returnedPlayer2 = playerRepository.findByPlayerId(testPlayer2.getPlayerId()).orElse(null);
        assert (testPlayer2 == returnedPlayer2);

        assert (playerRepository.findByPlayerId(0L).orElse(null) == null);
    }

    /**
     * Test the findAllByGameId method in PlayerRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void FindAllByGameIdTest() {
        List<Player> returnedPlayers = playerRepository.findAllByGameId(testPlayer1.getGameId()).orElse(null);
        assert (returnedPlayers.size() == 2);
        assert (testPlayer1 == returnedPlayers.get(0));
        assert (testPlayer2 == returnedPlayers.get(1));
    }
}
