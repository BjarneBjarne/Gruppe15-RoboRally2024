package gruppe15.roborally.model.utils;

import java.util.Arrays;
import java.util.List;

public class GameSettings {
    public final static List<Integer> OPTIONS_NO_OF_PLAYERS = Arrays.asList(2, 3, 4, 5, 6);
    public static int NO_OF_PLAYERS = 0;
    public final static List<String> OPTIONS_KEEP_HAND = Arrays.asList("No", "Yes");
    public static boolean KEEP_HAND = false;
}
