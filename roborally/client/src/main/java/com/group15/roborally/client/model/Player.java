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
package com.group15.roborally.client.model;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.exceptions.IllegalPlayerPropertyAccess;
import com.group15.roborally.client.model.boardelements.BE_SpawnPoint;
import com.group15.roborally.client.model.damage.Damage;
import com.group15.roborally.client.model.events.PlayerShootListener;
import com.group15.roborally.client.model.player_interaction.CommandOptionsInteraction;
import com.group15.roborally.client.model.player_interaction.RebootInteraction;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.client.utils.ImageUtils;
import com.group15.roborally.common.observer.Subject;
import javafx.scene.image.Image;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import com.group15.roborally.common.model.GamePhase;

import java.util.*;
import java.util.stream.Collectors;

import static com.group15.roborally.client.LobbySettings.DRAW_ON_EMPTY_REGISTER;
import static com.group15.roborally.client.model.EventHandler.getPlayerCardEventListeners;
import static com.group15.roborally.client.model.Heading.SOUTH;

/**
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class Player extends Subject {
    final public static int NO_OF_REGISTERS = 5;
    final public static int NO_OF_PERMANENT_UPGRADE_CARDS = 3;
    final public static int NO_OF_TEMPORARY_UPGRADE_CARDS = 3;
    final public static int NO_OF_ENERGY_CUBES = 10;
    @Setter
    @Getter
    private int maxNoOfCardsInHand = 9;

    @Getter
    private final long playerId;
    @Getter
    private String name;
    transient final public Board board;
    @Getter
    private Robots robot;

    @Getter
    private Space space;
    @Setter
    @Getter
    private Space temporarySpace = null;
    @Getter
    private Heading heading = SOUTH;

    @Getter
    private Space spawnPoint;

    @Getter
    private int energyCubes = 5;
    @Getter
    private int checkpoints = 0;

    @Getter
    transient private final CardField[] programFields;
    @Getter
    transient private final CardField[] cardHandFields;
    @Getter
    transient private final CardField[] permanentUpgradeCardFields;
    @Getter
    transient private final CardField[] temporaryUpgradeCardFields;

    @Getter
    @Setter
    transient private Command lastCmd;

    @Setter
    @Getter
    transient private int priority = 0;
    @Getter
    @Setter
    private Velocity velocity = new Velocity(0, 0);
    private boolean rebooting = false;
    @Setter
    @Getter
    transient private Image image;
    final transient private Image charIMG;

    @Setter
    @Getter
    transient private Queue<CommandCard> programmingDeck = new LinkedList<>();

    @Getter
    transient private final List<UpgradeCard> upgradeCards = new ArrayList<>();
    transient private final Damage temporaryBonusDamage = new Damage(0, 0, 0, 0);
    @Getter
    transient private final Damage permanentBonusDamage = new Damage(0, 0, 0, 0);


    public Player(long playerId, @NotNull String name, @NotNull Board board, @NotNull Robots robot) {
        this.playerId = playerId;
        this.name = name;
        this.board = board;
        this.robot = robot;
        this.space = null;
        this.image = ImageUtils.getImageFromName(robot.getBoardImageName());
        this.charIMG = ImageUtils.getImageFromName(robot.getSelectionImageName());

        programFields = new CardField[NO_OF_REGISTERS];
        for (int i = 0; i < programFields.length; i++) {
            programFields[i] = new CardField(this,i+1);
        }
        cardHandFields = new CardField[10];
        for (int i = 0; i < cardHandFields.length; i++) {
            cardHandFields[i] = new CardField(this, CardField.CardFieldTypes.COMMAND_CARD_FIELD);
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

    /**
     * returns the frontal image of the players robot
     * @return Image
     * @author Maximillian Bjørn Mortensen
     */
    public Image getCharImage() {
        return this.charIMG;
    }

    public void setCheckpoint(int checkpoints) {
        this.checkpoints = checkpoints;
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


    public void setRobot(Robots robot) {
        this.robot = robot;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    public void setSpawn(Space space) {
        if (this.spawnPoint == null) {
            setSpace(space);
            this.spawnPoint = space;
            if (space.getBoardElement() instanceof BE_SpawnPoint BE_spawnPoint) {
                BE_spawnPoint.setColor(this);
                board.updateBoard();
            }
        }
    }


    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace && (space == null || space.board == this.board)) {
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

    public void goToTemporarySpace() {
        if (temporarySpace == null) return;
        // Surpass the setSpace() checks
        // Set old space
        if (this.space != null && this.space.getPlayer() == this) {
            this.space.setPlayer(null);
        }
        // Go to temporarySpace
        this.space = this.temporarySpace;
        // Also tell the new space that this is where the player is
        if (this.space != null) {
            this.space.setPlayer(this);
        }
        this.temporarySpace = null;
    }

    public void setForwardVelocity(int velocity) {
        this.velocity.forward = velocity;
    }

    public void setRightVelocity(int velocity) {
        this.velocity.right = velocity;
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

    public void startRebooting(GameController gameController, boolean takeDamage) {
        System.out.println(name + " rebooting.");
        if (takeDamage) {
            for (int i = 0; i < 2; i++) {
                discard(new CommandCard(Command.SPAM));
            }
        }
        if (board.getCurrentPhase() != GamePhase.INITIALIZATION) {
            gameController.addPlayerInteraction(new RebootInteraction(gameController, this));
        }
        this.rebooting = true;
        space.updateSpace();
    }
    public void stopRebooting() {
        if (this.rebooting) {
            System.out.println(name + " stopped rebooting.");
        }
        this.rebooting = false;
        space.updateSpace();
    }
    public boolean getIsRebooting() {
        return this.rebooting;
    }

    public CardField getProgramField(int i) {
        return programFields[i];
    }

    public String[] getProgramFieldCardNames() {
        String[] names = new String[programFields.length];
        for (int i = 0; i < programFields.length; i++) {
            names[i] = programFields[i].getCard() != null ? ((CommandCard) programFields[i].getCard()).getCommand().name() : "";
        }
        return names;
    }
    public CardField getCardField(int i) {
        return cardHandFields[i];
    }

    public CardField getPermanentUpgradeCardField(int i) {
        return permanentUpgradeCardFields[i];
    }

    public CardField getTemporaryUpgradeCardField(int i) {
        return temporaryUpgradeCardFields[i];
    }

    public void addEnergyCube() {
        if (energyCubes < 10) {
            energyCubes++;
            board.updateBoard();
        }
    }

    public void updateUpgradeCards(String[] permCardsStr, String[] tempCardsStr, GameController gameController) {
        // Remove cards that are no longer present
        Iterator<UpgradeCard> iterator = upgradeCards.iterator();
        while (iterator.hasNext()) {
            UpgradeCard upgradeCard = iterator.next();
            if (permCardsStr != null && upgradeCard instanceof UpgradeCardPermanent) {
                if (Arrays.stream(permCardsStr).allMatch(cardStr -> cardStr == null || cardStr.equals(upgradeCard.getEnum().name()))) {
                    iterator.remove();
                    System.out.println("Removed " + upgradeCard.getDisplayName() + " from player: \"" + name + "\".");
                }
            } else if (tempCardsStr != null && upgradeCard instanceof UpgradeCardTemporary) {
                if (Arrays.stream(tempCardsStr).noneMatch(cardStr -> cardStr != null && cardStr.equals(upgradeCard.getEnum().name()))) {
                    iterator.remove();
                    System.out.println("Removed " + upgradeCard.getDisplayName() + " from player: \"" + name + "\".");
                }
            }
        }

        // Ensure that present cards are added and initialized
        addAndInitializeNewCards(permCardsStr, gameController);
        addAndInitializeNewCards(tempCardsStr, gameController);

        // Ensure that present cards are set at the correct position.
        updateUpgradeCardPositions(permCardsStr, NO_OF_PERMANENT_UPGRADE_CARDS, permanentUpgradeCardFields);
        updateUpgradeCardPositions(tempCardsStr, NO_OF_TEMPORARY_UPGRADE_CARDS, temporaryUpgradeCardFields);
    }

    private void addAndInitializeNewCards(String[] cardsStr, GameController gameController) {
        if (cardsStr == null) return;

        for (String cardStr : cardsStr) {
            if (cardStr == null) continue;
            if (upgradeCards.stream().noneMatch(upgradeCard -> upgradeCard.getEnum().name().equals(cardStr))) {
                // Adding and initializing upgrade card
                UpgradeCard upgradeCard = UpgradeCard.getUpgradeCardFromClass(UpgradeCard.Types.valueOf(cardStr).upgradeCardClass);
                addUpgradeCard(upgradeCard, gameController);
                System.out.println("Added " + upgradeCard.getDisplayName() + " to player: \"" + name + "\".");
            }
        }
    }

    private void updateUpgradeCardPositions(String[] cardsStr, int noOfUpgradeCards, CardField[] upgradeCardFields) {
        if (cardsStr == null) return;

        for (int i = 0; i < noOfUpgradeCards; i++) {
            UpgradeCard upgradeCardAtPosition = null;
            if (cardsStr[i] != null) {
                for (UpgradeCard upgradeCard : upgradeCards) {
                    if (upgradeCard instanceof UpgradeCardPermanent) {
                        if (cardsStr[i].equals(upgradeCard.getEnum().name())) {
                            upgradeCardAtPosition = upgradeCard;
                            break;
                        }
                    }
                }
            }
            upgradeCardFields[i].setCard(upgradeCardAtPosition);
        }
    }

    public boolean attemptUpgradeCardPurchase(Card card, GameController gameController) {
        UpgradeCard boughtCard = null;

        try {
            for (UpgradeCard ownedCard : upgradeCards) { // First, check if player already has this card
                if (ownedCard.getClass().equals(card.getClass())) return false;
            }
            boughtCard = board.getUpgradeShop().attemptBuyCardFromShop(card, this);
            if (boughtCard != null) {
                System.out.println("Player: \"" + name + "\" bought: \"" + boughtCard.getDisplayName() + "\".");
                addUpgradeCard(boughtCard, gameController);
            }
            board.updateBoard();
        } catch (Exception e) {
            System.out.println("ERROR - Attempt to buy upgrade card from shopField failed.");
            System.out.println(e.getMessage());
        }

        return boughtCard != null;
    }

    public boolean tryAddFreeUpgradeCard(UpgradeCard card, GameController gameController, int index) {
        boolean couldAdd = false;

        if (card instanceof UpgradeCardPermanent) {
            couldAdd = couldAddUpgradeCard(card, index, permanentUpgradeCardFields);
        } else if (card instanceof UpgradeCardTemporary) {
            couldAdd = couldAddUpgradeCard(card, index, temporaryUpgradeCardFields);
        }

        if (couldAdd) {
            System.out.println("Player: \"" + name + "\" received: \"" + card.getDisplayName() + "\".");
            addUpgradeCard(card, gameController);
        }

        return couldAdd;
    }

    private boolean couldAddUpgradeCard(UpgradeCard card, int index, CardField[] cardFields) {
        for (int i = 0; i < cardFields.length; i++) {
            if (cardFields[i].getCard() == null) {
                if (index == -1 || i == index) {
                    cardFields[i].setCard(card);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Method for initializing an UpgradeCard and setting the player as owner. Should be called from Player.attemptUpgradeCardPurchase() or Player.tryAddFreeUpgradeCard()
     */
    private void addUpgradeCard(UpgradeCard upgradeCard, GameController gameController) {
        try {
            upgradeCards.add(upgradeCard);
            upgradeCard.initialize(this, gameController);
        } catch (NullPointerException e) {
            System.out.println("ERROR - Attempted to add upgradeCard of value NULL to player: \"" + name + "\".");
            System.out.println(e.getMessage());
        }
    }

    public void removeUpgradeCard(UpgradeCard upgradeCard) {
        if (!upgradeCards.contains(upgradeCard)) return;

        try {
            // Return to shop
            board.getUpgradeShop().returnCardToShop(upgradeCard);
            System.out.println("Player: \"" + name + "\" returned: \"" + upgradeCard.getDisplayName() + "\" to the shop.");

            // Remove from player
            upgradeCards.remove(upgradeCard);
            if (upgradeCard instanceof UpgradeCardPermanent) {
                for (CardField cardField : permanentUpgradeCardFields) {
                    if (upgradeCard.equals(cardField.getCard())) {
                        cardField.setCard(null);
                    }
                }
            } else if (upgradeCard instanceof UpgradeCardTemporary) {
                for (CardField cardField : temporaryUpgradeCardFields) {
                    if (upgradeCard.equals(cardField.getCard())) {
                        cardField.setCard(null);
                    }
                }
            }
            // Removes the player as owner of the card and removes card events from EventHandler.
            upgradeCard.unInitialize();
        } catch (NullPointerException e) {
            System.out.println("ERROR - Attempted to remove upgradeCard of value NULL from player: \"" + name + "\".");
            e.printStackTrace();
        }
    }

    /**
     * sets the fields programmingDeck to its default settings.
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
     * adds the parameter card into the discarded end of programmingDeck.
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
        for (int i = 0; i < maxNoOfCardsInHand; i++) {
            if (cardHandFields[i].getCard() == null) {
                cardHandFields[i].setCard(drawFromDeck());
            }
        }
    }

    /**
     * discards all cards in registeres and on hand
     * @author Maximillian Bjørn Mortensen
     */
    public void discardAll() {
        discardProgram();
        discardHand();
    }

    public void discardProgram() {
        for (CardField c: programFields) {
            if (c.getCard() != null) {
                if(c.getCard() instanceof CommandCard commandCard && !commandCard.getCommand().isDamage()){
                    discard(commandCard); // Discard used command cards
                }
                c.setCard(null);
            }
        }
    }

    public void discardHand() {
        for (CardField c: cardHandFields) {
            if (c.getCard() != null) {
                if(c.getCard() instanceof CommandCard commandCard) {
                    discard(commandCard);
                    c.setCard(null);
                }
            }
        }
    }

    public void setEnergyCubes(int energyCubes) {
        this.energyCubes = energyCubes;
        board.updateBoard();
    }

    public void fillRestOfRegisters() {
        for (int i = 0; i < programFields.length; i++) {
            CardField programField = programFields[i];
            boolean drawCard =
                    (programField.getCard() == null && DRAW_ON_EMPTY_REGISTER) ||
                    (programField.getCard() != null && ((CommandCard)programField.getCard()).getCommand().drawsTopCard());
            if (!drawCard) continue;

            if (i == 0) {
                programField.setCard(drawCardFromDeckForFirstRegister());
            } else {
                programField.setCard(drawFromDeck());
            }
        }
    }
    private CommandCard drawCardFromDeckForFirstRegister() {
        CommandCard card = drawFromDeck();
        if (card.getCommand().isAgainType()) {
            return drawCardFromDeckForFirstRegister();
        } else {
            return card;
        }
    }

    /**
     * Method for queuing a player command, that upgrade cards should listen to. (That means commands that are not a command option.)
     */
    public void queueCommand(Command command, GameController gameController) {
        queueCommand(command, true, gameController);
    }

    /**
     * * sets the players action from the command
     * @param notifyUpgradeCards Set to true, is the player is executing a programming card. If the player is executing an interaction option, we don't want to notify the Types (again).
     * @author Maximillian Bjørn Mortensen
     */
    public void queueCommand(Command command, boolean notifyUpgradeCards, GameController gameController) {
        if (command == null) return;
        // Call the event handler, and let it modify the command
        if (notifyUpgradeCards) {
            command = EventHandler.event_RegisterActivate(this, command);
        }

        switch (command) {
            case MOVE_0:
                break;
            case MOVE_1:
                setVelocity(new Velocity(1, 0));
                startMovement();
                break;
            case MOVE_2:
                setVelocity(new Velocity(2, 0));
                startMovement();
                break;
            case MOVE_3, SPEED_ROUTINE:
                setVelocity(new Velocity(3, 0));
                startMovement();
                break;
            case RIGHT_TURN:
                turn(1);
                break;
            case LEFT_TURN:
                turn(-1);
                break;
            case U_TURN:
                turn(2);
                break;
            case MOVE_BACK:
                setVelocity(new Velocity(-1, 0));
                startMovement();
                break;
            case AGAIN, REPEAT_ROUTINE:
                queueCommand(getLastCmd(), gameController);
                break;
            case POWER_UP:
                addEnergyCube();
                break;

            // Damage
            case SPAM:
                board.getBoardActionQueue().addFirst(new ActionWithDelay(() -> {
                }, 0, "{" + getName() + "} activated: (" + command.displayName + ") damage."));
                break;
            case TROJAN_HORSE:
                board.getBoardActionQueue().addFirst(new ActionWithDelay(() -> {
                    for (int i = 0; i < 2; i++) {
                        discard(new CommandCard(Command.SPAM));
                    }
                }, 150, "{" + getName() + "} activated: (" + command.displayName + ") damage."));
                break;
            case WORM:
                board.getBoardActionQueue().addFirst(new ActionWithDelay(() -> {
                    EventHandler.event_PlayerReboot(this, false, gameController);
                }, 150, "{" + getName() + "} activated: (" + command.displayName + ") damage."));
                break;
            case VIRUS:
                board.getBoardActionQueue().addFirst(new ActionWithDelay(() -> {
                    for (Player foundPlayer : board.getPlayers()) {
                        if (space.getDistanceFromOtherSpace(foundPlayer.space) <= 6) {
                            foundPlayer.discard(new CommandCard(Command.VIRUS));
                        }
                    }
                }, 150, "{" + getName() + "} activated: (" + command.displayName + ") damage."));
                break;

            // Special programming cards
            case ENERGY_ROUTINE:
                addEnergyCube();
                break;
            case SPAM_FOLDER:
                boolean inDiscardDeck = false;
                for (CommandCard card : programmingDeck) {
                    if (inDiscardDeck) {
                        if (card.command == Command.SPAM) {
                            programmingDeck.remove(card);
                        }
                    }
                    if (card == null) inDiscardDeck = true;
                }
                break;

            case CRAB_MOVE:
                setForwardVelocity(1);
                startMovement();
                queueCommand(Command.CRAB_DIRECTION, gameController);
                break;
            case CRAB_STAY:
                queueCommand(Command.CRAB_DIRECTION, gameController);
                break;
            case CRAB_MOVE_LEFT:
                setVelocity(new Velocity(0, -1));
                startMovement();
                queueCommand(Command.MOVE_1, false, gameController);

                break;
            case CRAB_MOVE_RIGHT:
                setVelocity(new Velocity(0, 1));
                startMovement();
                queueCommand(Command.MOVE_1, false, gameController);
                break;


            // Commands with options
            default:
                if (command.isInteractive()) {
                    gameController.addPlayerInteraction(new CommandOptionsInteraction(gameController, this, command.getOptions()));
                } else {
                    System.out.println("Can't find command: " + command.displayName);
                }
                break;
        }

        // If the card is repeatable and wasn't an option command.
        if (command.repeatable && notifyUpgradeCards) {
            setLastCmd(command);
        }

        // The current move counter is set to the "old movecounter" + "1"
        board.setMoveCounter(board.getMoveCounter() + 1); // Increase the move counter by one
    }

    /**
     * Moves the player based on heading and velocity
     * @author Maximillian Bjørn Mortensen
     */
    public void startMovement() {
        // We take stepwise movement, and call moveCurrentPlayerToSpace() for each.
        Velocity temp = new Velocity(velocity.forward,velocity.right);
        // For each forward movement
        for (int i = 0; i < Math.abs(temp.forward); i++) {
            board.getBoardActionQueue().addLast(new ActionWithDelay(() -> {
                if (temp.forward!=0){
                Heading direction = (temp.forward > 0) ? heading : heading.opposite();
                // Decrement
                //velocity.forward -= (velocity.forward > 0) ? 1 : -1;
                if (!getIsRebooting()) {
                    board.movePlayerToSpace(this, board.getNeighbour(space, direction));
                }}
            }, 150, "Player movement: " + getName()));
        }

        // For each sideways movement
        for (int i = 0; i < Math.abs(temp.right); i++) {
            board.getBoardActionQueue().addLast(new ActionWithDelay(() -> {
                if (temp.right!=0){
                Heading direction = (temp.right > 0) ? heading.next() : heading.prev();
                             // Decrement
                //velocity.right -= (velocity.right > 0) ? 1 : -1;
                if (!getIsRebooting()) {
                    board.movePlayerToSpace(this, board.getNeighbour(space, direction));
                }}
            }, 150, "Player movement: " + getName()));
        }
    }

    /**
     * sets heading for player based on paramater
     * @author Maximillian Bjørn Mortensen
     */
    public void turn(int quarterRotationClockwise) {
        boolean rotateClockwise = quarterRotationClockwise > 0;
        for (int i = 0; i < Math.abs(quarterRotationClockwise); i++) {
            board.getBoardActionQueue().addFirst(new ActionWithDelay(() -> {
                Heading prevOrientation = heading;
                Heading newOrientation = rotateClockwise ? prevOrientation.next() : prevOrientation.prev();
                if (!getIsRebooting()) {
                    setHeading(newOrientation);
                }
            }, 150, "Player rotation: " + getName()));
        }
    }

    /**
     * Method for immediately shooting a laser. It is used to make multiple lasers at the same time or forcing a laser to be fired.
     * @param direction The direction the laser should fire.
     */
    public void shootLaser(Heading direction) {
        Laser laser = new Laser(space, direction, this, Player.class, Space.class);

        List<PlayerShootListener> playerShootListeners = getPlayerCardEventListeners(this, PlayerShootListener.class);
        for (PlayerShootListener listener : playerShootListeners) {
            laser = listener.onPlayerShoot(this, laser,true);
        }

        EventHandler.event_PlayerShootHandle(this, laser);
    }

    public void addTemporaryBonusDamage(Damage bonusDamage) {
        this.temporaryBonusDamage.add(bonusDamage);
    }
    public Damage useTemporaryBonusDamage() {
        Damage usedTemporaryBonusDamage = new Damage(0, 0, 0, 0);
        usedTemporaryBonusDamage.add(this.temporaryBonusDamage);
        this.temporaryBonusDamage.clear();
        return usedTemporaryBonusDamage;
    }

    public void setRegisters(String[] registers) {
        for (int i = 0; i < programFields.length; i++) {
            String cardName = registers[i];
            if (cardName == null || cardName.isBlank()) {
                programFields[i].setCard(null);
            } else {
                programFields[i].setCard(new CommandCard(Command.valueOf(cardName)));
            }
        }
    }
}
