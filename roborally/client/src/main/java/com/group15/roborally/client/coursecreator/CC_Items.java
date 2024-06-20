package com.group15.roborally.client.coursecreator;

import com.group15.roborally.client.utils.ImageUtils;
import javafx.scene.image.Image;

// WARNING: Don't change the order of these. The CC_Items.ordinal() order determines the saved and loaded board elements. Also used to calculate conveyor belt images.
public enum CC_Items {
    SubBoard("subBoard.png"),
    StartSubBoard("startSubBoard.png", true),

    Wall("wall.png", true),

    SpawnPoint("startField.png"),
    Reboot("reboot.png", true),
    Hole("hole.png"),
    Antenna("antenna.png", true),

    BlueConveyorBelt("blueStraight.png", true),
    GreenConveyorBelt("greenStraight.png", true),
    PushPanel135("push135.png", true),
    PushPanel24("push24.png", true),
    GearRight("gearRight.png"),
    GearLeft("gearLeft.png"),
    BoardLaser("boardLaser.png", true),
    EnergySpace("energySpace.png"),
    Checkpoint1("1.png"),
    Checkpoint2("2.png"),
    Checkpoint3("3.png"),
    Checkpoint4("4.png"),
    Checkpoint5("5.png"),
    Checkpoint6("6.png");

    public final Image image;
    public final boolean canBeRotated;
    CC_Items(String imageName, boolean canBeRotated) {
        this.image = ImageUtils.getImageFromName("Board_Pieces/" + imageName);
        this.canBeRotated = canBeRotated;
    }
    CC_Items(String imageName) {
        this.image = ImageUtils.getImageFromName("Board_Pieces/" + imageName);
        this.canBeRotated = false;
    }
}
