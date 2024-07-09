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
import com.group15.roborally.client.view.*;
import com.group15.roborally.client.view.GameView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static com.group15.roborally.client.ApplicationSettings.*;

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

    private StackPane scalePane;
    private StackPane mainPane;
    private BorderPane root;
    private StackPane backgroundStackPane;

    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * JavaFX call to start the application.
     * The method sets the stage and scene. GameVariable START_FULLSCREEN is set up for future, but fullscreen is -
     *     currently not supported.
     * @param primaryStage Stage given from the JavaFX layer.
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        AlertUtils.setPrimaryStage(stage);

        root = new BorderPane();
        root.setMaxHeight(Double.MAX_VALUE);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setPrefHeight(Region.USE_COMPUTED_SIZE);
        root.setPrefWidth(Region.USE_COMPUTED_SIZE);
        root.setMinHeight(Region.USE_COMPUTED_SIZE);
        root.setMinWidth(Region.USE_COMPUTED_SIZE);
        mainPane = new StackPane(root);

        InfoPaneView infoPane = new InfoPaneView();
        mainPane.getChildren().add(infoPane);
        scalePane = new StackPane(mainPane);

        appController = new AppController(this, infoPane);
        try {
            appController.loadCourses();
        } catch (NoCoursesException e) {
            System.out.println(e.getMessage());
        }

        StackPane.setAlignment(root, Pos.CENTER);
        stage.setTitle("Robo Rally");
        //stage.setResizable(false);

        createMainMenu();

        if (FULLSCREEN) {
            stage.setFullScreen(true);
        }

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();

        //double initialHeight = primaryScreenBounds.getHeight();
        double initialWidth = primaryScreenBounds.getWidth();
        if (!FULLSCREEN) {
            initialWidth *= 0.65;
        }
        double initialHeight = initialWidth * (9.0 / 16.0);
        Scene primaryScene = new Scene(scalePane, initialWidth, initialHeight);
        primaryScene.setFill(Color.BLACK);

        stage.setScene(primaryScene);
        stage.setWidth(initialWidth);
        stage.setHeight(initialHeight);

        stage.widthProperty().addListener((_, _, newVal) -> scaleRoot(newVal.doubleValue()));
        APP_SCALE = initialWidth / REFERENCE_WIDTH;

        URL stylesCSS = getClass().getResource("styles.css");
        if (stylesCSS != null) {
            primaryScene.getStylesheets().add(stylesCSS.toExternalForm());
        }

        scaleRoot(initialWidth);

        System.out.println("Window size: " + stage.getWidth() + "x" + stage.getHeight());

        stage.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE && primaryStage.isFullScreen()) {
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

        stage.show();
    }

    private void scaleRoot(double newWidth) {
        APP_SCALE = newWidth / REFERENCE_WIDTH;
        scalePane.setScaleX(APP_SCALE);
        scalePane.setScaleY(APP_SCALE);
        StackPane.setMargin(root, new Insets(0, 0, 0, 0));
        APP_BOUNDS = new Rectangle2D(MIN_APP_WIDTH, MIN_APP_HEIGHT, stage.getWidth(), stage.getHeight());
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
        for (Node child : mainMenuPane.getChildren()) {
            if (child instanceof StackPane b) {
                backgroundStackPane = b;
                break;
            }
        }
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
        root.getChildren().clear(); // If present, remove old GameView
        root.setCenter(mainMenuPane);
        courseCreator = null;
        if (backgroundStackPane.getChildren().getFirst() instanceof ImageView background) {
            background.setImage(ImageUtils.getImageFromName("Background_MainMenu.png"));
        }
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
        // if present, remove old GameView
        root.getChildren().clear();
        root.setCenter(multiplayerMenuPane);
    }

    /**
     * Creates and shows the win screen.
     * @param gameController
     * @author Maximillian Bjørn Mortensen
     */
    public void goToWinScreen(GameController gameController, Player winner) {
        root.getChildren().clear();

        AnchorPane w = new WinScreenView().initialize(gameController, appController, this, winner).getWinScreen();

        root.setCenter(w);
    }

    public void createBoardView(GameController gameController) {
        root.getChildren().clear(); // If present, remove old GameView

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
            StackPane boardViewStackPane = new StackPane(backgroundStackPane, gameView);
            root.setCenter(boardViewStackPane);
            //BorderPane.set
            /*VBox.setVgrow(root, Priority.ALWAYS);
            VBox.setVgrow(gameView, Priority.ALWAYS);*/

            if (backgroundStackPane.getChildren().getFirst() instanceof ImageView background) {
                background.setImage(ImageUtils.getImageFromName("Background_SelectionMenu3.png"));
            }
        }
        //stage.sizeToScene();
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
        root.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("CourseCreator.fxml"));

        try {
            // Load the FXML and set the controller
            loader.load();
            courseCreator = loader.getController();
            courseCreator.setScene(primaryScene);

            // Set the loaded courseCreator as the center of the root layout
            root.setCenter(courseCreator);
        } catch (IOException e) {
            e.printStackTrace();
        }

        courseCreator.initializeExitButton(this::goToMainMenu);
    }

    public void exitApplication() {
        Platform.runLater(() -> stage.close());
    }
}
