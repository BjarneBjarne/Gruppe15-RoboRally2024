package com.group15.roborally.server.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class GameControllerTest {

    @Autowired
    private GameRepository gameRepository;
    
    @Test
    public void test() {
    }
}
