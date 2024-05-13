package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a checkpoint on the board and when
 * a player reaches a checkpoint, the player's checkpoint number is updated.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 */
public class BE_Checkpoint extends BoardElement {

    private final int number;
    private final int total;

    /**
     * Constructor for the checkpoint
     * @param number the number of the checkpoint
     * @param total the total number of checkpoints on the board
     */
    public BE_Checkpoint(int number, int total) {
        super(number + ".png");
        if (number < 1 || 6 < number)
            throw new IllegalArgumentException("Invalid checkpoint number: " + number);
        this.number = number;
        this.total = total;
    }

    /**
     * When a player reaches a checkpoint, the player's checkpoint number is updated.
     * @param space the space where the player is located
     * @param gameController the game controller
     * @param actionQueue the queue of actions
     */
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
            if(number == total){
                gameController.setWinner(player.getName(), player.getCharImage());
            }
        }
        return true;
    }
}
