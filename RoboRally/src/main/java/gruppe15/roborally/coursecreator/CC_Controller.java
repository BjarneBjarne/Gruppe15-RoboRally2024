package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.*;
import gruppe15.roborally.exceptions.EmptyCourseException;
import gruppe15.roborally.view.ZoomableScrollPane;
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
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.List;

import static gruppe15.roborally.model.utils.ImageUtils.*;

public class CC_Controller extends BorderPane {
    static AnchorPane CC_static_boardPane;
    @FXML
    HBox CC_elementButtonsHBox;
    @FXML
    MenuItem CC_saveCourse;
    @FXML
    MenuItem CC_loadCourse;
    @FXML
    Pane CC_boardView;
    ZoomableScrollPane zoomableScrollPane;
    AnchorPane CC_boardPane;

    private Scene primaryScene;

    private static int canvasSize = 5000;

    /**
     * Since subboards can be placed half a step, this makes up 5x5 subboards.
     */
    private final static int NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY = 10;
    private final static int NO_OF_SUBBOARD_POSITIONS_VERTICALLY = 10;

    private final SpaceEventHandler spaceEventHandler;

    private CC_Items selectedItem = CC_Items.SubBoard;
    private Heading currentRotation = Heading.NORTH;

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
        CC_boardPane = new AnchorPane();
        CC_static_boardPane = CC_boardPane;

        zoomableScrollPane = new ZoomableScrollPane(CC_boardPane);

        CC_boardView.getChildren().add(zoomableScrollPane);


        CC_boardView.setStyle("-fx-border-width: 20; -fx-border-color: red");
        zoomableScrollPane.setStyle("-fx-border-width: 20; -fx-border-color: blue");
        CC_boardPane.setStyle("-fx-border-width: 20; -fx-border-color: BLACK");

        CC_boardPane.setOnMouseMoved(spaceEventHandler);
        CC_boardPane.setOnMouseClicked(spaceEventHandler);
        Platform.runLater(() -> {
            drawSubBoardPositionLines();
            registerKeyEventHandlers();

            Platform.runLater(() -> {
                CC_boardPane.setMinSize(canvasSize, canvasSize);
                CC_boardPane.setPrefSize(canvasSize, canvasSize);
                CC_boardPane.setMaxSize(canvasSize, canvasSize);

                StackPane.setAlignment(CC_boardPane, Pos.CENTER);

                zoomableScrollPane.setVvalue(0.5);
                zoomableScrollPane.setHvalue(0.5);
            });

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

                CC_saveCourse.setOnAction( e -> {
                    saveCourse();
                });

                CC_loadCourse.setOnAction(e -> {
                    loadCourse();
                });
            }
        });
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
    }

    private void newSubBoard(Point2D position) {
        newSubBoard(position, false, Heading.NORTH);
    }
    private void newStartSubBoard(Point2D position) {
        newSubBoard(position, true, currentRotation);
    }
    private void newSubBoard(Point2D position, boolean isStartSubBoard, Heading direction) {
        // Making SpaceViews
        int xLength = 10;
        int yLength = 10;
        if (isStartSubBoard) {
            xLength = (direction == Heading.NORTH || direction == Heading.SOUTH) ? 10 : 3;
            yLength = (direction == Heading.EAST || direction == Heading.WEST) ? 10 : 3;
        }

        // Can't put new subboard where one already exists.
        if (isOverlapping(position, xLength, yLength)) {
            return;
        }

        // Making SpaceViews
        CC_SpaceView[][] subBoardSpaceViews;
        subBoardSpaceViews = new CC_SpaceView[xLength][yLength];
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
        for (int x = 0; x < subBoardSpaceViews.length; x++) {
            for (int y = 0; y < subBoardSpaceViews[x].length; y++) {
                CC_SpaceView spaceView;
                if (subBoardSpaceViews[x][y] == null) {
                    spaceView = new CC_SpaceView();
                } else {
                    spaceView = subBoardSpaceViews[x][y];
                }

                spaceView.initialize(spaceViewWidth, subBoardSpaceViews.length <= 3 || subBoardSpaceViews[0].length <= 3);
                spaceView.setPrefSize(spaceViewWidth, spaceViewHeight);
                subBoard.getGridPane().add(spaceView, x, y);
                subBoardSpaceViews[x][y] = spaceView;
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

    private boolean isOverlapping(Point2D position, int xLength, int yLength) {
        for (CC_SubBoard subBoard : subBoards) {
            Point2D existingPosition = subBoard.getPosition();
            int existingWidth = Math.ceilDiv(subBoard.getSpaceViews().length, 5);
            int existingHeight = Math.ceilDiv(subBoard.getSpaceViews()[0].length, 5);
            int otherWidth = Math.ceilDiv(xLength, 5);
            int otherHeight = Math.ceilDiv(yLength, 5);

            boolean overlaps = position.getX() < existingPosition.getX() + existingWidth &&
                    position.getX() + otherWidth > existingPosition.getX() &&
                    position.getY() < existingPosition.getY() + existingHeight &&
                    position.getY() + otherHeight > existingPosition.getY();

            if (overlaps) {
                return true;
            }
        }
        return false;
    }

    private Point2D getSubBoardPositionOnMouse(MouseEvent event) {
        Point2D mousePosition = new Point2D(event.getSceneX(), event.getSceneY());
        Point2D localMousePosition = CC_boardPane.sceneToLocal(mousePosition);
        int x = (int)(localMousePosition.getX() / CC_boardPane.getWidth() * NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY);
        int y = (int)(localMousePosition.getY() / CC_boardPane.getHeight() * NO_OF_SUBBOARD_POSITIONS_VERTICALLY);
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
        private Point2D previousSubBoardPosition = new Point2D(-1, -1);
        private final List<Line> highlightedSubBoardPositionLines = new ArrayList<>();
        private boolean rotationChanged = false;
        private MouseEvent previousMoveEvent = null;

        @Override
        public void handle(MouseEvent event) {
            // SubBoards
            if (selectedItem == CC_Items.SubBoard || selectedItem == CC_Items.StartSubBoard) {
                handleSubBoardMouseEvent(event);
            } else {
                // SpaceViews
                handleSpaceViewMouseEvent(event);
            }

            rotationChanged = false;

            if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
                previousMoveEvent = event;
            }
            event.consume();
        }

        public void rotationChanged() {
            rotationChanged = true;
            if (previousMoveEvent != null) {
                handle(previousMoveEvent);
            }
        }

        private void handleSubBoardMouseEvent(MouseEvent event) {
            Point2D hoveredSubBoardPosition = getSubBoardPositionOnMouse(event);

            if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
                if (hoveredSubBoardPosition.distance(previousSubBoardPosition) >= 1 || rotationChanged) {
                    removeSubBoardHighlight();
                    highlightSubBoard(hoveredSubBoardPosition);
                    previousSubBoardPosition = hoveredSubBoardPosition;
                }
            }
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if (event.isShiftDown()) {
                    removeSubBoard(hoveredSubBoardPosition);
                } else {
                    if (selectedItem == CC_Items.SubBoard) {
                        newSubBoard(hoveredSubBoardPosition);
                    } else if (selectedItem == CC_Items.StartSubBoard) {
                        newStartSubBoard(hoveredSubBoardPosition);
                    }
                }
            }
        }

        private void handleSpaceViewMouseEvent(MouseEvent event) {
            if (previousSpaceView != null) {
                previousSpaceView.CC_removeGhost();
                previousSpaceView = null;
            }
            List<CC_SpaceView> CC_SpaceViewsOnMouse = getSpacesAtMouse(event);
            // SpaceViews
            if (CC_SpaceViewsOnMouse.isEmpty()) {
                return;
            }
            CC_SpaceView hoveredSpaceView = CC_SpaceViewsOnMouse.getFirst();
            if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
                if (previousSpaceView == null || rotationChanged) {
                    hoveredSpaceView.CC_setGhost(selectedItem.image, selectedItem.canBeRotated ? currentRotation : Heading.NORTH);
                    previousSpaceView = hoveredSpaceView;
                }
            }
            if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
                if (event.isShiftDown()) {
                    // Deletion of item at space
                    if (selectedItem == CC_Items.Wall) {
                        hoveredSpaceView.CC_setWall(null, currentRotation);
                    } else {
                        hoveredSpaceView.CC_setBoardElement(null, null, -1);
                    }
                } else {
                    // Placement of item at space
                    if (selectedItem == CC_Items.Wall) {
                        hoveredSpaceView.CC_setWall(selectedItem.image, selectedItem.canBeRotated ? currentRotation : Heading.NORTH);
                    } else {
                        hoveredSpaceView.CC_setBoardElement(selectedItem.image, selectedItem.canBeRotated ? currentRotation : Heading.NORTH, selectedItem.ordinal());
                    }
                }
                // Remove ghost on this space when mouse clicked
                if (hoveredSpaceView != previousSpaceView) {
                    hoveredSpaceView.CC_removeGhost();
                }
            }
        }

        private void highlightSubBoard(Point2D position) {
            Point2D positionInScene = new Point2D(
                    (position.getX() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY) * CC_boardPane.getWidth(),
                    (position.getY() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY) * CC_boardPane.getHeight()
            );

            double spaceViewWidth = CC_boardPane.getWidth() / NO_OF_SUBBOARD_POSITIONS_HORIZONTALLY / 5;
            double spaceViewHeight = CC_boardPane.getHeight() / NO_OF_SUBBOARD_POSITIONS_VERTICALLY / 5;

            double subBoardWidth = spaceViewWidth * (selectedItem == CC_Items.SubBoard ? 10 : 5);
            double subBoardHeight = spaceViewHeight * (selectedItem == CC_Items.SubBoard ? 10 : 5);
            double leftBorder = positionInScene.getX();
            double topBorder = positionInScene.getY();

            if (selectedItem == CC_Items.StartSubBoard) {
                leftBorder += spaceViewWidth * (currentRotation == Heading.EAST ? 2 : 0);
                topBorder += spaceViewWidth * (currentRotation == Heading.SOUTH ? 2 : 0);
                int xLength = (currentRotation == Heading.NORTH || currentRotation == Heading.SOUTH) ? 10 : 3;
                int yLength = (currentRotation == Heading.EAST || currentRotation == Heading.WEST) ? 10 : 3;
                subBoardWidth = spaceViewWidth * xLength;
                subBoardHeight = spaceViewHeight * yLength;
            }

            double rightBorder = leftBorder + subBoardWidth;
            double bottomBorder = topBorder + subBoardHeight;

            Line upperLine = new Line(leftBorder, topBorder, rightBorder, topBorder);
            upperLine.setStroke(Color.BLACK);
            upperLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(upperLine);
            highlightedSubBoardPositionLines.add(upperLine);

            Line lowerLine = new Line(leftBorder, bottomBorder, rightBorder, bottomBorder);
            lowerLine.setStroke(Color.BLACK);
            lowerLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(lowerLine);
            highlightedSubBoardPositionLines.add(lowerLine);

            Line leftLine = new Line(leftBorder, topBorder, leftBorder, bottomBorder);
            leftLine.setStroke(Color.BLACK);
            leftLine.setStrokeWidth(4);
            CC_boardPane.getChildren().add(leftLine);
            highlightedSubBoardPositionLines.add(leftLine);

            Line rightLine = new Line(rightBorder, topBorder, rightBorder, bottomBorder);
            rightLine.setStroke(Color.BLACK);
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
        String relativePath = "\\RoboRally\\courses";
        String directoryPath = userHome + relativePath;

        File folderFile = new File(directoryPath);
        // Create saves folder if it doesn't exist
        if (!folderFile.exists()) {
            if (folderFile.mkdirs()) {
                System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
            }
        }

        fileChooser.setInitialDirectory(new File(directoryPath));
        File loadedFile = fileChooser.showOpenDialog(primaryScene.getWindow());
        if (loadedFile != null) {
            // Load course
            CC_CourseData courseData = CC_JsonUtil.loadCourseDataFromFile(loadedFile);
            List<CC_SubBoard> loadedBoard = courseData.getSubBoards();
            initializeLoadedBoard(loadedBoard);
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
    }

    private void saveCourse() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Course");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
        String userHome = System.getProperty("user.home");
        String relativePath = "\\RoboRally\\courses";
        String directoryPath = userHome + relativePath;

        File folderFile = new File(directoryPath);
        // Create saves folder if it doesn't exist
        if (!folderFile.exists()) {
            if (folderFile.mkdirs()) {
                System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
            } else {
                System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
            }
        }

        fileChooser.setInitialDirectory(new File(directoryPath));
        fileChooser.setInitialFileName("New course.json");
        File saveFile = fileChooser.showSaveDialog(primaryScene.getWindow());

        if (saveFile != null) {
            try {
                Dialog saveCourseImageDialog = new Dialog();
                saveCourseImageDialog.setHeaderText("Do you also want to save the course image as its own file?");
                saveCourseImageDialog.setTitle("Save course image as PNG");
                ButtonType yesButton = new ButtonType("Save as PNG");
                ButtonType noButton = new ButtonType("Don't Save");
                saveCourseImageDialog.getDialogPane().getButtonTypes().addAll(yesButton, noButton);
                Optional<ButtonType> saveGameResult = saveCourseImageDialog.showAndWait();
                boolean saveImageAsPNG = saveGameResult.get() == yesButton;

                setLinesVisible(false);
                CC_CourseData courseData = new CC_CourseData(saveFile.getName().replace(".json", ""), subBoards, getSnapshotAsBase64(CC_boardPane, 10));
                setLinesVisible(true);
                System.out.println("Saving course data to: " + saveFile.getAbsolutePath());
                CC_JsonUtil.saveCourseDataToFile(courseData, saveFile, saveImageAsPNG);
            } catch (EmptyCourseException e) {
                System.err.println(e.getMessage());
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setHeaderText("Error in saving course");
                errorAlert.setContentText("Courses can't be empty. The course was not saved.");
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
