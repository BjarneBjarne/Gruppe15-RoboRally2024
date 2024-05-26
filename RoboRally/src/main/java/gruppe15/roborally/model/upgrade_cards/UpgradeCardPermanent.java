package gruppe15.roborally.model.upgrade_cards;

import gruppe15.roborally.model.EventHandler;
import gruppe15.roborally.model.Phase;
import gruppe15.roborally.model.events.EventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class UpgradeCardPermanent extends UpgradeCard {
    protected final List<EventListener> eventListeners = new ArrayList<>();

    public UpgradeCardPermanent(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn, Phase... activatableOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn, activatableOn);
    }
    public UpgradeCardPermanent(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        super(title, purchaseCost, useCost, maxUses, refreshedOn);
    }

    @Override
    public void unInitialize() {
        super.unInitialize();

        for (EventListener eventListener : eventListeners) {
            EventHandler.unsubscribe(eventListener, owner);
        }
    }
}
