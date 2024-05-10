package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BE_Checkpoint extends BoardElement {

    private final int number;

    public BE_Checkpoint(int number) {
        super(number + ".png");
        if (number < 1 || 6 < number)
            throw new IllegalArgumentException("Invalid checkpoint number: " + number);
        this.number = number;
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        if (!(space.getBoardElement() instanceof BE_Checkpoint))
            return false;
        Player player = space.getPlayer();
        if (player == null)
            return false;
        if (player.getCheckpoints() == number - 1) {
            player.setCheckpoint(number);
            System.out.println(player.getName() + " has reached checkpoint " + number);
        }
        return true;
    }
}
