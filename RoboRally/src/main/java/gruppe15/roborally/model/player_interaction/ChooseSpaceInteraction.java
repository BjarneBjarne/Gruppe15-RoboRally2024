package gruppe15.roborally.model.player_interaction;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;

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
