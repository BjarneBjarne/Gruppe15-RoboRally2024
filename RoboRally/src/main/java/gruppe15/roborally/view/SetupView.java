package gruppe15.roborally.view;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.model.Robots;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static gruppe15.roborally.model.utils.GameSettings.*;

/**
 * @author Maximillian Bjørn Mortensen
 */
public class SetupView {
    private ArrayList<Image> mapGraphics = new ArrayList<>();
    private ArrayList<ComboBox> charSelection = new ArrayList<>();
    private ImageView[] playerRobotImageViews = new ImageView[6];
    private HBox[] playerHBoxes = new HBox[6];

    @FXML
    AnchorPane selection_menu;
    @FXML
    ScrollPane scrollPaneForMaps;
    @FXML
    VBox playersVBox;
    @FXML
    ImageView map;
    @FXML
    Button start;
    @FXML
    Button selection_back;

    @FXML
    ComboBox settings_noOfPlayers;
    @FXML
    ComboBox settings_keepHand;


    private int mapIndex = 0;
    private final String[] playerNames = new String[6];
    private final String[] playerCharacters = new String[6];

    public SetupView() {

    }

    /**
     * returns the field selection_menu
     * @return AnchorPane
     * @author Maximillian Bjørn Mortensen
     */
    public AnchorPane getSetupMenu() {
        return selection_menu;
    }

    public void setupStartButton(AppController appController) {
        // Start button
        start.setOnMouseClicked(e -> {
            if(isReady()){
                appController.beginCourse(mapIndex, playerNames, playerCharacters);
            }
        });
    }

    public void setupBackButton(RoboRally roboRally) {
        // Back button
        selection_back.setOnMouseClicked(e -> {
            roboRally.goToMainMenu();
        });
    }

    /**
     * creates the selection menu
     * @author Maximillian Bjørn Mortensen
     */
    @FXML
    public void initialize() {
        // Courses
        Platform.runLater(() -> {
            int scrollPaneSize = (int)(scrollPaneForMaps.getWidth() - 17);
            for(int i = 1; i < 7; i++){
                mapGraphics.add(ImageUtils.getImageFromName("Courses/Course_" + i + ".png"));
            }
            map.setImage(mapGraphics.getFirst());
            VBox coursesVBox = new VBox();
            for(int i = 0; i < mapGraphics.size(); i++){
                Button b = new Button();
                ImageView courseImageView = new ImageView(mapGraphics.get(i));
                b.setPrefWidth(scrollPaneSize);
                b.setPrefHeight(scrollPaneSize);
                b.setGraphic(courseImageView);
                int temp = i;
                b.setOnMouseClicked(e -> {
                    map.setImage(mapGraphics.get(temp));
                    mapIndex = temp;
                });
                coursesVBox.getChildren().add(b);
            }
            scrollPaneForMaps.setContent(coursesVBox);
        });

        // Players
        playerHBoxes = new HBox[6];
        int playerIndex = 0;
        for (int i = 0; i < 3; i++) {
            HBox playerRow = (HBox)playersVBox.getChildren().get(i);
            for (int j = 0; j < 2; j++) {
                playerHBoxes[playerIndex] = (HBox)playerRow.getChildren().get(j);
                playerIndex++;
            }
        }
        for (int i = 0; i < 6; i++) {
            int localI = i;
            for (Node child : playerHBoxes[localI].getChildren()) {
                // Robot image
                if (child instanceof ImageView robotImageView) {
                    playerRobotImageViews[localI] = robotImageView;
                }
                if (child instanceof VBox playerVBox) {
                    for (Node grandChild : playerVBox.getChildren()) {
                        // Name input
                        if (grandChild instanceof TextField nameInput) {
                            nameInput.setOnKeyReleased(e -> {
                                playerNames[localI] = nameInput.getText();
                                updateUI();
                            });
                        }
                        // Player robot
                        if (grandChild instanceof ComboBox chosenCharacter) {
                            List<String> robotNames = Arrays.stream(Robots.values())
                                    .map(Robots::getRobotName)
                                    .toList();
                            chosenCharacter.getItems().addAll(robotNames);
                            chosenCharacter.valueProperty().addListener((obs, oldValue, newValue) -> {
                                String name = (String) chosenCharacter.getSelectionModel().getSelectedItem();
                                String robotImageName = Robots.getRobotByName(name).getSelectionImageName();
                                playerRobotImageViews[localI].setImage(ImageUtils.getImageFromName(robotImageName));
                                playerCharacters[localI] = name;
                                updateUI();
                            });
                            charSelection.add(chosenCharacter);
                        }
                    }
                }
            }
        }

        // GameSettings
        // Number of players
        settings_noOfPlayers.getItems().addAll(OPTIONS_NO_OF_PLAYERS);
        settings_noOfPlayers.getSelectionModel().select(0);
        settings_noOfPlayers.setOnAction(e -> {
            NO_OF_PLAYERS = (int)(settings_noOfPlayers.getSelectionModel().getSelectedItem());
            updateUI();
        });

        // Keep hand
        settings_keepHand.getItems().addAll(OPTIONS_KEEP_HAND);
        settings_keepHand.getSelectionModel().select(0);
        settings_keepHand.setOnAction(e -> {
            String keepHandString = settings_keepHand.getSelectionModel().getSelectedItem().toString();
            KEEP_HAND = keepHandString.equals("Yes");
            updateUI();
        });

        updateUI();
    }

    private void updateUI() {
        for (int i = 2; i < 6; i++) {
            playerHBoxes[i].setVisible(i < NO_OF_PLAYERS);
            if (i >= NO_OF_PLAYERS) {
                playerNames[i] = null;
                playerCharacters[i] = null;
                //charSelection.get(i).getSelectionModel().clearSelection();
            }
        }
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

    /**
     * checks to see if the userinputs to start the game is valid
     * @return boolean
     * @author Maximillian Bjørn Mortensen
     */
    private boolean isReady(){
        for(int i = 0; i < NO_OF_PLAYERS; i++){
            if(playerNames[i] == null || playerCharacters[i] == null) return false;
            for(int j = i-1; j >= 0; j--){
                if(playerNames[i].equals(playerNames[j])) return false;
                if(playerCharacters[i].equals(playerCharacters[j])) return false;
            }
        }
        return true;
    }
}
