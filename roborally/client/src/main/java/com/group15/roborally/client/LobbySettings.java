package com.group15.roborally.client;

import com.group15.roborally.client.model.damage.Damage;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;

import java.util.Arrays;
import java.util.List;

public class LobbySettings {
    // Game options
    public final static List<String> OPTIONS_KEEP_HAND = Arrays.asList("Yes", "No");
    public final static List<String> OPTIONS_DRAW_ON_EMPTY_REGISTER = Arrays.asList("Yes", "No");
    public static int NO_OF_PLAYERS = 0;
    public static int NO_OF_CARDS_IN_HAND = 10;
    public static boolean KEEP_HAND = false;
    public static boolean DRAW_ON_EMPTY_REGISTER = false;
    public static UpgradeCard[] STARTING_UPGRADE_CARDS = new UpgradeCard[]{};
    public static Damage STANDARD_DAMAGE = new Damage(1, 0, 0, 0);
}
