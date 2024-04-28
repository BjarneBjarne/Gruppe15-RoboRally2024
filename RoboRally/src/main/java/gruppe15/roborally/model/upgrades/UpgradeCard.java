package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Player;

import java.util.List;

abstract class UpgradeCard {
    protected String title;
    protected int purchaseCost;
    protected int useCost;
    protected int uses;
    protected int currentUses;


    protected boolean onCooldown = false;
    private boolean activatable = false;

    public UpgradeCard() {

    }

    public void enable() {
        this.activatable = true;
        this.onEnabled();
    }
    protected abstract void onEnabled();
    public void disable() {
        this.activatable = false;
        this.onDisabled();
    }
    protected abstract void onDisabled();
    public boolean isActivatable() {
        return this.activatable;
    }
    protected abstract void onActivated();

    /**
     * Defines the behavior that should happen when manually activated by the player.
     * <br>
     * For permanent cards, this may include behavior that the player can choose when to use. (Maybe because activation has a cost or the card only can be used once per round, register, etc.)
     * <br>
     * For temporary cards, this typically includes most of their behavior.
     */
    public void tryActivate() {
        if (activatable && !onCooldown) {
            this.onActivated();
        }
    }
}
