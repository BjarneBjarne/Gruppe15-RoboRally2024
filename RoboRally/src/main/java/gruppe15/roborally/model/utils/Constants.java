package gruppe15.roborally.model.utils;

import javafx.stage.Screen;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Constants {
    public final static List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    public final static double REFERENCE_HEIGHT = 1440.0;

    public final static double MIN_APP_WIDTH = 1280;
    public final static double MIN_APP_HEIGHT = 720;
    public final static boolean START_FULLSCREEN = false;

    public static Rectangle2D APP_BOUNDS;
    public static double APP_SCALE;
    public static double SPACE_SIZE;
    public static double CARDFIELD_SIZE;

    public static void UpdateSizes() {
        SPACE_SIZE = 115 * APP_SCALE;
        CARDFIELD_SIZE = 150 * APP_SCALE;
    }
}
