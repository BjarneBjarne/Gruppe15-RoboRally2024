package com.gruppe15.model.boardelements;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.ActionWithDelay;
import com.gruppe15.model.Heading;
import com.gruppe15.model.Space;
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
