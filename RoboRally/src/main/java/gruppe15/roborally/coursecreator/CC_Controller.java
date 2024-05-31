package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.*;
import gruppe15.roborally.exceptions.EmptyCourseException;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.List;

import static gruppe15.roborally.GameVariables.*;
import static gruppe15.roborally.model.Heading.*;
import static gruppe15.roborally.model.utils.ImageUtils.*;

/**
 * The controller of the course creator. Manages the GUI, board, sub boards, space views, saving & loading, and input.
 * Should eventually be broken up into two classes, a view and the controller.
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class CC_Controller extends BorderPane {
    @FXML
    ScrollPane CC_boardScrollPane;
    @FXML
    AnchorPane CC_boardPane; // Course creator board pane
    static AnchorPane CC_static_boardPane;
    @FXML
    HBox CC_elementButtonsHBox;
    @FXML
    MenuItem CC_saveCourse;
    @FXML
    MenuItem CC_loadCourse;
    @FXML
    MenuItem CC_exitToMainMenu;

    private Scene primaryScene;

    private String latestFolderPath = "";
    private String latestLoadedCourseName = "";

    private static final int canvasSize = 5000;
    private static CC_SpaceView[][] spaces;

    /**
     * Since subboards can be placed half a step, this makes up 5x5 subboards.
     */
    private final static int NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY = 10;
    private final static int NO_OF_SUBBOARD_POSITIONS_VERTICALLY = 10;

    private final SpaceEventHandler spaceEventHandler;

    private CC_Items selectedItem = CC_Items.SubBoard;
    private Heading currentRotation = NORTH;

    private final List<CC_SubBoard> subBoards;

    public CC_Controller() {
        spaceEventHandler = new SpaceEventHandler();
        this.subBoards = new ArrayList<>();
    }

    public void setScene(Scene primaryScene) {
        this.primaryScene = primaryScene;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            CC_static_boardPane = CC_boardPane;
            drawSubBoardPositionLines();
            CC_boardPane.setPrefSize(canvasSize, canvasSize);
            CC_boardScrollPane.setVvalue(0.5);
            CC_boardScrollPane.setHvalue(0.5);

            CC_boardPane.setStyle("-fx-border-width: 25; -fx-border-color: BLACK");
            //CC_boardScrollPane.setStyle("-fx-border-width: 50; -fx-border-color: RED");

            CC_boardPane.addEventHandler(MouseEvent.MOUSE_MOVED, spaceEventHandler);
            CC_boardPane.addEventHandler(MouseEvent.MOUSE_CLICKED, spaceEventHandler);
            CC_boardPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, spaceEventHandler);
            CC_boardPane.addEventHandler(MouseEvent.MOUSE_PRESSED, spaceEventHandler);
            registerKeyEventHandlers();

            for (int i = 0; i < CC_Items.values().length; i++) {
                CC_Items item = CC_Items.values()[i];
                ImageView itemButtonImageView = new ImageView(item.image);

                itemButtonImageView.setFitWidth(100);
                itemButtonImageView.setFitHeight(100);
                itemButtonImageView.setImage(item.image);

                Button itemButton = new Button();
                itemButton.setPrefSize(100, 100);
                HBox.setMargin(itemButton, new Insets(25, 12.5, 25, 12.5));
                itemButton.setGraphic(itemButtonImageView);

                CC_elementButtonsHBox.getChildren().add(itemButton);
                itemButton.setOnMouseClicked(event -> {
                    selectedItem = item;
                    spaceEventHandler.removeSubBoardHighlight();
                });

                CC_elementButtonsHBox.setOnMouseClicked(event -> {
                    selectedItem = null;
                    spaceEventHandler.removeSubBoardHighlight();
                });

                CC_saveCourse.setOnAction( e -> {
                    saveCourse();
                });

                CC_loadCourse.setOnAction(e -> {
                    loadCourse();
                });
            }
        });

        spaces = new CC_SpaceView[NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY * 5][NO_OF_SUBBOARD_POSITIONS_VERTICALLY * 5];
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[0].length; y++) {
                spaces[x][y] = null;
            }
        }
    }

    public void initializeExitButton(Runnable goToMainMenu) {
        CC_exitToMainMenu.setOnAction( e -> {
            saveCourseDialog();
            goToMainMenu.run();
        });
    }

    public void zoom(ScrollEvent event) {
        double scaleFactor = (event.getDeltaY() > 0) ? 1 + ZOOM_SPEED : 1 - ZOOM_SPEED;
        CC_boardPane.setScaleX(CC_boardPane.getScaleX() * scaleFactor);
        CC_boardPane.setScaleY(CC_boardPane.getScaleY() * scaleFactor);
        if (CC_boardPane.getScaleX() < MIN_ZOOM) {
            CC_boardPane.setScaleX(MIN_ZOOM);
            CC_boardPane.setScaleY(MIN_ZOOM);
        } else if (CC_boardPane.getScaleX() > MAX_ZOOM) {
            CC_boardPane.setScaleX(MAX_ZOOM);
            CC_boardPane.setScaleY(MAX_ZOOM);
        }
        event.consume();
    }

    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.E) {
            currentRotation = currentRotation.next();
            spaceEventHandler.rotationChanged();
        }
        if (keyEvent.getCode() == KeyCode.Q) {
            currentRotation = currentRotation.prev();
            spaceEventHandler.rotationChanged();
        }
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            spaceEventHandler.shiftPressed(true);
        }
    }

    public void keyReleased(KeyEvent keyEvent) {
        if (keyEvent.getCode() == KeyCode.SHIFT) {
            spaceEventHandler.shiftPressed(false);
        }
    }

    private void newSubBoard(Point2D position) {
        newSubBoard(position, false, NORTH);
    }
    private void newStartSubBoard(Point2D position) {
        newSubBoard(position, true, currentRotation);
    }
    private void newSubBoard(Point2D position, boolean isStartSubBoard, Heading direction) {
        int subBoardWidth = 10;
        int subBoardHeight = 10;
        if (isStartSubBoard) {
            subBoardWidth = (direction == NORTH || direction == Heading.SOUTH) ? 10 : 3;
            subBoardHeight = (direction == Heading.EAST || direction == Heading.WEST) ? 10 : 3;
        }
        // Can't put new subboard where one already exists.
        if (subBoardOverlappingOrOOB(position, subBoardWidth, subBoardHeight)) return;
        // Can only place within bounds
        if (position.getX() >= NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY || position.getY() >= NO_OF_SUBBOARD_POSITIONS_VERTICALLY) return;

        CC_SpaceView[][] subBoardSpaceViews = new CC_SpaceView[subBoardWidth][subBoardHeight];
        Pair<Point2D, Point2D> subBoardBounds = getSubBoardBounds(position, isStartSubBoard, direction);
        Point2D boardRelativePos = subBoardBounds.getKey();

        for (int subBoardX = 0; subBoardX < subBoardWidth; subBoardX++) {
            for (int subBoardY = 0; subBoardY < subBoardHeight; subBoardY++) {
                int boardX = (int)(boardRelativePos.getX() + subBoardX);
                int boardY = (int)(boardRelativePos.getY() + subBoardY);
                // Spaces in subboard
                CC_SpaceView spaceView = new CC_SpaceView(boardX, boardY);
                // Add space to board and subboard
                spaces[boardX][boardY] = spaceView;
                subBoardSpaceViews[subBoardX][subBoardY] = spaceView;
            }
        }

        // Making GridPane
        GridPane subBoardGridPane = getNewGridPane(position, direction, subBoardSpaceViews);
        // Instantiating the new subboard
        CC_SubBoard newSubBoard = new CC_SubBoard(position, subBoardSpaceViews, subBoardGridPane, isStartSubBoard, direction);
        // Initializing the SpaceViews
        initializeSpaceViews(newSubBoard);
        CC_boardPane.getChildren().add(subBoardGridPane);
        subBoards.add(newSubBoard);
        //System.out.println("New subboard at: " + position.getX() + ", " + position.getY());
    }

    private Pair<Point2D, Point2D> getSubBoardBounds(Point2D position, boolean isStartSubBoard, Heading direction) {
        Point2D subBoardMin = new Point2D(position.getX() * 5, position.getY() * 5);
        Point2D subBoardMax = new Point2D(position.getX() * 5, position.getY() * 5);
        if (!isStartSubBoard) {
            // Square subboard
            subBoardMax = new Point2D(subBoardMax.getX() + 10, subBoardMax.getY() + 10);
        } else {
            // Start subboard
            //System.out.println("*** FOUND START SUBBOARD ***: Direction: " + subBoard.getDirection());
            switch (direction) {
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

    public static @NotNull GridPane getNewGridPane(Point2D position, Heading direction, CC_SpaceView[][] spaceViews) {
        double spaceViewWidth = (double) canvasSize / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY / 5;
        double spaceViewHeight = (double) canvasSize / NO_OF_SUBBOARD_POSITIONS_VERTICALLY / 5;
        double subBoardWidth = spaceViewWidth * spaceViews.length;
        double subBoardHeight = spaceViewHeight * spaceViews[0].length;

        GridPane subBoardGridPane = new GridPane();
        subBoardGridPane.setAlignment(Pos.TOP_LEFT);
        subBoardGridPane.setPrefSize(subBoardWidth, subBoardHeight);

        Point2D positionInScene = new Point2D(
                (position.getX() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY * canvasSize) + (direction == Heading.EAST ? (5 - spaceViews.length) * spaceViewWidth : 0),
                (position.getY() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY * canvasSize) + (direction == Heading.SOUTH ? (5 - spaceViews[0].length) * spaceViewHeight : 0)
        );

        subBoardGridPane.setLayoutX(positionInScene.getX());
        subBoardGridPane.setLayoutY(positionInScene.getY());

        return subBoardGridPane;
    }

    private void initializeSpaceViews(CC_SubBoard subBoard) {
        double spaceViewWidth = CC_boardPane.getWidth() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY / 5;
        double spaceViewHeight = CC_boardPane.getHeight() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY / 5;
        CC_SpaceView[][] subBoardSpaceViews = subBoard.getSpaceViews();
        boolean isStartSubBoard = subBoardSpaceViews.length <= 3 || subBoardSpaceViews[0].length <= 3;
        Pair<Point2D, Point2D> subBoardBounds = getSubBoardBounds(subBoard.getPosition(), isStartSubBoard, subBoard.getDirection());
        Point2D boardRelativePos = subBoardBounds.getKey();

        int subBoardWidth = 10;
        int subBoardHeight = 10;
        if (isStartSubBoard) {
            subBoardWidth = (subBoard.getDirection() == NORTH || subBoard.getDirection() == Heading.SOUTH) ? 10 : 3;
            subBoardHeight = (subBoard.getDirection() == Heading.EAST || subBoard.getDirection() == Heading.WEST) ? 10 : 3;
        }

        for (int subBoardX = 0; subBoardX < subBoardWidth; subBoardX++) {
            for (int subBoardY = 0; subBoardY < subBoardHeight; subBoardY++) {
                int boardX = (int)(boardRelativePos.getX() + subBoardX);
                int boardY = (int)(boardRelativePos.getY() + subBoardY);

                CC_SpaceView spaceView;
                if (subBoardSpaceViews[subBoardX][subBoardY] == null) {
                    // Create new space and add to the subboard
                    spaceView = new CC_SpaceView(boardX, boardY);
                    subBoardSpaceViews[subBoardX][subBoardY] = spaceView;
                } else {
                    // Get space from subboard and set its global position
                    spaceView = subBoardSpaceViews[subBoardX][subBoardY];
                    spaceView.setBoardXY(boardX, boardY);
                }

                // Add space to board
                spaces[boardX][boardY] = spaceView;

                spaceView.initialize(spaceViewWidth, isStartSubBoard);
                spaceView.setPrefSize(spaceViewWidth, spaceViewHeight);
                subBoard.getGridPane().add(spaceView, subBoardX, subBoardY);
            }
        }
    }

    private void removeSubBoard(Point2D position) {
        CC_SubBoard subBoardToRemove = null;
        for (CC_SubBoard subBoard : subBoards) {
            if (subBoard.getPosition().equals(position)) {
                subBoardToRemove = subBoard;
                break;
            }
        }
        if (subBoardToRemove == null) {
            return;
        }

        CC_boardPane.getChildren().remove(subBoardToRemove.getGridPane());
        subBoards.remove(subBoardToRemove);
    }

    private boolean subBoardOverlappingOrOOB(Point2D position, int xLength, int yLength) {
        return subBoardOOB(position, xLength, yLength) && getOverlappingSubBoard(position, xLength, yLength) == null;
    }

    private boolean subBoardOOB(Point2D position, int xLength, int yLength) {
        int otherWidth = Math.ceilDiv(xLength, 5);
        int otherHeight = Math.ceilDiv(yLength, 5);
        // Bounds
        if (position.getX() < 0 || position.getX() + otherWidth > NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY ||
                position.getY() < 0 || position.getY() + otherHeight > NO_OF_SUBBOARD_POSITIONS_VERTICALLY) {
            return true;
        }
        return false;
    }

    private CC_SubBoard getOverlappingSubBoard(Point2D position, int xLength, int yLength) {
        int otherWidth = Math.ceilDiv(xLength, 5);
        int otherHeight = Math.ceilDiv(yLength, 5);

        // Overlap
        for (CC_SubBoard subBoard : subBoards) {
            Point2D existingPosition = subBoard.getPosition();
            int existingWidth = Math.ceilDiv(subBoard.getSpaceViews().length, 5);
            int existingHeight = Math.ceilDiv(subBoard.getSpaceViews()[0].length, 5);

            if(position.getX() < existingPosition.getX() + existingWidth &&
                    position.getX() + otherWidth > existingPosition.getX() &&
                    position.getY() < existingPosition.getY() + existingHeight &&
                    position.getY() + otherHeight > existingPosition.getY()) {
                return subBoard;
            }
        }
        return null;
    }

    private Point2D getSubBoardPositionOnMouse(MouseEvent event, boolean snapToGrid, boolean withXOffset, boolean withYOffset) {
        Point2D mousePosition = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D localMousePosition = CC_boardPane.sceneToLocal(mousePosition);
        double cellWidth = CC_boardPane.getWidth() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY;
        double cellHeight = CC_boardPane.getHeight() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY;

        double offsetX = withXOffset ? (cellWidth / 2) : 0;
        double offsetY = withYOffset ? (cellHeight / 2) : 0;

        double x = (localMousePosition.getX() - offsetX) * NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY / CC_boardPane.getWidth();
        double y = (localMousePosition.getY() - offsetY) * NO_OF_SUBBOARD_POSITIONS_VERTICALLY / CC_boardPane.getHeight();

        if (snapToGrid) {
            return new Point2D((int)x, (int)y);
        }

        return new Point2D(x, y);
    }

    private List<CC_SpaceView> getSpacesAtMouse(MouseEvent event) {
        List<CC_SpaceView> spacesAtMouse = new ArrayList<>();
        for (CC_SubBoard subBoard : subBoards) {
            for (CC_SpaceView[] subBoardColumns : subBoard.getSpaceViews()) {
                for (CC_SpaceView spaceView : subBoardColumns) {
                    Bounds localBounds = spaceView.getBoundsInLocal();
                    Bounds sceneBounds = spaceView.localToScene(localBounds);
                    if (sceneBounds.contains(new Point2D(event.getSceneX(), event.getSceneY()))) {
                        // If mouse is within bounds of a node
                        spacesAtMouse.add(spaceView);
                    }
                }
            }
        }
        return spacesAtMouse;
    }

    private void drawSubBoardPositionLines() {
        double hSpacing = CC_boardPane.getWidth() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY;
        double vSpacing = CC_boardPane.getHeight() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY;

        // Horizontal lines
        for (int y = 0; y < NO_OF_SUBBOARD_POSITIONS_VERTICALLY; y++) {
            Line line = new Line(0, y * vSpacing, CC_boardPane.getWidth(), y * vSpacing);
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(3);
            CC_boardPane.getChildren().add(line);
        }

        // Vertical lines
        for (int x = 0; x < NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY; x++) {
            Line line = new Line(x * hSpacing, 0, x * hSpacing, CC_boardPane.getHeight());
            line.setStroke(Color.GRAY);
            line.setStrokeWidth(3);
            CC_boardPane.getChildren().add(line);
        }
    }

    public void registerKeyEventHandlers() {
        primaryScene.addEventFilter(KeyEvent.KEY_PRESSED, this::keyPressed);
        primaryScene.addEventFilter(KeyEvent.KEY_RELEASED, this::keyReleased);

        KeyCombination ctrlS = new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN);
        primaryScene.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
            if (ctrlS.match(keyEvent)) {
                saveCourse();
                keyEvent.consume();
            }
        });
    }

    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        private CC_SpaceView previousSpaceView = null;
        private final List<Line> highlightedSubBoardPositionLines = new ArrayList<>();
        private boolean rotationChanged = false;
        private MouseEvent previousMoveEvent = null;
        private boolean isDrawing = false;

        @Override
        public void handle(MouseEvent event) {
            if (selectedItem == null) return;

            if (event.isPrimaryButtonDown() && !event.isControlDown() && !event.isAltDown()) {
                isDrawing = true;
                event.consume();
            } else {
                isDrawing = false;
            }

            // SubBoards
            handleSubBoardMouseEvent(event, event.isShiftDown());
            // SpaceViews
            handleSpaceViewMouseEvent(event);

            rotationChanged = false;

            if (event.getEventType() == MouseEvent.MOUSE_MOVED || event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                previousMoveEvent = event;
            }
        }

        public void rotationChanged() {
            rotationChanged = true;
            handlePreviousEvent();
        }

        public void handlePreviousEvent() {
            if (previousMoveEvent != null) {
                handle(previousMoveEvent);
            }
        }

        public void shiftPressed(boolean shiftIsDown) {
            if (previousMoveEvent != null) {
                handleSubBoardMouseEvent(previousMoveEvent, shiftIsDown);
            }
        }

        private void handleSubBoardMouseEvent(MouseEvent event, boolean shiftIsDown) {
            if (!(selectedItem == CC_Items.SubBoard || selectedItem == CC_Items.StartSubBoard)) return;
            removeSubBoardHighlight();

            boolean withXOffset = selectedItem.ordinal() == 0 || (selectedItem.ordinal() == 1 && (currentRotation == NORTH || currentRotation == SOUTH));
            boolean withYOffset = selectedItem.ordinal() == 0 || (selectedItem.ordinal() == 1 && (currentRotation == EAST || currentRotation == WEST));
            Point2D hoveredSubBoardPosition = getSubBoardPositionOnMouse(event, true, withXOffset, withYOffset);

            int xLength = 10;
            int yLength = 10;
            if (selectedItem == CC_Items.StartSubBoard) {
                xLength = (currentRotation == NORTH || currentRotation == Heading.SOUTH) ? 10 : 3;
                yLength = (currentRotation == Heading.EAST || currentRotation == Heading.WEST) ? 10 : 3;
            }

            if (!subBoardOOB(hoveredSubBoardPosition, xLength, yLength)) {
                CC_SubBoard overLappingSubBoard = getOverlappingSubBoard(hoveredSubBoardPosition, xLength, yLength);
                if (overLappingSubBoard == null) {
                    if (!shiftIsDown) {
                        // Highlight new sub board placement
                        highlightSubBoard(hoveredSubBoardPosition, xLength, yLength, currentRotation, Color.BLACK);
                        if (isDrawing) {
                            // Creation of sub board
                            if (selectedItem == CC_Items.SubBoard) {
                                newSubBoard(hoveredSubBoardPosition);
                            } else if (selectedItem == CC_Items.StartSubBoard) {
                                newStartSubBoard(hoveredSubBoardPosition);
                            }
                        }
                    }
                } else {
                    if (shiftIsDown) {
                        Point2D hoveredPosition = getSubBoardPositionOnMouse(event, false, withXOffset, withYOffset);
                        hoveredPosition = new Point2D(hoveredPosition.getX() - 0.5, hoveredPosition.getY() - 0.5);
                        if (Math.abs(hoveredPosition.getX() - overLappingSubBoard.getPosition().getX()) <= 1 &&
                                Math.abs(hoveredPosition.getY() - overLappingSubBoard.getPosition().getY()) <= 1) {
                            // Highlight deletion of sub board
                            highlightSubBoard(overLappingSubBoard.getPosition(), overLappingSubBoard.getSpaceViews().length, overLappingSubBoard.getSpaceViews()[0].length, overLappingSubBoard.getDirection(), Color.RED);
                            if (isDrawing) {
                                // Deletion of sub board
                                removeSubBoard(overLappingSubBoard.getPosition());
                            }
                        }
                    }
                }
            }
        }

        private void handleSpaceViewMouseEvent(MouseEvent event) {
            if (selectedItem == CC_Items.SubBoard || selectedItem == CC_Items.StartSubBoard) return;
            if (previousSpaceView != null) {
                previousSpaceView.CC_setGhost(null, null);
                previousSpaceView = null;
            }
            List<CC_SpaceView> CC_SpaceViewsOnMouse = getSpacesAtMouse(event);
            // SpaceViews
            if (CC_SpaceViewsOnMouse.isEmpty()) {
                return;
            }
            CC_SpaceView hoveredSpaceView = CC_SpaceViewsOnMouse.getFirst();

            if (isDrawing) {
                if (event.isShiftDown()) {
                    // Deletion of item at space
                    if (selectedItem == CC_Items.Wall) {
                        hoveredSpaceView.CC_setWall(null, currentRotation);
                    } else if (selectedItem.ordinal() >= CC_Items.Checkpoint1.ordinal() && selectedItem.ordinal() <= CC_Items.Checkpoint6.ordinal()) {
                        hoveredSpaceView.CC_setCheckpoint(null, -1);
                    } else {
                        hoveredSpaceView.CC_setBoardElement(null, null, -1, spaces);
                    }
                } else {
                    // Placement of item at space
                    if (selectedItem == CC_Items.Wall) {
                        hoveredSpaceView.CC_setWall(selectedItem.image, selectedItem.canBeRotated ? currentRotation : NORTH);
                    } else if (selectedItem.ordinal() >= CC_Items.Checkpoint1.ordinal() && selectedItem.ordinal() <= CC_Items.Checkpoint6.ordinal()) {
                        hoveredSpaceView.CC_setCheckpoint(selectedItem.image, selectedItem.ordinal());
                    } else {
                        hoveredSpaceView.CC_setBoardElement(selectedItem.image, selectedItem.canBeRotated ? currentRotation : NORTH, selectedItem.ordinal(), spaces);
                    }
                }
                // Remove ghost on this space when mouse clicked
                if (hoveredSpaceView != previousSpaceView) {
                    hoveredSpaceView.CC_setGhost(null, null);
                }
            } else {
                if (previousSpaceView == null || rotationChanged) {
                    hoveredSpaceView.CC_setGhost(selectedItem.image, selectedItem.canBeRotated ? currentRotation : NORTH);
                    previousSpaceView = hoveredSpaceView;
                }
            }
        }

        private void highlightSubBoard(Point2D position, int xLength, int yLength, Heading direction, Color lineColor) {
            Point2D positionInScene = new Point2D(
                    (position.getX() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY) * CC_boardPane.getWidth(),
                    (position.getY() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY) * CC_boardPane.getHeight()
            );

            double spaceViewWidth = CC_boardPane.getWidth() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY / 5;
            double spaceViewHeight = CC_boardPane.getHeight() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY / 5;
            double subBoardWidth = spaceViewWidth * xLength;
            double subBoardHeight = spaceViewHeight * yLength;
            double leftBorder = positionInScene.getX();
            double topBorder = positionInScene.getY();

            if (xLength <= 5 || yLength <= 5) {
                leftBorder += spaceViewWidth * (direction == Heading.EAST ? 2 : 0);
                topBorder += spaceViewWidth * (direction == Heading.SOUTH ? 2 : 0);
            }

            double rightBorder = leftBorder + subBoardWidth;
            double bottomBorder = topBorder + subBoardHeight;

            Line upperLine = new Line(leftBorder, topBorder, rightBorder, topBorder);
            upperLine.setStroke(lineColor);
            upperLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(upperLine);
            highlightedSubBoardPositionLines.add(upperLine);

            Line lowerLine = new Line(leftBorder, bottomBorder, rightBorder, bottomBorder);
            lowerLine.setStroke(lineColor);
            lowerLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(lowerLine);
            highlightedSubBoardPositionLines.add(lowerLine);

            Line leftLine = new Line(leftBorder, topBorder, leftBorder, bottomBorder);
            leftLine.setStroke(lineColor);
            leftLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(leftLine);
            highlightedSubBoardPositionLines.add(leftLine);

            Line rightLine = new Line(rightBorder, topBorder, rightBorder, bottomBorder);
            rightLine.setStroke(lineColor);
            rightLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(rightLine);
            highlightedSubBoardPositionLines.add(rightLine);
        }

        public void removeSubBoardHighlight() {
            Iterator<Node> iterator = CC_boardPane.getChildren().iterator();
            while (iterator.hasNext()) {
                Node child = iterator.next();
                if (child instanceof Line line) {
                    if (highlightedSubBoardPositionLines.contains(line)) {
                        iterator.remove();
                        highlightedSubBoardPositionLines.remove(line);
                    }
                }
            }
        }
    }

    private void loadCourse(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load Course");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        String userHome = System.getProperty("user.home");
        String relativePath = "RoboRally/courses";
        String directoryPath = latestFolderPath.isEmpty() ? (userHome + File.separator + relativePath) : latestFolderPath;

        File folderFile = new File(directoryPath);
        // Create the courses directory if it doesn't exist
        if (!folderFile.exists()) {
            if (folderFile.mkdirs()) {
                System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
            }
        }

        fileChooser.setInitialDirectory(folderFile);
        File loadedFile = fileChooser.showOpenDialog(primaryScene.getWindow());
        if (loadedFile != null) {
            latestFolderPath = loadedFile.getParent();
            // Load course data from the selected file
            CC_CourseData courseData = CC_JsonUtil.loadCourseDataFromFile(loadedFile);
            if (courseData != null) {
                latestLoadedCourseName = courseData.getCourseName();
                List<CC_SubBoard> loadedBoard = courseData.getSubBoards();
                initializeLoadedBoard(loadedBoard);
            } else {
                System.err.println("Failed to load course data from file: " + loadedFile.getAbsolutePath());
            }
        }
    }

    private void initializeLoadedBoard(List<CC_SubBoard> loadedBoard) {
        // Remove old
        for (CC_SubBoard subBoard : subBoards) {
            CC_boardPane.getChildren().remove(subBoard.getGridPane());
        }
        subBoards.clear();

        // Add new
        for (CC_SubBoard subBoard : loadedBoard) {
            initializeSpaceViews(subBoard);
            CC_boardPane.getChildren().add(subBoard.getGridPane());
            subBoards.add(subBoard);
        }

        // Updating conveyor belt images
        for (CC_SubBoard subBoard : loadedBoard) {
            for (CC_SpaceView[] spaceColumn : subBoard.getSpaceViews()) {
                for (CC_SpaceView space : spaceColumn) {
                    if (space.getPlacedBoardElement() == 7 || space.getPlacedBoardElement() == 8) {
                        space.updateConveyorBeltImages(spaces);
                    }
                    if (space.getPlacedBoardElement() >= CC_Items.Checkpoint1.ordinal() && space.getPlacedBoardElement() <= CC_Items.Checkpoint6.ordinal()) {
                        space.CC_setCheckpoint(CC_Items.values()[space.getPlacedBoardElement()].image, space.getPlacedBoardElement());
                        space.CC_setBoardElement(null, null, -1);
                        space.updateConveyorBeltImages(spaces);
                    }
                }
            }
        }
    }

    public void saveCourseDialog() {
        Dialog saveGameDialog = new Dialog();
        saveGameDialog.setHeaderText("Do you want to save the course?");
        saveGameDialog.setTitle("Save Course");
        ButtonType saveButton = new ButtonType("Save");
        ButtonType dontSaveButton = new ButtonType("Don't Save");
        saveGameDialog.getDialogPane().getButtonTypes().addAll(saveButton, dontSaveButton);
        Optional<ButtonType> saveCourseResult = saveGameDialog.showAndWait();

        // Method appController.saveGame() will return false if the game is not in the programming
        // phase, and an error message will be shown to the user. Game will then continue to run.
        if (saveCourseResult.get() == saveButton){
            saveCourse();
        }
    }

    public void saveCourse() {
        String playableMessage = CC_CourseData.getIsPlayable(subBoards);

        if (!playableMessage.equals("playable")) {
            Alert notPlayableAlert = new Alert(Alert.AlertType.ERROR);
            notPlayableAlert.setHeaderText("Course not playable");
            notPlayableAlert.setContentText("The course can be saved, but is not playable. To make the course playable, the following conditions need to be met:\n" + playableMessage + "\nSave anyways?");
            Optional<ButtonType> notPlayableResult = notPlayableAlert.showAndWait();

            boolean saveAnyways = notPlayableResult.isPresent() && notPlayableResult.get() == ButtonType.OK;
            if (!saveAnyways) return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Course");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));

        // Determine the user's home directory and set the relative path for RoboRally courses
        String userHome = System.getProperty("user.home");
        String relativePath = "RoboRally/courses";
        String directoryPath = latestFolderPath.isEmpty() ? (userHome + File.separator + relativePath) : latestFolderPath;

        File folderFile = new File(directoryPath);
        // Create the courses directory if it doesn't exist
        if (!folderFile.exists()) {
            if (folderFile.mkdirs()) {
                System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
            }
        }

        fileChooser.setInitialDirectory(folderFile);
        fileChooser.setInitialFileName(latestLoadedCourseName.isEmpty() ? "New course.json" : latestLoadedCourseName);
        File saveFile = fileChooser.showSaveDialog(primaryScene.getWindow());

        if (saveFile != null) {
            try {
                Dialog<ButtonType> saveCourseImageDialog = new Dialog<>();
                saveCourseImageDialog.setHeaderText("Do you also want to save the course image as its own file?");
                saveCourseImageDialog.setTitle("Save course image as PNG");
                ButtonType yesButton = new ButtonType("Save as PNG");
                ButtonType noButton = new ButtonType("Don't Save");
                saveCourseImageDialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
                Optional<ButtonType> saveGameResult = saveCourseImageDialog.showAndWait();
                boolean saveImageAsPNG = saveGameResult.isPresent() && saveGameResult.get() == yesButton;

                setLinesVisible(false);
                CC_CourseData courseData = new CC_CourseData(
                        saveFile.getName().replace(".json", ""),
                        subBoards,
                        getSnapshotAsBase64(CC_boardPane, 10)
                );
                setLinesVisible(true);

                System.out.println("Saving course data to: " + saveFile.getAbsolutePath());
                CC_JsonUtil.saveCourseDataToFile(courseData, saveFile, saveImageAsPNG);
            } catch (EmptyCourseException e) {
                System.err.println(e.getMessage());
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Error in saving course");
                errorAlert.setContentText("Course name can not be empty. The course was not saved.");
                errorAlert.showAndWait();
            }
        }
        setLinesVisible(true);
    }

    private void setLinesVisible(boolean visible) {
        if (visible) {
            CC_boardPane.setStyle("-fx-border-width: 25; -fx-border-color: BLACK");
        } else {
            CC_boardPane.setStyle("");
        }
        for (Node child : CC_boardPane.getChildren()) {
            if (child instanceof Line line) {
                line.setVisible(visible);
            }
        }
    }
}
