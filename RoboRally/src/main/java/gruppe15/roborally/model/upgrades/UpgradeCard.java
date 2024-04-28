package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.Player;

/**
 * This is the superclass for any upgrade card.
 * Players will initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * This way, any player will invoke events, but the card will only trigger, if the player invoking the event, is also the initializing owner of the card.
 */
public abstract class UpgradeCard {
    public UpgradeCard(String title, int purchaseCost, int useCost, int uses) {
        this.title = title;
        this.purchaseCost = purchaseCost;
        this.useCost = useCost;
        this.uses = uses;
    }

    protected String title;
    protected int purchaseCost;
    protected int useCost;
    protected int uses;
    protected int currentUses = uses;


    protected boolean onCooldown = false;
    private boolean activatable = false;

    public UpgradeCard() {

    }

    /**
     * Initializes the card to respond to actions performed by the owner. Can "maybe" be initialized to multiple owners?
     * @param owner The player who buys the card.
     */
    public abstract void initialize(Player owner);

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
