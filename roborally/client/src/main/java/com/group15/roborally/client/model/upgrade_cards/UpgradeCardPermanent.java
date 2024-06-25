package com.group15.roborally.client.model.upgrade_cards;

import com.group15.roborally.client.model.CardField;
import com.group15.roborally.client.model.EventHandler;
import com.group15.roborally.client.model.events.EventListener;
import com.group15.roborally.server.model.GamePhase;

import java.util.ArrayList;
import java.util.List;

public abstract class UpgradeCardPermanent extends UpgradeCard {
    protected final List<EventListener> eventListeners = new ArrayList<>();

    public UpgradeCardPermanent(String title, int purchaseCost, int useCost, int maxUses, GamePhase refreshedOn, GamePhase... activatableOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn, activatableOn);
    }
    public UpgradeCardPermanent(String title, int purchaseCost, int useCost, int maxUses, GamePhase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }
    public UpgradeCardPermanent(String title, int purchaseCost, int useCost) {
        super(title, purchaseCost, useCost, 0, null);
    }
    public UpgradeCardPermanent(String title, int purchaseCost) {
        super(title, purchaseCost, 0, 0, null);
    }

 

    @Override
    public void unInitialize() {
        // Unsubscribe
        for (EventListener eventListener : eventListeners) {
            EventHandler.unsubscribe(eventListener, owner);
        }
        // Remove from player UI
        for (CardField cardField : owner.getPermanentUpgradeCardFields()) {
            if (cardField.getCard() == this) {
                cardField.setCard(null);
            }
        }
        super.unInitialize();
    }
}
