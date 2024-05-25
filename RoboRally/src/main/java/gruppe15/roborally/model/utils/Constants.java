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

    // Controls
    public static double ZOOM_SPEED = 0.1;
    public static double MIN_ZOOM = 0.2;
    public static double MAX_ZOOM = 10;

    // Debug
    public final static boolean SHOW_DEBUG_COORDINATES = false;
}
