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
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.Phase.INITIALIZATION;

public class CC_Controller extends VBox {
    @FXML
    ScrollPane CC_boardScrollPane;
    @FXML
    StackPane CC_boardPane; // Course creator board pane
    @FXML
    HBox CC_elementButtonsHBox;
    GridPane CC_Grid; // Course creator grid

    private final CC_SpaceView[][] CC_SpaceViews;
    private final SpaceEventHandler spaceEventHandler;

    enum CC_Items {
        Background("empty.png"),
        BackgroundStart("emptyStart.png"),

        Wall("wall.png"),

        SpawnPoint("startField.png"),
        Reboot("reboot.png"),
        Hole("hole.png"),
        Antenna("antenna.png"),

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
        CC_boardPane.getChildren().add(CC_Grid);

        CC_boardScrollPane.setVvalue(0.5);
        CC_boardScrollPane.setHvalue(0.5);

        CC_Grid.setAlignment(Pos.CENTER);
        //CC_boardPane.setAlignment(Pos.CENTER);

        for (int x = 0; x < boardWidth; x++) {
            for (int y = 0; y < boardHeight; y++) {
                CC_SpaceView CC_SpaceView = new CC_SpaceView();
                CC_SpaceView.setBackground(CC_Items.Background.image, 0);
                CC_Grid.add(CC_SpaceView, x, y);
                CC_SpaceViews[x][y] = CC_SpaceView;

                CC_SpaceView.setOnMouseEntered(spaceEventHandler);
                CC_SpaceView.setOnMouseExited(spaceEventHandler);
            }
        }
        CC_boardPane.setOnMouseClicked(spaceEventHandler);
        CC_boardPane.setOnKeyPressed(spaceEventHandler::keyPressed);

        for (int i = 0; i < CC_Items.values().length; i++) {
            CC_Items item = CC_Items.values()[i];
            ImageView itemButtonImageView = new ImageView(item.image);

            itemButtonImageView.setFitWidth(50);
            itemButtonImageView.setFitHeight(50);
            itemButtonImageView.setImage(item.image);

            Button itemButton = new Button();
            itemButton.setPrefSize(50, 50);
            HBox.setMargin(itemButton, new Insets(25, 12.5, 25, 12.5));
            itemButton.setGraphic(itemButtonImageView);

            CC_elementButtonsHBox.getChildren().add(itemButton);
            itemButton.setOnMouseClicked(event -> {
                selectedItem = item;
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
            CC_SpaceView hoveredSpaceView = CC_SpaceViewsOnMouse.getFirst();

            if (event.getEventType() == MouseEvent.MOUSE_ENTERED) {
                hoveredSpaceView.setGhost(selectedItem.image);
            }

            if (event.getEventType() == MouseEvent.MOUSE_EXITED) {
                hoveredSpaceView.removeGhost();
            }

            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                if (selectedItem.ordinal() < 2) {

                } else if (selectedItem.ordinal() < 3) {

                } else {

                }

                if (event.isShiftDown()) {
                    spaceClicked.setPlayer(board.getPlayer(1));
                }

                hoveredSpaceView.removeGhost();
            }
            event.consume();
        }

        private void keyPressed(KeyEvent event) {
            System.out.println("Pressed: " + event.getCode());
        }
    }
}
