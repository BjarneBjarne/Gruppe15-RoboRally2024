package gruppe15.roborally.model.utils;

import javafx.stage.Screen;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Constants {
    public final static Rectangle2D PRIMARY_SCREEN_BOUNDS = Screen.getPrimary().getVisualBounds();
    private final static int MIN_APP_WIDTH = 1280;
    private final static int MIN_APP_HEIGHT = 740;
    public final static double APP_SCALE = (PRIMARY_SCREEN_BOUNDS.getHeight() / 1440);
    public final static double SPACE_SIZE = 65 * APP_SCALE;
    public final static double CARDFIELD_SIZE = 125 * APP_SCALE;
    public final static List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
}
