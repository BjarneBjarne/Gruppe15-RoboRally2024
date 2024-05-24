package gruppe15.roborally.coursecreator;

import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class CC_SpaceView extends StackPane {
    private final Image backgroundImage = ImageUtils.getImageFromName("Board Pieces/empty.png");
    private final Image backgroundStartImage = ImageUtils.getImageFromName("Board Pieces/emptyStart.png");
    private boolean isOnStartBoard;

    private int placedBoardElement = -1;
    private Heading direction;
    private Heading[] placedWalls = new Heading[4];

    private ImageView backgroundImageView = new ImageView();
    private ImageView boardElementImageView = new ImageView();
    private ImageView[] wallImageViews = new ImageView[4];

    private ImageView ghostImageView = new ImageView();

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
        CC_setImageView(image, direction, boardElementImageView);
        this.placedBoardElement = placedBoardElement;
        this.direction = direction;
    }
    public void CC_setWall(Image image, Heading direction) {
        CC_setImageView(image, direction, wallImageViews[direction.ordinal()]);
        this.placedWalls[direction.ordinal()] = image != null ? direction : null;
    }

    public void CC_setGhost(Image image) {
        CC_setImageView(image, Heading.NORTH, ghostImageView);
    }
    public void CC_setGhost(Image image, Heading direction) {
        CC_setImageView(image, direction, ghostImageView);
    }

    public void CC_removeGhost() {
        ghostImageView.setImage(null);
    }

    private void CC_setImageView(Image image, Heading direction, ImageView imageView) {
        imageView.setImage(ImageUtils.getRotatedImageByHeading(image, direction));
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
}
