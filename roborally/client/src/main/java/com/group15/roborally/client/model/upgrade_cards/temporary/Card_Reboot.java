package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.common.model.GamePhase;

public class Card_Reboot extends UpgradeCardTemporary {

    public Card_Reboot() {
        super("Reboot", 1, 0, 1, null, false, GamePhase.PLAYER_ACTIVATION, GamePhase.BOARD_ACTIVATION, GamePhase.PROGRAMMING);
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
        EventHandler.event_PlayerReboot(owner, false, gameController);
        gameController.handleNextInteraction();
        super.onActivated();
    }
}
