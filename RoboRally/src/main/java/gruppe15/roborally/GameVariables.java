package gruppe15.roborally;

import javafx.geometry.Rectangle2D;

public class GameVariables {
    // Action delay
    public final static int NEXT_REGISTER_DELAY = 1000; // In milliseconds.
    public final static boolean WITH_ACTION_DELAY = true;

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
        SPACE_SIZE = 90;
        CARDFIELD_SIZE = 168.75;
    }

    // Controls
    public static double ZOOM_SPEED = 0.05;
    public static double MIN_ZOOM = 0.35;
    public static double MAX_ZOOM = 10;

    // Debug
    public final static boolean SHOW_DEBUG_COORDINATES = false;
    public final static boolean WITH_ACTION_MESSAGE = false;
    public static final boolean SHOW_DEBUG_UPGRADE_CARD_USAGE = true;
}
