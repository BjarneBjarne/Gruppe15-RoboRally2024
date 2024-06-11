package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbyServerSend {
    private String[] playerNames;
    private String[] robots;
    private int[] areReady;
    private String map;
    private String hostName;
}
