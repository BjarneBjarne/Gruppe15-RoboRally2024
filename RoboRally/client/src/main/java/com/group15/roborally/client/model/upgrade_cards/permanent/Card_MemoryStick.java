package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

public class Card_MemoryStick extends UpgradeCardPermanent {
    public Card_MemoryStick() {
        super("Memory Stick", 3);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        printUsage();
        owner.setMaxNoOfCardsInHand(owner.getMaxNoOfCardsInHand() + 1);
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

    @Override
    public void unInitialize() {
        owner.setMaxNoOfCardsInHand(owner.getMaxNoOfCardsInHand() - 1);
        super.unInitialize();
    }
}
