package gruppe15.roborally.view;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.model.Robots;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SetupView {
    AppController appController;
    VBox playersVBox;
    ArrayList<Image> mapGraphics = new ArrayList<>();
    ArrayList<ComboBox> charSelection = new ArrayList<>();
    @FXML
    AnchorPane setupMenu;
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

    int noOfPlayers = 2;
    int mapIndex = 0;
    String[] playerNames = new String[6];
    String[] playerCharacters = new String[6];


    public AnchorPane getSetupMenu() {
        if(setupMenu == null){
            System.out.println("SetupMenu is null");
        }
        return setupMenu;
    }
    @FXML
    public SetupView initialize(AppController appController) {
        this.appController = appController;
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("SetupGame.fxml"));
            setupMenu = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        scrollPane = (ScrollPane) setupMenu.lookup("#scrollpaneformaps");

        for(int i = 1; i < 7; i++){
            mapGraphics.add(ImageUtils.getImageFromName(i+".png"));
        }

        map = (ImageView) setupMenu.lookup("#map");
        map.setImage(mapGraphics.getFirst());

        VBox vBox = new VBox();

        for(int i = 0; i <mapGraphics.size(); i++){
            Button b = new Button();
            ImageView mapImageView = new ImageView(mapGraphics.get(i));
            mapImageView.setFitWidth(227);
            mapImageView.setFitHeight(227);
            b.setGraphic(mapImageView);
            int temp = i;
            b.setOnAction(e -> {
                map.setImage(mapGraphics.get(temp));
                mapIndex = temp;
            });
            vBox.getChildren().add(b);
        }

        scrollPane.setContent(vBox);

        playersVBox = (VBox) setupMenu.lookup("#players");

        for(int i = 0; i < 6; i++){
            int nr = i+1;
            TextField nameInput = (TextField) setupMenu.lookup("#player"+nr+"Name");
            nameInput.setOnAction(e -> {
                playerNames[nr-1] = nameInput.getText();
                if(isReady()){
                    start.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
                }else{
                    start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
                }
            });

            List<String> robotNames = Arrays.stream(Robots.values())
                    .map(Robots::getRobotName)
                    .collect(Collectors.toList());
            ComboBox choseChar = (ComboBox) setupMenu.lookup("#player"+nr+"Charecter");
            choseChar.getItems().addAll(robotNames);
            choseChar.setOnInputMethodTextChanged(e -> {
                String name = (String) choseChar.getSelectionModel().getSelectedItem();
                playerCharacters[nr-1] = name;
                if(isReady()){
                    start.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
                }else{
                    start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
                }
            });
            charSelection.add(choseChar);
        }

        playerCount = (ComboBox) setupMenu.lookup("#playersCount");
        playerCount.getItems().addAll(2,3,4,5,6);
        playerCount.getSelectionModel().select(0);
        playerCount.setOnInputMethodTextChanged(e -> {
            noOfPlayers = (int) playerCount.getSelectionModel().getSelectedItem();
            for(int i = 2; i < 6; i++){
                playersVBox.getChildren().get(i).setVisible(i < noOfPlayers);
                if(i >= noOfPlayers) {
                    playerNames[i] = null;
                    playerCharacters[i] = null;
                    charSelection.get(i).getSelectionModel().clearSelection();
                }
            }
        });

        start = (Button) setupMenu.lookup("#start");
        start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
        start.setOnMouseClicked(e -> {
            if(isReady()){
                appController.beginCourse(noOfPlayers, mapIndex, playerNames, playerCharacters);
            }
        });

        return this;
    }

    private boolean isReady(){
        for(int i = 0; i < noOfPlayers; i++){
            if(playerNames[i] == null || playerCharacters[i] == null) return false;
            for(int j = i-1; j >= 0; j--){
                if(playerNames[i].equals(playerNames[j])) return false;
                if(playerCharacters[i].equals(playerCharacters[j])) return false;
            }
        }
        return true;
    }

}
