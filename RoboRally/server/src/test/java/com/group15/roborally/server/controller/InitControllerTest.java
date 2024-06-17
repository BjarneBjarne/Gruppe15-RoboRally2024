package com.group15.roborally.server.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.google.gson.Gson;

// A Spring Boot Test which tests the endpoints in the InitController
@SpringBootTest
@AutoConfigureMockMvc
public class InitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson;

    public InitControllerTest() {
        this.gson = new Gson();
    }

    /**
     * Test the postSpawnpoint endpoint in the InitController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void postSpawnpointTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"));

        int[] spawnpoint = { 5, 5 };
        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/spawnpoint")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(spawnpoint)))
                .andExpect(status().isOk());

        spawnpoint = new int[] { 5 };
        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/spawnpoint")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(spawnpoint)))
                .andExpect(status().is(422));

        spawnpoint = new int[] { 1, 2, 3 };
        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/spawnpoint")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson(spawnpoint)))
                .andExpect(status().is(422));
    }

    /**
     * Test the postSpawndirection endpoint in the InitController
     * 
     * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
     * 
     * @throws Exception
     */
    @Test
    @DirtiesContext
    public void postSpawndirectionTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/players")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"));

        mockMvc.perform(MockMvcRequestBuilders.post("/players/1/spawndirection")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(gson.toJson("NORTH")))
                .andExpect(status().isOk());

        // TODO: Should error throw error because the direction being send is incorrect
        // mockMvc.perform(MockMvcRequestBuilders.post("/players/1/spawndirection")
        // .contentType(MediaType.APPLICATION_JSON_VALUE)
        // .content("NORTHPOLE"))
        // .andExpect(status().isOk());
    }
}
