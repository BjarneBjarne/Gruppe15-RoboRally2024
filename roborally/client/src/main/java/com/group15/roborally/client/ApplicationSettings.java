package com.group15.roborally.client;

import javafx.geometry.Rectangle2D;
import lombok.Getter;
import lombok.Setter;

public class ApplicationSettings {
    // UI
    public static final double REFERENCE_WIDTH = 2560.0;
    public static final double REFERENCE_HEIGHT = 1440.0;
    public static final double MIN_APP_WIDTH = 800;
    public static final double MIN_APP_HEIGHT = 600;
    public static final double SPACE_SIZE = 90;
    public static final double CARDFIELD_SIZE = 185;
    public static Rectangle2D APP_BOUNDS;
    public static double APP_SCALE;
    @Getter @Setter
    public static boolean FULLSCREEN = false;

    // Action delay
    public static final int NEXT_REGISTER_DELAY = 500; // In milliseconds.
    public static final boolean WITH_ACTION_DELAY = true;
    public static final int CARD_USAGE_DELAY_MILLIS = 500;

    // Controls
    public static double ZOOM_SPEED = 1;
    public static double MIN_ZOOM = 0.2;
    public static double MAX_ZOOM = 10;

    // Debug
    public static final boolean DEBUG_SHOW_COORDINATES = false;
    public static final boolean DEBUG_WITH_ACTION_MESSAGE = true;
    public static final boolean DEBUG_SHOW_UPGRADE_CARD_USAGE = true;
    //public static final boolean DEBUG_ALLOW_MANUAL_PLAYER_POSITION = false;

    // Network
    public static final long TIME_BEFORE_TIMEOUT_SECONDS = 10;
}
