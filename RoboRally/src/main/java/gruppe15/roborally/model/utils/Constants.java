package gruppe15.roborally.model.utils;

import javafx.geometry.Rectangle2D;

public class Constants {
    // UI
    public final static double REFERENCE_HEIGHT = 1440.0;
    public final static double MIN_APP_WIDTH = 1280;
    public final static double MIN_APP_HEIGHT = 720;
    public final static boolean START_FULLSCREEN = false;
    public static Rectangle2D APP_BOUNDS;
    public static double APP_SCALE;
    public static double SPACE_SIZE;
    public static double CARDFIELD_SIZE;

    public static void UpdateSizes() {
        SPACE_SIZE = 120 * 0.75;
        CARDFIELD_SIZE = 225 * 0.75;
    }

    public final static boolean SHOW_DEBUG_COORDINATES = true;
}
