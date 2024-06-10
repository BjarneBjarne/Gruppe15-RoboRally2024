package com.gruppe15.templates;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.gruppe15.model.Command;
import com.gruppe15.model.CommandCard;
import com.gruppe15.model.Heading;
import com.gruppe15.model.Robots;
import com.gruppe15.model.Space;
import com.gruppe15.model.upgrade_cards.UpgradeCard;

public class PlayerTemplate {
    public String name;
    public Robots robot;
    public Space space;
    public Heading heading;
    public int checkpoints;
    public int energyCubes;
    public Space spawnPoint;

    public Command[] cardsInHand;
    public Command[] programCards;
    public UpgradeCard[] permanentUpgradeCards;
    public UpgradeCard[] temporaryUpgradeCards;

    public Command[] programmingDeck;

    transient public List<UpgradeCard> upgradeCards;

    public void setProgrammingDeck(List<CommandCard> programmingDeck) {
        this.programmingDeck = new Command[programmingDeck.size()];
        for (int i = 0; i < programmingDeck.size(); i++) {
            if (programmingDeck.get(i) == null) {
                this.programmingDeck[i] = null;
                continue;
            }
            this.programmingDeck[i] = programmingDeck.get(i).getCommand();
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
