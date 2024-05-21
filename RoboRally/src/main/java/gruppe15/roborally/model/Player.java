/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package gruppe15.roborally.model;

import gruppe15.observer.Subject;
import gruppe15.roborally.model.exceptions.*;
import gruppe15.roborally.model.upgrades.*;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static gruppe15.roborally.model.Heading.SOUTH;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {
    final public static int NO_OF_REGISTERS = 5;
    final public static int NO_OF_CARDS = 8;
    final public static int NO_OF_PERMANENT_UPGRADE_CARDS = 3;
    final public static int NO_OF_TEMPORARY_UPGRADE_CARDS = 3;
    final public static int NO_OF_ENERGY_CUBES = 10;

    final public Board board;

    private String name;
    private Robots robot;

    private Space space;
    private Space temporarySpace = null;
    private Heading heading = SOUTH;

    transient private Command lastCmd;

    transient private final CardField[] programFields;
    transient private final CardField[] cardFields;
    transient private final CardField[] permanentUpgradeCardFields;
    transient private final CardField[] temporaryUpgradeCardFields;
    private int energyCubes = 5;
    private int checkpoints = 0;
    transient private int priority = 0;
    private Velocity velocity;
    private boolean rebooting = false;
    private Space spawnPoint; //  If you rebooted from the start board, place your robot on the space where you started the game.
    transient private Image image;
    transient private Image charIMG;

    transient private Queue<CommandCard> programmingDeck = new LinkedList<>();
    transient private final List<UpgradeCard> upgradeCards = new ArrayList<>(); // Not for card function, but could be used for showing the players upgrade cards.


    public Player(@NotNull Board board, @NotNull Robots robot, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.robot = robot;
        this.space = null;
        this.image = ImageUtils.getImageFromName(robot.getBoardImageName());
        this.charIMG = ImageUtils.getImageFromName(robot.getSelectionImageName());

        programFields = new CardField[NO_OF_REGISTERS];
        for (int i = 0; i < programFields.length; i++) {
            programFields[i] = new CardField(this,i+1);
        }
        cardFields = new CardField[NO_OF_CARDS];
        for (int i = 0; i < cardFields.length; i++) {
            cardFields[i] = new CardField(this, CardField.CardFieldTypes.COMMAND_CARD_FIELD);
        }
        permanentUpgradeCardFields = new CardField[NO_OF_PERMANENT_UPGRADE_CARDS];
        for (int i = 0; i < permanentUpgradeCardFields.length; i++) {
            permanentUpgradeCardFields[i] = new CardField(this, CardField.CardFieldTypes.PERMANENT_UPGRADE_CARD_FIELD);
        }
        temporaryUpgradeCardFields = new CardField[NO_OF_TEMPORARY_UPGRADE_CARDS];
        for (int i = 0; i < temporaryUpgradeCardFields.length; i++) {
            temporaryUpgradeCardFields[i] = new CardField(this, CardField.CardFieldTypes.TEMPORARY_UPGRADE_CARD_FIELD);
        }

        setProgrammingDeckToDefault();
    }

    public Image getImage() {
        return this.image;
    }

    /**
     * returns the frontal image of the players robot
     * @return Image
     * @author Maximillian Bjørn Mortensen
     */
    public Image getCharImage() {
        return this.charIMG;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public Queue<CommandCard> getProgrammingDeck() {
        return programmingDeck;
    }

    public void setProgrammingDeck(Queue<CommandCard> programmingDeck) {
        this.programmingDeck = programmingDeck;
    }

    public void setCharImage(Image image) {
        this.charIMG = image;
    }

    public int getEnergyCubes() {
        return energyCubes;
    }

    public int getCheckpoints() {
        return checkpoints;
    }

    public void setCheckpoint(int checkpoints) {
        this.checkpoints = checkpoints;
    }

    /**
     * sets the paramater as the last command
     * @param lastCmd
     * @author Maximillian Bjørn Mortensen
     */
    public void setLastCmd(Command lastCmd) {
        this.lastCmd = lastCmd;
    }

    /**
     * returns the field lastCmd
     * @return Command
     * @author Maximillian Bjørn Mortensen
     */
    public Command getLastCmd(){
        return lastCmd;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public int getPriority() {return priority;}
    public void setPriority(int priority) {this.priority=priority;}


    public Robots getRobot() {
        return robot;
    }

    public void setRobot(Robots robot) {
        this.robot = robot;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public void setSpawn(Space space) {
        setSpace(space);
        this.spawnPoint = space;
    }
    public Space getSpawnPoint() {
        return this.spawnPoint;
    }


    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    public Space getTemporarySpace() {
        return this.temporarySpace;
    }
    public void setTemporarySpace(Space space) {
        this.temporarySpace = space;
    }
    public void goToTemporarySpace() {
        if (this.temporarySpace != null) {
            // Surpass the setSpace() checks.
            if (this.space != null) {
                this.space.setPlayer(null);
            }
            this.space = this.temporarySpace;
            this.space.setPlayer(this);
            this.temporarySpace = null;
        }
    }

    public void setVelocity(Velocity velocity) {
        this.velocity = velocity;
    }
    public Velocity getVelocity() {
        return velocity;
    }

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    public void startRebooting() {
        for (int i = 0; i < 2; i++) {
            discard(new CommandCard(Command.SPAM));
        }
        this.rebooting = true;
    }
    public void stopRebooting() {
        this.rebooting = false;
    }
    public boolean getIsRebooting() {
        return this.rebooting;
    }

    public CardField getProgramField(int i) {
        return programFields[i];
    }
    public CardField[] getProgramFields() {
        return programFields;
    }

    public CardField getCardField(int i) {
        return cardFields[i];
    }
    public CardField[] getCardFields() {
        return cardFields;
    }

    public CardField getPermanentUpgradeCardField(int i) {
        return permanentUpgradeCardFields[i];
    }
    public CardField[] getPermanentUpgradeCardFields() {
        return permanentUpgradeCardFields;
    }

    public CardField getTemporaryUpgradeCardField(int i) {
        return temporaryUpgradeCardFields[i];
    }
    public CardField[] getTemporaryUpgradeCardFields() {
        return temporaryUpgradeCardFields;
    }

    public void addEnergyCube() {
        energyCubes++;
    }

    public boolean attemptUpgradeCardPurchase(CardField shopField) {
        UpgradeCard boughtCard = null;
        try {
            for (UpgradeCard ownedCards : upgradeCards) // First, check if player already has this card
                if (ownedCards.getClass().isAssignableFrom(shopField.getCard().getClass())) return false;
            boughtCard = board.getUpgradeShop().attemptBuyCardFromShop(shopField, this);
            if (boughtCard != null) {
                addUpgradeCard(boughtCard);
            }
            board.updateBoard();
        } catch (Exception e) {
            System.out.println("ERROR - Attempt to buy upgrade card from shopField failed.");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        return boughtCard != null;
    }
    public void addUpgradeCard(UpgradeCard upgradeCard) {
        try {
            System.out.println("Player: \"" + name + "\" bought upgrade: \"" + upgradeCard.getName() + "\".");
            upgradeCards.add(upgradeCard);
            upgradeCard.initialize(board, this);
        } catch (NullPointerException e) {
            System.out.println("ERROR - Attempted to add upgradeCard of value NULL to player: \"" + name + "\".");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }
    public void removeUpgradeCard(UpgradeCard upgradeCard) {
        try {
            if (!upgradeCards.contains(upgradeCard)) {
                throw new IllegalPlayerPropertyAccess("ERROR - Attempted to remove upgradeCard: \"" + upgradeCard.getName() + "\" that player: \"" + name + "\" doesn't own.");
            }
            System.out.println("Player: \"" + name + "\" discarded upgrade: \"" + upgradeCard.getName() + "\".");
            upgradeCards.remove(upgradeCard);
            upgradeCard.unInitialize();
            board.getUpgradeShop().returnCardToShop(upgradeCard);
        } catch (NullPointerException e) {
            System.out.println("ERROR - Attempted to remove upgradeCard of value NULL from player: \"" + name + "\".");
        } catch (IllegalPlayerPropertyAccess e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * sets the fiels programmingDeck to its defoult settings
     * @author Maximillian Bjørn Mortensen
     */
    public void setProgrammingDeckToEven() {
        List<Integer> index = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            index.add(i % 9);
        }
        Collections.shuffle(index);
        Command[] commands = Command.values();
        for (int i = 0; i < 20; i++) {
            programmingDeck.add(new CommandCard(commands[index.removeFirst()]));
        }
        programmingDeck.add(null);
        //printProgrammingDeck();
    }

    public void setProgrammingDeckToDefault() {
        List<CommandCard> tempDeck = new ArrayList<>();
        addCommandCardsToDeck(tempDeck, Command.MOVE_1, 4);
        addCommandCardsToDeck(tempDeck, Command.MOVE_2, 3);
        addCommandCardsToDeck(tempDeck, Command.MOVE_3, 1);
        addCommandCardsToDeck(tempDeck, Command.RIGHT_TURN, 4);
        addCommandCardsToDeck(tempDeck, Command.LEFT_TURN, 4);
        addCommandCardsToDeck(tempDeck, Command.U_TURN, 1);
        addCommandCardsToDeck(tempDeck, Command.MOVE_BACK, 1);
        addCommandCardsToDeck(tempDeck, Command.POWER_UP, 1);
        addCommandCardsToDeck(tempDeck, Command.AGAIN, 1);
        Collections.shuffle(tempDeck);
        programmingDeck = new LinkedList<>();
        programmingDeck.add(null);

        programmingDeck.addAll(tempDeck);

        //printProgrammingDeck();
    }
    private static void addCommandCardsToDeck(List<CommandCard> deck, Command command, int count) {
        deck.addAll(Collections.nCopies(count, new CommandCard(command)));
    }

    /**
     * shuffels the contents of the field programmingDeck
     * @author Maximillian Bjørn Mortensen
     */
    private void shuffleDiscardedIntoDeck() {
        List<CommandCard> temp = new ArrayList<>(programmingDeck);
        programmingDeck.clear();
        Collections.shuffle(temp);
        programmingDeck.addAll(temp);
        programmingDeck.add(null);

        //printProgrammingDeck();
    }

    public void printProgrammingDeck() {
        System.out.println("Player cards: " + name);
        System.out.println("Number of cards: " + programmingDeck.size());
        Map<Command, Long> commandCardCounts = programmingDeck.stream()
                .filter(Objects::nonNull) // Filter out null CommandCards
                .collect(Collectors.groupingBy(CommandCard::getCommand, Collectors.counting()));
        commandCardCounts.forEach((key, value) -> System.out.println(key + ": " + value));
        System.out.println();
    }

    /**
     * adds the paramater card into the discarded end of programmingDeck
     * @param card
     * @author Maximillian Bjørn Mortensen
     */
    public void discard(CommandCard card) {
        programmingDeck.add(new CommandCard(card.command));
    }

    /**
     * removes the top card in programmingDeck and returns a copy
     * @return CommandCard
     * @author Maximillian Bjørn Mortensen
     */
    public CommandCard drawFromDeck() {
        CommandCard temp = programmingDeck.remove();
        if (temp == null) {
            shuffleDiscardedIntoDeck();
            temp = drawFromDeck();
        }
        return new CommandCard(temp.command);
    }

    /**
     * inputs cards from programmingDeck into all CommandCardFields in cards
     * @author Maximillian Bjørn Mortensen
     */
    public void drawHand() {
        for (CardField c: cardFields) {
            if (c.getCard() == null) {
                c.setCard(drawFromDeck());
            }
        }
    }

    /**
     * discards all cards in registeres and on hand
     * @author Maximillian Bjørn Mortensen
     */
    public void discardAll() {
        for (CardField c: programFields) {
            if(c.getCard() != null) {
                discard((CommandCard) c.getCard());
                c.setCard(null);
            }
        }
        for (CardField c: cardFields) {
            if(c.getCard() != null) {
                discard((CommandCard) c.getCard());
                c.setCard(null);
            }
        }
    }

    public void removeFromDeck(CommandCard card) {
        programmingDeck.remove(card);
    }

    public void setEnergyCubes(int energyCubes) {
        this.energyCubes = energyCubes;
    }

    public void fillRestOfRegisters() {
        for (CardField programField : programFields) {
            if (programField.getCard() == null) {
                programField.setCard(drawFromDeck());
            }
        }
    }
}
