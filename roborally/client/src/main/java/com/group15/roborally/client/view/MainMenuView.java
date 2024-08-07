package com.group15.roborally.client.view;

import java.io.IOException;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.client.controller.AppController;
import com.group15.roborally.client.utils.ButtonUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class MainMenuView {
    AppController appController;

    @FXML
    AnchorPane mainMenu;
    @FXML
    Button mainMenuButtonMultiplayer;
    @FXML
    Button mainMenuButtonCourseCreator;
    @FXML
    Button mainMenuButtonSettings;
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
            initializeMultiplayerButton();
            initializeCourseCreatorButton();
            initializeSettingsButton();
            initializeQuitButton();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return this;
    }

    private void initializeMultiplayerButton() {
        mainMenuButtonMultiplayer = (Button) mainMenu.lookup("#mainMenuButtonMultiplayer");
        ButtonUtils.setupDefaultButton(mainMenuButtonMultiplayer, () -> appController.goToMultiplayerMenu());
    }

    private void initializeCourseCreatorButton() {
        mainMenuButtonCourseCreator = (Button) mainMenu.lookup("#mainMenuButtonCourseCreator");
        ButtonUtils.setupDefaultButton(mainMenuButtonCourseCreator, () -> appController.createCourseCreator());
    }

    private void initializeSettingsButton() {
        mainMenuButtonSettings = (Button) mainMenu.lookup("#mainMenuButtonSettings");
        ButtonUtils.setupDefaultButton(mainMenuButtonSettings, () -> appController.goToSettings());
    }

    private void initializeQuitButton() {
        mainMenuButtonQuit = (Button) mainMenu.lookup("#mainMenuButtonQuit");
        ButtonUtils.setupDefaultButton(mainMenuButtonQuit, () -> appController.quitGame(false));
    }
}
