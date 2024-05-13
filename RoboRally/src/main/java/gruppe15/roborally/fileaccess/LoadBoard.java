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
package gruppe15.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonWriter;

import gruppe15.roborally.fileaccess.model.BoardTemplate;
import gruppe15.roborally.fileaccess.model.PlayerTemplate;
import gruppe15.roborally.fileaccess.model.SpaceTemplate;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Command;
import gruppe15.roborally.model.CommandCard;
import gruppe15.roborally.model.Player;
import gruppe15.roborally.model.Space;
import gruppe15.roborally.model.boardelements.BoardElement;
import gruppe15.roborally.model.utils.ImageUtils;

import java.io.*;
import java.util.Scanner;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(File loadedFile) {

		// In simple cases, we can create a Gson object with new Gson():
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
                excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        Gson gson = simpleBuilder.create();

		Board result;

		try {
            Scanner scanner = new Scanner(loadedFile);
            String testJson = "";
            while (scanner.hasNextLine()) {
                testJson += scanner.nextLine();
            }

            BoardTemplate template = gson.fromJson(testJson, BoardTemplate.class);
			result = new Board(template.width, template.height, -1);
			for (SpaceTemplate spaceTemplate: template.spaces) {
			    Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
			    if (space != null) {
                    space.setBoardElement(spaceTemplate.boardElement);
                    space.setBackgroundImage(ImageUtils.base64ToImage(spaceTemplate.backgroundImage));
                    space.getWalls().addAll(spaceTemplate.walls);
                    if (spaceTemplate.elementImage != null) {
                        space.getBoardElement().setImage(ImageUtils.base64ToImage(spaceTemplate.elementImage));
                    }
                }
            }
            
            for(int i = 0; i < template.players.length; i++){
                Player player = loadPlayer(template.players[i], result);
                result.getPlayers().add(player);
                if(player.getName() == template.currentPlayer){
                    result.setCurrentPlayer(player);
                }
            }
            scanner.close();
            System.out.println("Load complete");
			return result;
		} catch (IOException e1) {
            // if (reader != null) {
            //     try {
            //         reader.close();
            //         inputStream = null;
            //     } catch (IOException e2) {}
            // }
            // if (inputStream != null) {
			// 	try {
			// 		inputStream.close();
			// 	} catch (IOException e2) {}
			// }
		}
		return null;
    }

    private static PlayerTemplate savePlayer(Player player){
        PlayerTemplate playerTemplate = new PlayerTemplate();
        playerTemplate.name = player.getName();
        playerTemplate.robot = player.getRobot();
        playerTemplate.space = player.getSpace();
        playerTemplate.heading = player.getHeading();
        playerTemplate.cards = new Command[player.getCards().length];
        for(int i = 0; i < player.getCards().length; i++){
            playerTemplate.cards[i] = player.getCardField(i).getCard().getCommand();
        }
        playerTemplate.checkpoints = player.getCheckpoints();
        playerTemplate.energyCubes = player.getEnergyCubes();
        playerTemplate.spawnPoint = player.getSpawnPoint();

        return playerTemplate;
    }

    private static Player loadPlayer(PlayerTemplate playerTemplate, Board board){
        Player player = new Player(board, playerTemplate.robot, playerTemplate.name);
        player.setHeading(playerTemplate.heading);
        for(int i = 0; i < playerTemplate.cards.length; i++){
            player.getCardField(i).setCard(new CommandCard(playerTemplate.cards[i]));
        }
        player.setCheckpoint(playerTemplate.checkpoints);
        player.setEnergyCubes(playerTemplate.energyCubes);

        int x = playerTemplate.space.x;
        int y = playerTemplate.space.y;
        player.setSpace(board.getSpace(x, y));
        board.getSpace(x, y).setPlayer(player);

        x = playerTemplate.spawnPoint.x;
        y = playerTemplate.spawnPoint.y;
        player.setSpawn(board.getSpace(x, y));
        
        player.setImage(ImageUtils.getImageFromName(player.getRobot().getBoardImageName()));
        player.setCharImage(ImageUtils.getImageFromName(player.getRobot().getSelectionImageName()));
        return player;
    }

    public static void serializePlayers(PlayerTemplate[] players){
        Gson gson = new GsonBuilder().
            excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).
            create();
        String playersToJson = gson.toJson(players);
        System.out.println(playersToJson);
    }

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;
        template.players = new PlayerTemplate[board.getPlayers().size()];
        for(int i = 0; i < board.getPlayers().size(); i++){
            template.players[i] = savePlayer(board.getPlayers().get(i));
        };
        template.currentPlayer = board.getCurrentPlayer().getName();

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.boardElement = space.getBoardElement();
                    spaceTemplate.walls.addAll(space.getWalls());
                    spaceTemplate.backgroundImage = ImageUtils.imageToBase64(space.getImage());
                    if (space.getBoardElement() != null)
                        spaceTemplate.elementImage = ImageUtils.imageToBase64(space.getBoardElement().getImage());
                    template.spaces.add(spaceTemplate);
            }
        }

        // TODO: this is not very defensive, and will result in a NullPointerException
        //       when the folder "resources" does not exist! But, it does not need
        //       the file "simpleCards.json" to exist!
        String filename = "RoboRally/src/main/resources/gruppe15/roborally/saveGames/" + name + "." + JSON_EXT;
                // classLoader.getResource(BOARDSFOLDER).getPath() + "/" + name + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
                excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;
        try {
            fileWriter = new FileWriter(filename);
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

}
