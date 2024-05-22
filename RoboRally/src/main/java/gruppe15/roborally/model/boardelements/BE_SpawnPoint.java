package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;

/**
 * This class represents a spawn point on the board and when a player is
 * rebooted, the player is rebooted on the spawn point.
 */
public class BE_SpawnPoint extends BoardElement {
    /**
     * Constructor for the spawn point
     * 
     * @param direction The direction players should be pushed, if they stand on
     *                  this, while someone is rebooted here.
     */
    public BE_SpawnPoint(Heading direction) {
        super("startField.png", direction);
    }

    public void setColor(Player player) {
        Color playerColor = Color.valueOf(player.getRobot().name());
        this.image = ImageUtils.getImageColored(this.image, playerColor, .75);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
