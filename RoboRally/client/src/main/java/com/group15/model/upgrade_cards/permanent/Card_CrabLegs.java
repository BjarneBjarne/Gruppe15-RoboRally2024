package com.group15.model.upgrade_cards.permanent;

import com.group15.controller.GameController;
import com.group15.model.Command;
import com.group15.model.EventHandler;
import com.group15.model.Player;
import com.group15.model.events.PlayerCommandListener;
import com.group15.model.upgrade_cards.UpgradeCardPermanent;

public class Card_CrabLegs extends UpgradeCardPermanent {

    public Card_CrabLegs() {
        super("Crab Legs", 5, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnMoveStart
        eventListeners.add(EventHandler.subscribe((PlayerCommandListener) command -> {
            if (command == Command.MOVE_1) {
                printUsage();
                owner.queueCommand(Command.CRAB_LEGS, gameController);
                owner.queueCommand(Command.MOVE_1, false, gameController);
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
