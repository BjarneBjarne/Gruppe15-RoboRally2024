package gruppe15.roborally.model.lobby;

import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class LobbyPlayer {
    // Server variables (To be synced/serialized)
    private int id;
    private String playerName;
    private String robotName;
    private boolean isReady;

    // Local variables (Not to be synced/serialized)
    private Text nameText;
    private Text robotText;
    private ImageView robotImageView;

    /**
     * Constructor for a new player in the lobby.
     */
    public LobbyPlayer(int id, String playerName, String robotName, boolean isReady, Text nameText, Text robotText, ImageView robotImageView) {
        this.id = id;
        this.playerName = playerName;
        this.robotName = robotName;
        this.isReady = isReady;
        this.nameText = nameText;
        this.robotText = robotText;
        this.robotImageView = robotImageView;
    }

    public int getId() {
        return id;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRobotName() {
        return robotName;
    }

    public boolean isReady() {
        return isReady;
    }

    public Text getNameText() {
        return nameText;
    }

    public Text getRobotText() {
        return robotText;
    }

    public ImageView getRobotImageView() {
        return robotImageView;
    }
}
