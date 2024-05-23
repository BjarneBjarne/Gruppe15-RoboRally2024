package gruppe15.roborally.view;

import java.io.File;
import java.io.IOException;

import gruppe15.roborally.RoboRally;
import gruppe15.roborally.controller.AppController;
import gruppe15.roborally.model.utils.Constants;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

public class MainMenuView {
    
    @FXML
    AnchorPane mainMenu;
    @FXML
    Button newGame;
    @FXML
    Button loadGame;
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
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param appController the app controller
     * @return itself
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
        createNewGameButton();
        createLoadGameButton();
        createCourseCreatorButton();
        createExitButton();
        setButtonEffect();
        return this;
    }

    private void createNewGameButton(){
        newGame = (Button) mainMenu.lookup("#newGame");
        newGame.setOnAction(e -> appController.courseSelection());
        buttons[0] = newGame;

        //newGame.setGraphic(createButtonTextPane(newGame.getText()));
    }

    private void createLoadGameButton(){
        loadGame = (Button) mainMenu.lookup("#loadGame");
        loadGame.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Load Game");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON files", "*.json"));
            String userHome = System.getProperty("user.home");
            String relativePath = "\\RoboRally\\saves";
            String directoryPath = userHome + relativePath;

            File folderFile = new File(directoryPath);
            // Create saves folder if it doesn't exist
            if (!folderFile.exists()) {
                if (folderFile.mkdirs()) {
                    System.out.println("Directory created successfully: " + folderFile.getAbsolutePath());
                } else {
                    System.err.println("Failed to create directory: " + folderFile.getAbsolutePath());
                }
            }

            fileChooser.setInitialDirectory(new File(directoryPath));
            File loadedFile = fileChooser.showOpenDialog(mainMenu.getScene().getWindow());
            if (loadedFile != null) {
                appController.loadGame(loadedFile);
            }
        });
        buttons[1] = loadGame;

        //loadGame.setGraphic(createButtonTextPane(loadGame.getText()));
    }

    private void createCourseCreatorButton(){
        courseCreator = (Button) mainMenu.lookup("#courseCreator");
        courseCreator.setOnMouseClicked(e -> appController.courseCreator(mainMenu.getScene()));
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
