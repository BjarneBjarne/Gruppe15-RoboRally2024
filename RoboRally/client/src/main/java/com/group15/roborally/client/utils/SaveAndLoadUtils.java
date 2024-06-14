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
package com.group15.roborally.client.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import com.group15.roborally.client.BoardOptions;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.exceptions.EmptyCourseException;
import com.group15.roborally.client.templates.BoardTemplate;
import com.group15.roborally.client.templates.PlayerTemplate;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.boardelements.BoardElement;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCard;
import com.group15.roborally.client.model.Player;
import javafx.util.Pair;

import java.io.*;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class SaveAndLoadUtils {
    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    /**
     * This method loads a board from a .json file. The file is deserialized into 
     * BoardTemplate and PlayerTemplate object, which is then used to create new 
     * Board and Player objects. The board is then populated with the players and
     * the board elements. The relations between the players and spaces on the board
     * are also set.
     *
     * @param boardTemplate the board template from which the board is to created from.
     * @return the board loaded from the file.
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public static Board loadBoard(BoardTemplate boardTemplate, List<CC_CourseData> courses) throws EmptyCourseException {
        CC_CourseData boardCourseData = null;
        for (CC_CourseData courseData : courses) {
            if (courseData.getCourseName().equals(boardTemplate.getCourseName())) {
                boardCourseData = courseData;
            }
        }
        if (boardCourseData == null) {
            throw new EmptyCourseException("No course found with course name: \"" + boardTemplate.getCourseName() + "\".");
        }

        Pair<List<Space[][]>, Space[][]> courseSpaces = boardCourseData.getGameSubBoards();
        List<Space[][]> boardSubBoards = courseSpaces.getKey();
        Space[][] boardSpaces = courseSpaces.getValue();

        return new Board(boardSubBoards, boardSpaces, boardCourseData.getCourseName(), boardCourseData.getNoOfCheckpoints());
    }

    public static void loadPlayers(BoardTemplate boardTemplate, Board board, GameController gameController) {
        for (int i = 0; i < boardTemplate.getPlayers().length; i++) {
            com.group15.roborally.client.model.Player player = loadPlayer(boardTemplate.getPlayers()[i], board, gameController);
            board.getPlayers().add(player);
        }
    }

    /**
     * Method imports field values from the PlayerTemplate object into a new Player object.
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param playerTemplate the playerTemplate object to be loaded
     * @param board the board on which the player is to be loaded
     * @return the player object loaded from the PlayerTemplate object
     */
    private static com.group15.roborally.client.model.Player loadPlayer(PlayerTemplate playerTemplate, Board board, GameController gameController) {
        com.group15.roborally.client.model.Player player = new com.group15.roborally.client.model.Player(board, playerTemplate.robot, playerTemplate.name);

        // SpawnPoint
        int spawnX = playerTemplate.spawnPoint.x;
        int spawnY = playerTemplate.spawnPoint.y;
        player.setSpawn(board.getSpace(spawnX, spawnY));

        // Position and heading
        int x = playerTemplate.space.x;
        int y = playerTemplate.space.y;
        player.setSpace(board.getSpace(x, y));
        player.setHeading(playerTemplate.heading);

        // Stats
        player.setEnergyCubes(playerTemplate.energyCubes);
        player.setCheckpoint(playerTemplate.checkpoints);

        // Programming deck
        player.setProgrammingDeck(playerTemplate.getProgrammingDeck());

        // Card fields
        for (int i = 0; i < player.getCardHandFields().length; i++) {
            Command command = playerTemplate.cardsInHand[i];
            if (command == null) continue;
            player.getCardField(i).setCard(new CommandCard(command));
        }
        for (int i = 0; i < player.getProgramFields().length; i++) {
            Command command = playerTemplate.programCards[i];
            if (command == null) continue;
            player.getProgramField(i).setCard(new CommandCard(command));
        }
        for (int i = 0; i < player.getPermanentUpgradeCardFields().length; i++) {
            if (playerTemplate.permanentUpgradeCards[i] == null) continue;
            UpgradeCard upgradeCard = board.getUpgradeShop().attemptReceiveFreeCardFromShop(playerTemplate.permanentUpgradeCards[i].getClass(), player);
            player.tryAddFreeUpgradeCard(upgradeCard, gameController, i);
        }
        for (int i = 0; i < player.getTemporaryUpgradeCardFields().length; i++) {
            if (playerTemplate.temporaryUpgradeCards[i] == null) continue;
            UpgradeCard upgradeCard = board.getUpgradeShop().attemptReceiveFreeCardFromShop(playerTemplate.temporaryUpgradeCards[i].getClass(), player);
            player.tryAddFreeUpgradeCard(upgradeCard, gameController, i);
        }

        return player;
    }

    /**
     * This method saves a board to a .json file. Field values board are imported 
     * into a BoardTemplate object, which is then serialized into a .json file.
     *
     * @param board the board to be saved.
     * @param file the file to be written to.
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     */
    public static void saveBoard(Board board, File file) {
        BoardTemplate template = new BoardTemplate();
        template.setCourseName(board.getCourseName());
        PlayerTemplate[] playerTemplates = new PlayerTemplate[BoardOptions.NO_OF_PLAYERS];
        for(int i = 0; i < board.getPlayers().size(); i++) {
            playerTemplates[i] = savePlayer(board.getPlayers().get(i));
        }
        template.setPlayers(playerTemplates);

        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
                excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(file);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {}
            }
        }
    }

    /**
     * Method imports field values from Player object into PlayerTemplate object, which
     * will be inserted as a field variable in BoardTemplate object.
     * 
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @param player the player object to be saved
     * @return the player object as a PlayerTemplate object
     */

     private static PlayerTemplate savePlayer(Player player) {
         PlayerTemplate playerTemplate = new PlayerTemplate();
         playerTemplate.name = player.getName();
         playerTemplate.robot = player.getRobot();
         playerTemplate.space = player.getSpace();
         playerTemplate.heading = player.getHeading();

         CardField[] playerCardHandFields = player.getCardHandFields();
         playerTemplate.cardsInHand = new Command[playerCardHandFields.length];
         for(int i = 0; i < playerCardHandFields.length; i++) {
             CommandCard commandCard = (CommandCard) playerCardHandFields[i].getCard();
             if (commandCard != null) {
                 playerTemplate.cardsInHand[i] = commandCard.getCommand();
             }
         }

         CardField[] playerProgramFields = player.getProgramFields();
         playerTemplate.programCards = new Command[playerProgramFields.length];
         for(int i = 0; i < playerProgramFields.length; i++) {
             CommandCard commandCard = (CommandCard) playerProgramFields[i].getCard();
             if (commandCard != null) {
                 playerTemplate.programCards[i] = commandCard.getCommand();
             }
         }

         CardField[] playerPermanentUpgradeFields = player.getPermanentUpgradeCardFields();
         playerTemplate.permanentUpgradeCards = new UpgradeCard[playerPermanentUpgradeFields.length];
         for(int i = 0; i < playerPermanentUpgradeFields.length; i++) {
             UpgradeCard upgradeCard = (UpgradeCard) playerPermanentUpgradeFields[i].getCard();
             if (upgradeCard != null) {
                 playerTemplate.permanentUpgradeCards[i] = upgradeCard;
             }
         }

         CardField[] playerTemporaryUpgradeFields = player.getTemporaryUpgradeCardFields();
         playerTemplate.temporaryUpgradeCards = new UpgradeCard[playerTemporaryUpgradeFields.length];
         for(int i = 0; i < playerTemporaryUpgradeFields.length; i++) {
             UpgradeCard upgradeCard = (UpgradeCard) playerTemporaryUpgradeFields[i].getCard();
             if (upgradeCard != null) {
                 playerTemplate.temporaryUpgradeCards[i] = upgradeCard;
             }
         }

         playerTemplate.energyCubes = player.getEnergyCubes();
         playerTemplate.checkpoints = player.getCheckpoints();
         playerTemplate.spawnPoint = player.getSpawnPoint();
         playerTemplate.setProgrammingDeck(player.getProgrammingDeck().stream().toList());

         return playerTemplate;
    }

}
