package gruppe15.roborally.controller;

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
    public StackPane CC_boardPane;
    @FXML
    public HBox elementButtonsHBox;
    public GridPane CC_Grid;

    private Board board;
    private SpaceView[][] spaces;
    private SpaceEventHandler spaceEventHandler;
    private final Image backgroundImage = ImageUtils.getImageFromName("empty.png");
    private final Image backgroundImageStart = ImageUtils.getImageFromName("emptyStart.png");
    private BoardElements currentBoardElement;
    private final ImageView selectedBoardElementImageView = new ImageView();


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
        CC_boardPane.setOnMouseMoved(spaceEventHandler);
        CC_boardPane.setOnKeyPressed(event -> spaceEventHandler.keyPressed(event));

        for (BoardElements boardElement : BoardElements.values()) {
            Image boardElementImage = ImageUtils.getImageFromName(boardElement.imageName);
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
                return;
            }
            SpaceView spaceView = spaceViewsOnMouse.getFirst();
            //SpaceView spaceView = (SpaceView) source;
            Space space = spaceView.space;
            Board board = space.board;

            if (space.getBoardElement() instanceof BE_SpawnPoint) {
                Player currentPlayer = board.getCurrentPlayer();
                if (board.getPhase() == INITIALIZATION) {
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

           /* if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {

            }*/


            event.consume();
        }

        public void keyPressed(KeyEvent event) {
            System.out.println("Pressed: " + event.getCode());
        }


    }

    enum BoardElements {
        Antenna("antenna.png"),
        Laser("laserStart.png"),
        ConveyorBelt("greenStraight.png"),
        EnergySpace("energySpace.png"),
        Gear("gearRight.png"),
        Hole("hole.png"),
        PushPanel("push135.png"),
        Reboot("reboot.png"),
        SpawnPoint("startField.png");
        String imageName;
        BoardElements(String imageName) {
            this.imageName = imageName;
        }
    }

    /*private class OnDragDetectedHandler implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView source = (CardFieldView) t;
                CommandCardField cardField = source.field;
                if (cardField != null &&
                        cardField.getCard() != null &&
                        cardField.player != null &&
                        cardField.player.board != null &&
                        cardField.player.board.getPhase().equals(Phase.PROGRAMMING)) {
                    Dragboard db = source.startDragAndDrop(TransferMode.MOVE);
                    Image image = source.snapshot(null, null);
                    db.setDragView(image);

                    ClipboardContent content = new ClipboardContent();
                    content.put(ROBO_RALLY_CARD, cardFieldRepresentation(cardField));

                    db.setContent(content);
                    source.setBackground(BG_DRAG);
                }
            }
            event.consume();
        }

    }

    private class OnDragOverHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        (cardField.getCard() == null || event.getGestureSource() == target) &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            }
            event.consume();
        }

    }

    private class OnDragEnteredHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        target.setBackground(BG_DROP);
                    }
                }
            }
            event.consume();
        }

    }

    private class OnDragExitedHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            event.getDragboard().hasContent(ROBO_RALLY_CARD)) {
                        target.setBackground(BG_DEFAULT);
                    }
                }
            }
            event.consume();
        }

    }

    private class OnDragDroppedHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView target = (CardFieldView) t;
                CommandCardField cardField = target.field;

                Dragboard db = event.getDragboard();
                boolean success = false;
                if (cardField != null &&
                        cardField.getCard() == null &&
                        cardField.player != null &&
                        cardField.player.board != null) {
                    if (event.getGestureSource() != target &&
                            db.hasContent(ROBO_RALLY_CARD)) {
                        Object object = db.getContent(ROBO_RALLY_CARD);
                        if (object instanceof String) {
                            CommandCardField source = cardFieldFromRepresentation((String) object);
                            if (source != null && gameController.moveCards(source, cardField)) {
                                // CommandCard card = source.getCard();
                                // if (card != null) {
                                // if (gameController.moveCards(source, cardField)) {
                                // cardField.setCard(card);
                                success = true;
                                // }
                            }
                        }
                    }
                }
                event.setDropCompleted(success);
                target.setBackground(BG_DEFAULT);
            }
            event.consume();
        }

    }

    private class OnDragDoneHandler implements EventHandler<DragEvent> {

        @Override
        public void handle(DragEvent event) {
            Object t = event.getTarget();
            if (t instanceof CardFieldView) {
                CardFieldView source = (CardFieldView) t;
                source.setBackground(BG_DEFAULT);
            }
            event.consume();
        }

    }*/
}
