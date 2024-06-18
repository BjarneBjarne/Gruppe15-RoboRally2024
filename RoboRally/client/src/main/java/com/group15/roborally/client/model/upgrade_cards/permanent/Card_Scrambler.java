package com.group15.roborally.client.model.upgrade_cards.permanent;

import com.group15.roborally.client.controller.GameController;
import com.group15.roborally.client.model.CardField;
import com.group15.roborally.client.model.CommandCard;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.Player;
import com.group15.roborally.client.model.events.PlayerLaserHitListener;
import com.group15.roborally.client.model.upgrade_cards.UpgradeCardPermanent;

public class Card_Scrambler extends UpgradeCardPermanent {

    public Card_Scrambler() {
        super("Scrambler", 3, 0, 0, null);
    }

    @Override
    public void initialize(Player owner, GameController gameController) {
        super.initialize(owner, gameController);

        // Defining effects on events

        // OnDamageDealt
        eventListeners.add(EventHandler.subscribe((PlayerLaserHitListener) (damage, playerTakingDamage) -> {
            if (owner != playerTakingDamage) {
                int register = owner.board.getCurrentRegister();
                if (register < 4) {
                    printUsage();
                    CardField otherPlayerNextProgramField = playerTakingDamage.getProgramFields()[register + 1];
                    if (otherPlayerNextProgramField.getCard() != null) {
                        if(otherPlayerNextProgramField.getCard() instanceof CommandCard commandCard){
                            playerTakingDamage.discard(commandCard);
                        }
                        otherPlayerNextProgramField.setCard(playerTakingDamage.drawFromDeck());
                    }
                }
            }
            return damage;
        }, owner));
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
}
