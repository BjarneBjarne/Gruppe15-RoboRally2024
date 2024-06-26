package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.server.model.GamePhase;

public class Card_EnergyRoutine extends UpgradeCardTemporary {

    public Card_EnergyRoutine() {
        super("Energy Routine", 3, 0, 1, null, GamePhase.PROGRAMMING);
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
        owner.discard(new CommandCard(Command.ENERGY_ROUTINE));
        super.onActivated();
    }
}
