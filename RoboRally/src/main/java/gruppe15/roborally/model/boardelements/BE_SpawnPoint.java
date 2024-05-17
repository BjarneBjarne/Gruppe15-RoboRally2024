package gruppe15.roborally.model.boardelements;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.ActionWithDelay;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
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
        Color spawnpointColor = Color.valueOf(player.getRobot().name());

        WritableImage writableImage = new WritableImage((int) this.image.getWidth(), (int) this.image.getHeight());
        PixelReader pixelReader = this.image.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < this.image.getHeight(); y++) {
            for (int x = 0; x < this.image.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                Color coloredColor = colorMultiply(color, spawnpointColor);
                Color newColor = color.interpolate(coloredColor, .75);
                pixelWriter.setColor(x, y, newColor);
            }
        }

        this.image = writableImage;
    }

    private Color colorMultiply(Color color, Color multiplier) {
        double red = color.getRed() * multiplier.getRed();
        double green = color.getGreen() * multiplier.getGreen();
        double blue = color.getBlue() * multiplier.getBlue();
        double opacity = color.getOpacity(); // Keep the original opacity
        return new Color(red, green, blue, opacity);
    }

    @Override
    public boolean doAction(@NotNull Space space, @NotNull GameController gameController,
            LinkedList<ActionWithDelay> actionQueue) {
        return false;
    }
}
