package com.group15.roborally.server.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.group15.roborally.server.model.Player;

import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final Gson gson;

        public GameControllerTest() {
                this.gson = new Gson();
        }

    @Test
    @DirtiesContext
    public void createGameTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/games"))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));

        mockMvc.perform(MockMvcRequestBuilders.get("/games"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    @DirtiesContext
    public void joinGameTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/games"));

        // Player expectedPlayer = new Player();
        // expectedPlayer.setPlayerName("player1");
        // expectedPlayer.setGameId(1L);
        // expectedPlayer.setPlayerId(1L);
        // expectedPlayer.setIsReady(0);
        // String expectedPlayerJson = gson.toJson(expectedPlayer);
        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .andExpect(content().string(expectedPlayerJson));

        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .andExpect(content().string(
                //         "{\"playerId\":2,\"gameId\":1,\"robotName\":null,\"playerName\":\"player2\",\"isReady\":0}"));

        mockMvc.perform(MockMvcRequestBuilders.post("/games/2/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"))
                .andExpect(status().isNotFound());

        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1"))
                .andExpect(status().is(409));
    }

    @Test
    @DirtiesContext
    public void getLobbyTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/games"));

        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(content().string("[]"));

        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1")).andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .andExpect(content().string(
                //         "[{\"playerId\":1,\"gameId\":1,\"robotName\":null,\"playerName\":\"player1\",\"isReady\":0}]"));

        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player2")).andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1/players"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .andExpect(content().string(
                //         "[{\"playerId\":1,\"gameId\":1,\"robotName\":null,\"playerName\":\"player1\",\"isReady\":0},{\"playerId\":2,\"gameId\":1,\"robotName\":null,\"playerName\":\"player2\",\"isReady\":0}]"));

        mockMvc.perform(MockMvcRequestBuilders.get("/games/2/players"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    public void getGameTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/games"));

        mockMvc.perform(MockMvcRequestBuilders.get("/games/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .andExpect(content().string(
                //         "{\"gameId\":1,\"nrOfPlayers\":0,\"phase\":\"LOBBY\",\"courseName\":null,\"players\":[]}"));

        mockMvc.perform(MockMvcRequestBuilders.post("/games/1/join").contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("player1")).andExpect(status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.get("/games/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
                // .content(
                //         "{\"gameId\":1,\"nrOfPlayers\":1,\"phase\":\"LOBBY\",\"courseName\":null,\"players\":[]}"))

        mockMvc.perform(MockMvcRequestBuilders.get("/games/2"))
                .andExpect(status().isNotFound());
    }
}
