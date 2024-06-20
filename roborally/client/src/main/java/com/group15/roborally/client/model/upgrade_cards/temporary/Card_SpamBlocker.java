package com.group15.roborally.client.model.upgrade_cards.temporary;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.*;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardTemporary;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.server.model.GamePhase;

public class Card_SpamBlocker extends UpgradeCardTemporary {

    public Card_SpamBlocker() {
        super("Spam Blocker", 3, 0, 1, null, GamePhase.PROGRAMMING);
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
        CardField[] playerCardFields = owner.getCardHandFields();
        for (CardField playerCardField : playerCardFields) {
            if (playerCardField.getCard() instanceof CommandCard card) {
                if (card.command == Command.SPAM) {
                    playerCardField.setCard(owner.drawFromDeck());
                }
            }
        }
        super.onActivated();
    }
}
