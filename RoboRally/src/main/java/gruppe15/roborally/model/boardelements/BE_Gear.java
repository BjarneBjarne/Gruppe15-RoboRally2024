package gruppe15.roborally.model.boardelements;

import java.util.LinkedList;

import org.jetbrains.annotations.NotNull;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;

public class BE_Gear extends BoardElement {

    private final String direction;

    public BE_Gear(String direction) {
        if (direction != "Right" && direction != "Left")
            throw new IllegalArgumentException("Invalid direction: " + direction);
        this.direction = direction;
        setImage("gear" + this.direction + ".png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null)
            return false;
        Heading newHeading;
        if (this.direction == "Right")
            newHeading = player.getHeading().next();
        else if (this.direction == "Left")
            newHeading = player.getHeading().prev();
        else
            return false;
        player.setHeading(newHeading);
        return true;
    }

}
