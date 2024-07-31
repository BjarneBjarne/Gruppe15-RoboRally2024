package com.group15.roborally.client.model;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import lombok.Getter;

import static com.group15.roborally.client.ApplicationSettings.DEBUG_WITH_ACTION_MESSAGE;
import static com.group15.roborally.client.ApplicationSettings.WITH_ACTION_DELAY;

public class ActionWithDelay {
    private final int delayInMillis;
    private final Runnable action;
    private final String actionName;

    private static int actionInitCounter = 0;
    private static int actionExecCounter = 0;

    public ActionWithDelay(Runnable action, int delayInMillis, String actionName) {
        this.action = action;
        this.delayInMillis = delayInMillis;
        this.actionName = actionName;
        actionInitCounter++;
    }
    public ActionWithDelay(Runnable action, int delayInMillis) {
        this(action, delayInMillis, "NO_ACTION_NAME");
    }

    public void runAndCallback(Runnable callback) {
        actionExecCounter++;
        if (DEBUG_WITH_ACTION_MESSAGE) {
            System.out.println(
                    "\t-> Executing action: {\n" +
                            "\t\t" + actionName + "\n" +
                            "\t}"
            );
        }
        action.run();
        int delay = WITH_ACTION_DELAY ? this.delayInMillis : 0;
        PauseTransition pause = new PauseTransition(Duration.millis(delay));
        pause.setOnFinished(a -> callback.run());
        pause.play();
    }
}
