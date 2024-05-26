package gruppe15.roborally.model.upgrade_cards;

import gruppe15.roborally.controller.GameController;
import gruppe15.roborally.model.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This is the superclass for any upgrade card.
 * Players will initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * This way, any player will invoke events, but the card will only trigger, if the player invoking the event, is also the initializing owner of the card.
 */
public abstract class UpgradeCard extends Card {
    protected final String title;
    protected final int purchaseCost;

    // Uses handling
    protected final int useCost;
    protected final int maxUses;
    protected int currentUses;
    protected final Phase refreshedOn;

    // Activating
    protected final List<Phase> activatableOn;
    private boolean enabled = false;

    protected Player owner;
    protected GameController gameController;

    public UpgradeCard(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn, Phase... activatableOn) {
        this.title = title;
        this.purchaseCost = purchaseCost;
        this.useCost = useCost;
        this.maxUses = maxUses;
        this.refreshedOn = refreshedOn;
        this.activatableOn = Collections.unmodifiableList(Arrays.asList(activatableOn));
    }

    protected abstract void onEnabled();
    protected abstract void onDisabled();
    protected abstract void onActivated();

    /**
     * Cards must override this method.
     * Initializes the card to respond to actions performed by the owner.
     */
    public void initialize(Player owner, GameController gameController) {
        this.owner = owner;
        this.gameController = gameController;
        owner.board.setOnPhaseChange(phase -> {
            if (phase == refreshedOn) {
                refresh();
            }
            setEnabled(activatableOn.contains(phase));
        });
        refresh();
    }

    public void unInitialize() {
        this.owner = null;
    }

    protected void refresh() {
        currentUses = maxUses;
    }

    @Override
    public String getName() {
        return title;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
        if (this.enabled)
            this.onEnabled();
        else
            this.onDisabled();
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean onCooldown() {
        return currentUses == 0;
    }

    /**
     * Defines the behavior that should happen when manually activated by the player.
     * <br>
     * For permanent cards, this may include behavior that the player can choose when to use. (Maybe because activation has a cost or the card only can be used once per round, register, etc.)
     * <br>
     * For temporary cards, this typically includes most of their behavior.
     */
    public void tryActivate() {
        if (canBeActivated()) {
            this.onActivated();
            this.currentUses--;
        }
    }

    public boolean canBeActivated() {
        return enabled && !onCooldown();
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }

    public boolean getHasActivateButton() {
        return activatableOn != null;
    }
}
