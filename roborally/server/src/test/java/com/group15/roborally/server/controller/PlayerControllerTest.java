package com.group15.roborally.server.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.group15.roborally.common.model.GamePhase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.group15.roborally.common.model.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

// A Spring Boot Test which tests the endpoints in the PlayerController
@SpringBootTest
@AutoConfigureMockMvc
public class PlayerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private Gson gson;

    public PlayerControllerTest() {
        gson = new GsonBuilder().serializeNulls().create();
    }

    /**
     * Test the createPlayer endpoint in the PlayerController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void createPlayerTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player2"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));

        // mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
        // .content("player1"))
        // .andExpect(status().is(409));
    }

    /**
     * Test the updatePlayer endpoint in the PlayerController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void updatePlayer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"));

        Player player = new Player(1, "1", null, "player1", null, null, GamePhase.LOBBY, 0, null, null, null, null, null);
        mockMvc.perform(MockMvcRequestBuilders.put("/players/1").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(player)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.put("/players/2").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(player)))
                .andExpect(status().isBadRequest());

        player.setPlayerName("player2");
        mockMvc.perform(MockMvcRequestBuilders.put("/players/1").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(player)))
                .andExpect(status().isOk());
    }

    /**
     * Test the deletePlayer endpoint in the PlayerController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void deletePlayer() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/players").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player2"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/1"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/1"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(MockMvcRequestBuilders.delete("/players/2"))
                .andExpect(status().isOk());
    }

    @Test
    @DirtiesContext
    public void orphanRemovalTest() throws Exception {
        // mockMvc.perform(MockMvcRequestBuilders.post("/players")
        //         .contentType(MediaType.APPLICATION_JSON_VALUE)
        //         .content("player1"));

        // mockMvc.perform(MockMvcRequestBuilders.post("/players/1/registers/1")
        //         .contentType(MediaType.APPLICATION_JSON_VALUE)
        //         .content("[]"))
        //         .andExpect(status().isOk());

        // mockMvc.perform(MockMvcRequestBuilders.delete("/players/1"))
        //         .andExpect(status().isOk());

        // mockMvc.perform(MockMvcRequestBuilders.get("/players/1/registers/1"))
        //         .andExpect(status().isNotFound());
    }
}
