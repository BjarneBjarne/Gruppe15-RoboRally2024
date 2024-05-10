package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BE_Reboot extends BoardElement {
    private final Heading direction;
    public BE_Reboot(Heading direction) {
        super("reboot.png");
        this.direction = direction;
    }
    public Heading getDirection() {
        return this.direction;
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController, LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
