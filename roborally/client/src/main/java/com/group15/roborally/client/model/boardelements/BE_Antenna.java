package com.group15.roborally.client.model.boardelements;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.ActionWithDelay;
import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BE_Antenna extends BoardElement {
    public BE_Antenna(Heading direction) {
        super("antenna.png", direction);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
