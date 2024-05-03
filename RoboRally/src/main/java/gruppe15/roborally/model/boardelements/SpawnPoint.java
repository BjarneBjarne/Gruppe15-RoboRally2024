package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

public class SpawnPoint extends BoardElement {
    public SpawnPoint() {
        super("startField.png");
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Space[][] spaces) {
        return false;
    }
}
