package gruppe15.roborally.model.upgrades;

import gruppe15.roborally.model.*;

/**
 * This is the superclass for any upgrade card.
 * Players will initialize the upgrade cards with themselves as owner, whenever they purchase a card.
 * This way, any player will invoke events, but the card will only trigger, if the player invoking the event, is also the initializing owner of the card.
 */
public abstract class UpgradeCard extends Card {
    protected String title;
    protected int purchaseCost;
    protected int useCost;
    private int maxUses;
    protected Phase refreshedOn;
    protected Player owner;
    public UpgradeCard(String title, int purchaseCost, int useCost, int maxUses, Phase refreshedOn) {
        this.title = title;
        this.purchaseCost = purchaseCost;
        this.useCost = useCost;
        this.maxUses = maxUses;
        this.refreshedOn = refreshedOn;
    }

    private boolean enabled = false;
    private int currentUses = maxUses;


    /**
     * Cards must override this method.
     * Initializes the card to respond to actions performed by the owner. Can "maybe" be initialized to multiple owners?
     */
    public void initialize(Board board, Player owner) {
        this.owner = owner;
        board.setOnPhaseChange(phase -> {
            if (phase == refreshedOn) {
                refresh();
            }
        });
    }

    public void unInitialize() {
        this.owner = null;
    }

    protected void refresh() {

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
    protected abstract void onEnabled();
    protected abstract void onDisabled();
    public boolean isEnabled() {
        return this.enabled;
    }
    protected abstract void onActivated();

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
    protected boolean tryActivate() {
        if (enabled && !onCooldown()) {
            this.onActivated();
            this.currentUses--;
            return true;
        }
        return false;
    }

    public int getPurchaseCost() {
        return purchaseCost;
    }
}
