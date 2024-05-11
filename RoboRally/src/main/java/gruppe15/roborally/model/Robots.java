package gruppe15.roborally.model;

public enum Robots {
    Blue("SPIN BOT", "Robot_Blue.png"),
    Green("ZOOM BOT", "Robot_Green.png"),
    Orange("TWONKY", "Robot_Orange.png"),
    Purple("HAMMER BOT", "Robot_Purple.png"),
    Red("HULK X90", "Robot_Red.png"),
    Yellow("SMASH BOT", "Robot_Yellow.png");

    public final String robotName;
    public final String imageName;
    Robots(String robotName, String imageName) {
        this.robotName = robotName;
        this.imageName= imageName;
    }
    public String getRobotName() {
        return robotName;
    }
}
