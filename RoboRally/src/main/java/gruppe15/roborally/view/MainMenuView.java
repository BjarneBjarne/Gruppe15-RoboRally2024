package gruppe15.roborally.view;

import java.io.IOException;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Shadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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

    Button[] buttons = new Button[4];
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
        Effect hover = new InnerShadow(20, Color.STEELBLUE);
        newGame = (Button) mainMenu.lookup("#newGame");
        newGame.setOnAction(e -> appController.courseSelection());
        loadGame = (Button) mainMenu.lookup("#loadGame");
        loadGame.setOnAction(e -> appController.loadGame());
        help = (Button) mainMenu.lookup("#help");
        // help.setOnAction(e -> appController.help());
        exit = (Button) mainMenu.lookup("#exit");
        exit.setOnAction(e -> appController.exit());
        buttons[0] = newGame;
        buttons[1] = loadGame;
        buttons[2] = help;
        buttons[3] = exit;
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> button.setEffect(hover));
            button.setOnMouseExited(e -> button.setEffect(null));
        }
        return this;
    }
}
