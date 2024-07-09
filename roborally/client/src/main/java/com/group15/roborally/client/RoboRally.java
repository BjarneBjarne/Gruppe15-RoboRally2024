/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.group15.roborally.client;

import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.coursecreator.CC_Controller;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.utils.AlertUtils;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.view.*;
import com.group15.roborally.client.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {
    private GridPane directionOptionsPane;
    private StackPane upgradeShopPane;
    private Stage stage;
    private AnchorPane mainMenuPane;
    private AnchorPane multiplayerMenuPane;

    private static CC_Controller courseCreator;
    private static AppController appController;

    @FXML
    StackPane upgradeShopTitelPane;
    @FXML
    StackPane upgradeShopMainPane;
    @FXML
    HBox upgradeShopCardsHBox;
    @FXML
    Button finishUpgradingButton;

    private Scene primaryScene;
    private final StackPane root = new StackPane();
    private final StackPane scalePane = new StackPane();
    private final ImageView backgroundImageView = new ImageView();
    private final StackPane backgroundStackPane = new StackPane(backgroundImageView);

    private final InfoPaneView infoPane = new InfoPaneView();
    private final Label debugLabel = new Label();

    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * JavaFX call to start the application.
     * The method sets the stage and scene.
     * @param primaryStage Stage given from the JavaFX layer.
     */
    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        stage.setTitle("Robo Rally");
        AlertUtils.setPrimaryStage(stage);

        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 36);
        debugLabel.setFont(textFont);
        debugLabel.setTextAlignment(TextAlignment.LEFT);
        StackPane.setAlignment(debugLabel, Pos.TOP_LEFT);

        root.setMinSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
        root.setPrefSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
        root.setMaxSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
        root.getChildren().setAll(scalePane, debugLabel);

        StackPane.setAlignment(scalePane, Pos.CENTER);
        StackPane.setMargin(scalePane, new Insets(0, 0, 0, 0));
        /*AnchorPane.setTopAnchor(scalePane, 0.0);
        AnchorPane.setBottomAnchor(scalePane, 0.0);
        AnchorPane.setRightAnchor(scalePane, 0.0);
        AnchorPane.setLeftAnchor(scalePane, 0.0);*/

        setBackgroundImage("Background_MainMenu2.png");

        StackPane.setAlignment(infoPane, Pos.CENTER);
        /*AnchorPane.setTopAnchor(debugLabel, 0.0);
        AnchorPane.setLeftAnchor(debugLabel, 0.0);*/

        scalePane.setStyle(
                        "-fx-background: transparent; " +
                        "-fx-background-color: transparent; " +
                        "-fx-border-color: blue; " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5"
        );
        root.setStyle(
                        "-fx-background: transparent; " +
                        "-fx-background-color: transparent; "/* +
                        "-fx-border-color: rgba(255,46,83,0.35); " +
                        "-fx-border-width: 2px; " +
                        "-fx-border-radius: 5"*/
        );

        appController = new AppController(this, infoPane);
        try {
            appController.loadCourses();
        } catch (NoCoursesException e) {
            System.out.println(e.getMessage());
        }

        if (ApplicationSettings.FULLSCREEN) {
            stage.setFullScreen(true);
        }

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();

        double initialWidth = primaryScreenBounds.getWidth();
        if (!ApplicationSettings.FULLSCREEN) {
            initialWidth *= 0.65;
        }
        double initialHeight = initialWidth * (9.0 / 16.0);
        primaryScene = new Scene(root, initialWidth, initialHeight);
        primaryScene.setFill(Color.BLACK);

        stage.setScene(primaryScene);

        stage.setOnShown(_ -> {
            //setViewableWindowSize(finalInitialWidth, initialHeight);

            Window window = stage.getScene().getWindow();
            double windowHeightIncludingTitleBar = window.getHeight();

            System.out.println("Total Window Height (including title bar): " + windowHeightIncludingTitleBar);

            // Layout bounds
            System.out.println("Root Pane Layout Bounds: " + root.getLayoutBounds());
            System.out.println("Scale Pane Layout Bounds: " + scalePane.getLayoutBounds());
            System.out.println("Info Pane Layout Bounds: " + infoPane.getLayoutBounds());

            scaleUI();
        });

        //ApplicationSettings.APP_SCALE = initialWidth / ApplicationSettings.REFERENCE_WIDTH;

        URL stylesCSS = getClass().getResource("styles.css");
        if (stylesCSS != null) {
            primaryScene.getStylesheets().add(stylesCSS.toExternalForm());
        }

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && stage.isFullScreen()) {
                event.consume(); // Prevent default behavior of exiting fullscreen
            }
        });

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.Q && event.isMetaDown()) {
                event.consume();
                closeRequest();
            }
        });

        stage.setOnCloseRequest(e -> {
            e.consume();
            closeRequest();
        });

        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        createMainMenu();

        stage.show();

        primaryScene.widthProperty().addListener((_, _, _) -> scaleUI());
        primaryScene.heightProperty().addListener((_, _, _) -> scaleUI());
    }

    private void setBackgroundImage(String imageString) {
        Image backgroundImage = ImageUtils.getImageFromName(imageString);
        assert backgroundImage != null;
        backgroundImageView.setFitWidth(backgroundImage.getWidth());
        backgroundImageView.setFitHeight(backgroundImage.getHeight());
    }

    private void scaleUI() {
        Platform.runLater(() -> {
            double newWidth = primaryScene.widthProperty().doubleValue();
            double newHeight = primaryScene.heightProperty().doubleValue();

            ApplicationSettings.APP_SCALE = Math.min(newWidth / ApplicationSettings.REFERENCE_WIDTH, newHeight / ApplicationSettings.REFERENCE_HEIGHT);
            scalePane.setScaleX(ApplicationSettings.APP_SCALE);
            scalePane.setScaleY(ApplicationSettings.APP_SCALE);
            scalePane.setTranslateX(0);
            scalePane.setTranslateY(0);
            scalePane.setMinSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
            scalePane.setPrefSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
            scalePane.setMaxSize(ApplicationSettings.REFERENCE_WIDTH, ApplicationSettings.REFERENCE_HEIGHT);
            ApplicationSettings.APP_BOUNDS = new Rectangle2D(ApplicationSettings.MIN_APP_WIDTH, ApplicationSettings.MIN_APP_HEIGHT, newWidth, newHeight);
        });
        updateDebug();
    }

    private void updateDebug() {
        Platform.runLater(() -> {
            Bounds scalePaneBounds = scalePane.getLayoutBounds();
            debugLabel.setText(
                    "App bounds: " + (int)ApplicationSettings.APP_BOUNDS.getWidth() + "x" + (int)ApplicationSettings.APP_BOUNDS.getHeight() + "\n" +
                            "Scale: " + (int)ApplicationSettings.APP_SCALE + "\n" +
                            "Stage size: " + stage.getWidth() + "x" + stage.getHeight() + "\n" +
                            "Scale pane: " + scalePane.getWidth() + "x" + scalePane.getHeight() + " - " + scalePane.getLayoutX() + ", " + scalePane.getLayoutY() + "\n" +
                            "Scale pane: " + scalePaneBounds.getWidth() + "x" + scalePaneBounds.getHeight() + " - " + scalePaneBounds.getMinX() + ", " + scalePaneBounds.getMaxY() + "\n" +
                            "Scale pane scale: " + (int)scalePane.getScaleX() + ", " + (int)scalePane.getScaleY() + "\n" +
                    "Root pane: " + root.getWidth() + "x" + root.getHeight() + " - " + root.getLayoutX() + ", " + root.getLayoutY() + "\n"
            );
            debugLabel.setTextFill(Color.DARKRED);
        });
    }

    private void setMainPane(Pane mainPane) {
        if (mainPane == null) return;
        scalePane.getChildren().clear();

        scalePane.getChildren().setAll(backgroundStackPane, mainPane, infoPane);
    }

    private void setViewableWindowSize(double width, double height) {
        // Calculate the height of the window decorations (title bar)
        double windowDecorationHeight = stage.getHeight() - stage.getScene().getHeight();
        stage.setWidth(width);
        stage.setHeight(height + windowDecorationHeight);
        System.out.println("Setting stage size: " + width + "x" + height);
        System.out.println("Size with title bar: " + width + "x" + height);
        // Perform layout updates after setting the stage size
        primaryScene.getWindow().sizeToScene();
    }

    /**
     * Method to close the game. Will first ask the user if they are sure they want to exit the game.
     * If the user chooses to exit the game, they will be asked if they want to save the game.
     * If the user chooses to save the game, they will be asked to enter a filename.
     * Then game will be saved, and closed
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    public static void closeRequest() {
        System.out.println("close");
        boolean isGameRunning = appController.isGameRunning();
        boolean isCourseCreatorRunning = appController.isCourseCreatorOpen;

        if (isGameRunning) {
            appController.quitGame(true);
        } else if (isCourseCreatorRunning) {
            Optional<ButtonType> result = AlertUtils.showConfirmationAlert(
                    "Exit RoboRally course creator?",
                    "Are you sure you want to exit the RoboRally course creator?"
            );

            if (result.isEmpty() || result.get() != ButtonType.OK) return; // return without exiting the application

            courseCreator.saveCourseDialog();

            appController.quitGame(false);
        } else {
            appController.quitGame(false);
        }
    }

    public void createMainMenu() {
        // create and add view for new board
        mainMenuPane = new MainMenuView().initialize(appController).getMainMenu();
        goToMainMenu();
    }

    /**
     * Method to go to the main menu, so that the game can go back to the main menu,
     * and not recreate an instance of the main menu.
     *
     * @Author Marcus Rémi Lemser Eychenne, s230985
     */
    public void goToMainMenu() {
        appController.disconnectFromServer("", 1000);
        resetMultiplayerMenu();
        appController.resetGameController();
        setMainPane(mainMenuPane);
        courseCreator = null;
    }

    /**
     * Sets multiplayer menu and pane to null.
     * @author Maximillian Bjørn Mortensen
     */
    public void resetMultiplayerMenu() {
        appController.resetMultiplayerMenuView();
        multiplayerMenuPane = null;
    }

    public void createMultiplayerMenu(MultiplayerMenuView multiplayerMenuView) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MultiplayerMenu.fxml"));
            loader.setController(multiplayerMenuView);
            multiplayerMenuPane = loader.load();
            goToMultiplayerMenu();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void goToMultiplayerMenu() {
        setMainPane(multiplayerMenuPane);
    }

    /**
     * Creates and shows the win screen.
     * @param gameController
     * @author Maximillian Bjørn Mortensen
     */
    public void goToWinScreen(GameController gameController, Player winner) {
        AnchorPane w = new WinScreenView().initialize(gameController, appController, this, winner).getWinScreen();

        setMainPane(w);
    }

    public void createBoardView(GameController gameController) {
        if (gameController != null) {
            // Loading UpgradeShop.fxml
            try {
                FXMLLoader upgradeShopFXMLLoader = new FXMLLoader(getClass().getResource("UpgradeShop.fxml"));
                upgradeShopFXMLLoader.setController(this);
                upgradeShopPane = upgradeShopFXMLLoader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }

            // Loading DirectionArrows.fxml
            FXMLLoader directionOptionsFXMLLoader = new FXMLLoader(RoboRally.class.getResource("DirectionArrows.fxml"));
            try {
                directionOptionsPane = directionOptionsFXMLLoader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }

            // Creating and adding view for new board
            GameView gameView = new GameView(gameController, directionOptionsPane);
            gameView.setUpgradeShopFXML(upgradeShopPane, upgradeShopTitelPane, upgradeShopMainPane, upgradeShopCardsHBox, finishUpgradingButton);
            gameView.getStyleClass().add("transparent-scroll-pane");
            setMainPane(gameView);

            setBackgroundImage("Background_SelectionMenu3.png");
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Unchecked exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void createCourseCreator(Scene primaryScene) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseCreator.fxml"));

        try {
            // Load the FXML and set the controller
            loader.load();
            courseCreator = loader.getController();
            courseCreator.setScene(primaryScene);

            setMainPane(courseCreator);
        } catch (IOException e) {
            e.printStackTrace();
        }

        courseCreator.initializeExitButton(this::goToMainMenu);
    }

    public void exitApplication() {
        Platform.runLater(() -> stage.close());
    }
}
