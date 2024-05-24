package gruppe15.roborally.model.utils;

import gruppe15.roborally.RoboRally;
import javafx.scene.text.Font;

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
}
