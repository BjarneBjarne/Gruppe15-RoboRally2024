package com.gruppe15.view;

import com.gruppe15.RoboRally;
import com.gruppe15.controller.AppController;
import com.gruppe15.coursecreator.CC_CourseData;
import com.gruppe15.model.Robots;
import com.gruppe15.exceptions.NoCoursesException;
import com.gruppe15.model.lobby.LobbyData;
import com.gruppe15.model.lobby.LobbyPlayerSlot;
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
import java.util.Objects;

import static com.gruppe15.BoardOptions.*;

/**
 * @author Maximillian Bjørn Mortensen
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class MultiplayerMenuView {
    @FXML
    Button multiplayerMenuButtonBack;

    // "Join or host" menu
    @FXML
    StackPane multiplayerMenuPaneJoinOrHost;
    @FXML
    TextField multiplayerMenuTextFieldPlayerName;
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

    // LobbyData menu
    @FXML
    StackPane multiplayerMenuLobbyPane;
    @FXML
    Text lobbyTextGameID;
    @FXML
    VBox lobbyVBoxLocalPlayer;
    @FXML
    HBox lobbyHBoxProxyPlayers;
    @FXML
    ScrollPane lobbyCoursesScrollPane;
    @FXML
    VBox lobbyCoursesVBox;
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

    private final List<CC_CourseData> courses = new ArrayList<>();
    private final LobbyPlayerSlot[] playerSlots = new LobbyPlayerSlot[6];

    private LobbyData lobbyData = null;
    private boolean isHost = false;
    CC_CourseData selectedCourse = null;

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

    /**
     *
     * @param appController
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupJoinButton(AppController appController) {
        // Join button
        multiplayerMenuButtonJoin.setOnMouseClicked(e -> {
            if(!multiplayerMenuTextFieldGameID.getText().isBlank()) {
                setConnectionInfo("Attempting to connect to lobbyData...");
                appController.tryJoinLobbyWithGameID(Long.parseLong(multiplayerMenuTextFieldGameID.getText()), multiplayerMenuTextFieldPlayerName.getText());
            }
        });
    }

    /**
     *
     * @param appController
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupHostButton(AppController appController) {
        // Join button
        multiplayerMenuButtonHost.setOnMouseClicked(e -> {
            setConnectionInfo("Waiting for server...");
            appController.tryHostNewLobby(multiplayerMenuTextFieldPlayerName.getText());
        });
    }

    /**
     *
     * @param appController
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupStartButton(AppController appController) {
        // Start button
        lobbyButtonStart.setOnMouseClicked(e -> {
            if(isReady() && isHost) {
                appController.beginCourse(selectedCourse, lobbyData);
            }
        });
    }

    /**
     *
     * @param roboRally
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupBackButton(RoboRally roboRally) {
        // Back button
        multiplayerMenuButtonBack.setOnMouseClicked(e -> {
            roboRally.goToMainMenu();
        });
    }

    /**
     * Sets up the UI from the lobbyData object received. Called when the user connects to the server.
     * @param lobbyData The lobbyData object received from the server.
     * @param loadedCourses The courses loaded from the courses folder.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupLobby(AppController appController, LobbyData lobbyData, List<CC_CourseData> loadedCourses) {
        isHost = lobbyData.gethName().equals(lobbyData.getPlayerName());
        initializeCourses(appController, loadedCourses);
        initializeLobby(appController);
        lobbyTextGameID.setText("Game ID: " + lobbyData.getGameId());
        playerSlots[0].setName(lobbyData.getPlayerName());

        updateLobby(lobbyData);

        setConnectionInfo("");
        showLobby(true);
    }

    public void updateLobby(LobbyData lobbyData) {
        this.lobbyData = lobbyData;

        // Course
        for (CC_CourseData course : courses) {
            if (course.getCourseName().equals(lobbyData.getMap())) {
                selectedCourse = course;
                break;
            }
        }
        if (selectedCourse != null) {
            lobbySelectedCourseImageView.setImage(selectedCourse.getImage());
            lobbySelectedCourseText.setText(selectedCourse.getCourseName().toUpperCase());
        }

        // Players
        // Local
        playerSlots[0].setHostStarVisible(lobbyData.getPlayerName().equals(lobbyData.gethName()));
        playerSlots[0].setReadyCheckVisible(lobbyData.getIsReady() == 1);

        // Proxy
        NO_OF_PLAYERS = lobbyData.getPlayerNames().length;
        int proxyPlayerSlotIndex = 1;
        for (int i = 0; (i < playerSlots.length) && (i < lobbyData.getPlayerNames().length); i++) {
            String playerName = lobbyData.getPlayerNames()[i];
            if (!playerName.equals(playerSlots[0].getName())) {
                // Name
                playerSlots[proxyPlayerSlotIndex].setName(playerName);
                // Robot
                if (lobbyData.getRobots()[i] != null && !lobbyData.getRobots()[i].isBlank()) {
                    playerSlots[proxyPlayerSlotIndex].setRobot(Objects.requireNonNull(Robots.getRobotByName(lobbyData.getRobots()[i])));
                } else {
                    playerSlots[proxyPlayerSlotIndex].setRobot(null);
                }
                // Host star
                playerSlots[proxyPlayerSlotIndex].setHostStarVisible(lobbyData.getPlayerNames()[i].equals(lobbyData.gethName()));
                playerSlots[proxyPlayerSlotIndex].setReadyCheckVisible(lobbyData.getAreReady()[i] == 1);

                proxyPlayerSlotIndex++;
            }
        }
        updateUI();
    }

    /**
     * Initializes the lobbyData nodes to make it ready to process lobbyData data from the server.
     * @author Maximillian Bjørn Mortensen
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeLobby(AppController appController) {
        lobbyCoursesScrollPane.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        lobbyCoursesVBox.setBackground(new Background(new BackgroundFill(
                Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
        lobbyCoursesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        lobbyCoursesVBox.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");

        // Local player
        {
            ImageView localPlayerHostStarImageView = null;
            Text localPlayerNameText = null;
            ImageView localPlayerReadyCheckImageView = null;
            ImageView localPlayerRobotImageView = null;
            ComboBox<String> localPlayerRobotComboBox = null;
            for (Node localPlayerNode : lobbyVBoxLocalPlayer.getChildren()) {
                if (localPlayerNode instanceof HBox hBox) {
                    if (hBox.getChildren().get(0) instanceof ImageView imageView) {
                        localPlayerHostStarImageView = imageView;
                    }
                    if (hBox.getChildren().get(1) instanceof Text text) {
                        localPlayerNameText = text;
                    }
                    if (hBox.getChildren().get(2) instanceof ImageView imageView) {
                        localPlayerReadyCheckImageView = imageView;
                    }
                }
                if (localPlayerNode instanceof ImageView imageView) {
                    localPlayerRobotImageView = imageView;
                }
                if (localPlayerNode instanceof ComboBox comboBox) {
                    localPlayerRobotComboBox = comboBox;
                }
            }
            if (localPlayerHostStarImageView == null || localPlayerNameText == null || localPlayerReadyCheckImageView == null || localPlayerRobotImageView == null || localPlayerRobotComboBox == null) {
                new Exception("One or more PlayerSlot UI elements could not be instantiated for the local player.").printStackTrace();
            } else {
                playerSlots[0] = new LobbyPlayerSlot(localPlayerNameText, localPlayerRobotImageView, null, localPlayerHostStarImageView, localPlayerReadyCheckImageView);

                List<String> robotNames = Arrays.stream(Robots.values())
                        .map(Robots::getRobotName)
                        .toList();
                localPlayerRobotComboBox.getItems().addAll(robotNames);
                ComboBox<String> finalLocalPlayerRobotComboBox = localPlayerRobotComboBox;
                localPlayerRobotComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
                    String localRobotName = finalLocalPlayerRobotComboBox.getSelectionModel().getSelectedItem();
                    Robots localRobot = Robots.getRobotByName(localRobotName);
                    if (localRobot != null) {
                        playerSlots[0].setRobot(localRobot);
                        appController.changeRobot(localRobot);
                    }
                    updateUI();
                });
            }
        }

        // Proxy players
        for (int i = 1; i < lobbyHBoxProxyPlayers.getChildren().size(); i++) {
            ImageView proxyPlayerHostStarImageView = null;
            Text proxyPlayerNameText = null;
            ImageView proxyPlayerReadyCheckImageView = null;
            ImageView proxyPlayerRobotImageView = null;
            Text proxyPlayerRobotNameText = null;

            if (lobbyHBoxProxyPlayers.getChildren() instanceof VBox proxyPlayerVBox) {
                for (Node proxyPlayerNode : proxyPlayerVBox.getChildren()) {
                    if (proxyPlayerNode instanceof HBox hBox) {
                        if (hBox.getChildren().get(0) instanceof ImageView imageView) {
                            proxyPlayerHostStarImageView = imageView;
                        }
                        if (hBox.getChildren().get(1) instanceof Text text) {
                            proxyPlayerNameText = text;
                        }
                        if (hBox.getChildren().get(2) instanceof ImageView imageView) {
                            proxyPlayerReadyCheckImageView = imageView;
                        }
                    }
                    if (proxyPlayerNode instanceof ImageView imageView) {
                        proxyPlayerRobotImageView = imageView;
                    }
                    if (proxyPlayerNode instanceof Text text) {
                        proxyPlayerRobotNameText = text;
                    }
                }

                if (proxyPlayerHostStarImageView == null) System.out.println("proxyPlayerHostStarImageView is null.");
                if (proxyPlayerNameText == null) System.out.println("proxyPlayerNameText is null.");
                if (proxyPlayerReadyCheckImageView == null) System.out.println("proxyPlayerReadyCheckImageView is null.");
                if (proxyPlayerRobotImageView == null) System.out.println("proxyPlayerRobotImageView is null.");
                if (proxyPlayerRobotNameText == null) System.out.println("proxyPlayerRobotNameText is null.");


                playerSlots[i] = new LobbyPlayerSlot(proxyPlayerNameText, proxyPlayerRobotImageView, proxyPlayerRobotNameText, proxyPlayerHostStarImageView, proxyPlayerReadyCheckImageView);
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

    /**
     *
     * @param loadedCourses
     * @author Maximillian Bjørn Mortensen
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeCourses(AppController appController, List<CC_CourseData> loadedCourses) {
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

                    // Course buttons OnMouseClicked
                    courseButton.setOnMouseClicked(e -> {
                        if (isHost) {
                            appController.changeCourse(course.getCourseName());
                        }
                    });
                    courseButton.setDisable(!isHost);
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

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateUI() {
        for (int i = 1; i < 6; i++) {
            playerSlots[i].setVisible(i < NO_OF_PLAYERS);
        }
        if (isReady()) {
            lobbyButtonStart.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
            lobbyButtonStart.setStyle("-fx-background-color:  #3a993c60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        } else {
            lobbyButtonStart.setStyle("-fx-background-color:  #993a3a60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        }
    }

    /**
     * Checks whether the conditions to start the game is met.
     * @return boolean
     * @author Maximillian Bjørn Mortensen
     */
    private boolean isReady() {
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            if (lobbyData.getPlayerNames()[i].isBlank() || lobbyData.getRobots()[i] == null) return false;

            for (int j = i - 1; j >= 0; j--) {
                if(lobbyData.getPlayerNames()[i].equals(lobbyData.getPlayerNames()[j])) return false;
                if(lobbyData.getRobots()[i].equals(lobbyData.getRobots()[j])) return false;
            }
        }
        if (courses.isEmpty()) return false;
        if (selectedCourse == null) return false;

        if (lobbyData.getIsReady() == 0) return false;
        for (int i = 0; i < lobbyData.getAreReady().length; i++) {
            if (lobbyData.getAreReady()[i] == 0) return false;
        }

        return true;
    }

    /**
     *
     * @param showLobby
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void showLobby(boolean showLobby) {
        multiplayerMenuLobbyPane.setVisible(showLobby);
        multiplayerMenuLobbyPane.setDisable(!showLobby);

        multiplayerMenuPaneJoinOrHost.setVisible(!showLobby);
        multiplayerMenuPaneJoinOrHost.setDisable(showLobby);
    }

    /**
     * Method for showing the connection status to the player.
     * @param connectionInfo If blank, hides the connection info pane. If not, shows the pane and sets the connection info text to connectionInfo.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
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
