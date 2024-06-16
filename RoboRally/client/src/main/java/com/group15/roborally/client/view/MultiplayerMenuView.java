package com.group15.roborally.client.view;

import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.model.Robots;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.model.lobby.LobbyPlayerSlot;
import com.group15.roborally.server.model.Game;
import com.group15.roborally.server.model.Player;
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

import static com.group15.roborally.client.BoardOptions.*;

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

    // LobbyClientUpdate menu
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

    private Game game;
    private Player localPlayer;
    private List<Player> players;
    private boolean isHost = false;
    private CC_CourseData selectedCourse = null;

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
     * Sets up the UI from the lobbyData object received. Called when the user connects to the server.
     * @param game The game object received from the server.
     * @param loadedCourses The courses loaded from the courses folder.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupLobby(AppController appController, Game game, Player localPlayer, List<Player> players, List<CC_CourseData> loadedCourses) {
        this.game = game;
        this.localPlayer = localPlayer;
        this.players = players;
        if (localPlayer.getPlayerId() == game.getHostId()) {
            this.isHost = true;
        }

        initializeCourses(appController, loadedCourses);
        initializeLobby(appController);
        lobbyTextGameID.setText("Game ID: " + game.getGameId());
        playerSlots[0].setName(localPlayer.getPlayerName());

        updateLobby(game, players);

        setConnectionInfo("");
        showLobby(true);

        updateUI();
    }

    public void failedToConnect() {
        setConnectionInfo("");
        showLobby(false);
    }

    public void updateLobby(Game game, List<Player> players) {
        // Variables
        this.game = game;
        this.players = players;
        NO_OF_PLAYERS = game.getNrOfPlayers();

        // Course
        for (CC_CourseData course : courses) {
            if (course.getCourseName().equals(game.getCourseName())) {
                selectedCourse = course;
                break;
            }
        }
        if (selectedCourse != null) {
            lobbySelectedCourseImageView.setImage(selectedCourse.getImage());
            lobbySelectedCourseText.setText(selectedCourse.getCourseName().toUpperCase());
        } else {
            lobbySelectedCourseImageView.setImage(null);
            lobbySelectedCourseText.setText("Selected course");
        }

        players.forEach(player -> {

        });

        // Players
        int slotIndexer = 1;
        for (int playerIndex = 0; playerIndex < NO_OF_PLAYERS; playerIndex++) {
            int slotIndex = slotIndexer;
            boolean isLocalPlayer = players.get(playerIndex).getPlayerId() == localPlayer.getPlayerId();
            if (isLocalPlayer) {
                slotIndex = 0;
            }
            playerSlots[slotIndex].setName(players.get(playerIndex).getPlayerName());
            playerSlots[slotIndex].setRobotByRobotName(players.get(playerIndex).getRobotName());
            boolean playerIsHost = players.get(playerIndex).getPlayerId() == game.getHostId();
            playerSlots[slotIndex].setHostStarVisible(playerIsHost);
            playerSlots[slotIndex].setReadyCheckVisible(players.get(playerIndex).getIsReady() == 1);
            if (!isLocalPlayer) {
                slotIndexer++;
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
        playerSlots[0] = getNewPlayerSlotFromParentNode(lobbyVBoxLocalPlayer);
        ComboBox<String> localPlayerRobotComboBox = playerSlots[0].getRobotComboBox();
        List<String> robotNames = Arrays.stream(Robots.values())
                .map(Robots::getRobotName)
                .toList();
        localPlayerRobotComboBox.getItems().addAll(robotNames);
        localPlayerRobotComboBox.valueProperty().addListener((obs, oldValue, newValue) -> {
            String localRobotName = localPlayerRobotComboBox.getSelectionModel().getSelectedItem();
            playerSlots[0].setRobotByRobotName(localRobotName);
            appController.changeRobot(localPlayer, localRobotName);
            updateUI();
        });
        if (lobbyButtonStart.getChildrenUnmodifiable().getFirst() instanceof StackPane stackPane) {
            if (stackPane.getChildren().getFirst() instanceof Text text) {
                String buttonText = "Ready";
                if (isHost) {
                    buttonText = "Start";
                }
                text.setText(buttonText);
            }
        }

        // Proxy players
        for (int i = 0; i < lobbyHBoxProxyPlayers.getChildren().size(); i++) {
            if (lobbyHBoxProxyPlayers.getChildren().get(i) instanceof VBox proxyPlayerVBox) {
                playerSlots[i + 1] = getNewPlayerSlotFromParentNode(proxyPlayerVBox);
            }
        }

        // BoardOptions
        // Keep hand
        lobbySettingsKeepHand.getItems().addAll(OPTIONS_KEEP_HAND);
        lobbySettingsKeepHand.getSelectionModel().select(1);
        lobbySettingsKeepHand.setOnAction(e -> {
            String keepHandString = lobbySettingsKeepHand.getSelectionModel().getSelectedItem();
            KEEP_HAND = keepHandString.equals("Yes");
            updateUI();
        });

        // Draw on empty register
        lobbySettingsDrawOnEmpty.getItems().addAll(OPTIONS_DRAW_ON_EMPTY_REGISTER);
        lobbySettingsDrawOnEmpty.getSelectionModel().select(0);
        lobbySettingsDrawOnEmpty.setOnAction(e -> {
            String keepHandString = lobbySettingsDrawOnEmpty.getSelectionModel().getSelectedItem();
            DRAW_ON_EMPTY_REGISTER = keepHandString.equals("Yes");
            updateUI();
        });
    }

    private LobbyPlayerSlot getNewPlayerSlotFromParentNode(VBox playerVBox) {
        ImageView hostStarImageView = null;
        Text nameText = null;
        ImageView readyCheckImageView = null;
        ImageView robotImageView = null;
        Text proxyPlayerRobotNameText = null;
        ComboBox<String> localPlayerRobotComboBox = null;

        for (Node playerNode : playerVBox.getChildren()) {
            if (playerNode instanceof HBox hBox) {
                if (hBox.getChildren().get(0) instanceof ImageView imageView) {
                    hostStarImageView = imageView;
                }
                if (hBox.getChildren().get(1) instanceof Text text) {
                    nameText = text;
                }
                if (hBox.getChildren().get(2) instanceof ImageView imageView) {
                    readyCheckImageView = imageView;
                }
            }
            if (playerNode instanceof ImageView imageView) {
                robotImageView = imageView;
            }
            if (playerNode instanceof Text text) {
                proxyPlayerRobotNameText = text;
            }
            if (playerNode instanceof ComboBox<?> comboBox) {
                @SuppressWarnings("unchecked")
                ComboBox<String> castComboBox = (ComboBox<String>) comboBox;
                localPlayerRobotComboBox = castComboBox;
            }
        }

        if (hostStarImageView == null || nameText == null || readyCheckImageView == null || robotImageView == null || (proxyPlayerRobotNameText == null && localPlayerRobotComboBox == null)) {
            System.out.println("One or more PlayerSlot UI elements could not be instantiated for a player.");
        }
        return new LobbyPlayerSlot(playerVBox, hostStarImageView, nameText, readyCheckImageView, robotImageView, proxyPlayerRobotNameText, localPlayerRobotComboBox);
    }

    /**
     * @param loadedCourses The courses loaded from the courses folder in resources.
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
                for (CC_CourseData course : courses) {
                    // Course name text
                    Text courseNameText = getCourseButtonText(textFont, course, courseButtonSize);

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
                            appController.changeCourse(game, course);
                        }
                    });
                    courseButton.setDisable(!isHost);
                    lobbyCoursesVBox.getChildren().add(newCourseVBox);
                }
            } else {
                System.out.println(new NoCoursesException().getMessage());
            }
        });
    }

    private Text getCourseButtonText(Font textFont, CC_CourseData course, int courseButtonSize) {
        Text courseNameText = new Text();
        courseNameText.setFont(textFont);
        courseNameText.setFill(Color.WHITE);
        courseNameText.setStroke(Color.BLACK);
        courseNameText.setStrokeWidth(2);
        courseNameText.setStrokeType(StrokeType.OUTSIDE);
        courseNameText.setText(course.getCourseName().toUpperCase());
        courseNameText.setWrappingWidth(courseButtonSize);
        courseNameText.setTextAlignment(TextAlignment.CENTER);
        return courseNameText;
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupMenuUI(AppController appController) {
        // GameId TextField
        multiplayerMenuTextFieldGameID.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        // Join button
        multiplayerMenuButtonJoin.setOnMouseClicked(e -> {
            if(!multiplayerMenuTextFieldGameID.getText().isBlank() && !multiplayerMenuTextFieldPlayerName.getText().isBlank()) {
                setConnectionInfo("Attempting to connect to lobbyData...");
                appController.tryJoinGameWithGameID(Long.parseLong(multiplayerMenuTextFieldGameID.getText()), multiplayerMenuTextFieldPlayerName.getText());
            }
        });

        // Host button
        multiplayerMenuButtonHost.setOnMouseClicked(e -> {
            if (!multiplayerMenuTextFieldPlayerName.getText().isBlank()) {
                setConnectionInfo("Waiting for server...");
                appController.tryCreateAndJoinGame(multiplayerMenuTextFieldPlayerName.getText());
            }
        });

        // Ready/Start button
        lobbyButtonStart.setOnMouseClicked(e -> {
            if (isHost) {
                appController.beginCourse(selectedCourse, players);
            } else if (canReadyOrStart()) {
                // Toggling whether the player is ready.
                int isReady = localPlayer.getIsReady() == 0 ? 1 : 0;
                localPlayer.setIsReady(isReady);
                appController.setIsReady(localPlayer, isReady);
            }
        });
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupBackButton(Runnable backMethod) {
        // Back button
        multiplayerMenuButtonBack.setOnMouseClicked(e -> {
            backMethod.run();
        });
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateUI() {
        for (int i = 0; i < 6; i++) {
            playerSlots[i].setVisible(i < NO_OF_PLAYERS);
        }
        if (canReadyOrStart()) {
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
    private boolean canReadyOrStart() {
        if (localPlayer.getPlayerName() == null || localPlayer.getRobotName() == null || localPlayer.getPlayerName().isBlank() || localPlayer.getRobotName().isBlank() || Robots.getRobotByName(localPlayer.getRobotName()) == null) return false;
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            if (players.get(i).getPlayerId() != localPlayer.getPlayerId()) {
                if (localPlayer.getPlayerName().equals(players.get(i).getPlayerName())) return false;
                if (localPlayer.getRobotName().equals(players.get(i).getRobotName())) return false;
            }
        }
        if (isHost) {
            if (courses.isEmpty()) return false;
            if (selectedCourse == null) return false;
            for (int i = 0; i < NO_OF_PLAYERS; i++) {
                if (players.get(i).getPlayerId() != localPlayer.getPlayerId()) {
                    if (players.get(i).getIsReady() == 0) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
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
        if (connectionInfo == null || connectionInfo.isBlank()) {
            multiplayerMenuPaneConnectionInfo.setVisible(false);
            multiplayerMenuPaneConnectionInfo.setDisable(true);
        } else {
            multiplayerMenuTextConnectionInfo.setText(connectionInfo);
            multiplayerMenuPaneConnectionInfo.setVisible(true);
            multiplayerMenuPaneConnectionInfo.setDisable(false);
        }
    }

    public Game getCurrentGameData() {
        return game;
    }
    public Player getCurrentLocalPlayer() {
        return localPlayer;
    }
}
