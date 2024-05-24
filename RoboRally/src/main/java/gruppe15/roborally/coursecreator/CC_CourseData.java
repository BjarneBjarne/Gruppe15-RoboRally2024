package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;

import javafx.geometry.Point2D;
import javafx.util.Pair;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CC_CourseData {
    private String courseName;
    private List<CC_SubBoard> subBoards;
    private String snapshotAsBase64;

    public CC_CourseData(String courseName, List<CC_SubBoard> subBoards, String snapshotAsBase64) {
        this.courseName = courseName;
        this.subBoards = subBoards;
        this.snapshotAsBase64 = snapshotAsBase64;
    }

    public String getCourseName() {
        return courseName;
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

    public void saveImageToFile(String path) {
        Image courseImage = getImage();
        ImageUtils.saveImageToFile(courseImage, courseName, path);
    }

    public Pair<List<Space[][]>, Space[][]> getGameSubBoards() {
        // Board variables
        Point2D[] bounds = getBounds();
        Point2D topLeft = bounds[0];
        Point2D bottomRight = bounds[1];
        //System.out.println("topLeft: " + topLeft + ", bottomRight: " + bottomRight);

        int boardWidth = (int)(bottomRight.getX() - topLeft.getX());
        int boardHeight = (int)(bottomRight.getY() - topLeft.getY());
        List<Space[][]> subBoardList = new ArrayList<>();
        Space[][] boardSpaces = new Space[boardWidth][boardHeight];
        //System.out.println("boardWidth: " + boardWidth + ", boardHeight: " + boardHeight);

        for (CC_SubBoard subBoard : subBoards) {
            // Sub board variables
            CC_SpaceView[][] subBoardSpaceViews = subBoard.getSpaceViews();
            int subBoardWidth = subBoardSpaceViews.length;
            int subBoardHeight = subBoardSpaceViews[0].length;
            Space[][] subBoardSpaces = new Space[subBoardWidth][subBoardHeight];
            Point2D boardRelativePos = new Point2D(subBoard.getPosition().getX() * 5 - topLeft.getX(), subBoard.getPosition().getY() * 5 - topLeft.getY());
            /*System.out.println("boardRelativePos: " + boardRelativePos);
            System.out.println("subBoardWidth: " + subBoardWidth + ", subBoardHeight: " + subBoardHeight);*/

            for (int subBoardX = 0; subBoardX < subBoardWidth; subBoardX++) {
                for (int subBoardY = 0; subBoardY < subBoardHeight; subBoardY++) {
                    // SubBoard spaces
                    CC_SpaceView spaceView = subBoardSpaceViews[subBoardX][subBoardY];
                    boolean isOnStartBoard = boardWidth <= 3 || boardHeight <= 3;
                    Image backgroundImage = ImageUtils.getImageFromName(isOnStartBoard ? "Board Pieces/emptyStart.png" : "Board Pieces/empty.png");

                    // BoardElement
                    BoardElement boardElement = getBoardElementFromSpaceView(spaceView);

                    // Add space to board and subboard
                    int boardX = (int)(boardRelativePos.getX() + subBoardX);
                    int boardY = (int)(boardRelativePos.getY() + subBoardY);
                    //System.out.println("Local coordinates: " + subBoardX + ", " + subBoardY + ". Board coordinates: " + boardX + ", " + boardY);
                    addSpace(boardSpaces, boardX, boardY, subBoardSpaces, subBoardX, subBoardY, boardElement);

                    // Set background image
                    subBoardSpaces[subBoardX][subBoardY].setBackgroundImage(backgroundImage);

                    // Add walls if any
                    if (spaceView != null) {
                        for (Heading wall : spaceView.getPlacedWalls()) {
                            subBoardSpaces[subBoardX][subBoardY].addWall(wall);
                        }
                    }
                }
            }
        }

        // Calculate new conveyorBelt images.
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (boardSpaces[x][y] == null || boardSpaces[x][y].getBoardElement() == null) continue;
                if (boardSpaces[x][y].getBoardElement() instanceof BE_ConveyorBelt conveyorBelt) {
                    conveyorBelt.calculateImage(x, y, boardSpaces);
                }
            }
        }

        return new Pair<>(subBoardList, boardSpaces);
    }

    private void addSpace(Space[][] boardSpaces, int boardX, int boardY, Space[][] subBoardSpaces, int subBoardX, int subBoardY,  BoardElement boardElement) {
        Space newSpace = new Space(null, boardX, boardY, boardElement);
        boardSpaces[boardX][boardY] = newSpace;
        subBoardSpaces[subBoardX][subBoardY] = newSpace;
    }

    private BoardElement getBoardElementFromSpaceView(CC_SpaceView spaceView) {
        if (spaceView == null) return null;
        int itemBoardElement = spaceView.getPlacedBoardElement();
        if (itemBoardElement == -1) {
            return null;
        }
        CC_Items item = CC_Items.values()[itemBoardElement];

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
            case BoardLaser -> new BE_BoardLaser(spaceView.getDirection());
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

    public Point2D[] getBounds() {
        double minY = Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = 0;
        double maxX = 0;
        for (CC_SubBoard subBoard : subBoards) {
            Point2D point = subBoard.getPosition();
            Point2D subBoardMin = new Point2D(point.getX() * 5, point.getY() * 5);
            Point2D subBoardMax = new Point2D(subBoardMin.getX() + subBoard.getSpaceViews().length, subBoardMin.getY() + subBoard.getSpaceViews()[0].length);
            if (subBoardMin.getY() < minY) {
                minY = subBoardMin.getY();
            }
            if (subBoardMax.getY() > maxY) {
                maxY = subBoardMax.getY();
            }
            if (subBoardMin.getX() < minX) {
                minX = subBoardMin.getX();
            }
            if (subBoardMax.getX() > maxX) {
                maxX = subBoardMax.getX();
            }
        }

        Point2D min = new Point2D(minX, minY);
        Point2D max = new Point2D(maxX, maxY);
        return new Point2D[] { min, max };
    }
}
