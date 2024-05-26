package gruppe15.roborally.model;

import javafx.util.Duration;

public class ActionWithDelay {
    private final Runnable action;
    private final Duration delay;
    private final String actionName;

    public ActionWithDelay(Runnable action, Duration delay, String actionName) {
        this.action = action;
        this.delay = delay;
        this.actionName = actionName;
    }
    public ActionWithDelay(Runnable action, Duration delay) {
        this(action, delay, "");
    }

    public Runnable getAction(boolean displayActionName) {
        if (!actionName.isEmpty() && displayActionName) {
            System.out.println("Executing action: \"" + actionName + "\".");
        }
        return action;
    }

    public Duration getDelay() {
        return delay;
    }
}
