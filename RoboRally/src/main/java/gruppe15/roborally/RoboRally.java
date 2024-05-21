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
import gruppe15.roborally.controller.CourseCreatorController;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.MainMenuView;
import gruppe15.roborally.view.SetupView;
import gruppe15.roborally.view.WinScreenView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

import static gruppe15.roborally.model.utils.Constants.*;

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
    private BorderPane boardRoot;
    private BoardView boardView;
    private AnchorPane mainMenu;
    private AnchorPane selectionMenu;
    private CourseCreatorController courseCreator;

    @FXML
    public HBox upgradeShopCardsHBox;
    @FXML
    public Button finishUpgradingButton;

    // private RoboRallyMenuBar menuBar;
    // private AppController appController;

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        AppController appController = new AppController(this);

        // create the primary scene with a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        //RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();
        VBox vbox = new VBox(boardRoot);
        vbox.setAlignment(Pos.CENTER);
        vbox.setMinWidth(MIN_APP_WIDTH);
        vbox.setMinHeight(MIN_APP_HEIGHT);
        createMainMenu(appController);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(e -> {
            e.consume();
            closeGame(appController);
        });
        stage.setResizable(true);
        stage.sizeToScene();
        stage.show();
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
    public void closeGame(AppController appController) {
        Boolean isGameRunning = appController.isGameRunning();
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
                    TextInputDialog filenameInput = new TextInputDialog();
                    filenameInput.setHeaderText("Enter filename");
                    filenameInput.setTitle("Save Game");
                    Optional<String> filename = filenameInput.showAndWait();
                    String strFilename = filename.get().replace(' ', '_');
                    if(!appController.saveGame(strFilename)){
                        Alert errorAlert = new Alert(AlertType.ERROR);
                        errorAlert.setHeaderText("Error saving game");
                        errorAlert.setContentText("Game can only be saved during programming phase.");
                        errorAlert.showAndWait();
                        return;
                    }
                }
            }
        }
        Platform.exit();
    }
    
    public void createMainMenu(AppController appController) {
        // create and add view for new board
        mainMenu = new MainMenuView().initialize(appController).getMainMenu();
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
        boardRoot.getChildren().clear();
        boardRoot.setCenter(mainMenu);
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
        boardRoot.getChildren().clear();
        boardRoot.setCenter(selectionMenu);
    }

    /**
     * creates and apllys a winscreen
     * @param gameController
     * @param appController
     * @author Maximillian Bjørn Mortensen
     */
    public void goToWinScreen(GameController gameController, AppController appController){

        boardRoot.getChildren().clear();

        AnchorPane w = new WinScreenView().initialize(gameController, appController, this).getWinScreen();

        boardRoot.setCenter(w);

    }

    public void createBoardView(GameController gameController, boolean loadingGame) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

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
                boardView.setUpgradeShopFXML(upgradeShopPane, upgradeShopCardsHBox, finishUpgradingButton);
            } else {
                boardView = new BoardView(gameController);
            }
            boardRoot.setCenter(boardView);
        }
        stage.sizeToScene();
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

    public void createCourseCreator(AppController appController) {
        if (courseCreator != null) {
            goToCourseCreator();
        } else {
            boardRoot.getChildren().clear();
            courseCreator = new CourseCreatorController();

            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("CourseCreator.fxml"));
            loader.setController(courseCreator);

            try {
                courseCreator.getChildren().add(loader.load());
            } catch (IOException e) {
                e.printStackTrace();
            }

            boardRoot.setCenter(courseCreator);
            stage.sizeToScene();
        }
    }
    public void goToCourseCreator() {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();
        boardRoot.setCenter(courseCreator);
    }
}
