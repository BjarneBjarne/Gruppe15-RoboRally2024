package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Phase;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;

public class Card_Recompile extends UpgradeCardTemporary {

    public Card_Recompile() {
        super("Recompile", 1, 0, 1, null, Phase.PROGRAMMING);
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
        owner.discardHand();
        owner.drawHand();
        super.onActivated();
    }
}
