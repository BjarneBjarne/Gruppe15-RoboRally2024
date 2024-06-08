package gruppe15.roborally.view;

import java.io.IOException;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

public class MainMenuView {
    @FXML
    AnchorPane mainMenu;
    @FXML
    Button hostGame;
    @FXML
    Button joinGame;
    @FXML
    Button help;
    @FXML
    Button courseCreator;
    @FXML
    Button exit;

    Button[] buttons = new Button[4];
    AppController appController;

    /**
     * Returns the main menu view.
     * 
     * @return the main menu view as an AnchorPane
     */
    public AnchorPane getMainMenu() {
        if(mainMenu == null){
            System.out.println("MainMenu is null");
        }
        return mainMenu;
    }

    /**
     * Initializes the main menu view, and the action events for the buttons, along with
     * the button effects.
     * @param appController the app controller
     * @return itself
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    @FXML
    public MainMenuView initialize(AppController appController) {
        this.appController = appController;
        try {
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("mainMenu.fxml"));
            mainMenu = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        createHostGameButton();
        createJoinGameButton();
        createCourseCreatorButton();
        createExitButton();
        setButtonEffect();
        return this;
    }

    private void createHostGameButton() {
        hostGame = (Button) mainMenu.lookup("#hostGame");
        hostGame.setOnAction(e -> appController.courseSelection());
        buttons[0] = hostGame;

        //newGame.setGraphic(createButtonTextPane(newGame.getText()));
    }

    private void createJoinGameButton() {
        joinGame = (Button) mainMenu.lookup("#joinGame");
        joinGame.setOnAction(event -> {
            // TODO: Add join logic

        });
        buttons[1] = joinGame;

        //loadGame.setGraphic(createButtonTextPane(loadGame.getText()));
    }

    private void createCourseCreatorButton() {
        courseCreator = (Button) mainMenu.lookup("#courseCreator");
        courseCreator.setOnMouseClicked(e -> appController.createCourseCreator(mainMenu.getScene()));
        buttons[2] = courseCreator;

        //courseCreator.setGraphic(createButtonTextPane(courseCreator.getText()));
    }

    private void createExitButton(){
        exit = (Button) mainMenu.lookup("#exit");
        exit.setOnAction(e -> appController.exit());
        buttons[3] = exit;

        //exit.setGraphic(createButtonTextPane(exit.getText()));
    }

    private StackPane createButtonTextPane(String text) {
        // Button text graphics
        Label buttonLabelBackground = new Label(text);
        buttonLabelBackground.setTextFill(Color.BLACK);
        buttonLabelBackground.setStyle(
                "-fx-font-size: 48;"
        );
        Text buttonLabelForeground = new Text(text);
        buttonLabelForeground.setFill(Color.WHITE);
        buttonLabelForeground.setStyle(
                "-fx-font-size: 42;"
        );
        StackPane textPane = new StackPane(buttonLabelBackground, buttonLabelForeground);
        textPane.setAlignment(Pos.CENTER);
        return textPane;
    }

    private void setButtonEffect(){
        Effect hover = new InnerShadow(20, Color.STEELBLUE);
        for (Button button : buttons) {
            button.setOnMouseEntered(e -> button.setEffect(hover));
            button.setOnMouseExited(e -> button.setEffect(null));
        }
    }
}
