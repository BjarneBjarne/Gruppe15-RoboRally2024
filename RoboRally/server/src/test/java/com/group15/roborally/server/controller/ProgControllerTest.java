package com.group15.roborally.server.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;
import com.group15.roborally.server.model.Register;

// A Spring Boot Test which tests the endpoints in the ProgController
@SpringBootTest
@AutoConfigureMockMvc
public class ProgControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson;

    public ProgControllerTest() {
        this.gson = new Gson();
    }

    /**
     * Test the postRegister endpoint in the ProgController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @return Register - the register created to be tested
     * @throws Exception
     */
    private Register createGameAndPlayerWithRegister() throws Exception {
        /*
         * TO FIX: See outcommented lines
         */
        long expectedGameId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.get("/games")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedGameId + ""));
        long expectedPlayerId = 1L;
        mockMvc.perform(MockMvcRequestBuilders.post("/games/{gameId}/join", expectedGameId)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"))
                .andExpect(status().isOk());

        Register register = new Register();
        // register.setGameId(expectedGameId);
        register.setPlayerId(expectedPlayerId);
        // register.setRegisterId(1L);
        register.setTurn(0);
        // register.setM1("card53");
        // register.setM2("card2");
        // register.setM3("card32");
        // register.setM4("card21");
        // register.setM5("card75");
        register.setPlayer(null);

        return register;
    }

    /**
     * Test the postRegister endpoint in the ProgController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void postRegisterTest() throws Exception {
        Register register = createGameAndPlayerWithRegister();

        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/registers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(register)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.post("/players/2/registers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(register)))
                .andExpect(status().isNotFound());
        
        // register.setM3("");
        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/registers/2")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(register)))
                .andExpect(status().isUnprocessableEntity());
    }

    /**
     * Test the getRegisters endpoint in the ProgController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void getRegistersTest() throws Exception {
        Register register = createGameAndPlayerWithRegister();

        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/registers/1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(register)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/registers")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
        // .andExpect(content().string("[" + gson.toJson(register) + "]"))
        ;

        mockMvc.perform(MockMvcRequestBuilders.get("/games/2/registers")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
        
        mockMvc.perform(MockMvcRequestBuilders.get("/games/0/registers")
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound());
    }
}
