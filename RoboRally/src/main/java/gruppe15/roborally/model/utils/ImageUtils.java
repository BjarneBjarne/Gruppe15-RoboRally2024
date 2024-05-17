package gruppe15.roborally.model.utils;

import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.boardelements.BoardElement;
import gruppe15.roborally.model.Heading;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.scene.paint.Color;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Objects;

import javax.imageio.ImageIO;

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

    /**
     * @param image The image to be converted to a base64 string.
     * @return Returns the base64 string of the image.
     */
    public static String imageToBase64(Image image) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", byteArrayOutputStream);
            return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            System.out.println("Error converting image to base64");
            return null;
        }
    }

    /**
     * @param base64String The base64 string to be converted to an image.
     * @return Returns the image of the base64 string.
     */
    public static Image base64ToImage(String base64String) {
        try {
            if (base64String == null) {
                System.out.println("Base64 string is null");
                return null;
            }
            
            byte[] imageData = Base64.getDecoder().decode(base64String);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
            return SwingFXUtils.toFXImage(ImageIO.read(inputStream), null);
        } catch (Exception e) {
            System.out.println("Error converting base64 to image");
            e.printStackTrace();
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

    public static Image getImageColored(Image oldImage, Color blendColor, double colorBlend) {
        WritableImage writableImage = new WritableImage((int) oldImage.getWidth(), (int) oldImage.getHeight());
        PixelReader pixelReader = oldImage.getPixelReader();
        PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < oldImage.getHeight(); y++) {
            for (int x = 0; x < oldImage.getWidth(); x++) {
                Color color = pixelReader.getColor(x, y);
                Color coloredColor = colorMultiply(color, blendColor);
                Color newColor = color.interpolate(coloredColor, colorBlend);
                pixelWriter.setColor(x, y, newColor);
            }
        }

        return writableImage;
    }

    private static Color colorMultiply(Color color, Color multiplier) {
        double red = color.getRed() * multiplier.getRed();
        double green = color.getGreen() * multiplier.getGreen();
        double blue = color.getBlue() * multiplier.getBlue();
        double opacity = color.getOpacity(); // Keep the original opacity
        return new Color(red, green, blue, opacity);
    }
}
