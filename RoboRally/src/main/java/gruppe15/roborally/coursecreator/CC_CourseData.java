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
        Point2D[] bounds = getBounds();
        Point2D topLeft = bounds[0];
        Point2D bottomRight = bounds[1];

        double boardWidth = (bottomRight.getX() - topLeft.getX() + 1) * 5;
        double boardHeight = (bottomRight.getY() - topLeft.getY() + 1) * 5;

        //System.out.println("boardWidth: " + boardWidth + " boardHeight: " + boardHeight);

        Space[][] spaces = new Space[(int)boardWidth][(int)boardHeight];

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
                    int localX = x - subBoardStartX;
                    int localY = y - subBoardStartY;
                    System.out.println(localX + ", " + localY);
                    // Initializing values
                    CC_SpaceView spaceView = subBoardSpaceViews[localX][localY];
                    boolean isOnStartBoard = subBoardSpaceViews.length <= 3 || subBoardSpaceViews[0].length <= 3;
                    Image backgroundImage = ImageUtils.getImageFromName(isOnStartBoard ? "Board Pieces/emptyStart.png" : "Board Pieces/empty.png");

                    // BoardElement
                    BoardElement boardElement = getBoardElementFromSpaceView(spaceView);
                    // Add space
                    addSpace(x, y, boardElement, spaces);
                    // Set background image
                    spaces[x][y].setBackgroundImage(backgroundImage);
                    // Add walls if any
                    if (spaceView != null) {
                        for (Heading wall : spaceView.getPlacedWalls()) {
                            spaces[x][y].addWall(wall);
                        }
                    }
                }
            }
        }

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (spaces[x][y] == null || spaces[x][y].getBoardElement() == null) continue;
                if (spaces[x][y].getBoardElement() instanceof BE_ConveyorBelt conveyorBelt) {
                    conveyorBelt.calculateImage(x, y, spaces);
                }
            }
        }

        return new Pair<>(subBoardList, spaces);
    }

    private void addSpace(int x, int y, BoardElement boardElement, Space[][] spaces) {
        spaces[x][y] = new Space(null, x, y, boardElement);
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
        Point2D[] points = new Point2D[subBoards.size()];

        for (int i = 0; i < subBoards.size(); i++) {
            points[i] = subBoards.get(i).getPosition();
            /*System.out.println(subBoards.get(i).getSpaceViews().length);
            System.out.println(subBoards.get(i).getPosition());*/
        }

        double minY = Double.MAX_VALUE;
        double maxY = 0;
        double minX = Double.MAX_VALUE;
        double maxX = 0;
        for (int i = 0; i < points.length; i++) {
            if (points[i].getY() < minY) {
                minY = points[i].getY();
            }
            if (points[i].getY() > maxY) {
                maxY = points[i].getY();
                maxY += (subBoards.get(i).getSpaceViews()[0].length > 3 ? 1 : 0);
            }
            if (points[i].getX() < minX) {
                minX = points[i].getX();
            }
            if (points[i].getX() > maxX) {
                maxX = points[i].getX();
                maxX += (subBoards.get(i).getSpaceViews().length > 3 ? 1 : 0);
            }
        }

        Point2D topLeft = new Point2D(minX, minY);
        Point2D bottomRight = new Point2D(maxX, maxY);
        //System.out.println("topLeft: " + topLeft + ", bottomRight: " + bottomRight);
        return new Point2D[] { topLeft, bottomRight };
    }
}
