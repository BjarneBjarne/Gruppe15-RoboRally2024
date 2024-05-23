package gruppe15.roborally.coursecreator;

import gruppe15.observer.Subject;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Laser;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BE_EnergySpace;
import gruppe15.roborally.model.boardelements.BoardElement;
import gruppe15.roborally.model.utils.Constants;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.view.ViewObserver;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static gruppe15.roborally.model.utils.Constants.*;

public class CC_SpaceView extends StackPane {
    private int placedBackground = 0;
    private int placedBoardElement = 0;
    private Heading[] placedWalls = new Heading[4];

    private ImageView backgroundImageView = new ImageView();
    private ImageView boardElementImageView = new ImageView();
    private ImageView[] wallImageViews = new ImageView[4];

    private ImageView ghostImageView = new ImageView();

    public CC_SpaceView() {
        this.setPrefWidth(SPACE_SIZE);
        this.setMinWidth(SPACE_SIZE);
        this.setMaxWidth(SPACE_SIZE);
        this.setPrefHeight(SPACE_SIZE);
        this.setMinHeight(SPACE_SIZE);
        this.setMaxHeight(SPACE_SIZE);

        for (int i = 0; i < wallImageViews.length; i++) {
            wallImageViews[i] = new ImageView();
            wallImageViews[i].setFitWidth(SPACE_SIZE);
            wallImageViews[i].setFitHeight(SPACE_SIZE);
        }
        backgroundImageView.setFitWidth(SPACE_SIZE);
        backgroundImageView.setFitHeight(SPACE_SIZE);
        boardElementImageView.setFitWidth(SPACE_SIZE);
        boardElementImageView.setFitHeight(SPACE_SIZE);
        this.getChildren().addAll(backgroundImageView, boardElementImageView);
        this.getChildren().addAll(wallImageViews);

        ghostImageView.setFitWidth(SPACE_SIZE);
        ghostImageView.setFitHeight(SPACE_SIZE);
        this.getChildren().add(ghostImageView);
    }

    public void setBackground(Image image, int placedBackground) {
        setImageView(image, Heading.NORTH, backgroundImageView);
        this.placedBackground = placedBackground;
    }
    public void setBoardElement(Image image, int placedBoardElement) {
        setImageView(image, Heading.NORTH, boardElementImageView);
        this.placedBoardElement = placedBoardElement;
    }
    public void setWall(Image image, Heading direction) {
        setImageView(image, direction, wallImageViews[direction.ordinal()]);
        this.placedWalls[direction.ordinal()] = image != null ? direction : null;
    }

    public void setGhost(Image image) {
        setImageView(image, Heading.NORTH, ghostImageView);
    }
    public void setGhost(Image image, Heading direction) {
        setImageView(image, direction, ghostImageView);
    }

    public void removeGhost() {
        ghostImageView.setImage(null);
    }

    private void setImageView(Image image, Heading direction, ImageView imageView) {
        imageView.setImage(ImageUtils.getRotatedImageByHeading(image, direction));
    }
}
