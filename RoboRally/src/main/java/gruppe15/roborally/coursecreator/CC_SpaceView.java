package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.utils.ImageUtils.buildConveyorBeltStringFromConnections;

/**
 * Modified SpaceView class, that handles the view of each space view in the course creator.
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class CC_SpaceView extends StackPane {
    private final Image backgroundImage = ImageUtils.getImageFromName("Board Pieces/empty.png");
    private final Image backgroundStartImage = ImageUtils.getImageFromName("Board Pieces/emptyStart.png");
    private int boardX;
    private int boardY;

    private int placedBoardElement = -1;
    private int checkpoint = -1;
    private Heading direction;
    private final Heading[] placedWalls = new Heading[4];

    private final ImageView backgroundImageView = new ImageView();
    private final ImageView boardElementImageView = new ImageView();
    private final ImageView checkpointImageView = new ImageView();
    private final ImageView[] wallImageViews = new ImageView[4];
    private boolean isOnStartSubBoard;
    private final Text debugText = new Text();

    private final ImageView ghostImageView = new ImageView();
    private Image boardElementImage = null;
    private final Image[] wallImages = new Image[4];

    public CC_SpaceView(int boardX, int boardY) {
        this.boardX = boardX;
        this.boardY = boardY;
        for (int i = 0; i < wallImageViews.length; i++) {
            wallImageViews[i] = new ImageView();
            wallImageViews[i].setFitWidth(100);
            wallImageViews[i].setFitHeight(100);
        }
    }

    public CC_SpaceView() {
        for (int i = 0; i < wallImageViews.length; i++) {
            wallImageViews[i] = new ImageView();
            wallImageViews[i].setFitWidth(100);
            wallImageViews[i].setFitHeight(100);
        }
    }

    public void initialize(double size, boolean isOnStartSubBoard) {
        this.setPrefWidth(size);
        this.setMinWidth(size);
        this.setMaxWidth(size);
        this.setPrefHeight(size);
        this.setMinHeight(size);
        this.setMaxHeight(size);

        backgroundImageView.setFitWidth(size);
        backgroundImageView.setFitHeight(size);
        backgroundImageView.setImage(isOnStartSubBoard ? backgroundStartImage : backgroundImage);

        boardElementImageView.setFitWidth(size);
        boardElementImageView.setFitHeight(size);
        checkpointImageView.setFitWidth(size);
        checkpointImageView.setFitHeight(size);
        this.getChildren().addAll(backgroundImageView, boardElementImageView, checkpointImageView);
        this.getChildren().addAll(wallImageViews);

        ghostImageView.setFitWidth(size);
        ghostImageView.setFitHeight(size);
        this.getChildren().add(ghostImageView);
        this.getChildren().add(debugText);
        debugText.setStyle("-fx-font-size: 16px; -fx-fill: RED; ");
        debugText.setWrappingWidth(size);
    }

    public void setBoardXY(int boardX, int boardY) {
        this.boardX = boardX;
        this.boardY = boardY;
    }

    public void CC_setBoardElement(Image image, Heading direction, int placedBoardElement) {
        this.placedBoardElement = placedBoardElement;
        this.direction = direction;
        CC_setImageView(image, direction, boardElementImageView);
    }

    public void CC_setBoardElement(Image image, Heading direction, int placedBoardElement, CC_SpaceView[][] spaces) {
        if (placedBoardElement == 7 || placedBoardElement == 8 || this.placedBoardElement == 7 || this.placedBoardElement == 8) {
            this.placedBoardElement = placedBoardElement;
            this.direction = direction;
            updateConveyorBeltImages(spaces);
        } else {
            CC_setBoardElement(image, direction, placedBoardElement);
        }
    }

    public void CC_setWall(Image image, Heading direction) {
        if (image != null) {
            this.placedWalls[direction.ordinal()] = direction;
            CC_setImageView(image, direction, wallImageViews[direction.ordinal()]);
        } else {
            this.placedWalls[direction.ordinal()] = null;
            CC_setImageView(null, direction, wallImageViews[direction.ordinal()]);
        }
    }

    public void CC_setCheckpoint(Image image, int checkpoint) {
        if (checkpoint > 0) {
            this.checkpoint = checkpoint;
            CC_setImageView(image, Heading.NORTH, checkpointImageView);
        } else {
            this.checkpoint = -1;
            CC_setImageView(null, Heading.NORTH, checkpointImageView);
        }
    }

    public void CC_setGhost(Image image, Heading ghostDirection, boolean isWall, boolean isDeleting) {
        Image ghostImage = image;
        if (isDeleting) {
            ghostImage = null;
            if (!isWall) {
                if (this.boardElementImageView.getImage() != null) {
                    Image redBoardElementImage = ImageUtils.getImageColored(this.boardElementImageView.getImage(), new Color(1, 0, 0, 1), 1);
                    this.boardElementImageView.setImage(redBoardElementImage);
                }
            } else {
                if (ghostDirection != null) {
                    ImageView wallImageView = wallImageViews[ghostDirection.ordinal()];
                    if (wallImageView.getImage() != null) {
                        Image redBoardElementImage = ImageUtils.getImageColored(wallImageView.getImage(), new Color(1, 0, 0, 1), 1);
                        wallImageView.setImage(redBoardElementImage);
                    }
                }
            }
        } else if (ghostImage != null) {
            ghostImage = ImageUtils.getImageColored(ghostImage, Color.TRANSPARENT, .35);
        } else {
            if (this.boardElementImage != null) {
                this.boardElementImageView.setImage(this.boardElementImage);
            }
            for (int i = 0; i < wallImages.length; i++) {
                if (wallImages[i] != null) {
                    this.wallImageViews[i].setImage(wallImages[i]);
                }
            }
        }



        CC_setImageView(ghostImage, ghostDirection, this.ghostImageView);
    }

    private void CC_setImageView(Image image, Heading direction, ImageView imageView) {
        Image newImage = ImageUtils.getRotatedImageByHeading(image, direction);
        if (imageView == this.boardElementImageView) {
            this.boardElementImage = newImage;
        }
        for (int i = 0; i < wallImageViews.length; i++) {
            ImageView wallImageView = wallImageViews[i];
            if (imageView == wallImageView) {
                this.wallImages[i] = newImage;
            }
        }

        imageView.setImage(newImage);
    }

    public void updateConveyorBeltImages(CC_SpaceView[][] spaces) {
        // Self
        updateConveyorBeltImage(spaces);
        // Neighbors
        updateNeighborsConveyorBeltImages(spaces);
    }

    private void updateNeighborsConveyorBeltImages(CC_SpaceView[][] spaces) {
        // To the sides
        updateNeighborConveyorBeltImage(this.boardX + 1, this.boardY, spaces);
        updateNeighborConveyorBeltImage(this.boardX - 1, this.boardY, spaces);
        updateNeighborConveyorBeltImage(this.boardX, this.boardY + 1, spaces);
        updateNeighborConveyorBeltImage(this.boardX, this.boardY - 1, spaces);

        // Two spaces to the sides
        updateNeighborConveyorBeltImage(this.boardX + 2, this.boardY, spaces);
        updateNeighborConveyorBeltImage(this.boardX - 2, this.boardY, spaces);
        updateNeighborConveyorBeltImage(this.boardX, this.boardY + 2,  spaces);
        updateNeighborConveyorBeltImage(this.boardX, this.boardY - 2,  spaces);

        // Corners
        updateNeighborConveyorBeltImage(this.boardX - 1, this.boardY - 1,  spaces);
        updateNeighborConveyorBeltImage(this.boardX + 1, this.boardY - 1,  spaces);
        updateNeighborConveyorBeltImage(this.boardX - 1, this.boardY + 1,  spaces);
        updateNeighborConveyorBeltImage(this.boardX + 1, this.boardY + 1,  spaces);
    }

    private void updateNeighborConveyorBeltImage(int x, int y, CC_SpaceView[][] spaces) {
        if (x < 0 || x >= spaces.length || y < 0 || y >= spaces[0].length) return;

        CC_SpaceView neighbor = spaces[x][y];
        if (neighbor != null && (neighbor.placedBoardElement == 7 || neighbor.placedBoardElement == 8)) {
            neighbor.updateConveyorBeltImage(spaces);
        }
    }

    protected void updateConveyorBeltImage(CC_SpaceView[][] spaces) {
        if (this.direction == null) {
            CC_setImageView(null, null, this.boardElementImageView);
            this.debugText.setText("");
            return;
        }
        Image updatedConveyorImage = ImageUtils.getImageFromName("Board Pieces/" + this.getUpdatedConveyorBeltImage(spaces));
        CC_setImageView(updatedConveyorImage, this.direction, this.boardElementImageView);
    }


    public int getBoardX() {
        return boardX;
    }
    public int getBoardY() {
        return boardY;
    }
    public int getPlacedBoardElement() {
        return placedBoardElement;
    }
    public int getCheckpoint() {
        return checkpoint;
    }
    public Heading[] getPlacedWalls() {
        return placedWalls;
    }
    public Heading getDirection() {
        return direction;
    }

    List<CC_SpaceView> connections = new ArrayList<>();
    // Reused code from ImageUtils. Rewritten to be compatible with course creator, without using BE_ConveyorBelt objects.
    private String getUpdatedConveyorBeltImage(CC_SpaceView[][] spaces) {
        StringBuilder imageNameBuilder = new StringBuilder();
        // Green or blue
        imageNameBuilder.append(this.placedBoardElement == 8 ? "green" : "blue");

        // Neighbors and connections
        //this.debugText.setText("Conn: " + this.connections.size() + "\n");
        this.connections.clear();
        boolean[] relativeConnections = new boolean[4];

        // Checking if neighbors are a valid connection
        // relativeConnections[0], the direction this conveyor belt is facing, always counts as a connection.
        relativeConnections[0] = true;
        CC_SpaceView frontNeighbor = getSpaceNextTo(this.direction, spaces);
        this.connections.add(frontNeighbor);
        checkForConnectionAtDirection(2, spaces, relativeConnections); // Behind this conveyor belt
        checkForConnectionAtDirection(1, spaces, relativeConnections); // To the right of this conveyor belt
        checkForConnectionAtDirection(3, spaces, relativeConnections); // To the left of this conveyor belt

        // Building image string
        buildConveyorBeltStringFromConnections(imageNameBuilder, this.connections.size(), relativeConnections);

        return imageNameBuilder.toString();
    }

    private void checkForConnectionAtDirection(int directionIndex, CC_SpaceView[][] spaces, boolean[] relativeConnections) {
        Heading relativeDirection = Heading.values()[(this.direction.ordinal() + directionIndex) % 4];
        CC_SpaceView neighborSpace = getSpaceNextTo(relativeDirection, spaces);

        if (neighborSpace != null) {
            if (this.placedBoardElement != neighborSpace.placedBoardElement) return; // Only count same type

            boolean isValidConnection = false;

            // Space references
            Heading neighborDirection = neighborSpace.direction;
            CC_SpaceView spaceInFrontOfNeighbor = neighborSpace.getSpaceNextTo(neighborDirection, spaces);

            // Number of other connections
            int noOfThisOtherConnections = 0;
            int noOfNeighborsOtherConnections = 0;
            for (CC_SpaceView connection : this.connections) {
                if (connection == null) noOfThisOtherConnections++;
                if (connection != neighborSpace) noOfThisOtherConnections++;
            }
            for (CC_SpaceView connection : neighborSpace.connections) {
                if (connection == null) noOfNeighborsOtherConnections++;
                if (connection != this) noOfNeighborsOtherConnections++;
            }
            //this.debugText.setText(this.debugText.getText() + i + ": " + noOfThisOtherConnections + "\n");

            // Condition checking
            if (directionIndex == 2) {
                // Neighbor is behind
                if (this.equals(spaceInFrontOfNeighbor)) {
                    isValidConnection = true;
                }
            } else {
                // Neighbor is to the right or left
                List<Boolean> conditions = new ArrayList<>();
                conditions.add(this.equals(spaceInFrontOfNeighbor));
                conditions.add(noOfThisOtherConnections == 1 && noOfNeighborsOtherConnections == 1 && this.direction != neighborSpace.direction);
                for (Boolean condition : conditions) {
                    if (condition) {
                        isValidConnection = true;
                        break;
                    }
                }
            }

            if (isValidConnection) {
                relativeConnections[directionIndex] = true;
                this.connections.add(neighborSpace);
            }
        }
    }

    private CC_SpaceView getSpaceNextTo(Heading dir, CC_SpaceView[][] spaces) {
        return switch (dir) {
            case SOUTH -> {
                if (boardY + 1 >= spaces[boardX].length) {
                    yield null;
                }
                yield spaces[boardX][boardY + 1]; // out of bounds
            }
            case WEST -> {
                if (boardX - 1 < 0) {
                    yield null;
                }
                yield spaces[boardX - 1][boardY]; // out of bounds
            }
            case NORTH -> {
                if (boardY - 1 < 0) {
                    yield null;
                }
                yield spaces[boardX][boardY - 1]; // out of bounds
            }
            case EAST -> {
                if (boardX + 1 >= spaces.length) {
                    yield null;
                }
                yield spaces[boardX + 1][boardY]; // out of bounds
            }
        };
    }
}
