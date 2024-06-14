package com.group15.roborally.client.model.lobby;
import com.group15.roborally.client.model.Robots;
import com.group15.roborally.client.utils.ImageUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Local class to handle the lobby UI for players.
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class LobbyPlayerSlot {
    private final ImageView hostStarImageView;
    private final Text playerNameText;
    private final ImageView readyCheckImageView;
    private final ImageView robotImageView;
    private final Text robotText;
    private final ComboBox<String> robotComboBox;

    public LobbyPlayerSlot(ImageView hostStarImageView, Text playerNameText, ImageView readyCheckImageView, ImageView robotImageView, Text robotText, ComboBox<String> robotComboBox) {
        this.hostStarImageView = hostStarImageView;
        this.playerNameText = playerNameText;
        this.readyCheckImageView = readyCheckImageView;
        this.robotImageView = robotImageView;
        this.robotText = robotText;
        this.robotComboBox = robotComboBox;
        setRobotByRobotName(null);
        setHostStarVisible(false);
        setReadyCheckVisible(false);
    }

    public void setName(String name) {
        playerNameText.setText(name);
    }

    public String getName() {
        return playerNameText.getText();
    }

    public void setRobotByRobotName(String robotName) {
        Image robotImage = null;
        String robotNameText = "";

        if (robotName != null) {
            Robots robot = Robots.getRobotByName(robotName);
            if (robot != null) {
                robotImage = ImageUtils.getImageFromName(robot.getSelectionImageName());
                robotNameText = robotName;
            }
        }

        robotImageView.setImage(robotImage);
        if (robotComboBox == null) {
            robotText.setText(robotNameText);
        }
    }

    public void setHostStarVisible(boolean visible) {
        hostStarImageView.setVisible(visible);
    }

    public void setReadyCheckVisible(boolean visible) {
        readyCheckImageView.setVisible(visible);
    }

    public void setVisible(boolean visible) {
        playerNameText.setVisible(visible);
        robotImageView.setVisible(visible);
        robotText.setVisible(visible);
        hostStarImageView.setVisible(visible);
        readyCheckImageView.setVisible(visible);
    }

    public ComboBox<String> getRobotComboBox() {
        return robotComboBox;
    }
}
