package gruppe15.roborally.model.player_interaction;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Player;

public abstract class PlayerInteraction {
    protected final GameController gameController;
    public final Player player;
    private final Runnable callback;

    public PlayerInteraction(GameController gameController, Player player) {
        this.gameController = gameController;
        this.callback = gameController::handleNextInteraction;
        this.player = player;
    }

    public abstract void initializeInteraction();
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
