package com.group15.roborally.client.view;

import com.group15.observer.Subject;
import com.group15.observer.ViewObserver;
import com.group15.roborally.client.utils.TextUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

/**
 * InfoPaneView, a view for displaying information to the user.
 * 
 * @author Tobias Nicolai Frederiksen, s235086@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class InfoPaneView extends StackPane implements ViewObserver {

    private final Text infoText;

    public InfoPaneView() {
        // mainPane.getChildren().add(infoPane);

        // difference?
        StackPane.setAlignment(this, Pos.CENTER);
        setAlignment(Pos.CENTER);

        setStyle("-fx-background-color: #000000A5");

        infoText = new Text();
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 90);
        infoText.setFont(textFont);
        infoText.setFill(Color.WHITE);
        infoText.setWrappingWidth(2560);
        infoText.setTextAlignment(TextAlignment.CENTER);
        StackPane.setMargin(infoText, new Insets(0, 0, 75, 0));
        infoText.setText("");
        disable();
        getChildren().add(infoText);
    }

    public void setInfoText(String text) {
        if (text.isEmpty()) {
            disable();
            return;
        }
        infoText.setText(text);
        enable();
    }

    public void enable() {
        setDisable(false);
        setVisible(true);
    }

    public void disable() {
        setDisable(true);
        setVisible(false);
    }

    @Override
    public void updateView(Subject subject) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateView'");
    }
}
