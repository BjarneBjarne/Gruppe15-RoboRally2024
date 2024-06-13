package com.group15.model.upgrade_cards.temporary;

import com.group15.controller.GameController;
import com.group15.model.*;
import com.group15.model.upgrade_cards.UpgradeCardTemporary;

public class Card_Recharge extends UpgradeCardTemporary {

    public Card_Recharge() {
        super("Recharge", 0, 0, 1, null, Phase.PROGRAMMING, Phase.PLAYER_ACTIVATION, Phase.BOARD_ACTIVATION, Phase.UPGRADE);
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
        owner.setEnergyCubes(owner.getEnergyCubes() + 3);
        super.onActivated();
    }
}
