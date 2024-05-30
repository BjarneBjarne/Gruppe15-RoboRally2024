package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import static gruppe15.roborally.model.utils.ImageUtils.buildConveyorBeltStringFromNeighbors;

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
    private Heading direction;
    private final Heading[] placedWalls = new Heading[4];

    private final ImageView backgroundImageView = new ImageView();
    private final ImageView boardElementImageView = new ImageView();
    private final ImageView[] wallImageViews = new ImageView[4];

    private final ImageView ghostImageView = new ImageView();

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
        this.getChildren().addAll(backgroundImageView, boardElementImageView);
        this.getChildren().addAll(wallImageViews);

        ghostImageView.setFitWidth(size);
        ghostImageView.setFitHeight(size);
        this.getChildren().add(ghostImageView);
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
        this.placedBoardElement = placedBoardElement;
        this.direction = direction;
        if ((this.placedBoardElement == 7 || this.placedBoardElement == 8) && direction != null) {
            updateConveyorBeltImages(spaces);
        } else {
            CC_setImageView(image, this.direction, this.boardElementImageView);
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

    public void CC_setGhost(Image image, Heading ghostDirection) {
        CC_setImageView(image, ghostDirection, this.ghostImageView);
    }

    private void CC_setImageView(Image image, Heading direction, ImageView imageView) {
        imageView.setImage(ImageUtils.getRotatedImageByHeading(image, direction));
    }

    public void updateConveyorBeltImages(CC_SpaceView[][] spaces) {
        // Self
        updateConveyorBeltImage(spaces);
        // Neighbors
        updateNeighborsConveyorBeltImages(spaces);
    }

    private void updateNeighborsConveyorBeltImages(CC_SpaceView[][] spaces) {
        updateNeighborConveyorBeltImage(spaces[this.boardX + 1][this.boardY], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX - 1][this.boardY], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX][this.boardY + 1], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX][this.boardY - 1], spaces);
    }

    private void updateNeighborConveyorBeltImage(CC_SpaceView neighbor, CC_SpaceView[][] spaces) {
        if (neighbor != null && neighbor.placedBoardElement == this.placedBoardElement) {
            neighbor.updateConveyorBeltImage(spaces);
        }
    }

    protected void updateConveyorBeltImage(CC_SpaceView[][] spaces) {
        if (this.direction == null) {
            this.boardElementImageView.setImage(null);
            return;
        }
        Image updatedConveyorImage = ImageUtils.getImageFromName("Board Pieces/" + this.getUpdatedConveyorBeltImage(spaces));
        this.boardElementImageView.setImage(ImageUtils.getRotatedImageByHeading(updatedConveyorImage, this.direction));
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
    public Heading[] getPlacedWalls() {
        return placedWalls;
    }
    public Heading getDirection() {
        return direction;
    }


    // Reused code from ImageUtils. Rewritten to be compatible with course creator, without creating BE_ConveyorBelt objects.
    private String getUpdatedConveyorBeltImage(CC_SpaceView[][] spaces) {
        CC_SpaceView spaceInFrontOfThis = getSpaceNextTo(this.direction, spaces);
        CC_SpaceView spaceBehindThis = getSpaceNextTo(this.direction.opposite(), spaces);
        CC_SpaceView spaceToTheRightOfThis = getSpaceNextTo(this.direction.next(), spaces);
        CC_SpaceView spaceToTheLeftOfThis = getSpaceNextTo(this.direction.prev(), spaces);
        boolean thisHasFrontAndBack = false;
        if (spaceInFrontOfThis != null && spaceBehindThis != null) {
            thisHasFrontAndBack = (spaceInFrontOfThis.placedBoardElement == this.placedBoardElement) && (spaceBehindThis.placedBoardElement == this.placedBoardElement);
        }

        StringBuilder imageNameBuilder = new StringBuilder();
        // Green or blue
        imageNameBuilder.append(this.placedBoardElement == 8 ? "green" : "blue");

        // Neighbors and connections
        int noOfConnections = 0;
        boolean[] relativeConnections = new boolean[4];
        for (int i = 0; i < 4; i++) {
            Heading relativeDirection = Heading.values()[(this.direction.ordinal() + i) % 4];
            CC_SpaceView neighborSpace = getSpaceNextTo(relativeDirection, spaces);
            // i = 0, the direction this conveyor belt is facing, always counts as a "connection".
            if (i == 0) {
                relativeConnections[i] = true;
                noOfConnections++;
                continue;
            }
            if (neighborSpace != null) {
                if (this.placedBoardElement != neighborSpace.placedBoardElement) continue; // Only count same type

                Heading neighborDirection = neighborSpace.direction;
                CC_SpaceView spaceInFrontOfNeighbor = neighborSpace.getSpaceNextTo(neighborDirection, spaces);
                CC_SpaceView spaceBehindNeighbor = neighborSpace.getSpaceNextTo(neighborDirection.opposite(), spaces);

                boolean eitherHasFrontOrBack = this.equals(spaceInFrontOfNeighbor) || this.equals(spaceBehindNeighbor) || neighborSpace.equals(spaceInFrontOfThis) || neighborSpace.equals(spaceBehindThis);
                boolean neighborHasFrontAndBack = false;
                if (spaceInFrontOfNeighbor != null && spaceBehindNeighbor != null) {
                    neighborHasFrontAndBack = (spaceInFrontOfNeighbor.placedBoardElement == neighborSpace.placedBoardElement) && (spaceBehindNeighbor.placedBoardElement == neighborSpace.placedBoardElement);
                }

                if (eitherHasFrontOrBack || (!thisHasFrontAndBack && !neighborHasFrontAndBack && (((neighborSpace.equals(spaceToTheRightOfThis) && spaceToTheRightOfThis.direction != this.direction) || (neighborSpace.equals(spaceToTheLeftOfThis) && spaceToTheLeftOfThis.direction != this.direction))))) {
                    relativeConnections[i] = true;
                    noOfConnections++;
                }
            }
        }

        // Building string
        buildConveyorBeltStringFromNeighbors(imageNameBuilder, noOfConnections, relativeConnections);

        return imageNameBuilder.toString();
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
