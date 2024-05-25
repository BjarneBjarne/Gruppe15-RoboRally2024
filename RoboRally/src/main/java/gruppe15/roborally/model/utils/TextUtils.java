package gruppe15.roborally.model.utils;

import gruppe15.roborally.RoboRally;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.InputStream;

public class TextUtils {
    public static Font loadFont(String fontName, double size) {
        try {
            // Load the font file from the resources folder using ClassLoader
            InputStream fontStream = RoboRally.class.getResourceAsStream("fonts/" + fontName);
            if (fontStream == null) {
                throw new RuntimeException("Font file not found: " + fontName);
            }

            return Font.loadFont(fontStream, size);
        } catch (Exception e) {
            e.printStackTrace();
            return Font.getDefault();
        }
    }

    public static Text createStyledText(String text, Font font) {
        Text styledText = new Text(text);
        styledText.setFont(font);
        styledText.setFill(Color.WHITE);
        styledText.setStroke(Color.BLACK);
        styledText.setStrokeWidth(2);
        styledText.setStrokeType(StrokeType.OUTSIDE);

        return styledText;
    }
}
