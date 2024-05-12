package gruppe15.roborally.controller;

import gruppe15.observer.Subject;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.*;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.view.SpaceView;
import gruppe15.roborally.view.ViewObserver;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.Heading.*;
import static gruppe15.roborally.model.Phase.INITIALISATION;

public class CourseCreatorController extends VBox {
    @FXML
    public StackPane CC_boardPane;
    @FXML
    public HBox elementButtonsHBox;
    public GridPane CC_Grid;

    private Board board;
    private SpaceView[][] spaces;
    private SpaceEventHandler spaceEventHandler;
    private final Image backgroundImage = ImageUtils.getImageFromName("empty.png");
    private final Image backgroundImageStart = ImageUtils.getImageFromName("emptyStart.png");

    public CourseCreatorController() {
        board = new Board(13, 10, 1337);
        spaces = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler();
    }

    @FXML
    public void initialize() {
        CC_Grid = new GridPane();
        CC_boardPane.getChildren().add(CC_Grid);

        this.setAlignment(Pos.CENTER);
        CC_Grid.setAlignment(Pos.CENTER);
        CC_boardPane.setAlignment(Pos.CENTER);

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = new Space(board, x, y, null);
                space.setBackgroundImage(backgroundImage);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                CC_Grid.add(spaceView, x, y);
            }
        }
        CC_boardPane.setOnMouseClicked(spaceEventHandler);
        CC_boardPane.setOnKeyPressed(event -> spaceEventHandler.keyPressed(event));

        // Board elements
        List<BoardElement> boardElementList = new ArrayList<>();
        boardElementList.add(new BE_Antenna());
        boardElementList.add(new BE_BoardLaser(NORTH));
        boardElementList.add(new BE_ConveyorBelt(NORTH, 1));
        boardElementList.add(new BE_ConveyorBelt(NORTH, 2));
        boardElementList.add(new BE_EnergySpace());
        boardElementList.add(new BE_Gear("Right"));
        boardElementList.add(new BE_Hole());
        boardElementList.add(new BE_PushPanel("135", NORTH));
        boardElementList.add(new BE_PushPanel("24", NORTH));
        boardElementList.add(new BE_Reboot(NORTH));
        boardElementList.add(new BE_SpawnPoint(NORTH));
        for (int i = 1; i <= 6; i++) {
            boardElementList.add(new BE_Checkpoint(i));
        }

        for (BoardElement boardElement : boardElementList) {
            ImageView boardElementImageView = new ImageView(boardElement.getImage());



            //elementButtonsHBox.getChildren().add();
        }

    }

    private List<SpaceView> getSpacesAtMouse(MouseEvent event) {
        List<SpaceView> spacesAtMouse = new ArrayList<>();
        for (int x = 0; x < spaces.length; x++) {
            for (int y = 0; y < spaces[x].length; y++) {
                SpaceView space = spaces[x][y];
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
                for (int x = 0; x < board.width; x++) {
                    for (int y = 0; y < board.height; y++) {
                        SpaceView spaceView = spaces[x][y];
                        System.out.println(spaceView.getWidth() + ", " + spaceView.getHeight());
                    }
                }
                return;
            }
            SpaceView spaceView = spaceViewsOnMouse.getFirst();
            //SpaceView spaceView = (SpaceView) source;
            Space space = spaceView.space;
            Board board = space.board;

            System.out.println(space.x + ", " + space.y);

            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                Player currentPlayer = board.getCurrentPlayer();
                if (board.getPhase() == INITIALISATION) {
                    if (space.getPlayer() == null) {
                        currentPlayer.setSpawn(space);
                        currentPlayer.setSpace(space);
                    }
                }
            } else {
                if (event.isShiftDown()) {
                    space.setPlayer(board.getPlayer(1));
                } else if (event.isControlDown()) {
                    space.setPlayer(board.getPlayer(0));
                }
            }
            event.consume();
        }

        public void keyPressed(KeyEvent event) {
            System.out.println("Pressed: " + event.getCode());
        }
    }
}
