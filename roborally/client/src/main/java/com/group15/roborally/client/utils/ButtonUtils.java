package com.group15.roborally.client.utils;

import com.group15.roborally.client.RoboRally;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

public class ButtonUtils {
    private static final Effect effect_hover = new InnerShadow(20, Color.STEELBLUE);

    public static void setupDefaultButton(Button button, Runnable onClickAction) {
        button.setOnMouseEntered(e -> {
            button.setEffect(effect_hover);
            RoboRally.audioMixer.playUIHover();
        });
        button.setOnMouseExited(e -> button.setEffect(null));
        button.setOnAction(e -> {
            RoboRally.audioMixer.playUIClick();
            onClickAction.run();
        });
    }
    public static void setupDefaultButton(Button button) {
        EventHandler<ActionEvent> existingHandler = button.getOnAction();
        button.setOnMouseEntered(e -> {
            button.setEffect(effect_hover);
            RoboRally.audioMixer.playUIHover();
        });
        button.setOnMouseExited(e -> button.setEffect(null));
        button.setOnAction(e -> {
            RoboRally.audioMixer.playUIClick();
            if (existingHandler != null) {
                existingHandler.handle(e);
            }
        });
    }

    public static void setupAllFXMLButtons(Parent root) {
        root.getChildrenUnmodifiable().forEach(node -> {
            if (node instanceof Button) {
                setupDefaultButton((Button) node);
            } else if (node instanceof Parent) {
                setupAllFXMLButtons((Parent) node);
            }
        });
    }
}
