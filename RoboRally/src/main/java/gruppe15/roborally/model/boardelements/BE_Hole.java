package gruppe15.roborally.model.boardelements;


import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BE_Hole extends BoardElement {
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public BE_Hole(String imageName) {
        super("hole.png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Board board, LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
