package com.group15.roborally.client.view;

import com.group15.roborally.client.model.audio.AudioMixer;
import com.group15.roborally.client.utils.ButtonUtils;
import com.group15.roborally.common.observer.Observer;
import com.group15.roborally.common.observer.Subject;
import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.model.networking.ServerDataManager;
import com.group15.roborally.client.utils.NetworkedDataTypes;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.model.Robots;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.model.lobby.LobbyPlayerSlot;
import com.group15.roborally.common.model.Game;
import com.group15.roborally.common.model.GamePhase;
import com.group15.roborally.common.model.Player;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.List;

import static com.group15.roborally.client.LobbySettings.*;

/**
 * @author Maximillian Bjørn Mortensen
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class MultiplayerMenuView implements Observer {
    private final AppController appController;
    private final ServerDataManager serverDataManager;

    @FXML
    Button multiplayerMenuButtonBack;

    // "Join or host" menu
    @FXML
    StackPane multiplayerMenuPaneJoinOrHost;
    @FXML
    TextField multiplayerMenuTextFieldPlayerName;
    @FXML
    TextField multiplayerMenuTextFieldServerURL;
    @FXML
    TextField multiplayerMenuTextFieldGameID;
    @FXML
    Button multiplayerMenuButtonJoin;
    @FXML
    Button multiplayerMenuButtonHost;

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
    private Map<Long, Player> players;

    private boolean hasStartedGameLocally = false; // Condition to keep the application from starting the game more than once per lobby.
    @Getter
    private CC_CourseData selectedCourse = null;

    private boolean hasBeenSetup = false;

    public MultiplayerMenuView(AppController appController, ServerDataManager serverDataManager) {
        this.appController = appController;
        this.serverDataManager = serverDataManager;
        this.serverDataManager.attach(this);
    }

    /**
     * Initializes the multiplayer menu.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    @FXML
    public void initialize() {
        showLobby(false);

        hasStartedGameLocally = false;
        selectedCourse = null;
    }

    private void setupLobby() {
        this.game = serverDataManager.getUpdatedGame();
        this.players = serverDataManager.getUpdatedPlayerMap();

        hasBeenSetup =  true;
        initializeCourses();
        initializeLobby();
        updateLobby(this.game, this.players);
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void updateLobby(Game updatedGame, Map<Long, Player> updatedPlayers) {
        // Game
        if (updatedGame != null) {
            // GameId
            lobbyTextGameID.setText("Game ID: " + updatedGame.getGameId());
            // If the game had changes, the player gets set to not ready.
            if (updatedGame.hasChanges(this.game)) {
                serverDataManager.setReadyForPhase(GamePhase.LOBBY);
            }
            // Update selected course if it changed
            if (updatedGame.getCourseName() != null && !updatedGame.getCourseName().isBlank() && (this.selectedCourse == null || !updatedGame.getCourseName().equals(this.selectedCourse.getCourseName()))) {
                this.selectedCourse = AppController.getCourses().stream()
                        .filter(course -> course.getCourseName().equals(updatedGame.getCourseName()))
                        .findFirst()
                        .orElse(null);
            }
            // Course
            CC_CourseData updatedSelectedCourse = selectedCourse;
            if (updatedSelectedCourse != null) {
                lobbySelectedCourseImageView.setImage(updatedSelectedCourse.getImage());
                lobbySelectedCourseText.setText(updatedSelectedCourse.getCourseName().toUpperCase());
            } else {
                lobbySelectedCourseImageView.setImage(null);
                lobbySelectedCourseText.setText("Selected course");
            }
            this.game = updatedGame;
        }

        // Players
        if (updatedPlayers != null) {
            int slotIndex = 1;
            for (Player player : updatedPlayers.values()) {
                LobbyPlayerSlot playerSlot = playerSlots[slotIndex];
                boolean isLocalPlayer = player.getPlayerId() == serverDataManager.getLocalPlayer().getPlayerId();
                if (isLocalPlayer) {
                    playerSlot = playerSlots[0];
                }
                playerSlot.setName(player.getPlayerName());
                playerSlot.setRobotByRobotName(player.getRobotName());
                boolean playerIsHost = player.getPlayerId() == this.game.getHostId();
                playerSlot.setHostStarVisible(playerIsHost);
                playerSlot.setReadyCheckVisible(player.getReadyForPhase() == GamePhase.INITIALIZATION);
                if (!isLocalPlayer) {
                    slotIndex++;
                }
            }
            this.players = updatedPlayers;
        }

        if (updatedGame != null || updatedPlayers != null) {
            updateUI();
        }
    }

    /**
     * Initializes the lobbyData nodes to make it ready to process lobbyData data from the server.
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

        // Start/ready button
        if (lobbyButtonStart.getChildrenUnmodifiable().getFirst() instanceof StackPane stackPane) {
            if (stackPane.getChildren().getFirst() instanceof Text text) {
                text.setText(serverDataManager.isHost() ? "Start" : "Ready");
            }
        }

        // Local player
        playerSlots[0] = getNewPlayerSlotFromParentNode(lobbyVBoxLocalPlayer);
        ComboBox<String> localPlayerRobotComboBox = playerSlots[0].getRobotComboBox();
        List<String> robotNames = Arrays.stream(Robots.values())
                .map(Robots::getRobotName)
                .toList();
        localPlayerRobotComboBox.getItems().addAll(robotNames);
        localPlayerRobotComboBox.valueProperty().addListener((a, b, c) -> {
            String localRobotName = localPlayerRobotComboBox.getSelectionModel().getSelectedItem();
            playerSlots[0].setRobotByRobotName(localRobotName);
            serverDataManager.changeRobot(localRobotName);
            updateUI();
        });

        // Proxy players
        for (int i = 0; i < lobbyHBoxProxyPlayers.getChildren().size(); i++) {
            if (lobbyHBoxProxyPlayers.getChildren().get(i) instanceof VBox proxyPlayerVBox) {
                playerSlots[i + 1] = getNewPlayerSlotFromParentNode(proxyPlayerVBox);
            }
        }

        // LobbySettings
        // Keep hand
        lobbySettingsKeepHand.getItems().addAll(OPTIONS_KEEP_HAND);
        lobbySettingsKeepHand.getSelectionModel().select(1);
        lobbySettingsKeepHand.setOnAction(a -> {
            String keepHandString = lobbySettingsKeepHand.getSelectionModel().getSelectedItem();
            KEEP_HAND = keepHandString.equals("Yes");
            updateUI();
        });

        // Draw on empty register
        lobbySettingsDrawOnEmpty.getItems().addAll(OPTIONS_DRAW_ON_EMPTY_REGISTER);
        lobbySettingsDrawOnEmpty.getSelectionModel().select(0);
        lobbySettingsDrawOnEmpty.setOnAction(a -> {
            String keepHandString = lobbySettingsDrawOnEmpty.getSelectionModel().getSelectedItem();
            DRAW_ON_EMPTY_REGISTER = keepHandString.equals("Yes");
            updateUI();
        });
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
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
     * @author Maximillian Bjørn Mortensen
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeCourses() {
        // Courses
        Platform.runLater(() -> {
            List<CC_CourseData> loadedCourses = AppController.getCourses();
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
                    ButtonUtils.setupDefaultButton(courseButton, () -> {
                        if (serverDataManager.isHost()) {
                            serverDataManager.changeCourse(course);
                            this.selectedCourse = course;
                            lobbySelectedCourseImageView.setImage(course.getImage());
                            lobbySelectedCourseText.setText(course.getCourseName().toUpperCase());
                            updateUI();
                        }
                    });
                    ImageView courseImageView = new ImageView(course.getImage());
                    courseButton.setMinSize(courseButtonSize, courseButtonSize);
                    courseButton.setPrefSize(courseButtonSize, courseButtonSize);
                    courseButton.setMaxSize(courseButtonSize, courseButtonSize);
                    courseImageView.setFitWidth(courseButtonSize);
                    courseImageView.setFitHeight(courseButtonSize);
                    courseButton.setGraphic(courseImageView);
                    courseButton.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
                    courseButton.setDisable(!serverDataManager.isHost());

                    // VBox
                    VBox newCourseVBox = new VBox(courseNameText, courseButton);
                    newCourseVBox.setSpacing(5);
                    newCourseVBox.setAlignment(Pos.CENTER);

                    lobbyCoursesVBox.getChildren().add(newCourseVBox);
                }
            } else {
                System.out.println(new NoCoursesException().getMessage());
            }
        });
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private Text getCourseButtonText(Font textFont, CC_CourseData course, int courseButtonSize) {
        Text courseNameText = new Text();
        courseNameText.setFont(textFont);
        courseNameText.setFill(Color.WHITE);
        courseNameText.setText(course.getCourseName().toUpperCase());
        courseNameText.setWrappingWidth(courseButtonSize);
        courseNameText.setTextAlignment(TextAlignment.CENTER);
        return courseNameText;
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupMenuUI() {
        // GameId TextField
        multiplayerMenuTextFieldGameID.setTextFormatter(new TextFormatter<>((change) -> {
            change.setText(change.getText().toUpperCase());
            return change;
        }));

        // Join button
        multiplayerMenuButtonJoin.setOnMouseClicked(a -> {
            if(!multiplayerMenuTextFieldGameID.getText().isBlank() && !multiplayerMenuTextFieldPlayerName.getText().isBlank()) {
                onJoinOrHost();
                serverDataManager.tryJoinGameWithGameID(multiplayerMenuTextFieldServerURL.getText(), multiplayerMenuTextFieldGameID.getText(), multiplayerMenuTextFieldPlayerName.getText());
            }
        });

        // Host button
        multiplayerMenuButtonHost.setOnMouseClicked(a -> {
            if (!multiplayerMenuTextFieldPlayerName.getText().isBlank()) {
                onJoinOrHost();
                serverDataManager.tryCreateAndJoinGame(multiplayerMenuTextFieldServerURL.getText(), multiplayerMenuTextFieldPlayerName.getText());
            }
        });

        // Ready/Start button
        lobbyButtonStart.setOnMouseClicked(a -> {
            if (canReadyOrStart()) {
                if (serverDataManager.isHost()) {
                    serverDataManager.setReadyForPhase(GamePhase.INITIALIZATION);
                    serverDataManager.setGamePhase(GamePhase.INITIALIZATION);
                    startGame();
                } else {
                    // Toggling whether the player is ready.
                    GamePhase gamePhase = serverDataManager.getLocalPlayer().getReadyForPhase() != GamePhase.INITIALIZATION ? GamePhase.INITIALIZATION : GamePhase.LOBBY;
                    serverDataManager.setReadyForPhase(gamePhase);
                }
            }
        });

        // Ensuring the input for game id only contains letters and numbers, to prevent bugs.
        multiplayerMenuTextFieldGameID.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[a-zA-Z0-9]*")) {
                multiplayerMenuTextFieldGameID.setText(newValue.replaceAll("[^a-zA-Z0-9]", ""));
            }
        });

        // Ensuring player names only contain letters, numbers, and single spaces, to prevent bugs.
        multiplayerMenuTextFieldPlayerName.textProperty().addListener((observable, oldValue, newValue) -> {
            String sanitizedText = newValue.replaceAll("[^a-zA-Z0-9 ]", "");
            sanitizedText = sanitizedText.replaceAll("\\s{2,}", " ");
            if (!newValue.equals(sanitizedText)) {
                multiplayerMenuTextFieldPlayerName.setText(sanitizedText);
            }
        });
    }

    private void onJoinOrHost() {
        String playerName = multiplayerMenuTextFieldPlayerName.getText();
        playerName = playerName.trim();
        multiplayerMenuTextFieldPlayerName.setText(playerName);
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setupBackButton(Runnable backMethod) {
        // Back button
        multiplayerMenuButtonBack.setOnMouseClicked(a -> backMethod.run());
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    private void updateUI() {
        for (int i = 0; i < 6; i++) {
            playerSlots[i].setVisible(i < NO_OF_PLAYERS);
        }
        boolean canReadyOrStart = canReadyOrStart();
        if (canReadyOrStart) {
            lobbyButtonStart.setStyle("-fx-background-color: green; -fx-text-fill: white; -fx-font-weight: bold;");
            lobbyButtonStart.setStyle("-fx-background-color:  #3a993c60;" +
                    "-fx-background-radius:  15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        } else {
            lobbyButtonStart.setStyle("-fx-background-color:  #993a3a60;" +
                    "-fx-background-radius: 15;" +
                    "-fx-border-radius: 15;" +
                    "-fx-border-color:  ffffff;" +
                    "-fx-border-width: 1");
        }
        lobbyButtonStart.setDisable(!canReadyOrStart);
    }

    /**
     * Checks whether the conditions to start the game is met.
     * @return boolean
     * @author Maximillian Bjørn Mortensen
     */
    private boolean canReadyOrStart() {
        if (serverDataManager.getLocalPlayer().getPlayerName() == null || serverDataManager.getLocalPlayer().getRobotName() == null || serverDataManager.getLocalPlayer().getPlayerName().isBlank() || serverDataManager.getLocalPlayer().getRobotName().isBlank() || Robots.getRobotByName(serverDataManager.getLocalPlayer().getRobotName()) == null) return false;
        if (selectedCourse == null) return false;
        for (Player player : this.players.values()) {
            if (player.getPlayerId() != serverDataManager.getLocalPlayer().getPlayerId()) {
                if (serverDataManager.getLocalPlayer().getPlayerName().equals(player.getPlayerName())) return false;
                if (serverDataManager.getLocalPlayer().getRobotName().equals(player.getRobotName())) return false;
            }
        }
        if (serverDataManager.isHost()) {
            for (Player player : this.players.values()) {
                if (player.getPlayerId() != serverDataManager.getLocalPlayer().getPlayerId()) {
                    if (player.getReadyForPhase() != GamePhase.INITIALIZATION) {
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
    public void setServerURLInput(String serverURL) {
        multiplayerMenuTextFieldServerURL.setText(serverURL);
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

    @Override
    public void update(Subject subject) {
        if (subject == serverDataManager) {
            if (appController.isGameRunning()) return;
            if (serverDataManager.isConnectedToServer()) {
                // Setting up the lobby
                if (!hasBeenSetup) {
                    setupLobby();
                }
                // Updating data
                Game updatedGame = null;
                Map<Long, Player> updatedPlayers = null;
                if (ServerDataManager.getChangedData().contains(NetworkedDataTypes.GAME)) {
                    updatedGame = serverDataManager.getUpdatedGame();
                }
                if (ServerDataManager.getChangedData().contains(NetworkedDataTypes.PLAYERS)) {
                    updatedPlayers = serverDataManager.getUpdatedPlayerMap();
                }

                // Updating lobby
                updateLobby(updatedGame, updatedPlayers);
                showLobby(true);

                // Check if game started
                if (this.game != null) {
                    if (this.game.getPhase() != GamePhase.LOBBY) {
                        if (this.players != null) {
                            startGame();
                        }
                    }
                }
            } else {
                hasBeenSetup = false;
                showLobby(false);
                this.players = null;
                this.game = null;
                hasStartedGameLocally = false;
                selectedCourse = null;
            }
        }
    }

    private void startGame() {
        Platform.runLater(() -> {
            if (!hasStartedGameLocally) {
                hasStartedGameLocally = true;
                appController.startGame(selectedCourse, this.players, serverDataManager.getLocalPlayer().getPlayerId());
            }
        });
    }
}
