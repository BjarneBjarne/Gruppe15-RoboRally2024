package gruppe15.roborally;

import gruppe15.roborally.model.damage.Damage;
import gruppe15.roborally.model.upgrade_cards.UpgradeCard;

import java.util.Arrays;
import java.util.List;

public class BoardOptions {
    // Action delay
    public final static int NEXT_REGISTER_DELAY = 1000; // In milliseconds.
    public final static boolean WITH_ACTION_DELAY = true;

    // Game options
    public final static List<Integer> OPTIONS_NO_OF_PLAYERS = Arrays.asList(2, 3, 4, 5, 6);
    public final static List<String> OPTIONS_KEEP_HAND = Arrays.asList("Yes", "No");
    public final static List<String> OPTIONS_DRAW_ON_EMPTY_REGISTER = Arrays.asList("Yes", "No");
    public static int NO_OF_PLAYERS = 0;
    public static int NO_OF_CARDS_IN_HAND = 10;
    public static boolean KEEP_HAND = false;
    public static boolean DRAW_ON_EMPTY_REGISTER = true;
    public static UpgradeCard[] STARTING_UPGRADE_CARDS = new UpgradeCard[]{};
    public static Damage STANDARD_DAMAGE = new Damage(1, 0, 0, 0);
}
