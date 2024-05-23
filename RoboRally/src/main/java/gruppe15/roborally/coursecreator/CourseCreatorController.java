package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.view.SpaceView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.Phase.INITIALIZATION;

public class CourseCreatorController extends VBox {
    @FXML
    public StackPane CC_boardPane; // Course creator board pane
    @FXML
    public HBox elementButtonsHBox;
    public GridPane CC_Grid; // Course creator grid

    private final Board board;
    private final Space[][] spaces;
    private final SpaceView[][] spaceViews;
    private final SpaceEventHandler spaceEventHandler;
    private final Image backgroundImage = ImageUtils.getImageFromName("Board Pieces/empty.png");
    private final Image backgroundImageStart = ImageUtils.getImageFromName("Board Pieces/emptyStart.png");
    private BoardElements currentBoardElement;
    private final ImageView selectedBoardElementImageView = new ImageView();

    public CourseCreatorController() {
        board = new Board(13, 10, 1337);
        spaces = board.getSpaces();
        spaceViews = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler();
    }

    @FXML
    public void initialize() {
        CC_Grid = new GridPane();
        CC_boardPane.getChildren().addAll(CC_Grid, selectedBoardElementImageView);

        selectedBoardElementImageView.setMouseTransparent(true);

        this.setAlignment(Pos.CENTER);
        CC_Grid.setAlignment(Pos.CENTER);
        CC_boardPane.setAlignment(Pos.CENTER);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = spaces[x][y];
                SpaceView spaceView = new SpaceView(space);
                spaceViews[x][y] = spaceView;

                space.setBackgroundImage(backgroundImage);
                CC_Grid.add(spaceView, x, y);
            }
        }
        CC_boardPane.setOnMouseClicked(spaceEventHandler);
        CC_boardPane.setOnMouseMoved(spaceEventHandler);
        CC_boardPane.setOnKeyPressed(spaceEventHandler::keyPressed);

        for (int i = 0; i < BoardElements.values().length; i++) {
            BoardElements boardElement = BoardElements.values()[i];
            Image boardElementImage = ImageUtils.getImageFromName("Board Pieces/" + boardElement.imageName);
            ImageView boardElementImageView = new ImageView(boardElementImage);
            boardElementImageView.setFitWidth(50);
            boardElementImageView.setFitHeight(50);
            Button elementButton = new Button();
            elementButton.setPrefSize(50, 50);
            HBox.setMargin(elementButton, new Insets(25, 12.5, 25, 12.5));
            elementButton.setGraphic(boardElementImageView);
            elementButtonsHBox.getChildren().add(elementButton);
            elementButton.setOnMouseClicked(event -> {
                currentBoardElement = boardElement;
                selectedBoardElementImageView.setImage(boardElementImage);
                selectedBoardElementImageView.startDragAndDrop(TransferMode.MOVE);
            });
        }
    }

    private List<SpaceView> getSpacesAtMouse(MouseEvent event) {
        List<SpaceView> spacesAtMouse = new ArrayList<>();
        for (SpaceView[] spaceViewColumns : spaceViews) {
            for (SpaceView space : spaceViewColumns) {
                Bounds localBounds = space.getBoundsInLocal();
                Bounds sceneBounds = space.localToScene(localBounds);
                if (sceneBounds.contains(new Point2D(event.getSceneX(), event.getSceneY()))) {
                    // If mouse is within bounds of a node
                    spacesAtMouse.add(space);
                }
            }
        }
        return spacesAtMouse;
    }






    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            // Object source = event.getSource();
            List<SpaceView> spaceViewsOnMouse = getSpacesAtMouse(event);
            if (spaceViewsOnMouse.isEmpty()) {
                return;
            }
            SpaceView spaceView = spaceViewsOnMouse.getFirst();
            //SpaceView spaceView = (SpaceView) source;
            Space spaceClicked = spaceView.space;

            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (spaceClicked.getBoardElement() instanceof BE_SpawnPoint) {
                    Player currentPlayer = board.getCurrentPlayer();
                    if (board.getPhase() == INITIALIZATION) {
                        if (spaceClicked.getPlayer() == null) {
                            currentPlayer.setSpawn(spaceClicked);
                            currentPlayer.setSpace(spaceClicked);
                        }
                    }
                } else {
                    if (event.isShiftDown()) {
                        spaceClicked.setPlayer(board.getPlayer(1));
                    } else if (event.isControlDown()) {
                        spaceClicked.setPlayer(board.getPlayer(0));
                    }
                }

                //event.setDropCompleted(success);
            }

            event.consume();
        }

        public void keyPressed(KeyEvent event) {
            System.out.println("Pressed: " + event.getCode());
        }
    }

    enum BoardElements {
        Antenna(BE_Antenna.class, "antenna.png"),
        Laser(BE_BoardLaser.class, "laserStart.png"),
        ConveyorBelt(BE_ConveyorBelt .class, "greenStraight.png"),
        EnergySpace(BE_EnergySpace.class, "energySpace.png"),
        Gear(BE_Gear.class, "gearRight.png"),
        Hole(BE_Hole.class, "hole.png"),
        PushPanel(BE_PushPanel.class, "push135.png"),
        Reboot(BE_Reboot.class, "reboot.png"),
        SpawnPoint(BE_SpawnPoint.class, "startField.png");

        private final Class<? extends BoardElement> boardElementClass;
        private final String imageName;
        BoardElements(Class<? extends BoardElement> boardElementClass, String imageName) {
            this.boardElementClass = boardElementClass;
            this.imageName = imageName;
        }
    }
}
