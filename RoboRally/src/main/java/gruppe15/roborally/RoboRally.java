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
package gruppe15.roborally;

import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.coursecreator.CC_Controller;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.exceptions.NoCoursesException;
import gruppe15.roborally.model.lobby.LobbyData;
import gruppe15.utils.ImageUtils;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.MainMenuView;
import gruppe15.roborally.view.MultiplayerMenuView;
import gruppe15.roborally.view.WinScreenView;
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
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

import static gruppe15.roborally.ApplicationSettings.*;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {
    public GridPane directionOptionsPane;
    private StackPane upgradeShopPane;
    private Stage stage;
    private static Scene primaryScene;
    private BorderPane root;
    private BoardView boardView;
    private AnchorPane mainMenuPane;
    private AnchorPane multiplayerMenuPane;
    private MultiplayerMenuView multiplayerMenuView;
    private static CC_Controller courseCreator;

    private static AppController appController;

    public static final Logger logger = LoggerFactory.getLogger(RoboRally.class); // Can be used to log to a file. Doesn't work currently.

    @FXML
    StackPane upgradeShopTitelPane;
    @FXML
    StackPane upgradeShopMainPane;
    @FXML
    HBox upgradeShopCardsHBox;
    @FXML
    Button finishUpgradingButton;

    StackPane stackPane;
    StackPane backgroundStackPane;

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
        // TODO: Clean up this mess.
        stage = primaryStage;
        appController = new AppController(this);
        try {
            appController.loadCourses();
        } catch (NoCoursesException e) {
            logger.info(e.getMessage());
        }
        root = new BorderPane();
        root.setMaxHeight(Double.MAX_VALUE);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setPrefHeight(Region.USE_COMPUTED_SIZE);
        root.setPrefWidth(Region.USE_COMPUTED_SIZE);
        root.setMinHeight(Region.USE_COMPUTED_SIZE);
        root.setMinWidth(Region.USE_COMPUTED_SIZE);
        stackPane = new StackPane(root);

        StackPane.setAlignment(root, Pos.CENTER);
        stage.setTitle("Robo Rally");

        createMainMenu();

        stage.setFullScreen(START_FULLSCREEN);
        // Get screen bounds and calculate scale
        if (!START_FULLSCREEN) { // Currently, only windowed is supported.
            stage.setResizable(false);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
            double initialHeight = primaryScreenBounds.getHeight() * 0.75;
            double initialWidth = initialHeight * (16.0 / 9.0);
            primaryScene = new Scene(stackPane, initialWidth, initialHeight);
            primaryStage.setScene(primaryScene);
            URL stylesCSS = getClass().getResource("styles.css");
            if (stylesCSS != null) {
                primaryScene.getStylesheets().add(stylesCSS.toExternalForm());
            }
            primaryStage.setWidth(initialWidth);
            primaryStage.setHeight(initialHeight);
            primaryStage.show();

            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                APP_SCALE = newVal.doubleValue() / REFERENCE_HEIGHT;
                scaleRoot();
            });
            APP_SCALE = initialHeight / REFERENCE_HEIGHT;
        }/* else {
            primaryScene = new Scene(stackPane);
            stage.setScene(primaryScene);
            stage.setResizable(false);
            stage.show();
            APP_BOUNDS = Screen.getPrimary().getBounds();
            APP_SCALE = (APP_BOUNDS.getHeight() / REFERENCE_HEIGHT);
            // Show the stage
            stage.show();
            // Set stage dimensions
            stage.setWidth(APP_BOUNDS.getWidth());
            stage.setHeight(APP_BOUNDS.getHeight());
        }*/
        scaleRoot();

        System.out.println("Window size: " + stage.getWidth() + "x" + stage.getHeight());

        // Handling save option on close
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeGame();
        });
    }

    private void scaleRoot() {
        stackPane.setScaleX(APP_SCALE);
        stackPane.setScaleY(APP_SCALE);
        StackPane.setMargin(root, new Insets(0, 0, 0, 0));
        //System.out.println(APP_SCALE);
        APP_BOUNDS = new Rectangle2D(MIN_APP_WIDTH, MIN_APP_HEIGHT, stage.getWidth(), stage.getHeight());
        //root.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        UpdateSizes();
    }

    /**
     * Method to close the game. Will first ask the user if they are sure they want to exit the game.
     * If the user chooses to exit the game, they will be asked if they want to save the game.
     * If the user chooses to save the game, they will be asked to enter a filename.
     * Then game will be saved, and closed
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    public static void closeGame() {
        Boolean isGameRunning = appController.isGameRunning();
        Boolean isCourseCreatorRunning = appController.isCourseCreatorOpen;

        if (isGameRunning) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            } else {
                // If the user did not cancel, the RoboRally application will exit
                // after the option to save the game

                Dialog saveGameDialog = new Dialog();
                saveGameDialog.setHeaderText("Do you want to save the game?");
                saveGameDialog.setTitle("Save Game");
                ButtonType saveButton = new ButtonType("Save");
                ButtonType dontSaveButton = new ButtonType("Don't Save");
                saveGameDialog.getDialogPane().getButtonTypes().addAll(saveButton, dontSaveButton);
                Optional<ButtonType> saveGameResult = saveGameDialog.showAndWait();

                // Method appController.saveGame() will return false if the game is not in the programming
                // phase, and an error message will be shown to the user. Game will then continue to run.
                if (saveGameResult.get() == saveButton){
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save Game");
                    fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
                    String userHome = System.getProperty("user.home");
                    String relativePath = "\\RoboRally\\saves";
                    String directoryPath = userHome + relativePath;

                    File folderFile = new File(directoryPath);
                    // Create saves folder if it doesn't exist
                    if (!folderFile.exists()) {
                        if (folderFile.mkdirs()) {
                            System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
                        } else {
                            System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
                        }
                    }

                    fileChooser.setInitialDirectory(new File(directoryPath));
                    fileChooser.setInitialFileName("New save.json");
                    File saveFile = fileChooser.showSaveDialog(primaryScene.getWindow());

                    if (saveFile != null) {
                        if(!appController.saveGame(saveFile)){
                            Alert errorAlert = new Alert(AlertType.ERROR);
                            errorAlert.setHeaderText("Error saving game");
                            errorAlert.setContentText("Game can only be saved during programming phase.");
                            errorAlert.showAndWait();
                            return;
                        }
                    }
                }
            }
        } else if (isCourseCreatorRunning) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally course creator?");
            alert.setContentText("Are you sure you want to exit the RoboRally course creator?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            } else {
                courseCreator.saveCourseDialog();
            }
        }
        Platform.exit();
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
        resetMultiplayerMenu();
        appController.disconnectFromServer();
        root.getChildren().clear(); // If present, remove old BoardView
        root.setCenter(mainMenuPane);
        courseCreator = null;
    }

    /**
     * Sets multiplayer menu and pane to null.
     * @author Maximillian Bjørn Mortensen
     */
    public void resetMultiplayerMenu() {
        multiplayerMenuView = null;
        multiplayerMenuPane = null;
    }

    public void createMultiplayerMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gruppe15/roborally/MultiplayerMenu.fxml"));
            multiplayerMenuView = new MultiplayerMenuView();
            loader.setController(multiplayerMenuView);
            multiplayerMenuPane = loader.load();
            multiplayerMenuView.setupStartButton(appController);
            multiplayerMenuView.setupBackButton(this);
            multiplayerMenuView.setupJoinButton(appController);
            multiplayerMenuView.setupHostButton(appController);

            goToMultiplayerMenu();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    public void goToMultiplayerMenu() {
        // if present, remove old BoardView
        root.getChildren().clear();
        root.setCenter(multiplayerMenuPane);
    }

    /**
     * Initializes the lobbyData with the server data for the local player, either hosting or joining. Is called when the server tells the player they can join/host the lobbyData.
     * @param lobbyData The lobbyData object received from the server.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void connectedToLobby(LobbyData lobbyData) {
        multiplayerMenuView.setupLobby(lobbyData, appController.getCourses());
        appController.startLobbyUpdateLoop();
    }

    public void updateLobby(LobbyData updatedLobbyData) {
        multiplayerMenuView.updateLobby(updatedLobbyData);
    }

    /**
     * Creates and shows the win screen.
     * @param gameController
     * @author Maximillian Bjørn Mortensen
     */
    public void goToWinScreen(GameController gameController){
        root.getChildren().clear();

        AnchorPane w = new WinScreenView().initialize(gameController, appController, this).getWinScreen();

        root.setCenter(w);
    }

    public void createBoardView(GameController gameController) {
        root.getChildren().clear(); // If present, remove old BoardView

        if (gameController != null) {
            // Loading UpgradeShop.fxml
            try {
                FXMLLoader upgradeShopFXMLLoader = new FXMLLoader(getClass().getResource("/gruppe15/roborally/UpgradeShop.fxml"));
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
            boardView = new BoardView(gameController, directionOptionsPane);
            boardView.setUpgradeShopFXML(upgradeShopPane, upgradeShopTitelPane, upgradeShopMainPane, upgradeShopCardsHBox, finishUpgradingButton);
            boardView.getStyleClass().add("transparent-scroll-pane");
            StackPane boardViewStackPane = new StackPane(backgroundStackPane, boardView);
            root.setCenter(boardViewStackPane);
            //BorderPane.set
            VBox.setVgrow(root, Priority.ALWAYS);
            VBox.setVgrow(boardView, Priority.ALWAYS);

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
        launch(args);
    }

    public void createCourseCreator(Scene primaryScene) {
        root.getChildren().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gruppe15/roborally/CourseCreator.fxml"));

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
}
