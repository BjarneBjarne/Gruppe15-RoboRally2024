package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Laser;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

import javafx.geometry.Point2D;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CC_CourseData {
    private List<CC_SubBoard> subBoards;
    private String snapshotAsBase64;

    public CC_CourseData(List<CC_SubBoard> subBoards, String snapshotAsBase64) {
        this.subBoards = subBoards;
        this.snapshotAsBase64 = snapshotAsBase64;
    }

    public List<CC_SubBoard> getSubBoards() {
        return subBoards;
    }

    public String getSnapshotAsBase64() {
        return snapshotAsBase64;
    }

    public Image getImage() {
        // Decode Base64 to Image and save to file
        if (snapshotAsBase64 != null && !snapshotAsBase64.isEmpty()) {
            byte[] imageBytes = Base64.getDecoder().decode(snapshotAsBase64);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            Image courseImage = new Image(inputStream);
            return courseImage;
        }
        return null;
    }

    public void saveImageToFile() {
        Image courseImage = getImage();
        // Save the writable image to a file
        WritableImage writableImage = new WritableImage(courseImage.getPixelReader(), (int) courseImage.getWidth(), (int) courseImage.getHeight());
        File file = new File("savedImage.png");
        try {
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            System.out.println("Image saved to " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<List<Space[][]>, Space[][]> getGameSubBoards() {
        Point2D[] bounds = getBounds();
        Point2D topLeft = bounds[0];
        Point2D bottomRight = bounds[1];

        int boardWidth = (int)(bottomRight.getX() - topLeft.getX());
        int boardHeight = (int)(bottomRight.getY() - topLeft.getY());

        Space[][] spaces = new Space[boardWidth][boardHeight];

        List<Space[][]> subBoardList = new ArrayList<>();

        for (CC_SubBoard subBoard : subBoards) {
            CC_SpaceView[][] subBoardSpaceViews = subBoard.getSpaceViews();
            Point2D subBoardRelativePos = subBoard.getPosition().subtract(topLeft);
            // 5 spaces per position increment
            Point2D subBoardStartPos = new Point2D(subBoardRelativePos.getX() * 5, subBoardRelativePos.getY() * 5);
            int subBoardStartX = (int)subBoardStartPos.getX();
            int subBoardStartY = (int)subBoardStartPos.getY();

            for (int x = subBoardStartX; x < subBoardStartX + subBoardSpaceViews.length; x++) {
                for (int y = subBoardStartY; y < subBoardStartY + subBoardSpaceViews[0].length; y++) {
                    // Initializing values
                    CC_SpaceView spaceView = subBoardSpaceViews[x][y];
                    boolean isOnStartBoard = subBoardSpaceViews.length <= 3 || subBoardSpaceViews[0].length <= 3;
                    Image backgroundImage = ImageUtils.getImageFromName(isOnStartBoard ? "Board Pieces/emptyStart.png" : "Board Pieces/empty.png");

                    // BoardElement
                    BoardElement boardElement = getBoardElementFromSpaceView(spaceView);
                    // Set background image
                    spaces[x][y].setBackgroundImage(backgroundImage);
                    // Add walls if any
                    for (Heading wall : spaceView.getPlacedWalls()) {
                        spaces[x][y].addWall(wall);
                    }
                    // Add space
                    addSpace(x, y, boardElement, spaces);
                }
            }
        }

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (spaces[x][y].getBoardElement() instanceof BE_ConveyorBelt conveyorBelt) {
                    conveyorBelt.calculateImage(x, y, spaces);
                }
            }
        }

        return new Pair<>(subBoardList, spaces);
    }

    private BoardElement getBoardElementFromSpaceView(CC_SpaceView spaceView) {
        CC_Controller.CC_Items item = CC_Controller.CC_Items.values()[spaceView.getPlacedBoardElement()];

        return switch (item) {
            case SpawnPoint -> new BE_SpawnPoint(spaceView.getDirection());
            case Reboot -> new BE_Reboot(spaceView.getDirection());
            case Hole -> new BE_Hole();
            case Antenna -> new BE_Antenna();
            case BlueConveyorBelt -> new BE_ConveyorBelt(spaceView.getDirection(), 2);
            case GreenConveyorBelt -> new BE_ConveyorBelt(spaceView.getDirection(), 1);
            case PushPanel135 -> new BE_PushPanel("135", spaceView.getDirection());
            case PushPanel24 -> new BE_PushPanel("24", spaceView.getDirection());
            case GearRight -> new BE_Gear("Right");
            case GearLeft -> new BE_Gear("Left");
            case Laser -> new BE_BoardLaser(spaceView.getDirection());
            case EnergySpace -> new BE_EnergySpace();
            case Checkpoint1 -> new BE_Checkpoint(1);
            case Checkpoint2 -> new BE_Checkpoint(2);
            case Checkpoint3 -> new BE_Checkpoint(3);
            case Checkpoint4 -> new BE_Checkpoint(4);
            case Checkpoint5 -> new BE_Checkpoint(5);
            case Checkpoint6 -> new BE_Checkpoint(6);
            default -> null;
        };
    }

    private void addSpace(int x, int y, BoardElement boardElement, Space[][] spaces) {
        spaces[x][y] = new Space(null, x, y, boardElement);
    }

    public Point2D[] getBounds() {
        List<Point2D> points = new ArrayList<>();

        for (CC_SubBoard subBoard : subBoards) {
            points.add(subBoard.getPosition());
        }

        if (points.isEmpty()) {
            return null;
        }

        double minY = points.get(0).getY();
        double maxY = points.get(0).getY();
        double minX = points.get(0).getX();
        double maxX = points.get(0).getX();
        for (Point2D point : points) {
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
            }
            if (point.getX() < minX) {
                minX = point.getX();
            }
            if (point.getX() > maxX) {
                maxX = point.getX();
            }
        }
        Point2D topLeft = new Point2D(minX, minY);
        Point2D bottomRight = new Point2D(maxX, maxY);
        return new Point2D[] { topLeft, bottomRight };
    }
}
