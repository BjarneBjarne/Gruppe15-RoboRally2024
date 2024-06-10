package gruppe15.roborally.model.lobby;

import gruppe15.roborally.model.Robots;
import gruppe15.utils.ImageUtils;
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

    public LobbyPlayerSlot(Text nameText, ImageView robotImageView, Text robotText, ImageView hostStarImageView) {
        this.nameText = nameText;
        this.robotImageView = robotImageView;
        this.robotText = robotText;
        this.hostStarImageView = hostStarImageView;

        setRobot(null);
        setHostStartVisible(false);
    }

    public void setName(String name) {
        nameText.setText(name);
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

    public void setHostStartVisible(boolean visible) {
        hostStarImageView.setVisible(visible);
    }

    public void setVisible(boolean visible) {
        nameText.setVisible(visible);
        robotImageView.setVisible(visible);
        robotText.setVisible(visible);
    }
}
