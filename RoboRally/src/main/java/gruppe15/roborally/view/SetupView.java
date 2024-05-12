package gruppe15.roborally.view;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;

public class SetupView {

    AppController appController;
    VBox players;
    ArrayList<Image> mapGraphics = new ArrayList<>();
    String[] names = new String[6];
    String[] charecters = new String[6];
    ArrayList<ComboBox> charSelection = new ArrayList<>();
    Image[] charIMG = new Image[6];
    boolean ready;
    int playerNr = 2;
    int mapNr = 1;
    @FXML
    AnchorPane setupmenue;
    @FXML
    VBox maps;
    @FXML
    ComboBox<Integer> playerCount;
    @FXML
    ImageView map;
    @FXML
    Button start;
    @FXML
    ScrollPane scrollPane;

    public AnchorPane getSetupmenue() {
        if(setupmenue == null){
            System.out.println("Setupmenue is null");
        }
        return setupmenue;
    }
    @FXML
    public SetupView initialize(AppController appController) {
        this.appController = appController;
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("SetupGame.fxml"));
            setupmenue = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPane = (ScrollPane) setupmenue.lookup("#scrollpaneformaps");

        charIMG[0] = ImageUtils.getImageFromName("1.png");
        charIMG[1] = ImageUtils.getImageFromName("2.png");
        charIMG[2] = ImageUtils.getImageFromName("3.png");
        charIMG[3] = ImageUtils.getImageFromName("4.png");
        charIMG[4] = ImageUtils.getImageFromName("5.png");
        charIMG[5] = ImageUtils.getImageFromName("6.png");

        for(int i = 1; i < 7; i++){
            mapGraphics.add(ImageUtils.getImageFromName(i+".png"));
        }

        map = (ImageView) setupmenue.lookup("#map");
        map.setImage(mapGraphics.getFirst());

        VBox vBox = new VBox();

        for(int i = 0; i <mapGraphics.size(); i++){
            Button b = new Button();
            b.setGraphic(new ImageView(mapGraphics.get(i)));
            int temp = i;
            b.setOnAction(e -> {
                map.setImage(mapGraphics.get(temp));
                mapNr = temp;
            });
            vBox.getChildren().add(b);
        }

        scrollPane.setContent(vBox);

        players = (VBox) setupmenue.lookup("#players");

        for(int i = 0; i < 6; i++){
            int nr = i+1;
            TextField nameInput = (TextField) setupmenue.lookup("#player"+nr+"Name");
            nameInput.setOnAction(e -> {
                names[nr-1] = nameInput.getText();
                if(isReady()){
                    start.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
                }else{
                    start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
                }
            });

            ComboBox choseChar = (ComboBox) setupmenue.lookup("#player"+nr+"Charecter");
            choseChar.getItems().addAll("HAMMER BOT", "ZOOM BOT", "TWONKY", "STINKY", "GLUG BOT", "CRACKER");
            choseChar.setOnAction(e -> {
                String name = (String) choseChar.getSelectionModel().getSelectedItem();
                charecters[nr-1] = name;
                if(isReady()){
                    start.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
                }else{
                    start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
                }
            });
            charSelection.add(choseChar);
        }

        playerCount = (ComboBox) setupmenue.lookup("#playersCount");
        playerCount.getItems().addAll(2,3,4,5,6);
        playerCount.setOnAction(e -> {
            playerNr = (int) playerCount.getSelectionModel().getSelectedItem();
            for(int i = 2; i < 6; i++){
                players.getChildren().get(i).setVisible(i < playerNr);
                if(i >= playerNr) {
                    names[i] = null;
                    charecters[i] = null;
                    charSelection.get(i).getSelectionModel().clearSelection();
                }
            }
        });

        start = (Button) setupmenue.lookup("#start");
        start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
        start.setOnAction(e -> {
            if(ready){
                //start game
            }
        });

        return this;
    }

    private boolean isReady(){
        for(int i = 0; i < playerNr; i++){
            if(names[i] == null || charecters[i] == null) return false;
            for(int j = i-1; j >= 0; j--){
                if(names[i].equals(names[j])) return false;
                if(charecters[i].equals(charecters[j])) return false;
            }
        }
        return true;
    }

}
