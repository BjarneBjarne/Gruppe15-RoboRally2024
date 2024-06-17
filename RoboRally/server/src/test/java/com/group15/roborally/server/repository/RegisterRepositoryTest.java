package com.group15.roborally.server.repository;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.group15.roborally.server.model.Player;
import com.group15.roborally.server.model.Register;

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

        register1 = new Register();
        register1.setPlayerId(1L);
        register1.setGameId(1L);
        register1.setM1("card53");
        register1.setM2("card2");
        register1.setM3("card32");
        register1.setM4("card21");
        register1.setM5("card75");
        registerRepository.save(register1);

        // register2 = new Register();
        // register2.setPlayerId(1L);
        // register2.setGameId(1L);
        // register2.setM1("card85");
        // register2.setM2("card17");
        // register2.setM3("card36");
        // register2.setM4("car21");
        // register2.setM5("card23");
        // registerRepository.save(register2);
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
        Register register = registerRepository.findByPlayerId(1L);
        assert (register.equals(register1));
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
        Set<Register> register = registerRepository.findByGameId(1L);
        assert (register.iterator().next().equals(register1));
    }
}
