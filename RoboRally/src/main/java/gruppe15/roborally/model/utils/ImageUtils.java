package gruppe15.roborally.model.utils;

import gruppe15.roborally.model.BoardElement;
import gruppe15.roborally.model.Heading;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

import java.util.Objects;

public class ImageUtils {
    /**
     * @param imageName Specified by the file name + the file extension. E.g:
     *                  "empty.png".
     * @return Returns the image with the name and file extension from the
     *         /gruppe15/roborally/images/ folder.
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

    public static Image getRotatedImageByHeading(Image image, Heading heading) {
        ImageView imageView = new ImageView(image);
        switch (heading) {
            case EAST:
                imageView.setRotate(90);
                break;
            case SOUTH:
                imageView.setRotate(180);
                break;
            case WEST:
                imageView.setRotate(270);
                break;
            case NORTH:
                break;
            default:
                break;
        }
        SnapshotParameters params = new SnapshotParameters();
        params.setFill(Color.TRANSPARENT);
        return imageView.snapshot(params, null);
    }
}
