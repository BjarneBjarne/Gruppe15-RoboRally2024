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
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.MainMenuView;
import gruppe15.roborally.view.SetupView;
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
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static gruppe15.roborally.GameVariables.*;

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
    private AnchorPane mainMenu;
    private AnchorPane selectionMenu;
    private static CC_Controller courseCreator;

    public static final Logger logger = LoggerFactory.getLogger(RoboRally.class);

    @FXML
    StackPane upgradeShopTitelPane;
    @FXML
    StackPane upgradeShopMainPane;
    @FXML
    HBox upgradeShopCardsHBox;
    @FXML
    Button finishUpgradingButton;

    // private RoboRallyMenuBar menuBar;
    // private AppController appController;

    StackPane stackPane;
    StackPane backgroundStackPane;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        // Super messy.
        // TODO: Clean up this mess.

        stage = primaryStage;
        AppController appController = new AppController(this);
        //RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        root = new BorderPane();
        root.setMaxHeight(Double.MAX_VALUE);
        root.setMaxWidth(Double.MAX_VALUE);
        root.setPrefHeight(Region.USE_COMPUTED_SIZE);
        root.setPrefWidth(Region.USE_COMPUTED_SIZE);
        root.setMinHeight(Region.USE_COMPUTED_SIZE);
        root.setMinWidth(Region.USE_COMPUTED_SIZE);
        //root.setStyle("-fx-border-color: blue;");
        //root.setStyle("-fx-border-color: lightblue;");
        //VBox vBox = new VBox(root);
        stackPane = new StackPane(root);
        //vbox.setStyle("-fx-border-color: green;");
       // StackPane.setAlignment(vBox, Pos.CENTER);
        StackPane.setAlignment(root, Pos.CENTER);
        //root.setStyle("-fx-border-color: blue; " + "-fx-border-width: 5px; ");
        //vBox.setStyle("-fx-border-color: orange; " + "-fx-border-width: 5px; ");
        //stackPane.setStyle("-fx-border-color: orange; " + "-fx-border-width: 1px; ");
        //stackPane.setStyle("-fx-border-color: green;");

        stage.setTitle("Robo Rally");
        createMainMenu(appController);
        stage.setFullScreen(START_FULLSCREEN);

        // Get screen bounds and calculate scale
        if (START_FULLSCREEN) {
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

            /*primaryScene.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                event.consume();
                closeGame(appController);
            }
        });*/
        } else {
            stage.setResizable(false);
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getBounds();
            double initialHeight = primaryScreenBounds.getHeight() * 0.75;
            double initialWidth = initialHeight * (16.0 / 9.0);
            primaryScene = new Scene(stackPane, initialWidth, initialHeight);
            primaryStage.setScene(primaryScene);
            primaryScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            primaryStage.setWidth(initialWidth);
            primaryStage.setHeight(initialHeight);
            primaryStage.show();

            primaryStage.heightProperty().addListener((obs, oldVal, newVal) -> {
                APP_SCALE = newVal.doubleValue() / REFERENCE_HEIGHT;
                scaleRoot();
            });
            APP_SCALE = initialHeight / REFERENCE_HEIGHT;
        }
        scaleRoot();

        //System.out.println("Scale: " + APP_SCALE);
        System.out.println("Window size: " + stage.getWidth() + "x" + stage.getHeight());

        // Handling save option on close
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeGame(appController);
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
     * @param appController the AppController of the game
     */
    public static void closeGame(AppController appController) {
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

    public void createMainMenu(AppController appController) {
        // create and add view for new board
        mainMenu = new MainMenuView().initialize(appController).getMainMenu();
        for (Node child : mainMenu.getChildren()) {
            if (child instanceof StackPane b) {
                backgroundStackPane = b;
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
        // if present, remove old BoardView
        root.getChildren().clear();
        root.setCenter(mainMenu);
        courseCreator = null;
    }

    /**
     * sets selection menu to null as to not reuse saved information from last game
     * @author Maximillian Bjørn Mortensen
     */
    public void resetSelectionMenu(){
        selectionMenu = null;
    }

    public void createSetupMenu(AppController appController){
        if (selectionMenu != null) {
            goToSelectionMenu();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gruppe15/roborally/SetupGame.fxml"));
                SetupView setupView = new SetupView();
                loader.setController(setupView);
                selectionMenu = loader.load();
                setupView.setupStartButton(appController);
                setupView.setupBackButton(this);

                goToSelectionMenu();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public void goToSelectionMenu() {
        // if present, remove old BoardView
        root.getChildren().clear();
        root.setCenter(selectionMenu);
    }

    /**
     * creates and apllys a winscreen
     * @param gameController
     * @param appController
     * @author Maximillian Bjørn Mortensen
     */
    public void goToWinScreen(GameController gameController, AppController appController){
        root.getChildren().clear();

        AnchorPane w = new WinScreenView().initialize(gameController, appController, this).getWinScreen();

        root.setCenter(w);
    }

    public void createBoardView(GameController gameController, boolean loadingGame) {
        // if present, remove old BoardView
        root.getChildren().clear();

        if (gameController != null) {
            try {
                FXMLLoader upgradeShopFXMLLoader = new FXMLLoader(getClass().getResource("/gruppe15/roborally/UpgradeShop.fxml"));
                upgradeShopFXMLLoader.setController(this);
                upgradeShopPane = upgradeShopFXMLLoader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
            // create and add view for new board
            if(!loadingGame) {
                FXMLLoader directionOptionsFXMLLoader = new FXMLLoader(RoboRally.class.getResource("SpawnArrows.fxml"));
                try {
                    directionOptionsPane = directionOptionsFXMLLoader.load();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
                boardView = new BoardView(gameController, directionOptionsPane);
                boardView.setUpgradeShopFXML(upgradeShopPane, upgradeShopTitelPane, upgradeShopMainPane, upgradeShopCardsHBox, finishUpgradingButton);
            } else {
                boardView = new BoardView(gameController);
            }
            boardView.getStyleClass().add("transparent-scroll-pane");
            StackPane boardViewStackPane = new StackPane(backgroundStackPane, boardView);
            root.setCenter(boardViewStackPane);
            //BorderPane.set
            VBox.setVgrow(root, Priority.ALWAYS);
            VBox.setVgrow(boardView, Priority.ALWAYS);

            if (backgroundStackPane.getChildren().get(0) instanceof ImageView background) {
                background.setImage(ImageUtils.getImageFromName("Background_SelectionMenu3.png"));
            }
        }
        //stage.sizeToScene();
    }

    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
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
