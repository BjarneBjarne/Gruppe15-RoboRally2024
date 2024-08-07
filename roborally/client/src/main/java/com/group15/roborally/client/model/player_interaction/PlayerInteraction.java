package com.group15.roborally.client.model.player_interaction;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;
import lombok.Getter;

public abstract class PlayerInteraction {
    protected final GameController gameController;
    @Getter
    protected final Player player;

    public PlayerInteraction(GameController gameController, Player player) {
        this.gameController = gameController;
        this.player = player;
    }

    public void initializeInteraction() {
        gameController.getServerDataManager().waitForInteractionAndCallback(
                gameController::continueFromInteraction,
                gameController.getCurrentPlayerInteraction().getPlayer().getPlayerId(),
                gameController.getInteractionCounter()
        );
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("Generic PlayerInteraction - ");
        str.append("Player: \"").append(player.getName()).append("\"");
        str.append(".");
        return str.toString();
    }
}
