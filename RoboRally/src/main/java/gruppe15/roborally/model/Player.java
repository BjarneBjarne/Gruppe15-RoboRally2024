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
import gruppe15.roborally.model.upgrades.*;
import gruppe15.roborally.model.utils.ImageUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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

    final public Board board;

    private String name;
    private Robots robot;

    private Space space;
    private Space temporarySpace = null;
    private Heading heading = SOUTH;

    private Command lastCmd;

    private final CommandCardField[] program;
    private final CommandCardField[] cards;
    private int energyCubes = 0;
    private int checkpoints = 0;
    private int priority = 0;
    private Velocity velocity;
    private boolean rebooting = false;
    private Space spawnPoint; //  If you rebooted from the start board, place your robot on the space where you started the game.
    private Image image;
    private Image charIMG;

    transient private Queue<CommandCard> programmingDeck = new LinkedList<>();
    transient private final List<UpgradeCard> upgradeCards = new ArrayList<>(); // Not for card function, but could be used for showing the players upgrade cards.


    public Player(@NotNull Board board, @NotNull Robots robot, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.robot = robot;
        this.space = null;
        this.image = ImageUtils.getImageFromName(robot.getBoardImageName());
        this.charIMG = ImageUtils.getImageFromName(robot.getSelectionImageName());

        program = new CommandCardField[NO_OF_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this,i+1);
        }

        cards = new CommandCardField[NO_OF_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }

        setProgrammingDeckToDefoult();
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
        return temporarySpace;
    }
    public void setTemporarySpace(Space space) {
        this.temporarySpace = space;
    }
    public void goToTemporarySpace() {
        if (temporarySpace != null && temporarySpace.getPlayer() == null) {
            // Surpass the setSpace() checks.
            this.space.setPlayer(null);
            this.space = temporarySpace;
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

    public void setIsRebooting(boolean isRebooting) {
        System.out.println(name + " rebooting: " + isRebooting);
        this.rebooting = isRebooting;
    }
    public boolean getIsRebooting() {
        return this.rebooting;
    }

    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    public void addEnergyCube() {
        energyCubes++;
    }

    public CommandCardField[] getProgram() {
        return program;
    }

    public CommandCardField[] getCards() {
        return cards;

    }

    public void buyUpgradeCard(UpgradeCard upgradeCard) {
        upgradeCards.add(upgradeCard);
        upgradeCard.initialize(board, this);
    }

    /**
     * sets the fiels programmingDeck to its defoult settings
     * @author Maximillian Bjørn Mortensen
     */
    public void setProgrammingDeckToDefoult(){
        List<Integer> index = new ArrayList<Integer>();
        for(int i = 0; i < 20; i++){
            if(i<9){
                index.add(i);
            }else if(i<18){
                index.add(i-9);
            }else{
                index.add(i-18);
            }
        }
        Collections.shuffle(index);
        Command[] commands = Command.values();
        for(int i = 0; i < 20; i++) {
            programmingDeck.add(new CommandCard(commands[index.remove(0)]));
        }
        programmingDeck.add(null);
    }

    /**
     * shuffels the contents of the field programmingDeck
     * @author Maximillian Bjørn Mortensen
     */
    private void shuffleDiscardedIntoDeck(){
        List<CommandCard> temp = new ArrayList<>(programmingDeck);
        programmingDeck.clear();
        Collections.shuffle(temp);
        programmingDeck.addAll(temp);
        programmingDeck.add(null);
    }

    /**
     * adds the paramater card into the discarded end of programmingDeck
     * @param card
     * @author Maximillian Bjørn Mortensen
     */
    private void discard(CommandCard card){
        programmingDeck.add(new CommandCard(card.command));
    }

    /**
     * removes the top card in programmingDeck and returns a copy
     * @return CommandCard
     * @author Maximillian Bjørn Mortensen
     */
    private CommandCard drawFromDeck(){
        CommandCard temp = programmingDeck.remove();
        if(temp == null) return null;
        return new CommandCard(temp.command);
    }

    /**
     * inputs cards from programmingDeck into all CommandCardFields in cards
     * @author Maximillian Bjørn Mortensen
     */
    public void drawHand(){
        for(CommandCardField c: cards){
            if(c.getCard() == null){
                CommandCard temp = drawFromDeck();
                if(temp == null){
                    shuffleDiscardedIntoDeck();
                    temp = drawFromDeck();
                }
                c.setCard(temp);
            }
        }
    }

    /**
     * discards all cards in registeres and on hand
     * @author Maximillian Bjørn Mortensen
     */
    public void discardAll(){
        for(CommandCardField c: program){
            if(c.getCard() != null){
                discard(c.getCard());
                c.setCard(null);
            }
        }
        for(CommandCardField c: cards){
            if(c.getCard() != null){
                discard(c.getCard());
                c.setCard(null);
            }
        }
    }
}
