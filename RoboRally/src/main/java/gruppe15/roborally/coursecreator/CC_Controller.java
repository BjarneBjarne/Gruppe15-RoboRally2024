package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.*;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
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

public class CC_Controller extends VBox {
    @FXML
    public StackPane CC_boardPane; // Course creator board pane
    @FXML
    public HBox elementButtonsHBox;
    public GridPane CC_Grid; // Course creator grid

    private final CC_SpaceView[][] CC_SpaceViews;
    private final SpaceEventHandler spaceEventHandler;

    enum CC_Items {
        Background("empty.png"),
        BackgroundStart("emptyStart.png"),

        SpawnPoint("startField.png"),
        Reboot("reboot.png"),
        Hole("hole.png"),
        Antenna("antenna.png"),
        Wall("wall.png"),

        BlueConveyorBelt("blueStraight.png"),
        GreenConveyorBelt("greenStraight.png"),
        PushPanel135("push135.png"),
        PushPanel24("push24.png"),
        GearRight("gearRight.png"),
        GearLeft("gearLeft.png"),
        Laser("laserStart.png"),
        EnergySpace("energySpace.png"),
        Checkpoint1("1.png"),
        Checkpoint2("2.png"),
        Checkpoint3("3.png"),
        Checkpoint4("4.png"),
        Checkpoint5("5.png"),
        Checkpoint6("6.png");

        private final Image image;
        CC_Items(String imageName) {
            this.image = ImageUtils.getImageFromName("Board Pieces/" + imageName);
        }
    }

    private CC_Items selectedItem = CC_Items.Background;

    private final int boardWidth = 13;
    private final int boardHeight = 10;

    public CC_Controller() {
        CC_SpaceViews = new CC_SpaceView[boardWidth][boardHeight];
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

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                CC_SpaceView CC_SpaceView = new CC_SpaceView();
                CC_SpaceViews[x][y] = CC_SpaceView;
                CC_SpaceViews[x][y].setBackground(backgroundImage, 0);
                CC_Grid.add(CC_SpaceView, x, y);
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

    private List<CC_SpaceView> getSpacesAtMouse(MouseEvent event) {
        List<CC_SpaceView> spacesAtMouse = new ArrayList<>();
        for (CC_SpaceView[] CC_SpaceViewColumns : CC_SpaceViews) {
            for (CC_SpaceView space : CC_SpaceViewColumns) {
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
            List<CC_SpaceView> CC_SpaceViewsOnMouse = getSpacesAtMouse(event);
            if (CC_SpaceViewsOnMouse.isEmpty()) {
                return;
            }
            CC_SpaceView CC_SpaceView = CC_SpaceViewsOnMouse.getFirst();
            //CC_SpaceView CC_SpaceView = (CC_SpaceView) source;
            Space spaceClicked = CC_SpaceView.space;

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
}
