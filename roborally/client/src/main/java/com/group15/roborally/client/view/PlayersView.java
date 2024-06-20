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
package com.group15.roborally.client.view;

import com.group15.observer.Subject;
import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Board;
import javafx.scene.control.TabPane;

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
        playerViews = new PlayerView[1];
        playerViews[0] = new PlayerView(gameController, gameController.getLocalPlayer());
        this.getTabs().add(playerViews[0]);

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
        this.setTabMaxHeight(Double.MAX_VALUE);
        this.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab != null) {
                System.out.println("Selected Tab: " + newTab.getText());
                selectedPlayerView = (PlayerView) newTab;
            }
        });
        selectedPlayerView = (PlayerView)this.getSelectionModel().selectedItemProperty().get();

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
