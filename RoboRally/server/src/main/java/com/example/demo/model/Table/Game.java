package com.example.demo.model.Table;

import com.example.demo.model.GamePhase;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.security.SecureRandom;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Game {
    @Id
    private String gameId;

    private int turnId;
    private long hostId;
    private int nrOfPlayers;
    private GamePhase phase;
    private String courseName;

    public static class GameIdGenerator {
        final static String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final static int ID_LENGTH = 4;
        final static SecureRandom random = new SecureRandom();

        public static String generateGameId() {
            StringBuilder sb = new StringBuilder(ID_LENGTH);
            for (int i = 0; i < ID_LENGTH; i++) {
                sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            return sb.toString();
        }
    }
}
