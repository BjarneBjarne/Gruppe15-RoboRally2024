package com.gruppe15.model.upgrade_cards.temporary;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.*;
import com.gruppe15.model.upgrade_cards.UpgradeCard;
import com.gruppe15.model.upgrade_cards.UpgradeCardTemporary;

public class Card_EnergyRoutine extends UpgradeCardTemporary {

    public Card_EnergyRoutine() {
        super("Energy Routine", 3, 0, 1, null, Phase.PROGRAMMING);
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
        owner.discard(new CommandCard(Command.ENERGY_ROUTINE));
        super.onActivated();
    }
}
