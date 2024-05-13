package gruppe15.roborally.fileaccess.model;

import static gruppe15.roborally.model.Heading.SOUTH;

import java.util.List;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.Heading;
import gruppe15.roborally.model.Robots;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.upgrades.UpgradeCard;

public class PlayerTemplate {
    public String name;
    public Robots robot;

    public Space space;

    public Heading heading;

    public Command[] cards;

    public int checkpoints;
    public int energyCubes;

    public Space spawnPoint;
    
    transient public List<UpgradeCard> upgradeCards;
}
