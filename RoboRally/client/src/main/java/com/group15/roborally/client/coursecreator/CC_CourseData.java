package com.group15.roborally.client.coursecreator;

import com.group15.roborally.client.model.Heading;
import com.group15.roborally.client.model.Space;
import com.group15.roborally.client.model.boardelements.*;
import com.group15.roborally.client.utils.ImageUtils;
import javafx.scene.image.Image;

import javafx.geometry.Point2D;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * This class is what courses are saved as in memory, when a course JSON file is loaded.
 * It also handles converting the course creator board into a playable one.
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
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

    /**
     * Converts the course image from the JSON from Base64 to a JavaFX image.
     * @return Returns the JavaFX image of the course.
     */
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

    /**
     * Saves the course image as a PNG.
     * @param path A system path for where to save the course image PNG.
     */
    public void saveImageToFile(String path) {
        Image courseImage = getImage();
        ImageUtils.saveImageToFile(courseImage, courseName, path);
    }

    /**
     * Method for converting the course creator board and sub boards into a playable one.
     * @return Returns the game board and its sub boards.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public Pair<List<Space[][]>, Space[][]> getGameSubBoards() {
        // --- Board ---
        Pair<Point2D, Point2D> boardBounds = getBoardBounds();
        Point2D boardTopLeft = boardBounds.getKey();
        Point2D boardBottomRight = boardBounds.getValue();
        //System.out.println("boardTopLeft: " + boardTopLeft + ", boardBottomRight: " + boardBottomRight);

        int boardWidth = (int)(boardBottomRight.getX() - boardTopLeft.getX());
        int boardHeight = (int)(boardBottomRight.getY() - boardTopLeft.getY());
        List<Space[][]> subBoardList = new ArrayList<>();
        Space[][] boardSpaces = new Space[boardWidth][boardHeight];
        //System.out.println("boardWidth: " + boardWidth + ", boardHeight: " + boardHeight);

        // --- Sub boards ---
        for (CC_SubBoard subBoard : subBoards) {
            CC_SpaceView[][] subBoardSpaceViews = subBoard.getSpaceViews();
            int subBoardWidth = subBoardSpaceViews.length;
            int subBoardHeight = subBoardSpaceViews[0].length;
            Space[][] subBoardSpaces = new Space[subBoardWidth][subBoardHeight];
            Pair<Point2D, Point2D> subBoardBounds = getSubBoardBounds(subBoard);
            Point2D subBoardTopLeft = subBoardBounds.getKey();
            Point2D boardRelativePos = subBoardTopLeft.subtract(boardTopLeft);
            //System.out.println("boardRelativePos: " + boardRelativePos);
            //System.out.println("subBoardWidth: " + subBoardWidth + ", subBoardHeight: " + subBoardHeight);

            // --- Spaces ---
            for (int subBoardX = 0; subBoardX < subBoardWidth; subBoardX++) {
                for (int subBoardY = 0; subBoardY < subBoardHeight; subBoardY++) {
                    CC_SpaceView spaceView = subBoardSpaceViews[subBoardX][subBoardY];
                    boolean isOnStartBoard = boardWidth <= 3 || boardHeight <= 3;
                    Image backgroundImage = ImageUtils.getImageFromName(isOnStartBoard ? "Board_Pieces/emptyStart.png" : "Board_Pieces/empty.png");
                    BoardElement boardElement = getBoardElementFromSpaceView(spaceView);
                    BE_Checkpoint checkpoint = getCheckpointFromSpaceView(spaceView);
                    int boardX = (int)(boardRelativePos.getX() + subBoardX);
                    int boardY = (int)(boardRelativePos.getY() + subBoardY);
                    //System.out.println("Local coordinates: " + subBoardX + ", " + subBoardY + ". Board coordinates: " + boardX + ", " + boardY);

                    // Add space to board and subboard
                    Space newSpace = new Space(null, boardX, boardY, boardElement, null, checkpoint);
                    boardSpaces[boardX][boardY] = newSpace;
                    subBoardSpaces[subBoardX][subBoardY] = newSpace;
                    subBoardSpaces[subBoardX][subBoardY].setBackgroundImage(backgroundImage);

                    // Add walls
                    if (spaceView != null) {
                        for (Heading wall : spaceView.getPlacedWalls()) {
                            subBoardSpaces[subBoardX][subBoardY].addWall(wall);
                        }
                    }
                }
            }

            subBoardList.add(subBoardSpaces);
        }

        // Calculate new conveyorBelt images.
        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                if (boardSpaces[x][y] == null || boardSpaces[x][y].getBoardElement() == null) continue;
                if (boardSpaces[x][y].getBoardElement() instanceof BE_ConveyorBelt conveyorBelt) {
                    conveyorBelt.updateConveyorBeltImage(x, y, boardSpaces);

                    // To the sides
                    updateNeighborConveyorBeltImage(x + 1, y, boardSpaces);
                    updateNeighborConveyorBeltImage(x - 1, y, boardSpaces);
                    updateNeighborConveyorBeltImage(x, y + 1, boardSpaces);
                    updateNeighborConveyorBeltImage(x, y - 1, boardSpaces);

                    // Two spaces to the sides
                    updateNeighborConveyorBeltImage(x + 2, y, boardSpaces);
                    updateNeighborConveyorBeltImage(x - 2, y, boardSpaces);
                    updateNeighborConveyorBeltImage(x, y + 2, boardSpaces);
                    updateNeighborConveyorBeltImage(x, y - 2, boardSpaces);

                    // Corners
                    updateNeighborConveyorBeltImage(x - 1, y - 1, boardSpaces);
                    updateNeighborConveyorBeltImage(x + 1, y - 1, boardSpaces);
                    updateNeighborConveyorBeltImage(x - 1, y + 1, boardSpaces);
                    updateNeighborConveyorBeltImage(x + 1, y + 1, boardSpaces);
                }
            }
        }

        return new Pair<>(subBoardList, boardSpaces);
    }

    private void updateNeighborConveyorBeltImage(int x, int y, Space[][] spaces) {
        if (x < 0 || x >= spaces.length || y < 0 || y >= spaces[0].length) return;

        Space neighborSpace = spaces[x][y];
        if (neighborSpace == null) return;
        if (neighborSpace.getBoardElement() instanceof BE_ConveyorBelt neighbor) {
            neighbor.updateConveyorBeltImage(neighborSpace.x, neighborSpace.y, spaces);
        }
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
            case Antenna -> new BE_Antenna(spaceView.getDirection());
            case BlueConveyorBelt -> new BE_ConveyorBelt(spaceView.getDirection(), 2);
            case GreenConveyorBelt -> new BE_ConveyorBelt(spaceView.getDirection(), 1);
            case PushPanel135 -> new BE_PushPanel("135", spaceView.getDirection());
            case PushPanel24 -> new BE_PushPanel("24", spaceView.getDirection());
            case GearRight -> new BE_Gear("Right");
            case GearLeft -> new BE_Gear("Left");
            case BoardLaser -> new BE_BoardLaser(spaceView.getDirection());
            case EnergySpace -> new BE_EnergySpace();
            default -> null;
        };
    }

    private BE_Checkpoint getCheckpointFromSpaceView(CC_SpaceView spaceView) {
        if (spaceView == null) return null;
        int itemBoardElement = spaceView.getCheckpoint();
        if (itemBoardElement == -1) {
            return null;
        }
        CC_Items item = CC_Items.values()[itemBoardElement];

        return switch (item) {
            case Checkpoint1 -> new BE_Checkpoint(1);
            case Checkpoint2 -> new BE_Checkpoint(2);
            case Checkpoint3 -> new BE_Checkpoint(3);
            case Checkpoint4 -> new BE_Checkpoint(4);
            case Checkpoint5 -> new BE_Checkpoint(5);
            case Checkpoint6 -> new BE_Checkpoint(6);
            default -> null;
        };
    }

    private Pair<Point2D, Point2D> getBoardBounds() {
        double minY = Double.MAX_VALUE;
        double minX = Double.MAX_VALUE;
        double maxY = 0;
        double maxX = 0;

        for (CC_SubBoard subBoard : subBoards) {
            Pair<Point2D, Point2D> subBoardBounds = getSubBoardBounds(subBoard);
            Point2D subBoardTopLeft = subBoardBounds.getKey();
            Point2D subBoardBottomRight = subBoardBounds.getValue();

            //Point2D subBoardBottomRight = new Point2D(subBoardTopLeft.getX() + subBoard.getSpaceViews().length, subBoardTopLeft.getY() + subBoard.getSpaceViews()[0].length);
            if (subBoardTopLeft.getY() < minY) {
                minY = subBoardTopLeft.getY();
            }
            if (subBoardBottomRight.getY() > maxY) {
                maxY = subBoardBottomRight.getY();
            }
            if (subBoardTopLeft.getX() < minX) {
                minX = subBoardTopLeft.getX();
            }
            if (subBoardBottomRight.getX() > maxX) {
                maxX = subBoardBottomRight.getX();
            }
        }

        Point2D topLeft = new Point2D(minX, minY);
        Point2D bottomRight = new Point2D(maxX, maxY);
        return new Pair<>(topLeft, bottomRight);
    }

    private Pair<Point2D, Point2D> getSubBoardBounds(CC_SubBoard subBoard) {
        Point2D point = subBoard.getPosition();
        Point2D subBoardMin = new Point2D(point.getX() * 5, point.getY() * 5);
        Point2D subBoardMax = new Point2D(point.getX() * 5, point.getY() * 5);
        if (!subBoard.isStartSubBoard()) {
            // Square subboard
            subBoardMax = new Point2D(subBoardMax.getX() + 10, subBoardMax.getY() + 10);
        } else {
            // Start subboard
            //System.out.println("*** FOUND START SUBBOARD ***: Direction: " + subBoard.getDirection());
            switch (subBoard.getDirection()) {
                case NORTH -> {
                    subBoardMax = new Point2D(subBoardMax.getX() + 10, subBoardMax.getY() + 3);
                }
                case EAST -> {
                    subBoardMin = new Point2D(subBoardMin.getX() + 2, subBoardMin.getY());
                    subBoardMax = new Point2D(subBoardMax.getX() + 5, subBoardMax.getY() + 10);
                }
                case SOUTH -> {
                    subBoardMin = new Point2D(subBoardMin.getX(), subBoardMin.getY() + 2);
                    subBoardMax = new Point2D(subBoardMax.getX() + 10, subBoardMax.getY() + 5);
                }
                case WEST -> {
                    subBoardMax = new Point2D(subBoardMax.getX() + 3, subBoardMax.getY() + 10);
                }
            }
        }
        return new Pair<>(subBoardMin, subBoardMax);
    }

    public String getIsPlayable() {
        return getIsPlayable(this.subBoards);
    }

    public static String getIsPlayable(List<CC_SubBoard> subBoards) {
        int noOfSpawnPoints = 0;
        int noOfAntennas = 0;
        boolean[] hasCheckpoint = new boolean[6];

        for (CC_SubBoard subBoard : subBoards) {
            for (CC_SpaceView[] spaceColumn : subBoard.getSpaceViews()) {
                for (CC_SpaceView space : spaceColumn) {
                    if (space == null) continue;
                    if (space.getPlacedBoardElement() == CC_Items.SpawnPoint.ordinal()) noOfSpawnPoints++;
                    if (space.getPlacedBoardElement() == CC_Items.Antenna.ordinal()) noOfAntennas++;
                    if (space.getCheckpoint() == CC_Items.Checkpoint1.ordinal()) hasCheckpoint[0] = true;
                    if (space.getCheckpoint() == CC_Items.Checkpoint2.ordinal()) hasCheckpoint[1] = true;
                    if (space.getCheckpoint() == CC_Items.Checkpoint3.ordinal()) hasCheckpoint[2] = true;
                    if (space.getCheckpoint() == CC_Items.Checkpoint4.ordinal()) hasCheckpoint[3] = true;
                    if (space.getCheckpoint() == CC_Items.Checkpoint5.ordinal()) hasCheckpoint[4] = true;
                    if (space.getCheckpoint() == CC_Items.Checkpoint6.ordinal()) hasCheckpoint[5] = true;
                }
            }
        }
        boolean checkpointsAreInOrder = true;
        boolean missingACheckpoint = false;
        for (boolean c : hasCheckpoint) {
            if (c) {
                if (missingACheckpoint) {
                    checkpointsAreInOrder = false;
                }
            } else {
                missingACheckpoint = true;
            }
        }

        boolean playable = noOfSpawnPoints >= 6 && noOfAntennas == 1 && hasCheckpoint[0] && checkpointsAreInOrder;
        if (playable) return "playable";

        String spawnPointsMessage = noOfSpawnPoints >= 6 ? "" : "Need at least 6 spawn points. Found " + noOfSpawnPoints + " spawn point" + (noOfSpawnPoints != 1 ? "s" : "" ) + ".\n";
        String antennaMessage = noOfAntennas == 1 ? "" : noOfAntennas < 1 ? "Missing an antenna.\n" : "Can only have 1 antenna. Found : " + noOfAntennas + " antennas.\n";
        String hasFirstCheckpointMessage = hasCheckpoint[0] ? "" : "Missing the first checkpoint.\n";
        StringBuilder missingCheckpointsMessage = getMissingCheckpointsAsString(checkpointsAreInOrder, hasCheckpoint);
        return "\n" + spawnPointsMessage + antennaMessage + hasFirstCheckpointMessage + missingCheckpointsMessage;
    }

    private static @NotNull StringBuilder getMissingCheckpointsAsString(boolean checkpointsAreInOrder, boolean[] hasCheckpoint) {
        StringBuilder hasCheckpointsInMessage = new StringBuilder(checkpointsAreInOrder ? "" : "Checkpoints are not in order. Found checkpoints: ");
        boolean foundCheckpoint = false;
        for (int i = 0; i < hasCheckpoint.length; i++) {
            if (foundCheckpoint)
                hasCheckpointsInMessage.append(", ");
            if (hasCheckpoint[i]) {
                foundCheckpoint = true;
                hasCheckpointsInMessage.append(i);
            }
        }
        hasCheckpointsInMessage.append("\n");

        return hasCheckpointsInMessage;
    }

    public int getNoOfCheckpoints() {
        int noOfCheckpoints = 0;
        for (CC_SubBoard subBoard : subBoards) {
            for (CC_SpaceView[] spaceViewColumn : subBoard.getSpaceViews()) {
                for (CC_SpaceView spaceView : spaceViewColumn) {
                    if (getCheckpointFromSpaceView(spaceView) != null) noOfCheckpoints++;
                }
            }
        }
        return noOfCheckpoints;
    }
}
