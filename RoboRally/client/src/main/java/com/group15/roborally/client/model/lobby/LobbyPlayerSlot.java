package com.group15.roborally.client.model.lobby;

import com.group15.roborally.client.model.Robots;
import com.group15.roborally.client.utils.ImageUtils;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

/**
 * Local class to handle the lobby UI for players.
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class LobbyPlayerSlot {
    private final Text nameText;
    private final ImageView robotImageView;
    private final Text robotText;
    private final ImageView hostStarImageView;
    private final ImageView readyCheckImageView;

    public LobbyPlayerSlot(Text nameText, ImageView robotImageView, Text robotText, ImageView hostStarImageView, ImageView readyCheckImageView) {
        this.nameText = nameText;
        this.robotImageView = robotImageView;
        this.robotText = robotText;
        this.hostStarImageView = hostStarImageView;
        this.readyCheckImageView = readyCheckImageView;

        setRobot(null);
        setHostStarVisible(false);
        setReadyCheckVisible(false);
    }

    public void setName(String name) {
        nameText.setText(name);
    }

    public String getName() {
        return nameText.getText();
    }

    public void setRobot(Robots robot) {
        if (robot != null) {
            robotImageView.setImage(ImageUtils.getImageFromName(robot.getSelectionImageName()));
            if (robotText != null) {
                robotText.setText(robot.getRobotName());
            }
        } else {
            robotImageView.setImage(null);
            if (robotText != null) {
                robotText.setText("");
            }
        }
    }

    public void setHostStarVisible(boolean visible) {
        hostStarImageView.setVisible(visible);
    }

    public void setReadyCheckVisible(boolean visible) {
        readyCheckImageView.setVisible(visible);
    }

    public void setVisible(boolean visible) {
        nameText.setVisible(visible);
        robotImageView.setVisible(visible);
        robotText.setVisible(visible);
    }
}
