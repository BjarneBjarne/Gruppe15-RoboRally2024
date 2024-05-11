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
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.view.BoardView;
import gruppe15.roborally.view.MainMenuView;
import gruppe15.roborally.view.SetupView;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;
    public GridPane directionOptionsPane;
    private Stage stage;
    private BorderPane boardRoot;
    private BoardView boardView;


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
        createMainMenu(appController);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();
        stage.show();
    }

    public void createMainMenu(AppController appController) {
        // if present, remove old BoardView
        boardRoot.getChildren().clear();

        // create and add view for new board
        AnchorPane mainMenu = new MainMenuView().initialize(appController).getMainMenu();
        boardRoot.setCenter(mainMenu);

        // stage.sizeToScene();
    }

    public void createSetupMenue(AppController appController){
        boardRoot.getChildren().clear();
        AnchorPane mainMenu = new SetupView().initialize(appController).getSetupMenu();
        boardRoot.setCenter(mainMenu);

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
}
