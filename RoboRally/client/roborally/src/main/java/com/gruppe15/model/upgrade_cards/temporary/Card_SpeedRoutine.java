package com.gruppe15.model.upgrade_cards.temporary;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.Command;
import com.gruppe15.model.CommandCard;
import com.gruppe15.model.Phase;
import com.gruppe15.model.Player;
import com.gruppe15.model.upgrade_cards.UpgradeCardTemporary;

public class Card_SpeedRoutine extends UpgradeCardTemporary {

    public Card_SpeedRoutine() {
        super("Speed Routine", 3, 0, 1, null, Phase.PROGRAMMING);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);
    }

    @Override
    protected void onEnabled() {

    }

    @Override
    protected void onDisabled() {

    }

    @Override
    protected void onActivated() {
        owner.discard(new CommandCard(Command.SPEED_ROUTINE));
        super.onActivated();
    }
}