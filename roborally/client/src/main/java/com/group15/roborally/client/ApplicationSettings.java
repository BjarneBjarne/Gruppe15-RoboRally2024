package com.group15.roborally.client;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
    @Getter
    public static BooleanProperty FULLSCREEN = new SimpleBooleanProperty(false);

    // Action delay - All in milliseconds:
    public static final boolean WITH_ACTION_DELAY = true;
    public static final int DELAY_END_OF_ROUND = 0;
    public static final int DELAY_NEXT_REGISTER = 0;
    public static final int DELAY_NEXT_PLAYER_REGISTER = 1000;
    public static final int DELAY_NEXT_PLAYER_REBOOT = 350;
    public static final int DELAY_NEXT_BOARD_ELEMENT = 200;
    public static final int DELAY_NEXT_PLAYER_LASER = 150;
    public static final int DELAY_PLAYER_HIT = 250;
    public static final int DELAY_PLAYER_MOVE = 200;
    public static final int DELAY_PLAYER_TURN = 200;
    public static final int DELAY_AFTER_UPGRADE = 1000;
    public static final int DELAY_INSTANT = 0;


    // Controls
    public static double ZOOM_SPEED = 1;
    public static double MIN_ZOOM = 0.2;
    public static double MAX_ZOOM = 10;

    // Debug
    public static final boolean DEBUG_SHOW_DEBUG_UI = true;
    public static final boolean DEBUG_SHOW_COORDINATES = false;
    public static final boolean DEBUG_WITH_ACTION_MESSAGE = true;
    public static final boolean DEBUG_SHOW_UPGRADE_CARD_USAGE = true;
    //public static final boolean DEBUG_ALLOW_MANUAL_PLAYER_POSITION = false;

    // Network
    public static final int SERVER_PORT = 8080;
    public static final long TIME_BEFORE_TIMEOUT_SECONDS = 10;
}
