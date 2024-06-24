package com.group15.roborally.client.model.player_interaction;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;

public class RebootInteraction extends PlayerInteraction {
    public RebootInteraction(GameController gameController, Player player) {
        super(gameController, player);
    }

    @Override
    public void initializeInteraction() {
        gameController.setDirectionOptionsPane(player, player.getSpace());
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
