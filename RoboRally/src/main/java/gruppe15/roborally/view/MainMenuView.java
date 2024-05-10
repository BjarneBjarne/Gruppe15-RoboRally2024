package gruppe15.roborally.view;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import gruppe15.roborally.RoboRally;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;

public class MainMenuView {
    
    @FXML
    VBox mainMenu;

    public MainMenuView() {
        initialize();
    }

    public VBox getMainMenu() {
        if(mainMenu == null){
            System.out.println("MainMenu is null");
        }
        return mainMenu;
    }

    @FXML
    public void newGame(ActionEvent event){
        System.out.println("New Game");
    }

    @FXML
    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(MainMenuView.class.getResource("mainMenu.fxml"));
            mainMenu = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
