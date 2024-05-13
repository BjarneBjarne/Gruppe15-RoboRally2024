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
import gruppe15.roborally.model.Player;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.MainMenuView;
import gruppe15.roborally.view.SetupView;
import gruppe15.roborally.view.WinScreenView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 1280;
    private static final int MIN_APP_HEIGHT = 740;
    public GridPane directionOptionsPane;
    private Stage stage;
    private BorderPane boardRoot;
    private BoardView boardView;
    private AnchorPane mainMenu;
    private AnchorPane selectionMenu;
    private CourseCreatorController courseCreator;


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
        stage.setOnCloseRequest(e -> closeGame(appController));
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

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

                if (saveGameResult.get() == saveButton){
                    //Implement ocndition to only save in programming phase
                    TextInputDialog filenameInput = new TextInputDialog();
                    filenameInput.setHeaderText("Enter filename");
                    filenameInput.setTitle("Save Game");
                    Optional<String> filename = filenameInput.showAndWait();
                    String strFilename = filename.get().replace(' ', '_');
                    appController.saveGame(strFilename);
                }
                Platform.exit();
            }
        }
    }

    public void createMainMenu(AppController appController) {
        // create and add view for new board
        mainMenu = new MainMenuView().initialize(appController).getMainMenu();
        goToMainMenu();
    }
    public void goToMainMenu() {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();
        boardRoot.setCenter(mainMenu);
    }

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

    public void goToWinScreen(GameController gameController, AppController appController){

        boardRoot.getChildren().clear();

        AnchorPane w = new WinScreenView().initialize(gameController, appController, this).getWinScreen();

        boardRoot.setCenter(w);

    }

    public void createBoardView(GameController gameController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            FXMLLoader fxmlLoader = new FXMLLoader(RoboRally.class.getResource("SpawnArrows.fxml"));
            try {
                directionOptionsPane = fxmlLoader.load();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            boardView = new BoardView(gameController, directionOptionsPane);
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
