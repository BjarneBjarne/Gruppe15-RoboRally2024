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
    Image[] charIMG = new Image[6];
    ImageView[] charIMGSelected = new ImageView[6];
    @FXML
    AnchorPane setupMenu;
    @FXML
    VBox maps;
    @FXML
    ComboBox playerCount;
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

    public SetupView initialize(AppController appController) {
        // Load FXML
        this.appController = appController;
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("SetupGame.fxml"));
            setupMenu = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Show courses
        scrollPane = (ScrollPane) setupMenu.lookup("#scrollpaneformaps");

        charIMG[0] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Blue.png");
        charIMG[1] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Green.png");
        charIMG[2] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Orange.png");
        charIMG[3] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Purple.png");
        charIMG[4] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Red.png");
        charIMG[5] = ImageUtils.getImageFromName("Robots/CharacterSelection/RobotSelection_Yellow.png");

        for(int i = 0; i < 6; i++){
            int nr = i+1;
            charIMGSelected[i] = (ImageView) setupMenu.lookup("#player"+nr+"Pic");
        }

        for(int i = 1; i < 7; i++){
            mapGraphics.add(ImageUtils.getImageFromName(i+".png"));
        }
        map = (ImageView) setupMenu.lookup("#map");
        map.setImage(mapGraphics.getFirst());
        VBox coursesVBox = new VBox();
        for(int i = 0; i <mapGraphics.size(); i++){
            Button b = new Button();
            ImageView courseImageView = new ImageView(mapGraphics.get(i));
            courseImageView.setFitWidth(200 - 10);
            courseImageView.setFitHeight(200 - 10);
            b.setGraphic(courseImageView);
            int temp = i;
            b.setOnMouseClicked(e -> {
                map.setImage(mapGraphics.get(temp));
                mapIndex = temp;
            });
            coursesVBox.getChildren().add(b);
        }
        scrollPane = (ScrollPane) setupMenu.lookup("#scrollpaneformaps");
        scrollPane.setContent(coursesVBox);

        for(int i = 0; i < 6; i++){
            // Player name fields
            int playerNo = i + 1;
            TextField nameInput = (TextField) setupMenu.lookup("#player" + playerNo + "Name");
            nameInput.setOnKeyReleased(e -> {
                playerNames[playerNo - 1] = nameInput.getText();
                updateReadyButton();
            });

            // Player robot
            List<String> robotNames = Arrays.stream(Robots.values())
                    .map(Robots::getRobotName)
                    .collect(Collectors.toList());
            ComboBox choseChar = (ComboBox) setupMenu.lookup("#player" + playerNo + "Character");
            choseChar.getItems().addAll(robotNames);
            String[] t = new String[6];
            robotNames.toArray(t);
            choseChar.valueProperty().addListener((obs, oldValue, newValue) -> {
                String name = (String) choseChar.getSelectionModel().getSelectedItem();
                playerCharacters[playerNo - 1] = name;
                for(int j = 0; j < 6; j++){
                    if (name.equals(t[j])) {
                        charIMGSelected[playerNo - 1].setImage(charIMG[j]);
                    }
                }
                playerCharacters[playerNo - 1] = name;
                updateReadyButton();
            });
            charSelection.add(choseChar);
        }

        // Settings
        // Number of players
        playersVBox = (VBox) setupMenu.lookup("#players");
        playerCount = (ComboBox)setupMenu.lookup("#playersCount");
        playerCount.getItems().addAll(2, 3, 4, 5, 6);
        playerCount.getSelectionModel().select(0);
        playerCount.setOnMouseReleased(e -> {
            noOfPlayers = (int)(playerCount.getSelectionModel().getSelectedItem());
            for (int i = 2; i < 6; i++){
                playersVBox.getChildren().get(i).setVisible(i < noOfPlayers);
                if (i >= noOfPlayers) {
                    playerNames[i] = null;
                    playerCharacters[i] = null;
                    charSelection.get(i).getSelectionModel().clearSelection();
                }
            }
        });

        // Start button
        start = (Button) setupMenu.lookup("#start");
        start.setOnMouseClicked(e -> {
            if(isReady()){
                appController.beginCourse(noOfPlayers, mapIndex, playerNames, playerCharacters);
            }
        });

        updateReadyButton();

        return this;
    }

    private void updateReadyButton() {
        if (isReady()) {
            start.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
            start.setStyle("-fx-background-color:  #3a993c60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        } else {
            //start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
            start.setStyle("-fx-background-color:  #993a3a60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        }
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
