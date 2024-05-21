package gruppe15.roborally.model.utils;

import javafx.stage.Screen;

import java.util.Arrays;
import java.util.List;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.geometry.Rectangle2D;

public class Constants {
    public final static List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);

    public final static double MIN_APP_WIDTH = 800;
    public final static double MIN_APP_HEIGHT = 600;
    public static Rectangle2D APP_BOUNDS;
    public static double APP_SCALE;
    public static double SPACE_SIZE;
    public static double CARDFIELD_SIZE;

    public static void UpdateSizes() {
        //APP_BOUNDS = Screen.getPrimary().getBounds();
        APP_SCALE = (APP_BOUNDS.getHeight() / 1440);
        SPACE_SIZE = 65 * APP_SCALE;
        CARDFIELD_SIZE = 125 * APP_SCALE;

        System.out.println("App scale: " + APP_SCALE);
    }
}
