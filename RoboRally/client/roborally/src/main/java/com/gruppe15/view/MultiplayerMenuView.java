package com.gruppe15.view;

import com.gruppe15.RoboRally;
import com.gruppe15.controller.AppController;
import com.gruppe15.coursecreator.CC_CourseData;
import com.gruppe15.model.Robots;
import com.gruppe15.exceptions.NoCoursesException;
import com.gruppe15.utils.ImageUtils;
import com.gruppe15.utils.TextUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.gruppe15.BoardOptions.*;

/**
 * @author Maximillian Bjørn Mortensen
 */
public class MultiplayerMenuView {
    private final ImageView[] playerRobotImageViews = new ImageView[6];
    private final HBox[] playerHBoxes = new HBox[6];
    private final List<CC_CourseData> courses = new ArrayList<>();

    @FXML
    Button multiplayerMenuButtonBack;

    // "Join or host" menu
    @FXML
    StackPane multiplayerMenuPaneJoinOrHost;
    @FXML
    TextField multiplayerMenuTextFieldGameID;
    @FXML
    Button multiplayerMenuButtonJoin;
    @FXML
    Button multiplayerMenuButtonHost;
    @FXML
    StackPane multiplayerMenuPaneConnectionInfo;
    @FXML
    Text multiplayerMenuTextConnectionInfo;

    // Lobby menu
    @FXML
    StackPane multiplayerMenuLobbyPane;
    @FXML
    Text lobbyTextGameID;
    @FXML
    ScrollPane lobbyCoursesScrollPane;
    @FXML
    VBox lobbyCoursesVBox;
    @FXML
    VBox lobbyPlayersVBox;
    @FXML
    ImageView lobbySelectedCourseImageView;
    @FXML
    Button lobbyButtonStart;
    @FXML
    Text lobbySelectedCourseText;

    @FXML
    ComboBox<String> lobbySettingsKeepHand;
    @FXML
    ComboBox<String> lobbySettingsDrawOnEmpty;


    private int mapIndex = 0;
    private final String[] playerNames = {"", "", "", "", "", ""};
    private final String[] playerCharacters = new String[6];

    public MultiplayerMenuView() {

    }

    /**
     * Initializes the multiplayer menu.
     */
    @FXML
    public void initialize() {
        showLobby(false);
        setConnectionInfo("");
    }

    public void setupJoinButton(AppController appController) {
        // Join button
        multiplayerMenuButtonJoin.setOnMouseClicked(e -> {
            if(!multiplayerMenuTextFieldGameID.getText().isBlank()) {
                setConnectionInfo("Attempting to connect to lobby...");
                appController.tryJoinLobbyWithGameID(multiplayerMenuTextFieldGameID.getText());
            }
        });
    }

    public void setupHostButton(AppController appController) {
        // Join button
        multiplayerMenuButtonHost.setOnMouseClicked(e -> {
            setConnectionInfo("Waiting for server...");
            appController.tryHostNewLobby();
        });
    }

    public void setupStartButton(AppController appController) {
        // Start button
        lobbyButtonStart.setOnMouseClicked(e -> {
            if(isReady()) {
                appController.beginCourse(appController.getCourses().get(mapIndex), playerNames, playerCharacters);
            }
        });
    }

    public void setupBackButton(RoboRally roboRally) {
        // Back button
        multiplayerMenuButtonBack.setOnMouseClicked(e -> {
            roboRally.goToMainMenu();
        });
    }

    public void setupLobby(boolean isHost, String gameID) {
        initializeLobby();
        lobbyTextGameID.setText("Game ID: " + gameID);
        setConnectionInfo("");
        showLobby(true);

        if (isHost) {

        } else {

        }
    }

    /**
     * Initializes the lobby.
     * @author Maximillian Bjørn Mortensen
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeLobby() {
        lobbyCoursesScrollPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        lobbyCoursesVBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        lobbyCoursesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        lobbyCoursesVBox.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");

        // Players
        NO_OF_PLAYERS = 2;
        int playerIndex = 0;
        for (int i = 0; i < 3; i++) {
            HBox playerRow = (HBox) lobbyPlayersVBox.getChildren().get(i);
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
                            Platform.runLater(() -> {
                                String tempName = "Player " + (localI + 1);
                                playerNames[localI] = tempName;
                                nameInput.setText(tempName);
                            });
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
                                String localName = (String) chosenCharacter.getSelectionModel().getSelectedItem();
                                String localRobotImageName = Robots.getRobotByName(localName).getSelectionImageName();
                                playerRobotImageViews[localI].setImage(ImageUtils.getImageFromName(localRobotImageName));
                                playerCharacters[localI] = localName;
                                updateUI();
                            });
                            // Set next character as default.
                            Platform.runLater(() -> {
                                chosenCharacter.getSelectionModel().select(localI);
                                String robotName = (String) chosenCharacter.getSelectionModel().getSelectedItem();
                                String robotImageName = Robots.getRobotByName(robotName).getSelectionImageName();
                                playerRobotImageViews[localI].setImage(ImageUtils.getImageFromName(robotImageName));
                                playerCharacters[localI] = robotName;
                            });
                        }
                    }
                }
            }
        }

        // BoardOptions
        // Keep hand
        lobbySettingsKeepHand.getItems().addAll(OPTIONS_KEEP_HAND);
        lobbySettingsKeepHand.getSelectionModel().select(1);
        lobbySettingsKeepHand.setOnAction(e -> {
            String keepHandString = lobbySettingsKeepHand.getSelectionModel().getSelectedItem().toString();
            KEEP_HAND = keepHandString.equals("Yes");
            updateUI();
        });

        // Draw on empty register
        lobbySettingsDrawOnEmpty.getItems().addAll(OPTIONS_DRAW_ON_EMPTY_REGISTER);
        lobbySettingsDrawOnEmpty.getSelectionModel().select(0);
        lobbySettingsDrawOnEmpty.setOnAction(e -> {
            String keepHandString = lobbySettingsDrawOnEmpty.getSelectionModel().getSelectedItem().toString();
            DRAW_ON_EMPTY_REGISTER = keepHandString.equals("Yes");
            updateUI();
        });
        updateUI();
    }

    public void initializeCourses(List<CC_CourseData> loadedCourses) {
        // Courses
        Platform.runLater(() -> {
            courses.addAll(loadedCourses);

            int courseButtonSize = (int)(lobbyCoursesVBox.getWidth() - lobbyCoursesVBox.getPadding().getLeft() - lobbyCoursesVBox.getPadding().getRight());
            Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 32);

            // Making course button
            if (!courses.isEmpty()) {
                for(int i = 0; i < courses.size(); i++){
                    CC_CourseData course = courses.get(i);

                    // Course name text
                    Text courseNameText = new Text();
                    courseNameText.setFont(textFont);
                    courseNameText.setFill(Color.WHITE);
                    courseNameText.setStroke(Color.BLACK);
                    courseNameText.setStrokeWidth(2);
                    courseNameText.setStrokeType(StrokeType.OUTSIDE);
                    courseNameText.setText(course.getCourseName().toUpperCase());
                    courseNameText.setWrappingWidth(courseButtonSize);
                    courseNameText.setTextAlignment(TextAlignment.CENTER);

                    // Button
                    Button courseButton = new Button();
                    ImageView courseImageView = new ImageView(course.getImage());
                    courseButton.setMinSize(courseButtonSize, courseButtonSize);
                    courseButton.setPrefSize(courseButtonSize, courseButtonSize);
                    courseButton.setMaxSize(courseButtonSize, courseButtonSize);
                    courseImageView.setFitWidth(courseButtonSize);
                    courseImageView.setFitHeight(courseButtonSize);
                    courseButton.setGraphic(courseImageView);
                    courseButton.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

                    // VBox
                    VBox newCourseVBox = new VBox(courseNameText, courseButton);
                    newCourseVBox.setSpacing(5);
                    newCourseVBox.setAlignment(Pos.CENTER);

                    // Buttons OnMouseClicked
                    int courseIndex = i;
                    courseButton.setOnMouseClicked(e -> {
                        lobbySelectedCourseImageView.setImage(courses.get(courseIndex).getImage());
                        mapIndex = courseIndex;
                        lobbySelectedCourseText.setText(course.getCourseName().toUpperCase());
                    });
                    lobbyCoursesVBox.getChildren().add(newCourseVBox);
                }
                lobbySelectedCourseText.setText(courses.getFirst().getCourseName().toUpperCase());
                lobbySelectedCourseImageView.setImage(courses.getFirst().getImage());
            } else {
                System.out.println(new NoCoursesException().getMessage());
            }
            updateUI();
        });
    }

    private void updateUI() {
        for (int i = 2; i < 6; i++) {
            playerHBoxes[i].setVisible(i < NO_OF_PLAYERS);
            /*if (i >= NO_OF_PLAYERS) {
                playerNames[i] = "";
                playerCharacters[i] = "";
                //charSelection.get(i).getSelectionModel().clearSelection();
            }*/
        }
        if (isReady()) {
            lobbyButtonStart.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
            lobbyButtonStart.setStyle("-fx-background-color:  #3a993c60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        } else {
            //start.setStyle("-fx-background-color: lightgray; -fx-text-fill: black; -fx-font-weight: bold;");
            lobbyButtonStart.setStyle("-fx-background-color:  #993a3a60;" +
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
    private boolean isReady() {
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            if (playerNames[i] == null || playerNames[i].isBlank() || playerCharacters[i] == null) return false;

            for (int j = i - 1; j >= 0; j--) {
                if(playerNames[i].equals(playerNames[j])) return false;
                if(playerCharacters[i].equals(playerCharacters[j])) return false;
            }
        }
        if (courses.isEmpty()) return false;
        if (courses.size() <= mapIndex) return false;

        return true;
    }

    public void showLobby(boolean showLobby) {
        multiplayerMenuLobbyPane.setVisible(showLobby);
        multiplayerMenuLobbyPane.setDisable(!showLobby);

        multiplayerMenuPaneJoinOrHost.setVisible(!showLobby);
        multiplayerMenuPaneJoinOrHost.setDisable(showLobby);
    }

    /**
     * Method for showing the connection status to the player.
     * @param connectionInfo If blank, hides the connection info pane. If not, shows the pane and sets the connection info text to connectionInfo.
     */
    private void setConnectionInfo(String connectionInfo) {
        if (connectionInfo.isBlank()) {
            multiplayerMenuPaneConnectionInfo.setVisible(false);
            multiplayerMenuPaneConnectionInfo.setDisable(true);
            multiplayerMenuTextConnectionInfo.setText(connectionInfo);
        } else {
            multiplayerMenuPaneConnectionInfo.setVisible(true);
            multiplayerMenuPaneConnectionInfo.setDisable(false);
        }
    }
}
