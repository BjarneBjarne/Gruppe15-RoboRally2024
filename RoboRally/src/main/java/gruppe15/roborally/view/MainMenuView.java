package gruppe15.roborally.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainMenuView {

    private VBox mainMenu;

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

    public void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(MainMenuView.class.getResource("MainMenu.fxml"));
            System.out.println(loader.getLocation());
            mainMenu = loader.load();
        } catch (IOException e) {
            System.out.println("wutDUHELL " + e.getMessage());
            //e.printStackTrace();
        }
    }
}
