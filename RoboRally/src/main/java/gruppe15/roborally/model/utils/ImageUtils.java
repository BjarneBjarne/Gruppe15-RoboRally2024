package gruppe15.roborally.model.utils;

import gruppe15.roborally.model.BoardElement;
import javafx.scene.image.Image;

import java.util.Objects;

public class ImageUtils {
    /**
     * @param imageName Specified by the file name + the file extension. E.g: "empty.png".
     * @return Returns the image with the name and file extension from the /gruppe15/roborally/images/ folder.
     */
    public static Image getImageFromName(String imageName) {
        String imagePath = "/gruppe15/roborally/images/" + imageName;
        try {
            return new Image(Objects.requireNonNull(BoardElement.class.getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.out.println("Error importing image with path: " + imagePath);
            return null;
        }
    }
}
