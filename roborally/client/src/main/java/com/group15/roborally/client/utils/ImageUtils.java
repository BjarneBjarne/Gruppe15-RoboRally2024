package com.group15.roborally.client.utils;

import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.model.Space;
import com.group15.roborally.client.model.boardelements.BE_ConveyorBelt;
import com.group15.roborally.client.model.boardelements.BoardElement;
import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.exceptions.EmptyCourseException;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.*;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import javax.imageio.ImageIO;

public class ImageUtils {
    /**
     * @param imageName Specified by the file name + the file extension. E.g:
     *                  "empty.png".
     * @return Returns the image with the name and file extension from the
     *         /group15/roborally/images/ folder.
     */
    public static Image getImageFromName(String imageName) {
        String imagePath = "/com/group15/roborally/client/images/" + imageName;
        try {
            return new Image(Objects.requireNonNull(RoboRally.class.getResourceAsStream(imagePath)));
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
        if (image == null || heading == null) return null;

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
        double opacity = color.getOpacity() * multiplier.getOpacity();
        return new Color(red, green, blue, opacity);
    }

    public static void saveImageToFile(Image courseImage, String imageName, String path) {
        WritableImage writableImage = new WritableImage(courseImage.getPixelReader(), (int) courseImage.getWidth(), (int) courseImage.getHeight());
        File file = new File(path + "\\" + imageName + ".png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Image saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getSnapshotAsBase64(Node node, int padding) throws EmptyCourseException {
        // Taking snapshot
        BufferedImage bufferedImage = takeSnapshot(node);

        // Crop image to fit the course
        Rectangle bounds = findBounds(bufferedImage);
        BufferedImage croppedImage = cropImage(bufferedImage, bounds);
        BufferedImage scaledImage = scaleImage(croppedImage, 256 - 2 * padding);

        // Make the background transparent
        makeWhiteTransparent(scaledImage);

        // Get background image
        BufferedImage backgroundImage;
        try {
            backgroundImage = loadBackgroundImage("images/Icon/RobotRallyIconCourseBackground.png");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Overlay the course image on top of the background image
        BufferedImage finalImage = overlayImages(backgroundImage, scaledImage, padding);
        try {
            return encodeImageToBase64(finalImage);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static BufferedImage takeSnapshot(Node node) {
        WritableImage snapshot = node.snapshot(new SnapshotParameters(), null);
        return SwingFXUtils.fromFXImage(snapshot, null);
    }

    public static Rectangle findBounds(BufferedImage image) throws EmptyCourseException {
        int minX = image.getWidth(), minY = image.getHeight();
        int maxX = 0, maxY = 0;
        boolean foundNonWhitePixel = false;

        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                if (!isWhite(argb)) {
                    foundNonWhitePixel = true;
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }

        Rectangle bounds = foundNonWhitePixel ? new Rectangle(minX, minY, maxX - minX + 1, maxY - minY + 1) : null;

        if (bounds == null) {
            throw new EmptyCourseException("No course pieces found for snapshot.");
        }

        return bounds;
    }

    public static int WHITE_THRESHOLD = 250;
    public static boolean isWhite(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return r >= WHITE_THRESHOLD && g >= WHITE_THRESHOLD && b >= WHITE_THRESHOLD;
    }

    public static BufferedImage cropImage(BufferedImage image, Rectangle bounds) {
        return image.getSubimage(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public static BufferedImage scaleImage(BufferedImage image, int targetSize) {
        int scaledWidth = (int) (image.getWidth() * ((double) targetSize / Math.max(image.getWidth(), image.getHeight())));
        int scaledHeight = (int) (image.getHeight() * ((double) targetSize / Math.max(image.getWidth(), image.getHeight())));

        BufferedImage scaledImage = new BufferedImage(scaledWidth, scaledHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = scaledImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(image, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();
        return scaledImage;
    }

    public static void makeWhiteTransparent(BufferedImage image) {
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int argb = image.getRGB(x, y);
                if (isWhite(argb)) {
                    image.setRGB(x, y, (argb & 0x00FFFFFF));
                }
            }
        }
    }

    public static BufferedImage loadBackgroundImage(String path) throws IOException {
        InputStream backgroundImageStream = RoboRally.class.getResourceAsStream(path);
        if (backgroundImageStream == null) {
            throw new IOException("Failed to load background image.");
        }
        return ImageIO.read(backgroundImageStream);
    }

    public static BufferedImage overlayImages(BufferedImage background, BufferedImage overlay, int padding) {
        int x = (background.getWidth() - overlay.getWidth()) / 2;
        int y = (background.getHeight() - overlay.getHeight()) / 2;

        Graphics2D g2d = background.createGraphics();
        g2d.drawImage(overlay, x, y, null);
        g2d.dispose();
        return background;
    }

    public static String encodeImageToBase64(BufferedImage image) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return Base64.getEncoder().encodeToString(outputStream.toByteArray());
    }

    /**
     * Calculates the image of the conveyor belt based on the direction and the number of neighbors.
     * @param x the x-coordinate of the conveyor belt
     * @param y the y-coordinate of the conveyor belt
     * @param spaces the spaces on the board
     *
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public static String getUpdatedConveyorBeltImage(BE_ConveyorBelt thisConveyorBelt, int x, int y, Space[][] spaces) {
        StringBuilder imageNameBuilder = new StringBuilder();
        Space thisSpace = spaces[x][y];
        // Green or blue
        imageNameBuilder.append(thisConveyorBelt.getStrength() == 1 ? "green" : "blue");

        // Neighbors and connections
        thisConveyorBelt.clearConnections();
        boolean[] relativeConnections = new boolean[4];

        // Checking if neighbors are a valid connection
        // relativeConnections[0], the direction this conveyor belt is facing, always counts as a connection.
        relativeConnections[0] = true;
        Space frontNeighborSpace = thisSpace.getSpaceNextTo(thisConveyorBelt.getDirection(), spaces);
        thisConveyorBelt.addConnections(frontNeighborSpace);
        /*if (frontNeighborSpace != null) {
            thisConveyorBelt.debugText += "Con (" + frontNeighborSpace.x + ", " + frontNeighborSpace.y + ")" + "\n";
        }*/
        checkForConnectionAtDirection(thisConveyorBelt, thisSpace, 2, spaces, relativeConnections); // Behind this conveyor belt
        checkForConnectionAtDirection(thisConveyorBelt, thisSpace, 1, spaces, relativeConnections); // To the right of this conveyor belt
        checkForConnectionAtDirection(thisConveyorBelt, thisSpace, 3, spaces, relativeConnections); // To the left of this conveyor belt

        //thisConveyorBelt.debugText += "Size: " + thisConveyorBelt.getConnections().size() + "\n";

        // Building image string
        buildConveyorBeltStringFromConnections(imageNameBuilder, thisConveyorBelt.getConnections().size(), relativeConnections);

        return imageNameBuilder.toString();
    }

    private static void checkForConnectionAtDirection(BE_ConveyorBelt thisConveyorBelt, Space thisSpace, int directionIndex, Space[][] spaces, boolean[] relativeConnections) {
        Heading relativeDirection = Heading.values()[(thisConveyorBelt.getDirection().ordinal() + directionIndex) % 4];
        Space neighborSpace = thisSpace.getSpaceNextTo(relativeDirection, spaces);

        if (neighborSpace == null) return;

        if (neighborSpace.getBoardElement() instanceof BE_ConveyorBelt neighbor) {
            if (thisConveyorBelt.getStrength() != neighbor.getStrength()) return; // Only count same type

            boolean isValidConnection = false;

            // Space references
            Heading neighborDirection = neighbor.getDirection();
            Space spaceInFrontOfNeighbor = neighborSpace.getSpaceNextTo(neighborDirection, spaces);

            // Number of other connections
            int noOfThisOtherConnections = 0;
            int noOfNeighborsOtherConnections = 0;
            for (Space connection : thisConveyorBelt.getConnections()) {
                if (connection == null) noOfThisOtherConnections++;
                if (connection != neighborSpace) noOfThisOtherConnections++;
            }
            for (Space connection : neighbor.getConnections()) {
                if (connection == null) noOfNeighborsOtherConnections++;
                if (connection != thisSpace) noOfNeighborsOtherConnections++;
            }

            // Condition checking
            if (directionIndex == 2) {
                // Neighbor is behind
                if (thisSpace.equals(spaceInFrontOfNeighbor)) {
                    isValidConnection = true;
                }
            } else {
                // Neighbor is to the right or left
                List<Boolean> conditions = new ArrayList<>();
                conditions.add(thisSpace.equals(spaceInFrontOfNeighbor));
                conditions.add(noOfThisOtherConnections == 1 && noOfNeighborsOtherConnections == 1 && thisConveyorBelt.getDirection() != neighbor.getDirection());
                for (Boolean condition : conditions) {
                    if (condition) {
                        isValidConnection = true;
                        break;
                    }
                }
            }

            if (isValidConnection) {
                relativeConnections[directionIndex] = true;
                thisConveyorBelt.addConnections(neighborSpace);
                //thisConveyorBelt.debugText += "Con (" + neighborSpace.x + ", " + neighborSpace.y + ")" + "\n";
            }
        }
    }

    public static void buildConveyorBeltStringFromConnections(StringBuilder stringBuilder, int noOfNeighbors, boolean[] relativeNeighbors) {
        switch (noOfNeighbors) {
            case 2:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                if (relativeNeighbors[2]) {
                    stringBuilder.append("Straight");
                } else {
                    stringBuilder.append("Turn").append(relativeNeighbors[1] ? "Right" : "Left");
                }
                break;
            case 3:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                stringBuilder.append("T").append(relativeNeighbors[2] ? (relativeNeighbors[1] ? "Right" : "Left") : "Sides");
                break;
            default:
                stringBuilder.append("Straight");
                break;
        }
        stringBuilder.append(".png");
    }
}
