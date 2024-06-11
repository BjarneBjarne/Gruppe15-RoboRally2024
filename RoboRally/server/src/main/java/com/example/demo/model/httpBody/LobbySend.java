package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LobbySend {
    private String[] playerNames;
    private String[] robots;
    private int[] areReady;
    private String map;
    private String hostName;
}
