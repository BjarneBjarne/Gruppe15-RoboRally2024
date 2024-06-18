package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Command;
import com.group15.roborally.client.model.CommandCard;
import com.group15.roborally.client.model.Phase;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;

public class Card_WeaselRoutine extends UpgradeCardTemporary {

    public Card_WeaselRoutine() {
        super("Weasel Routine", 3, 0, 1, null, Phase.PROGRAMMING);
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
        owner.discard(new CommandCard(Command.WEASEL_ROUTINE));
        super.onActivated();
    }
}