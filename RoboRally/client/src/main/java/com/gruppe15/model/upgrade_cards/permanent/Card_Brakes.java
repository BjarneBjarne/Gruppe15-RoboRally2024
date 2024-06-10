package com.gruppe15.model.upgrade_cards.permanent;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.*;
import com.gruppe15.model.events.PlayerCommandListener;
import com.gruppe15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_Brakes extends UpgradeCardPermanent {

    public Card_Brakes() {
        super("Brakes", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnMoveStart
        eventListeners.add(EventHandler.subscribe((PlayerCommandListener) command -> {
            if (command == Command.MOVE_1) {
                printUsage();
                return Command.BRAKES;
            } else {
                return command;
            }
        }, owner));
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {

    }
}
