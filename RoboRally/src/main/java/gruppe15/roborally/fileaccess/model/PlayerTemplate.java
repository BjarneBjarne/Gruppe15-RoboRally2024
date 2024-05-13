package gruppe15.roborally.fileaccess.model;

import static gruppe15.roborally.model.Heading.SOUTH;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
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
    
    public Command[] programmingDeck;

    transient public List<UpgradeCard> upgradeCards;

    public void setProgrammingDeck(Queue<CommandCard> programmingDeck) {
        this.programmingDeck = new Command[programmingDeck.size()];
        CommandCard commandCard;
        for (int i = 0; (commandCard = programmingDeck.poll()) != null; i++) {
            this.programmingDeck[i] = commandCard.getCommand();
        }
    }

    public Queue<CommandCard> getProgrammingDeck() {
        Queue<CommandCard> queueProgrammingDeck = new LinkedList<>();
        for (Command command : programmingDeck) {
            if(command == null) {
                continue;
            }
            queueProgrammingDeck.add(new CommandCard(command));
        }
        return queueProgrammingDeck;
    }
}
