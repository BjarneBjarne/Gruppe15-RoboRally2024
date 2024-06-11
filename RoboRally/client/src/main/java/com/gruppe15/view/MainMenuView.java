package com.gruppe15.view;

import java.io.IOException;

import com.gruppe15.RoboRally;
import com.gruppe15.controller.AppController;
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
    Button mainMenuButtonMultiplayer;
    @FXML
    Button mainMenuButtonCourseCreator;
    @FXML
    Button mainMenuButtonQuit;

    Button[] buttons = new Button[3];
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
            FXMLLoader loader = new FXMLLoader(RoboRally.class.getResource("MainMenu.fxml"));
            mainMenu = loader.load();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        createMultiplayerButton();
        //createJoinGameButton();
        createCourseCreatorButton();
        createExitButton();
        setButtonEffect();
        return this;
    }

    private void createMultiplayerButton() {
        mainMenuButtonMultiplayer = (Button) mainMenu.lookup("#mainMenuButtonMultiplayer");
        mainMenuButtonMultiplayer.setOnAction(event -> appController.initializeMultiplayerMenu());
        buttons[0] = mainMenuButtonMultiplayer;

        //newGame.setGraphic(createButtonTextPane(newGame.getText()));
    }

    private void createCourseCreatorButton() {
        mainMenuButtonCourseCreator = (Button) mainMenu.lookup("#mainMenuButtonCourseCreator");
        mainMenuButtonCourseCreator.setOnMouseClicked(e -> appController.createCourseCreator(mainMenu.getScene()));
        buttons[1] = mainMenuButtonCourseCreator;

        //courseCreator.setGraphic(createButtonTextPane(courseCreator.getText()));
    }

    private void createExitButton() {
        mainMenuButtonQuit = (Button) mainMenu.lookup("#mainMenuButtonQuit");
        mainMenuButtonQuit.setOnAction(e -> appController.quit());
        buttons[2] = mainMenuButtonQuit;

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
