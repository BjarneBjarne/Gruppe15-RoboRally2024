package com.group15.roborally.client.model.player_interaction;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;
import lombok.Getter;

public abstract class PlayerInteraction {
    protected final GameController gameController;
    @Getter
    protected final Player player;
    private final Runnable callback;

    public PlayerInteraction(GameController gameController, Player player) {
        this.gameController = gameController;
        this.callback = gameController::handleNextInteraction;
        this.player = player;
    }

    public void initializeInteraction() {
        gameController.getServerDataManager().updateInteraction(
                gameController::continueFromInteraction,
                gameController.getCurrentPlayerInteraction().getPlayer().getPlayerId(),
                gameController.getTurnCounter(),
                gameController.getMovementCounter()
        );
    }
    public void interactionFinished() {
        callback.run();
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
