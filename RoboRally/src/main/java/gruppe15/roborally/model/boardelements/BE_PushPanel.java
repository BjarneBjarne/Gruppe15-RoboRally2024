package gruppe15.roborally.model.boardelements;

import java.util.LinkedList;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;

public class BE_PushPanel extends BoardElement {

    public final int[] pushRegisters;
    public final Heading heading;

    public BE_PushPanel(String type, Heading heading) {
        if (type != "24" && type != "135")
            throw new IllegalArgumentException("Invalid direction: " + type);
        if (type == "24") {
            this.pushRegisters = new int[] { 2, 4 };
        } else if (type == "135") {
            this.pushRegisters = new int[] { 1, 3, 5 };
        } else {
            throw new IllegalArgumentException("Invalid direction: " + type);
        }
        this.heading = heading;
        setImage("push" + type + ".png", heading.next().next());

    }

    private static boolean contains(int[] arr, int target) {
        for (int i : arr) {
            if (i == target) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean doAction(Space space, GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null)
            return false;
        int currentRegister = gameController.board.getCurrentRegister();
        if (contains(pushRegisters, currentRegister + 1)) {
            gameController.movePlayerToSpace(player, space.getSpaceNextTo(heading, gameController.board.getSpaces()));
        }
        return true;
    }
}
