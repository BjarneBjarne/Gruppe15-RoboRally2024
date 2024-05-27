package gruppe15.roborally.model.player_interaction;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Player;

public class RebootInteraction extends PlayerInteraction {
    public RebootInteraction(GameController gameController, Player player) {
        super(gameController, player);
    }

    @Override
    public void initializeInteraction() {
        gameController.setDirectionOptionsPane(player.getTemporarySpace(), player);
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
