package com.group15.roborally.client;

import javafx.geometry.Rectangle2D;

public class ApplicationSettings {
    // UI
    public final static double REFERENCE_WIDTH = 2560.0;
    public final static double REFERENCE_HEIGHT = 1440.0;
    public final static double MIN_APP_WIDTH = 800;
    public final static double MIN_APP_HEIGHT = 600;
    public final static boolean FULLSCREEN = false;
    public static Rectangle2D APP_BOUNDS;
    public static double APP_SCALE;
    public static double SPACE_SIZE = 90;
    public static double CARDFIELD_SIZE = 185;

    // Controls
    public static double ZOOM_SPEED = 1;
    public static double MIN_ZOOM = 0.2;
    public static double MAX_ZOOM = 10;

    // Debug
    public final static boolean DEBUG_SHOW_COORDINATES = false;
    public final static boolean DEBUG_WITH_ACTION_MESSAGE = false;
    public static final boolean DEBUG_SHOW_UPGRADE_CARD_USAGE = true;
    public final static boolean DEBUG_ALLOW_MANUAL_PLAYER_POSITION = false;
}
