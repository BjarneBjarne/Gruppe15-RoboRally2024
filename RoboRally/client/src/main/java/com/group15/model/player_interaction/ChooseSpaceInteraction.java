package com.group15.model.player_interaction;

import com.group15.controller.GameController;
import com.group15.model.Player;
import com.group15.model.Space;

import java.util.List;

public class ChooseSpaceInteraction extends PlayerInteraction {
    private final List<Space> spaces;

    public ChooseSpaceInteraction(GameController gameController, Player player, List<Space> spaces) {
        super(gameController, player);
        this.spaces = spaces;
    }
    public List<Space> getSpaces() {
        return spaces;
    }

    @Override
    public void initializeInteraction() {

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("ChooseSpaceInteraction - ");
        str.append("Player: \"").append(player.getName()).append("\"");
        str.append(", Options: \"").append(spaces.toString()).append("\"");
        str.append(".");
        return str.toString();
    }
}
