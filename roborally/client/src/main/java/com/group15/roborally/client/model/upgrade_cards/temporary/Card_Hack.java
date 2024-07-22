package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Command;
import com.group15.roborally.client.model.CommandCard;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.common.model.GamePhase;

public class Card_Hack extends UpgradeCardTemporary {

    public Card_Hack() {
        super("Hack", 1, 0, 1, null, true, GamePhase.PLAYER_ACTIVATION);
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
    public void onActivated() {
        Command command = ((CommandCard)(owner.getProgramField(owner.board.getCurrentRegister()).getCard())).getCommand();
        owner.queueCommand(command, gameController);
        super.onActivated();
    }
}
