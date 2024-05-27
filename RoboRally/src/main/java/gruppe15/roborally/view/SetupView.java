package gruppe15.roborally.view;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.coursecreator.CC_CourseData;
import gruppe15.roborally.coursecreator.CC_JsonUtil;
import gruppe15.roborally.model.Robots;
import gruppe15.roborally.exceptions.NoCoursesException;
import gruppe15.roborally.model.utils.ImageUtils;
import gruppe15.roborally.model.utils.TextUtils;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
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

import static gruppe15.roborally.model.utils.GameSettings.*;

/**
 * @author Maximillian Bjørn Mortensen
 */
public class SetupView {
    private List<CC_CourseData> courses = new ArrayList<>();
    private List<ComboBox> charSelection = new ArrayList<>();
    private ImageView[] playerRobotImageViews = new ImageView[6];
    private HBox[] playerHBoxes = new HBox[6];

    @FXML
    AnchorPane selection_menu;
    @FXML
    ScrollPane coursesScrollPane;
    @FXML
    VBox coursesVBox;
    @FXML
    VBox playersVBox;
    @FXML
    ImageView map;
    @FXML
    Button start;
    @FXML
    Button selection_back;
    @FXML
    Text selectedCourseText;

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
                appController.beginCourse(courses.get(mapIndex), playerNames, playerCharacters);
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
        // Course
        Platform.runLater(() -> {
            // Loading courses
            courses = CC_JsonUtil.getCoursesInFolder("courses");

            coursesScrollPane.setBackground(new Background(new BackgroundFill(
                    Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
            coursesVBox.setBackground(new Background(new BackgroundFill(
                    Color.rgb(0, 0, 0, 0), CornerRadii.EMPTY, null)));
            coursesScrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
            coursesVBox.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-width: 2; -fx-border-radius: 5;");

            int courseButtonSize = (int)(coursesVBox.getWidth() - coursesVBox.getPadding().getLeft() - coursesVBox.getPadding().getRight());
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
                        map.setImage(courses.get(courseIndex).getImage());
                        mapIndex = courseIndex;
                        selectedCourseText.setText(course.getCourseName().toUpperCase());
                    });
                    coursesVBox.getChildren().add(newCourseVBox);
                }
                selectedCourseText.setText(courses.getFirst().getCourseName().toUpperCase());
                map.setImage(courses.getFirst().getImage());
            } else {
                System.out.println(new NoCoursesException().getMessage());
            }
        });

        // Players
        NO_OF_PLAYERS = 2;
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
            if(playerNames[i] == null || playerNames[i].isBlank() || playerCharacters[i] == null) return false;
            for(int j = i-1; j >= 0; j--){
                if(playerNames[i].equals(playerNames[j])) return false;
                if(playerCharacters[i].equals(playerCharacters[j])) return false;
            }
        }
        return true;
    }
}
