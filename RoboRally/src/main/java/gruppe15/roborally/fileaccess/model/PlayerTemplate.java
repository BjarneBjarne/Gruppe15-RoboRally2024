package gruppe15.roborally.fileaccess.model;

import static gruppe15.roborally.model.Heading.SOUTH;

import java.util.List;

import gruppe15.roborally.model.CommandCardField;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.upgrades.UpgradeCard;

public class PlayerTemplate {
    public String name;
    public String color;

    public Space space;
    public Heading heading;

    public CommandCardField[] program;
    public CommandCardField[] cards;

    public  int priority;
    transient public List<UpgradeCard> upgradeCards;
}
