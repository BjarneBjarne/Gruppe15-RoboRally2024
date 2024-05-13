package gruppe15.roborally.view;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;

public class WinScreenView {
    @FXML
    AnchorPane wincon;
    @FXML
    Button toMainMenu;
    @FXML
    Text winnerText1;
    @FXML
    Text winnerText2;
    @FXML
    ImageView playerIMG;

    public AnchorPane getWinScreen() {
        if(wincon == null){
            System.out.println("winScreen is null");
        }
        return wincon;
    }

    @FXML
    public WinScreenView initialize(GameController gameController, AppController appController, RoboRally roboRally) {
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("WinScreen.fxml"));
            wincon = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        toMainMenu = (Button) wincon.lookup("#toMainMenu");
        toMainMenu.setOnAction(e -> {
            appController.setGameController(null);
            roboRally.resetSelectionMenu();
            roboRally.goToMainMenu();
        });

        winnerText1 = (Text) wincon.lookup("#winnerText1");
        winnerText2 = (Text) wincon.lookup("#winnerText2");
        winnerText1.setText(winnerText1.getText()+gameController.getWinnerName());
        winnerText2.setText(winnerText2.getText()+gameController.getWinnerName());

        playerIMG = (ImageView) wincon.lookup("#playerIMG");
        playerIMG.setImage(gameController.getWinnerIMG());

        return this;
    }
}
