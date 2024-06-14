package com.group15.model;

public enum Robots {
    Blue("SPIN BOT"),
    Green("ZOOM BOT"),
    Orange("TWONKY"),
    Purple("HAMMER BOT"),
    Red("HULK X90"),
    Yellow("SMASH BOT");

    private final String robotName;
    Robots(String robotName) {
        this.robotName = robotName;
    }
    public String getRobotName() {
        return robotName;
    }
    public String getBoardImageName() {
        return "Robots/Robot_" + this.name() + ".png";
    }
    public String getSelectionImageName() {
        return "Robots/CharacterSelection/RobotSelection_" + this.name() + ".png";
    }
    public static Robots getRobotByName(String robotName) {
        for (Robots robot : Robots.values()) {
            if (robot.getRobotName().equalsIgnoreCase(robotName)) {
                return robot;
            }
        }

        return null;
    }
}
