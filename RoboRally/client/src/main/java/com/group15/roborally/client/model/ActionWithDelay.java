package com.group15.roborally.client.model;

public class ActionWithDelay {
    private final Runnable action;
    private final int delayInMillis;
    private final String actionName;

    public ActionWithDelay(Runnable action, int delayInMillis, String actionName) {
        this.action = action;
        this.delayInMillis = delayInMillis;
        this.actionName = actionName;
    }
    public ActionWithDelay(Runnable action, int delayInMillis) {
        this(action, delayInMillis, "");
    }

    public Runnable getAction(boolean displayActionName) {
        if (!actionName.isEmpty() && displayActionName) {
            System.out.println("Executing action: \"" + actionName + "\".");
        }
        return action;
    }

    public int getDelayInMillis() {
        return delayInMillis;
    }
}
