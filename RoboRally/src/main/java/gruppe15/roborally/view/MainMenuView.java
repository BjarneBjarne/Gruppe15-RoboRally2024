package gruppe15.roborally.view;

import java.io.IOException;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainMenuView {
    
    @FXML
    AnchorPane mainMenu;
    @FXML
    Button newGame;
    @FXML
    Button loadGame;
    @FXML
    Button help;
    @FXML
    Button exit;
    AppController appController;

    public AnchorPane getMainMenu() {
        if(mainMenu == null){
            System.out.println("MainMenu is null");
        }
        return mainMenu;
    }


    @FXML
    public MainMenuView initialize(AppController appController) {
        this.appController = appController;
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("mainMenu.fxml"));
            mainMenu = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        newGame = (Button) mainMenu.lookup("#newGame");
        newGame.setOnAction(e -> appController.courseSelection());
        loadGame = (Button) mainMenu.lookup("#loadGame");
        loadGame.setOnAction(e -> appController.loadGame());
        help = (Button) mainMenu.lookup("#help");
        // help.setOnAction(e -> appController.help());
        exit = (Button) mainMenu.lookup("#exit");
        exit.setOnAction(e -> appController.exit());
        return this;
    }
}
