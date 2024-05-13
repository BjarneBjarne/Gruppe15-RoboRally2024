package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.Objects;

public class BE_Gear extends BoardElement {

    private final String rotateDirection;

    public BE_Gear(String rotateDirection) {
        if (!Objects.equals(rotateDirection, "Right") && !Objects.equals(rotateDirection, "Left"))
            throw new IllegalArgumentException("Invalid direction: " + rotateDirection);
        this.rotateDirection = rotateDirection;
        setImage("gear" + this.rotateDirection + ".png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player == null)
            return false;
        Heading newHeading;
        if (Objects.equals(this.rotateDirection, "Right"))
            newHeading = player.getHeading().next();
        else if (Objects.equals(this.rotateDirection, "Left"))
            newHeading = player.getHeading().prev();
        else
            return false;
        player.setHeading(newHeading);
        return true;
    }

}
