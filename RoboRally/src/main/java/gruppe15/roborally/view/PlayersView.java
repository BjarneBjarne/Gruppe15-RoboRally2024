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
package gruppe15.roborally.view;

import gruppe15.observer.Subject;
import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.Board;
import gruppe15.roborally.model.Player;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Region;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayersView extends TabPane implements ViewObserver {

    private final Board board;

    private final PlayerView[] playerViews;
    private PlayerView selectedPlayerView;

    public PlayersView(GameController gameController) {
        board = gameController.board;

        playerViews = new PlayerView[board.getNoOfPlayers()];
        for (int i = 0; i < board.getNoOfPlayers(); i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i));
            this.getTabs().add(playerViews[i]);
        }

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.setTabMaxHeight(Double.MAX_VALUE);
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                System.out.println("Selected Tab: " + newTab.getText());
                selectedPlayerView = (PlayerView) newTab;
            }
        });

        board.attach(this);
        update(board);
    }

    public PlayerView getSelectedPlayerView() {
        return selectedPlayerView;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            //Player current = board.getCurrentPlayer();
            //this.getSelectionModel().select(board.getPlayerNumber(current));
        }
    }

}
