package com.group15.roborally.server.repository;

import java.util.Arrays;

import com.group15.roborally.common.model.GamePhase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.group15.roborally.common.model.Player;
import com.group15.roborally.common.model.Register;

@DataJpaTest
public class RegisterRepositoryTest {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private RegisterRepository registerRepository;

    Player testPlayer1;
    Player testPlayer2;

    Register register1;
    Register register2;
    Register register3;

    /**
     * Initialize the test environment by deleting all players from the repository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */

    @BeforeEach
    public void initialize() {
        /*
         * TO FIX: See outcommented lines
         */
        playerRepository.deleteAll();

        testPlayer1 = new Player();
        testPlayer1.setPlayerName("Player Name");
        testPlayer1.setRobotName("Robot Name");
        testPlayer1.setReadyForPhase(GamePhase.LOBBY);
        testPlayer1.setGameId(1L);
        playerRepository.save(testPlayer1);

        testPlayer2 = new Player();
        testPlayer2.setPlayerName("Player Name 2");
        testPlayer2.setRobotName("Robot Name 2");
        testPlayer2.setReadyForPhase(GamePhase.LOBBY);
        testPlayer2.setGameId(1L);
        playerRepository.save(testPlayer2);

        register1 = new Register();
        register1.setPlayerId(1L);
        // register1.setGameId(1L);
        register1.setTurn(0);
        register1.setMoves(new String[]{"card53", "card2", "card32", "card21", "card75"});
        registerRepository.save(register1);

        register2 = new Register();
        register2.setPlayerId(2L);
        // register2.setGameId(1L);
        register2.setTurn(1);
        register2.setMoves(new String[]{"card85", "card17", "card36", "card21", "card23"});
        registerRepository.save(register2);

        register3 = new Register();
        register3.setPlayerId(2L);
        // register3.setGameId(2L);
        register3.setTurn(1);
        register2.setMoves(new String[]{"card53", "card2", "card32", "card21", "card75"});
        registerRepository.save(register3);
    }

    /**
     * Test the findByPlayerId method in RegisterRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void findByPlayerIdTest() {
        Register[] registers = registerRepository.findByPlayerId(1L);
        assert (Arrays.asList(registers).contains(register1));
        assert (!Arrays.asList(registers).contains(register2));
        assert (!Arrays.asList(registers).contains(register3));
    }

    /**
     * Test the findByGameId method in RegisterRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void findByGameIdTest() {
        /*
         * TO FIX: See outcommented lines
         */
        // Register[] registers = registerRepository.findByGameId(1L);
        // assert (Arrays.asList(registers).contains(register1));
        // assert (Arrays.asList(registers).contains(register2));
        // assert (!Arrays.asList(registers).contains(register3));
    }

    /**
     * Test the findByGameIdAndPlayerId method in RegisterRepository
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    public void existsByPlayerIdTest() {
        assert (registerRepository.existsByPlayerIdAndTurn(1L, 0));
        assert (registerRepository.existsByPlayerIdAndTurn(2L, 1));
        assert (!registerRepository.existsByPlayerIdAndTurn(3L, 1));
    }
}
