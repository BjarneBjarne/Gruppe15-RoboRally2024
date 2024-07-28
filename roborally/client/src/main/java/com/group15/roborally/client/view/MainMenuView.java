package com.group15.roborally.client.view;

import java.io.IOException;

import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.utils.ButtonUtils;
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
    AppController appController;

    @FXML
    AnchorPane mainMenu;
    @FXML
    Button mainMenuButtonMultiplayer;
    @FXML
    Button mainMenuButtonCourseCreator;
    @FXML
    Button mainMenuButtonQuit;

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
            createMultiplayerButton();
            createCourseCreatorButton();
            createExitButton();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    private void createMultiplayerButton() {
        mainMenuButtonMultiplayer = (Button) mainMenu.lookup("#mainMenuButtonMultiplayer");
        ButtonUtils.setupDefaultButton(mainMenuButtonMultiplayer, () -> appController.goToMultiplayerMenu());
    }

    private void createCourseCreatorButton() {
        mainMenuButtonCourseCreator = (Button) mainMenu.lookup("#mainMenuButtonCourseCreator");
        ButtonUtils.setupDefaultButton(mainMenuButtonCourseCreator, () -> appController.createCourseCreator());
    }

    private void createExitButton() {
        mainMenuButtonQuit = (Button) mainMenu.lookup("#mainMenuButtonQuit");
        ButtonUtils.setupDefaultButton(mainMenuButtonQuit, () -> appController.quitGame(false));
    }
}
