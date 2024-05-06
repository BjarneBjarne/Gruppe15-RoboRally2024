package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

public class BE_EnergySpace extends BoardElement {
    private boolean hasEnergyCube = true;
    public BE_EnergySpace() {
        super("energySpace.png");
    }

    public boolean getHasEnergyCube() {
        return hasEnergyCube;
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull Board board, LinkedList<ActionWithDelay> actionQueue) {
        Player player = space.getPlayer();
        if (player != null) {
            if (hasEnergyCube) {
                hasEnergyCube = false;
                player.addEnergyCube();
            }
            if (board.getCurrentRegister() == 5) {
                player.addEnergyCube();
            }
        }
        return false;
    }
}
