package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BE_ConveyorBelt;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class CC_SpaceView extends StackPane {
    private final Image backgroundImage = ImageUtils.getImageFromName("Board Pieces/empty.png");
    private final Image backgroundStartImage = ImageUtils.getImageFromName("Board Pieces/emptyStart.png");
    private boolean isOnStartBoard;
    private final int boardX;
    private final int boardY;

    private int placedBoardElement = -1;
    private int ghostBoardElement = -1;
    private Heading direction;
    private Heading ghostDirection;
    private Heading[] placedWalls = new Heading[4];

    private ImageView backgroundImageView = new ImageView();
    private ImageView boardElementImageView = new ImageView();
    private ImageView[] wallImageViews = new ImageView[4];

    private ImageView ghostImageView = new ImageView();

    public CC_SpaceView(int boardX, int boardY) {
        this.boardX = boardX;
        this.boardY = boardY;
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

        this.isOnStartBoard = isOnStartSubBoard;
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

    public void CC_setBoardElement(Image image, Heading direction, int placedBoardElement) {
        this.placedBoardElement = placedBoardElement;
        this.direction = direction;
        CC_setImageView(image, direction, boardElementImageView);
    }
    public void CC_setBoardElement(Image image, Heading direction, int placedBoardElement, CC_SpaceView[][] spaces) {
        this.placedBoardElement = placedBoardElement;
        this.direction = direction;
        if ((this.placedBoardElement == 7 || this.placedBoardElement == 8) && direction != null) {
            // Self
            updateConveyorBeltImage(this.boardElementImageView, this.boardX, this.boardY, spaces, this.placedBoardElement, this.direction);
            // Neighbors
            updateNeighborsConveyorBeltImages(spaces);
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

    public void CC_setGhost(Image image, Heading ghostDirection, int ghostBoardElement, CC_SpaceView[][] spaces) {
        this.ghostBoardElement = ghostBoardElement;
        this.ghostDirection = ghostDirection;
        if ((this.ghostBoardElement == 7 || this.ghostBoardElement == 8) && this.ghostDirection != null) {
            // Self
            updateConveyorBeltImage(this.ghostImageView, this.boardX, this.boardY, spaces, this.ghostBoardElement, this.ghostDirection);
            // Neighbors
            updateNeighborsConveyorBeltImages(spaces);
        } else {
            CC_setImageView(image, this.ghostDirection, this.ghostImageView);
        }
    }

    private void CC_setImageView(Image image, Heading direction, ImageView imageView) {
        imageView.setImage(ImageUtils.getRotatedImageByHeading(image, direction));
    }

    private void updateNeighborsConveyorBeltImages(CC_SpaceView[][] spaces) {
        updateNeighborConveyorBeltImage(spaces[this.boardX + 1][this.boardY], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX - 1][this.boardY], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX][this.boardY + 1], spaces);
        updateNeighborConveyorBeltImage(spaces[this.boardX][this.boardY - 1], spaces);
    }

    private void updateNeighborConveyorBeltImage(CC_SpaceView neighbor, CC_SpaceView[][] spaces) {
        if (neighbor != null) {
            neighbor.updateConveyorBeltImage(neighbor.boardElementImageView, neighbor.boardX, neighbor.boardY, spaces, neighbor.placedBoardElement, neighbor.direction);
        }
    }

    protected void updateConveyorBeltImage(ImageView imageView, int x, int y, CC_SpaceView[][] spaces, int b, Heading d) {
        if (d == null) {
            imageView.setImage(null);
            return;
        }
        Image updatedConveyorImage = ImageUtils.getImageFromName("Board Pieces/" + this.getUpdatedConveyorBeltImage(x, y, spaces, b, d));
        imageView.setImage(ImageUtils.getRotatedImageByHeading(updatedConveyorImage, d));
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
    private String getUpdatedConveyorBeltImage(int x, int y, CC_SpaceView[][] spaces, int myBoardElement, Heading myDirection) {
        StringBuilder imageNameBuilder = new StringBuilder();

        // Green or blue
        imageNameBuilder.append(myBoardElement == 8 ? "green" : "blue");

        // Neighbors
        int noOfNeighbors = 0;
        boolean[] relativeNeighbors = new boolean[4];

        for (int i = 0; i < 4; i++) {
            Heading relativeDirection = Heading.values()[(myDirection.ordinal() + i) % 4];
            CC_SpaceView neighborSpace = this.getSpaceNextTo(relativeDirection, x, y, spaces);
            // i = 0 always counts as a "neighbor".
            if (i == 0) {
                relativeNeighbors[i] = true;
                noOfNeighbors++;
                continue;
            }
            if (neighborSpace == null) continue;

            int neighborBoardElement = neighborSpace.placedBoardElement;
            Heading neighborDirection = neighborSpace.direction;
            if (neighborBoardElement == 7 || neighborBoardElement == 8) {
                if (myBoardElement != neighborBoardElement) continue; // Only count same type

                if (neighborDirection == relativeDirection || neighborDirection.opposite() == relativeDirection) {
                    relativeNeighbors[i] = true;
                    noOfNeighbors++;
                }
            }
        }

        // Building string
        switch (noOfNeighbors) {
            case 1:
                imageNameBuilder.append("Straight");
                break;
            case 2:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                if (relativeNeighbors[2]) {
                    imageNameBuilder.append("Straight");
                } else {
                    imageNameBuilder.append("Turn").append(relativeNeighbors[1] ? "Right" : "Left");
                }
                break;
            case 3:
                // Adjust the conditions for alignment based on the relative neighbors' indexes
                imageNameBuilder.append("T").append(relativeNeighbors[2] ? (relativeNeighbors[1] ? "Right" : "Left") : "Sides");
                break;
            default:
                break;
        }
        imageNameBuilder.append(".png");
        return imageNameBuilder.toString();
    }

    private CC_SpaceView getSpaceNextTo(Heading direction, int x, int y, CC_SpaceView[][] spaces) {
        return switch (direction) {
            case SOUTH -> {
                if (y + 1 >= spaces[x].length) {
                    yield null;
                }
                yield spaces[x][y + 1]; // out of bounds
            }
            case WEST -> {
                if (x - 1 < 0) {
                    yield null;
                }
                yield spaces[x - 1][y]; // out of bounds
            }
            case NORTH -> {
                if (y - 1 < 0) {
                    yield null;
                }
                yield spaces[x][y - 1]; // out of bounds
            }
            case EAST -> {
                if (x + 1 >= spaces.length) {
                    yield null;
                }
                yield spaces[x + 1][y]; // out of bounds
            }
        };
    }
}
