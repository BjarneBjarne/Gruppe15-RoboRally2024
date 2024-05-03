package gruppe15.roborally.model.boardelements;


import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class Hole extends BoardElement {
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     */
    public Hole(String imageName) {
        super("hole.png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Space[][] spaces) {
        return false;
    }
}
