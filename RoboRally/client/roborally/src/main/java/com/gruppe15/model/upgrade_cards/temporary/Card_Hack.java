package com.gruppe15.model.upgrade_cards.temporary;

import com.gruppe15.controller.GameController;
import com.gruppe15.model.Phase;
import com.gruppe15.model.Player;
import com.gruppe15.model.upgrade_cards.UpgradeCardTemporary;

public class Card_Hack extends UpgradeCardTemporary {

    public Card_Hack() {
        super("Hack", 1, 0, 1, null, Phase.PLAYER_ACTIVATION);
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
        owner.queueCommand(owner.getLastCmd(), gameController);
        super.onActivated();
    }
}
