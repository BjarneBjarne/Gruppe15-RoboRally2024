package com.example.demo.model.httpBody;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerUpdate {
    private String robotName;
    private String playerName;
    private int isReady;
}
