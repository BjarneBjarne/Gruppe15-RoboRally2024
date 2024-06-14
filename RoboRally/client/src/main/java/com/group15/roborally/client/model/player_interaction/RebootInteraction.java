package com.group15.model.player_interaction;

import com.group15.roborally.controller.GameController;
import com.group15.roborally.server.model.Player;

public class RebootInteraction extends PlayerInteraction {
    public RebootInteraction(GameController gameController, Player player) {
        super(gameController, player);
    }

    @Override
    public void initializeInteraction() {
        gameController.setDirectionOptionsPane(player.getSpace());
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("RebootInteraction - ");
        str.append("Player: \"").append(player.getName()).append("\"");
        str.append(".");
        return str.toString();
    }
}
